package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

public class Project {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private String svnUrl;
	private String gitFastImportFile;
	private long startRevision = 1;
	private long endRevision = 1;
	private boolean endHeadRevision = true;
	private SortedMap<String, String> authors = new TreeMap<>();
	private List<TrunkEntry> trunkEntries = new ArrayList<>();

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
			updateAuthor(null, svnUsername, gitAuthor);
		}
	}

	public Map<String, String> getAuthors() {
		return authors;
	}

	public void setAuthors(Map<String, String> authors) {
		this.authors = new TreeMap<>(authors);
	}

	public List<TrunkEntry> getTrunkEntries() {
		return trunkEntries;
	}

	public void setTrunkEntries(List<TrunkEntry> trunkEntries) {
		this.trunkEntries = trunkEntries;
	}

	public void addTrunkEntry(TrunkEntry trunkEntry) {
		List<TrunkEntry> oldEntries = new ArrayList<>(trunkEntries);
		trunkEntries.add(trunkEntry);
		propertyChangeSupport.firePropertyChange("trunkEntries", oldEntries, trunkEntries);
	}

	public void removeTrunkEntry(TrunkEntry trunkEntry) {
		List<TrunkEntry> oldEntries = new ArrayList<>(trunkEntries);
		trunkEntries.remove(trunkEntry);
		propertyChangeSupport.firePropertyChange("trunkEntries", oldEntries, trunkEntries);
	}

	public void updateAuthor(String existingSvnUsername, String newSvnUsername, String newGitAuthor) {
		String existingGitAuthor = authors.get(existingSvnUsername);
		authors.remove(existingSvnUsername);
		authors.put(newSvnUsername, newGitAuthor);
		propertyChangeSupport.firePropertyChange("authors", existingSvnUsername + ":" + existingGitAuthor, newSvnUsername + ":" + newGitAuthor);
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
