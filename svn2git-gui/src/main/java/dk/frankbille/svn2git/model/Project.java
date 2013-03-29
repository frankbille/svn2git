package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Properties;

public class Project {

	private Properties authors = new Properties();
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public Project() {
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

	public Properties getAuthors() {
		return authors;
	}

	public void updateAuthor(String existingSvnUsername, String newSvnUsername, String newGitAuthor) {
		String existingGitAuthor = authors.getProperty(existingSvnUsername);
		authors.remove(existingSvnUsername);
		authors.setProperty(newSvnUsername, newGitAuthor);
		propertyChangeSupport.firePropertyChange("authors", existingSvnUsername+":"+existingGitAuthor, newSvnUsername+":"+newGitAuthor);
	}
	
}
