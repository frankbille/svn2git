package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dk.frankbille.svn2git.model.Project;

public class AuthorsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private static class AuthorMapping {
		private final String svnUsername;
		private final String gitAuthor;

		public AuthorMapping(String svnUsername, String gitAuthor) {
			this.svnUsername = svnUsername;
			this.gitAuthor = gitAuthor;
		}

		public String getSvnUsername() {
			return svnUsername;
		}

		public String getGitAuthor() {
			return gitAuthor;
		}
	}

	private final Project project;
	private List<AuthorMapping> authors;

	public AuthorsTableModel(Project project) {
		this.project = project;
		this.project.addPropertyChangeListener("authors", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				authors = null;
			}
		});
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public int getRowCount() {
		return getAuthors().size();
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
		AuthorMapping authorMapping = getAuthors().get(rowIndex);
		if (columnIndex == 0) {
			return authorMapping.getSvnUsername();
		} else if (columnIndex == 1) {
			return authorMapping.getGitAuthor();
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		AuthorMapping authorMapping = getAuthors().get(rowIndex);
		String value = aValue != null ? aValue.toString() : null;
		if (columnIndex == 0) {
			project.updateAuthor(authorMapping.getSvnUsername(), value, authorMapping.getGitAuthor());
		} else if (columnIndex == 1) {
			project.updateAuthor(authorMapping.getSvnUsername(), authorMapping.getSvnUsername(), value);
		}
	}
	
	private List<AuthorMapping> getAuthors() {
		if (authors == null) {
			authors = new ArrayList<>();
			Map<String, String> authorMappings = project.getAuthors();
			for (String svnUsername : authorMappings.keySet()) {
				String gitAuthor = authorMappings.get(svnUsername);
				authors.add(new AuthorMapping(svnUsername, gitAuthor));
			}
		}
		
		return authors;
	}

}
