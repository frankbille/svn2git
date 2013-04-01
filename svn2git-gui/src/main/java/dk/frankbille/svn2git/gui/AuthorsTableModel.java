package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dk.frankbille.svn2git.model.AuthorMapping;
import dk.frankbille.svn2git.model.Project;

public class AuthorsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private class AuthorMappingChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			AuthorMapping authorMapping = (AuthorMapping) evt.getSource();
			int index = project.getAuthors().indexOf(authorMapping);
			if (index > -1) {
				fireTableRowsUpdated(index, index);
			} else {
				fireTableDataChanged();
			}
		}
	}
	
	private Project project;
	private final AuthorMappingChangeListener authorMappingChangeListener;

	public AuthorsTableModel() {
		this.authorMappingChangeListener = new AuthorMappingChangeListener();
	}

	public void setProject(Project project) {
		this.project = project;
		
		this.project.addPropertyChangeListener("authors", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				List<AuthorMapping> authors = AuthorsTableModel.this.project.getAuthors();
				for (AuthorMapping authorMapping : authors) {
					authorMapping.removePropertyChangeListener(authorMappingChangeListener);
					authorMapping.addPropertyChangeListener(authorMappingChangeListener);
				}
				
				fireTableDataChanged();
			}
		});
		
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return project != null ? project.getAuthors().size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "SVN username";
		} else if (column == 1) {
			return "GIT author";
		}
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		AuthorMapping authorMapping = project.getAuthors().get(rowIndex);
		if (columnIndex == 0) {
			return authorMapping.getSvnUsername();
		} else if (columnIndex == 1) {
			return authorMapping.getGitAuthor();
		}
		return null;
	}

	public void removeAuthors(int... rows) {
		Map<AuthorMapping, Integer> authorsToRemove = new HashMap<>();
		for (int row : rows) {
			authorsToRemove.put(project.getAuthors().get(row), row);
		}
		for (AuthorMapping authorMapping : authorsToRemove.keySet()) {
			project.removeAuthor(authorMapping);
		}
		fireTableDataChanged();
	}

}
