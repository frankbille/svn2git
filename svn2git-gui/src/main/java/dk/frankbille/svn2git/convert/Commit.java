package dk.frankbille.svn2git.convert;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Commit {

	private String author;
	private Date commitDate;
	private String commitMessage;
	private final Map<File, String> files = new HashMap<>();
	
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
	
	public Map<File, String> getFiles() {
		return files;
	}
	
	public void addFile(File file, String operation) {
		files.put(file, operation);
	}
	
}
