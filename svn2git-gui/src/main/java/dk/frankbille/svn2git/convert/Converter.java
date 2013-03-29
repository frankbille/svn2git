package dk.frankbille.svn2git.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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

import dk.frankbille.svn2git.model.TrunkEntry;

public class Converter {
	
	private final SVNURL svnUrl;
	private final File workspace;
	private final File usersFile;
	private final File gitFastImportFile;
	
	private FileOutputStream gitFastImportFileOutputStream;
	
	private SVNClientManager svnClient;
	
	private Set<TrunkEntry> trunkEntries = new HashSet<>();
	
	private Properties authors;
	
	public Converter(SVNURL svnUrl, File workspace, File usersFile, File gitFastImportFile) {
		this.svnUrl = svnUrl;
		this.workspace = workspace;
		this.usersFile = usersFile;
		this.gitFastImportFile = gitFastImportFile;

		svnClient = SVNClientManager.newInstance();
	}
	
	public void convert() throws Exception {
		long startRevision = 4398;
		long endRevision = 10000;
		
		SVNUpdateClient updateClient = svnClient.getUpdateClient();
		
		Set<String> trunkCheckoutPaths = new HashSet<>();
		for (TrunkEntry trunkEntry : trunkEntries) {
			trunkCheckoutPaths.add(trunkEntry.getCheckoutPath());
		}
		
		for (long currentRevision = startRevision; currentRevision <= endRevision; currentRevision++) {
			SVNRevision revision = SVNRevision.create(currentRevision);
			System.out.println("Processing revision: "+revision);
			
			for (String trunkCheckoutPath : trunkCheckoutPaths) {
				File trunkWorkspace = new File(workspace, trunkCheckoutPath);
				if (trunkWorkspace.exists()) {
					try {
						System.out.println("Starting to update "+trunkCheckoutPath);
						updateClient.doUpdate(trunkWorkspace, revision, SVNDepth.INFINITY, true, true);
						System.out.println("Update completed of "+trunkCheckoutPath);
						processWorkspace(trunkWorkspace, revision);
					} catch (SVNException e) {
						System.out.println("Nothing to update on "+trunkCheckoutPath);
						// Suppress
						FileUtils.deleteDirectory(trunkWorkspace);
					}
				} else {
					trunkWorkspace.mkdirs();
					try {
						System.out.println("Starting to checkout "+trunkCheckoutPath);
						updateClient.doCheckout(svnUrl.appendPath(trunkCheckoutPath, true), trunkWorkspace, revision, revision, SVNDepth.INFINITY, true);
						System.out.println("Checkout completed of "+trunkCheckoutPath);
						processWorkspace(trunkWorkspace, revision);
					} catch (SVNException e) {
						// Suppress
						System.out.println("Nothing to checkout on "+trunkCheckoutPath);
						FileUtils.deleteDirectory(trunkWorkspace);
					}
				}				
			}
		}
		
		IOUtils.closeQuietly(gitFastImportFileOutputStream);
	}
	
	private void processWorkspace(final File workspace, final SVNRevision revision) throws SVNException, IOException {
		final Commit commit = new Commit();
		
		SVNLogClient logClient = svnClient.getLogClient();
		logClient.doLog(new File[]{workspace}, revision, revision, false, true, 0, new ISVNLogEntryHandler() {
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
						commit.addFile(new File(Converter.this.workspace, changedPath), getOperation(svnLogEntryPath.getType()));
					}
				}
			}
		});
		
		Map<File, String> files = commit.getFiles();
		if (false == files.isEmpty()) {
			appendToFastImportFile("commit refs/heads/master");
			appendNewlineToFastImportFile();
			appendToFastImportFile("mark :"+revision.getNumber());
			appendNewlineToFastImportFile();
			appendToFastImportFile("committer "+getGitAuthor(commit.getAuthor())+" "+commit.getCommitDate().getTime()/1000+" +0100");
			appendNewlineToFastImportFile();
			appendToFastImportFile("data "+commit.getCommitMessage().getBytes().length);
			appendNewlineToFastImportFile();
			appendToFastImportFile(commit.getCommitMessage());
			appendNewlineToFastImportFile();
			for (File file : files.keySet()) {
				String operation = files.get(file);
				String filePath = file.getAbsolutePath().replace(this.workspace.getAbsolutePath(), "");
				TrunkEntry trunkEntry = getEntryForPath(filePath);
				filePath = filePath.replace(trunkEntry.getSourcePath(), trunkEntry.getDestinationPath());
				
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
		} else {
			System.out.println("Nothing to convert for revision: "+revision);
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
	
	private TrunkEntry getEntryForPath(String path) {
		for (TrunkEntry trunkEntry : trunkEntries) {
			if (path.startsWith(trunkEntry.getSourcePath())) {
				return trunkEntry;
			}
		}
		
		return null;
	}
	
	private boolean isPathIncluded(String path) {
		for (TrunkEntry trunkEntry : trunkEntries) {
			if (path.contains(trunkEntry.getSourcePath())) {
				return true;
			}
		}
		
		return false;
	}
	
	private void appendToFastImportFile(CharSequence text) throws IOException {
		IOUtils.write(text, getGitFastImportFileOutputStream());
	}
	
	private void appendNewlineToFastImportFile() throws IOException {
		appendToFastImportFile("\n");
	}
	
	private void appendToFastImportFile(File file) throws IOException {
		IOUtils.copy(new FileInputStream(file), getGitFastImportFileOutputStream());
	}
	
	private OutputStream getGitFastImportFileOutputStream() throws FileNotFoundException {
		if (gitFastImportFileOutputStream == null) {
			gitFastImportFileOutputStream = new FileOutputStream(gitFastImportFile, true);
		}
		
		return gitFastImportFileOutputStream;
	}
	
	private String getGitAuthor(String svnAuthor) throws FileNotFoundException, IOException {
		if (authors == null) {
			authors = new Properties();
			authors.load(new FileReader(usersFile));
		}
		
		return authors.getProperty(svnAuthor);
	}
}
