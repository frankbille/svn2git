package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;

public class MappingEntryListModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private final Project project;

	public MappingEntryListModel(Project project) {
		this.project = project;
		this.project.addPropertyChangeListener("trunkEntries", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				MappingEntryListModel.this.fireTableDataChanged();
			}
		});
	}

	@Override
	public int getRowCount() {
		return project.getMappingEntries().size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Checkout Path";
		case 1:
			return "Source Path";
		case 2:
			return "Destination Path";
		}

		return null;
	}

	@Override
	public Object getValueAt(final int rowIndex, int columnIndex) {
		MappingEntry mappingEntry = project.getMappingEntries().get(rowIndex);
		mappingEntry.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				MappingEntryListModel.this.fireTableRowsUpdated(rowIndex, rowIndex);
			}
		});
		switch (columnIndex) {
		case 0:
			return mappingEntry.getCheckoutPath();
		case 1:
			return mappingEntry.getSourcePath();
		case 2:
			return mappingEntry.getDestinationPath();
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
			fireTableRowsDeleted(entriesToRemove.get(trunkEntry), entriesToRemove.get(trunkEntry));
		}
	}

}
