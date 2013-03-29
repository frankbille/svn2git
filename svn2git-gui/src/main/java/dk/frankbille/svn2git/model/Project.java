package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Project {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Properties authors = new Properties();
	private List<TrunkEntry> trunkEntries = new ArrayList<>();
	
	public Project() {
	}

	public Properties getAuthors() {
		return authors;
	}
	
	public List<TrunkEntry> getTrunkEntries() {
		return trunkEntries;
	}
	
	public void addTrunkEntry(TrunkEntry trunkEntry) {
		trunkEntries.add(trunkEntry);
	}
	
	public void removeTrunkEntry(TrunkEntry trunkEntry) {
		trunkEntries.remove(trunkEntry);
	}

	public void updateAuthor(String existingSvnUsername, String newSvnUsername, String newGitAuthor) {
		String existingGitAuthor = authors.getProperty(existingSvnUsername);
		authors.remove(existingSvnUsername);
		authors.setProperty(newSvnUsername, newGitAuthor);
		propertyChangeSupport.firePropertyChange("authors", existingSvnUsername+":"+existingGitAuthor, newSvnUsername+":"+newGitAuthor);
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
