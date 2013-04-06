package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;

public class MappingEntryListModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private class MappingEntryChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			MappingEntry mappingEntry = (MappingEntry) evt.getSource();
			int index = project.getMappingEntries().indexOf(mappingEntry);
			if (index > -1) {
				fireTableRowsUpdated(index, index);
			} else {
				fireTableDataChanged();
			}
		}
	}

	private Project project;
	
	private final MappingEntryChangeListener mappingEntryChangeListener;

	public MappingEntryListModel() {
		this.mappingEntryChangeListener = new MappingEntryChangeListener();
	}

	public void setProject(Project project) {
		this.project = project;
		
		this.project.addPropertyChangeListener("mappingEntries", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				List<MappingEntry> mappingEntries = MappingEntryListModel.this.project.getMappingEntries();
				for (MappingEntry mappingEntry : mappingEntries) {
					mappingEntry.removePropertyChangeListener(mappingEntryChangeListener);
					mappingEntry.addPropertyChangeListener(mappingEntryChangeListener);
				}
				
				fireTableDataChanged();
			}
		});
		
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return project != null ? project.getMappingEntries().size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Source Path";
		case 1:
			return "Destination Path";
		case 2:
			return "Destination Ref";
		}

		return null;
	}

	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {
		MappingEntry mappingEntry = project.getMappingEntries().get(rowIndex);
		switch (columnIndex) {
		case 0:
			return mappingEntry.getSourcePath();
		case 1:
			return mappingEntry.getDestinationPath();
		case 2:
			return mappingEntry.getDestinationRef();
		}

		return null;
	}

	public void removeMappingEntries(int... rows) {
		Map<MappingEntry, Integer> entriesToRemove = new HashMap<>();
		for (int row : rows) {
			entriesToRemove.put(project.getMappingEntries().get(row), row);
		}
		for (MappingEntry trunkEntry : entriesToRemove.keySet()) {
			project.removeMappingEntry(trunkEntry);
		}
		fireTableDataChanged();
	}

}
