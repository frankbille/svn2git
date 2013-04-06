package dk.frankbille.svn2git.convert;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.SVNLogEntryPath;

public class Commit {

	private String author;
	private Date commitDate;
	private String commitMessage;
	private final Map<File, SVNLogEntryPath> files = new HashMap<>();
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public Date getCommitDate() {
		return commitDate;
	}
	
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}
	
	public String getCommitMessage() {
		return commitMessage;
	}
	
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public boolean hasFiles() {
		return files.isEmpty() == false;
	}
	
	public Map<File, SVNLogEntryPath> getAddedFiles() {
		return getFilesByChangeType(SVNLogEntryPath.TYPE_ADDED);
	}
	
	public Map<File, SVNLogEntryPath> getModifiedFiles() {
		return getFilesByChangeType(SVNLogEntryPath.TYPE_MODIFIED);
	}
	
	public Map<File, SVNLogEntryPath> getDeletedFiles() {
		return getFilesByChangeType(SVNLogEntryPath.TYPE_DELETED);
	}
	
	public Map<File, SVNLogEntryPath> getReplacedFiles() {
		return getFilesByChangeType(SVNLogEntryPath.TYPE_REPLACED);
	}

	private Map<File, SVNLogEntryPath> getFilesByChangeType(char changeType) {
		Map<File, SVNLogEntryPath> changedFiles = new HashMap<>();
		for (Entry<File, SVNLogEntryPath> entry : files.entrySet()) {
			if (entry.getValue().getType() == changeType) {
				changedFiles.put(entry.getKey(), entry.getValue());
			}
		}
		return changedFiles;
	}
	
	public void addFile(File file, SVNLogEntryPath logEntryPath) {
		files.put(file, logEntryPath);
	}
	
}
