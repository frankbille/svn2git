package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

public class Project {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private String svnUrl;
	private String gitFastImportFile;
	private String workspaceFolder;
	private long startRevision = 1;
	private long endRevision = 1;
	private boolean endHeadRevision = true;
	private List<AuthorMapping> authors = new ArrayList<>();
	private List<MappingEntry> mappingEntries = new ArrayList<>();

	public Project() {
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public void setSvnUrl(String svnUrl) {
		final String oldSvnUrl = this.svnUrl;
		this.svnUrl = svnUrl;
		propertyChangeSupport.firePropertyChange("svnUrl", oldSvnUrl, svnUrl);
	}

	public String getGitFastImportFile() {
		return gitFastImportFile;
	}

	public void setGitFastImportFile(String gitFastImportFile) {
		final String oldGitFastImportFile = this.gitFastImportFile;
		this.gitFastImportFile = gitFastImportFile;
		propertyChangeSupport.firePropertyChange("gitFastImportFile", oldGitFastImportFile, gitFastImportFile);
	}
	
	public String getWorkspaceFolder() {
		return workspaceFolder;
	}
	
	public void setWorkspaceFolder(String workspaceFolder) {
		String oldWorkspaceFolder = this.workspaceFolder;
		this.workspaceFolder = workspaceFolder;
		propertyChangeSupport.firePropertyChange("workspaceFolder", oldWorkspaceFolder, workspaceFolder);
	}

	public long getStartRevision() {
		return startRevision;
	}

	public void setStartRevision(long startRevision) {
		final long oldStartRevision = this.startRevision;
		this.startRevision = startRevision;
		propertyChangeSupport.firePropertyChange("startRevision", oldStartRevision, startRevision);
	}

	public long getEndRevision() {
		return endRevision;
	}

	public void setEndRevision(long endRevision) {
		final long oldEndRevision = this.endRevision;
		this.endRevision = endRevision;
		propertyChangeSupport.firePropertyChange("endRevision", oldEndRevision, endRevision);
	}

	public boolean isEndHeadRevision() {
		return endHeadRevision;
	}

	public void setEndHeadRevision(boolean endHeadRevision) {
		final boolean oldEndHeadRevision = this.endHeadRevision;
		this.endHeadRevision = endHeadRevision;

		if (false == endHeadRevision) {
			if (endRevision < startRevision) {
				setEndRevision(startRevision);
			}
		}

		propertyChangeSupport.firePropertyChange("endHeadRevision", oldEndHeadRevision, endHeadRevision);
	}

	public void loadAuthors(InputStream authorStream) throws IOException {
		authors.clear();

		String authorsContent = IOUtils.toString(authorStream, "UTF-8");
		StringTokenizer contentTokenizer = new StringTokenizer(authorsContent, "\r\n");
		while (contentTokenizer.hasMoreTokens()) {
			String line = contentTokenizer.nextToken();
			StringTokenizer lineTokenizer = new StringTokenizer(line, "=");
			String svnUsername = lineTokenizer.nextToken().trim();
			String gitAuthor = lineTokenizer.nextToken().trim();
			addAuthor(new AuthorMapping(svnUsername, gitAuthor));
		}
	}

	public List<AuthorMapping> getAuthors() {
		return authors;
	}

	public void setAuthors(List<AuthorMapping> authors) {
		this.authors = authors;
	}
	
	public void addAuthor(AuthorMapping authorMapping) {
		List<AuthorMapping> oldAuthors = new ArrayList<>(authors);
		authors.add(authorMapping);
		propertyChangeSupport.firePropertyChange("authors", oldAuthors, authors);
	}

	public void removeAuthor(AuthorMapping authorMapping) {
		List<AuthorMapping> oldAuthors = new ArrayList<>(authors);
		authors.remove(authorMapping);
		propertyChangeSupport.firePropertyChange("authors", oldAuthors, authors);
	}

	public String getGitAuthor(String svnUsername) {
		for (AuthorMapping authorMapping : authors) {
			if (svnUsername.equals(authorMapping.getSvnUsername())) {
				return authorMapping.getGitAuthor();
			}
		}
		return null;
	}

	public List<MappingEntry> getMappingEntries() {
		return mappingEntries;
	}

	public void setMappingEntries(List<MappingEntry> mappingEntries) {
		this.mappingEntries = mappingEntries;
	}

	public void addMappingEntry(MappingEntry mappingEntry) {
		List<MappingEntry> oldEntries = new ArrayList<>(mappingEntries);
		mappingEntries.add(mappingEntry);
		propertyChangeSupport.firePropertyChange("mappingEntries", oldEntries, mappingEntries);
	}

	public void removeMappingEntry(MappingEntry mappingEntry) {
		List<MappingEntry> oldEntries = new ArrayList<>(mappingEntries);
		mappingEntries.remove(mappingEntry);
		propertyChangeSupport.firePropertyChange("mappingEntries", oldEntries, mappingEntries);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
