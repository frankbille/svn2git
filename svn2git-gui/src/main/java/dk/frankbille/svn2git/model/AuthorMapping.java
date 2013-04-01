package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AuthorMapping {

	private String svnUsername;
	private String gitAuthor;
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public AuthorMapping() {
	}

	public AuthorMapping(String svnUsername, String gitAuthor) {
		this.svnUsername = svnUsername;
		this.gitAuthor = gitAuthor;
	}

	public String getSvnUsername() {
		return svnUsername;
	}

	public void setSvnUsername(String svnUsername) {
		String oldSvnUsername = this.svnUsername;
		this.svnUsername = svnUsername;
		propertyChangeSupport.firePropertyChange("svnUsername", oldSvnUsername, svnUsername);
	}

	public String getGitAuthor() {
		return gitAuthor;
	}

	public void setGitAuthor(String gitAuthor) {
		String oldGitAuthor = this.gitAuthor;
		this.gitAuthor = gitAuthor;
		propertyChangeSupport.firePropertyChange("gitAuthor", oldGitAuthor, gitAuthor);
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
