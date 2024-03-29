package dk.frankbille.svn2git.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class MappingEntry {

	private String sourcePath;
	private String destinationPath;
	private String destinationRef;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public MappingEntry() {
	}

	public MappingEntry(String sourcePath, String destinationPath, String destinationRef) {
		this.sourcePath = sourcePath;
		this.destinationPath = destinationPath;
		this.destinationRef = destinationRef;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		String oldSourcePath = this.sourcePath;
		this.sourcePath = sourcePath;
		propertyChangeSupport.firePropertyChange("sourcePath", oldSourcePath, sourcePath);
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		String oldDestinationPath = this.destinationPath;
		this.destinationPath = destinationPath;
		propertyChangeSupport.firePropertyChange("destinationPath", oldDestinationPath, destinationPath);
	}

	public String getDestinationRef() {
		return destinationRef;
	}

	public void setDestinationRef(String destinationRef) {
		String oldDestinationRef = this.destinationRef;
		this.destinationRef = destinationRef;
		propertyChangeSupport.firePropertyChange("destinationRef", oldDestinationRef, destinationRef);
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

	@Override
	public String toString() {
		return sourcePath + " => " + destinationPath;
	}

}
