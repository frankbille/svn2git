package dk.frankbille.svn2git.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
	
	private final Project project;
	private final SVNClientManager svnClient;

	private FileOutputStream gitFastImportFileOutputStream;
	
	private List<ConversionListener> conversionListeners = new ArrayList<>();
	
	public Converter(Project project) {
		this.project = project;
		svnClient = SVNClientManager.newInstance();
	}
	
	public void convert() throws Exception {
		long startRevision = project.getStartRevision();
		long endRevision;
		if (project.isEndHeadRevision()) {
			throw new UnsupportedOperationException("Not implemented yet");
		} else {
			endRevision = project.getEndRevision();
		}
		
		for (long currentRevision = startRevision; currentRevision <= endRevision; currentRevision++) {
			SVNRevision revision = SVNRevision.create(currentRevision);
			
			for (MappingEntry mappingEntry : project.getMappingEntries()) {
				checkoutOrUpdateMappingEntry(mappingEntry, revision);
				fireMappingEntryUpdated(mappingEntry);
			}
			
			processWorkspace(revision);
			fireRevisionProcessed(currentRevision);
		}
		
		IOUtils.closeQuietly(gitFastImportFileOutputStream);
	}
	
	public void addConversionListener(ConversionListener conversionListener) {
		conversionListeners.add(conversionListener);
	}
	
	public void removeConversionListener(ConversionListener conversionListener) {
		conversionListeners.remove(conversionListener);
	}
	
	private void checkoutOrUpdateMappingEntry(MappingEntry mappingEntry, SVNRevision revision) throws SVNException, IOException {		
		SVNUpdateClient updateClient = svnClient.getUpdateClient();
		SVNURL svnUrl = SVNURL.parseURIEncoded(project.getSvnUrl());
		
		File mappingEntryWorkspace = new File(project.getWorkspaceFolder(), mappingEntry.getSourcePath());
		
		if (mappingEntryWorkspace.exists()) {
			try {
				updateClient.doUpdate(mappingEntryWorkspace, revision, SVNDepth.INFINITY, true, true);
			} catch (SVNException e) {
				// Suppress
				FileUtils.deleteDirectory(mappingEntryWorkspace);
			}
		} else {
			mappingEntryWorkspace.mkdirs();
			try {
				updateClient.doCheckout(svnUrl.appendPath(mappingEntry.getSourcePath(), true), mappingEntryWorkspace, revision, revision, SVNDepth.INFINITY, true);
			} catch (SVNException e) {
				// Suppress
				FileUtils.deleteDirectory(mappingEntryWorkspace);
			}
		}
	}
	
	private void processWorkspace(final SVNRevision revision) throws SVNException, IOException {
		// For each destination ref
		Map<String, Set<MappingEntry>> destinationRefs = new HashMap<>();
		for (MappingEntry mappingEntry : project.getMappingEntries()) {
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
					logClient.doLog(new File[]{mappingEntryWorkspace}, revision, revision, false, true, 0, new ISVNLogEntryHandler() {
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
					// Suppress
				}
			}
			
			Map<File, String> files = commit.getFiles();
			if (false == files.isEmpty()) {
				appendToFastImportFile("commit "+destinationRef);
				appendNewlineToFastImportFile();
				appendToFastImportFile("mark :"+revision.getNumber());
				appendNewlineToFastImportFile();
				appendToFastImportFile("committer "+getGitAuthor(commit.getAuthor())+" "+commit.getCommitDate().getTime()/1000+" +0100");
				appendNewlineToFastImportFile();
				appendToFastImportFile("data "+commit.getCommitMessage().getBytes("UTF-8").length);
				appendNewlineToFastImportFile();
				appendToFastImportFile(commit.getCommitMessage());
				appendNewlineToFastImportFile();
				for (File file : files.keySet()) {
					String operation = files.get(file);
					String filePath = file.getAbsolutePath().replace(project.getWorkspaceFolder(), "");
					MappingEntry mappingEntry = getEntryForPath(filePath);
					filePath = filePath.replace(mappingEntry.getSourcePath(), mappingEntry.getDestinationPath());
					
					if ("M".equals(operation)) {
						appendToFastImportFile("M 644 inline "+filePath);
						appendNewlineToFastImportFile();
						appendToFastImportFile("data "+file.length());
						appendNewlineToFastImportFile();
						appendToFastImportFile(file);
						appendNewlineToFastImportFile();
					} else if ("D".equals(operation)) {
						appendToFastImportFile("D "+filePath);
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
		
		throw new IllegalArgumentException("Don't know how to handle "+svnType);
	}
	
	private MappingEntry getEntryForPath(String path) {
		for (MappingEntry mappingEntry : project.getMappingEntries()) {
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
		fireGitFastImportFileChanged();
	}
	
	private void appendToFastImportFile(File file) throws IOException {
		IOUtils.copy(new FileInputStream(file), getGitFastImportFileOutputStream());
		fireGitFastImportFileChanged();
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
	
	private void fireRevisionProcessed(long revisionNumber) {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.revisionProcessed(revisionNumber);
		}
	}
	
	private void fireMappingEntryUpdated(MappingEntry mappingEntry) {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.mappingEntryUpdated(mappingEntry);
		}
	}
	
	private void fireGitFastImportFileChanged() {
		for (ConversionListener conversionListener : conversionListeners) {
			conversionListener.gitFastImportFileChanged(new File(project.getGitFastImportFile()));
		}
	}
}
