package dk.frankbille.svn2git.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;

public class Converter {

	private static final Logger log = LoggerFactory.getLogger(Converter.class);
	
	private static class RevisionHandler implements ISVNLogEntryHandler {
		private long revision = -1;
		
		@Override
		public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
			revision = logEntry.getRevision();
		}
		
		public long getRevision() {
			return revision;
		}
	}

	private final Project project;
	private final SVNClientManager svnClient;

	private FileOutputStream gitFastImportFileOutputStream;

	private List<ConversionListener> conversionListeners = new ArrayList<>();

	private boolean run = true;

	private SVNURL svnUrl;

	public Converter(Project project) {
		this.project = project;
		svnClient = SVNClientManager.newInstance();
	}

	public void convert() throws SVNException, IOException {
		svnUrl = SVNURL.parseURIEncoded(project.getSvnUrl());

		long startRevision = project.getStartRevision();
		long endRevision;
		if (project.isEndHeadRevision()) {
			RevisionHandler headRevisionHandler = new RevisionHandler();
			svnClient.getLogClient().doLog(svnUrl, new String[] { "/" }, SVNRevision.HEAD, SVNRevision.HEAD, SVNRevision.HEAD, false, true, 0, headRevisionHandler);
			endRevision = headRevisionHandler.getRevision();
		} else {
			endRevision = project.getEndRevision();
		}

		try {
			for (long currentRevision = startRevision; currentRevision <= endRevision && run; currentRevision++) {
				SVNRevision revision = SVNRevision.create(currentRevision);

				log.info("Handling Subversion revision: " + currentRevision);

				// Find out which mapping entries is covered by the revision
				final Set<MappingEntry> revisionEntries = new HashSet<>();
				svnClient.getLogClient().doLog(svnUrl, new String[] { "/" }, revision, revision, revision, false, true, 0, new ISVNLogEntryHandler() {
					@Override
					public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
						Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
						for (String changedPath : changedPaths.keySet()) {
							MappingEntry entry = getEntryForPath(changedPath);
							if (entry != null) {
								revisionEntries.add(entry);
							}
						}
					}
				});

				if (log.isDebugEnabled()) {
					if (revisionEntries.isEmpty()) {
						log.debug("Nothing to convert");
					} else {
						log.debug("These entries will be processed in this revision:");
						for (MappingEntry mappingEntry : revisionEntries) {
							log.debug(mappingEntry.toString());
						}
					}
				}

				for (MappingEntry mappingEntry : project.getMappingEntries()) {
					if (revisionEntries.contains(mappingEntry)) {
						checkoutOrUpdateMappingEntry(mappingEntry, revision);
					}
					fireMappingEntryUpdated(mappingEntry, currentRevision, endRevision);
				}

				processWorkspace(revision, revisionEntries);
				fireRevisionProcessed(currentRevision, endRevision);
			}
		} catch (SVNException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw e;
		}

		IOUtils.closeQuietly(gitFastImportFileOutputStream);
	}

	public void addConversionListener(ConversionListener conversionListener) {
		conversionListeners.add(conversionListener);
	}

	public void removeConversionListener(ConversionListener conversionListener) {
		conversionListeners.remove(conversionListener);
	}

	public void stop() {
		run = false;
	}

	private void checkoutOrUpdateMappingEntry(MappingEntry mappingEntry, SVNRevision revision) throws SVNException, IOException {
		SVNUpdateClient updateClient = svnClient.getUpdateClient();

		File mappingEntryWorkspace = new File(project.getWorkspaceFolder(), mappingEntry.getSourcePath());

		if (mappingEntryWorkspace.exists()) {
			updateClient.doUpdate(mappingEntryWorkspace, revision, SVNDepth.INFINITY, true, true);
		} else {
			mappingEntryWorkspace.mkdirs();
			updateClient.doCheckout(svnUrl.appendPath(mappingEntry.getSourcePath(), true), mappingEntryWorkspace, revision, revision, SVNDepth.INFINITY, true);
		}
	}

	private void processWorkspace(final SVNRevision revision, Set<MappingEntry> revisionEntries) throws SVNException, IOException {
		// For each destination ref
		Map<String, Set<MappingEntry>> destinationRefs = new HashMap<>();
		for (MappingEntry mappingEntry : revisionEntries) {
			Set<MappingEntry> destinationRefEntries = destinationRefs.get(mappingEntry.getDestinationRef());
			if (destinationRefEntries == null) {
				destinationRefEntries = new HashSet<>();
				destinationRefs.put(mappingEntry.getDestinationRef(), destinationRefEntries);
			}
			destinationRefEntries.add(mappingEntry);
		}

		for (String destinationRef : destinationRefs.keySet()) {
			final Commit commit = new Commit();

			SVNLogClient logClient = svnClient.getLogClient();
			for (MappingEntry mappingEntry : destinationRefs.get(destinationRef)) {
				File mappingEntryWorkspace = new File(project.getWorkspaceFolder(), mappingEntry.getSourcePath());

				try {
					logClient.doLog(new File[] { mappingEntryWorkspace }, revision, revision, false, true, 0, new ISVNLogEntryHandler() {
						@Override
						public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
							commit.setAuthor(logEntry.getAuthor());
							commit.setCommitDate(logEntry.getDate());
							commit.setCommitMessage(logEntry.getMessage());

							Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
							for (Entry<String, SVNLogEntryPath> changedPathEntries : changedPaths.entrySet()) {
								String changedPath = changedPathEntries.getKey();
								SVNLogEntryPath svnLogEntryPath = changedPathEntries.getValue();

								if (svnLogEntryPath.getKind().equals(SVNNodeKind.FILE) && isPathIncluded(changedPath)) {
									commit.addFile(new File(project.getWorkspaceFolder(), changedPath), getOperation(svnLogEntryPath.getType()));
								}
							}
						}
					});
				} catch (SVNException e) {
					log.error("Suppressed exception", e);
				}
			}

			Map<File, String> files = commit.getFiles();
			if (false == files.isEmpty()) {
				appendToFastImportFile("commit " + destinationRef);
				appendNewlineToFastImportFile();
				appendToFastImportFile("mark :" + revision.getNumber());
				appendNewlineToFastImportFile();
				appendToFastImportFile("committer " + getGitAuthor(commit.getAuthor()) + " " + commit.getCommitDate().getTime() / 1000 + " +0100");
				appendNewlineToFastImportFile();
				appendToFastImportFile("data " + commit.getCommitMessage().getBytes("UTF-8").length);
				appendNewlineToFastImportFile();
				appendToFastImportFile(commit.getCommitMessage());
				appendNewlineToFastImportFile();
				for (File file : files.keySet()) {
					String operation = files.get(file);
					String filePath = file.getAbsolutePath().replace(project.getWorkspaceFolder(), "");
					filePath = filePath.replace(System.getProperty("file.separator"), "/");
					MappingEntry mappingEntry = getEntryForPath(filePath);
					filePath = filePath.replace(mappingEntry.getSourcePath(), mappingEntry.getDestinationPath());

					if ("M".equals(operation)) {
						appendToFastImportFile("M 644 inline " + filePath);
						appendNewlineToFastImportFile();
						appendToFastImportFile("data " + file.length());
						appendNewlineToFastImportFile();
						appendToFastImportFile(file);
						appendNewlineToFastImportFile();
					} else if ("D".equals(operation)) {
						appendToFastImportFile("D " + filePath);
						appendNewlineToFastImportFile();
					}
				}
			}
		}
	}

	private String getOperation(char svnType) {
		switch (svnType) {
		case 'M':
		case 'A':
		case 'R':
			return "M";
		case 'D':
			return "D";
		}

		throw new IllegalArgumentException("Don't know how to handle " + svnType);
	}

	private MappingEntry getEntryForPath(String path) {
		List<MappingEntry> mappingEntries = new ArrayList<>(project.getMappingEntries());
		Collections.sort(mappingEntries, new Comparator<MappingEntry>() {
			@Override
			public int compare(MappingEntry o1, MappingEntry o2) {
				return o2.getSourcePath().compareTo(o1.getSourcePath());
			}
		});
		for (MappingEntry mappingEntry : mappingEntries) {
			if (path.startsWith(mappingEntry.getSourcePath())) {
				return mappingEntry;
			}
		}
		return null;
	}

	private boolean isPathIncluded(String path) {
		for (MappingEntry mappingEntry : project.getMappingEntries()) {
			if (path.contains(mappingEntry.getSourcePath())) {
				return true;
			}
		}

		return false;
	}

	private void appendNewlineToFastImportFile() throws IOException {
		appendToFastImportFile("\n");
	}

	private void appendToFastImportFile(CharSequence text) throws IOException {
		IOUtils.write(text, getGitFastImportFileOutputStream(), "UTF-8");
		fireGitFastImportFileChanged(text.toString().getBytes("UTF-8").length);
	}

	private void appendToFastImportFile(File file) throws IOException {
		int bytesCopied = IOUtils.copy(new FileInputStream(file), getGitFastImportFileOutputStream());
		fireGitFastImportFileChanged(bytesCopied);
	}

	private OutputStream getGitFastImportFileOutputStream() throws FileNotFoundException {
		if (gitFastImportFileOutputStream == null) {
			gitFastImportFileOutputStream = new FileOutputStream(project.getGitFastImportFile(), true);
		}

		return gitFastImportFileOutputStream;
	}

	private String getGitAuthor(String svnUsername) throws FileNotFoundException, IOException {
		return project.getGitAuthor(svnUsername);
	}

	private void fireRevisionProcessed(long revisionNumber, long endRevisionNumber) {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.revisionProcessed(revisionNumber, endRevisionNumber);
		}
	}

	private void fireMappingEntryUpdated(MappingEntry mappingEntry, long revisionNumber, long endRevisionNumber) {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.mappingEntryUpdated(mappingEntry, revisionNumber, endRevisionNumber);
		}
	}

	private void fireGitFastImportFileChanged(int numberOfBytesAppended) {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.gitFastImportFileChanged(new File(project.getGitFastImportFile()), numberOfBytesAppended);
		}
	}
}
