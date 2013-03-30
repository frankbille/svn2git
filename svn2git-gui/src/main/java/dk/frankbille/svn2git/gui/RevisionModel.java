package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JSpinner;

import dk.frankbille.svn2git.model.Project;

public class RevisionModel extends AbstractSpinnerModel implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	private final boolean start;
	private final Project project;
	
	public RevisionModel(boolean startRevision, Project project, JSpinner revisionField) {
		start = startRevision;
		this.project = project;
		project.addPropertyChangeListener("startRevision", this);
		project.addPropertyChangeListener("endRevision", this);
		
		revisionField.setModel(this);
		
		fireStateChanged();
	}
	
	@Override
	public Long getValue() {
		if (start) {
			return project.getStartRevision();
		} else {
			return project.getEndRevision();
		}
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Integer) {
			Integer intValue = (Integer) value;
			value = intValue.longValue();
		}
		
		if (value instanceof Long == false) {
			throw new IllegalArgumentException("Must be a long as parameter");
		}
		
		long revision = (long) value;
		
		if (revision < 1) {
			revision = 1;
		}
		
		if (start) {
			if (false == project.isEndHeadRevision() && revision > project.getEndRevision()) {
				throw new IllegalArgumentException("Start must not be after end");
			}
			
			project.setStartRevision(revision);
		} else {
			if (revision < project.getStartRevision()) {
				throw new IllegalArgumentException("End must not be before start");
			}
			
			project.setEndRevision(revision);
		}
		
		fireStateChanged();
	}

	@Override
	public Object getNextValue() {
		if (start) {
			if (false == project.isEndHeadRevision() && project.getStartRevision() == project.getEndRevision()) {
				return null;
			}
			
			return project.getStartRevision()+1;
		} else {
			return project.getEndRevision()+1;
		}
	}

	@Override
	public Object getPreviousValue() {
		if (start) {
			if (project.getStartRevision() == 1) {
				return null;
			}
			
			return project.getStartRevision()-1;
		} else {
			if (project.getStartRevision() == project.getEndRevision()) {
				return null;
			}
			
			return project.getEndRevision()-1;
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		fireStateChanged();
	}

}
