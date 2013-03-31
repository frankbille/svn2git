package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;

import dk.frankbille.svn2git.model.Project;
import dk.frankbille.svn2git.model.MappingEntry;

public class MappingEntryListModel extends AbstractListModel<MappingEntry> {
	private static final long serialVersionUID = 1L;

	private final Project project;

	public MappingEntryListModel(Project project) {
		this.project = project;
		this.project.addPropertyChangeListener("trunkEntries", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				MappingEntryListModel.this.fireContentsChanged(MappingEntryListModel.this, 0, MappingEntryListModel.this.project.getMappingEntries().size()-1);
			}
		});
	}

	@Override
	public int getSize() {
		return project.getMappingEntries().size();
	}

	@Override
	public MappingEntry getElementAt(final int index) {
		MappingEntry trunkEntry = project.getMappingEntries().get(index);
		trunkEntry.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				MappingEntryListModel.this.fireContentsChanged(MappingEntryListModel.this, index, index);
			}
		});
		return trunkEntry;
	}
	
	public void removeTrunkEntries(int... indices) {
		Map<MappingEntry, Integer> entriesToRemove = new HashMap<>();
		for (int selectedIndex : indices) {
			entriesToRemove.put(getElementAt(selectedIndex), selectedIndex);
		}
		for (MappingEntry trunkEntry : entriesToRemove.keySet()) {
			project.removeMappingEntry(trunkEntry);
			fireIntervalRemoved(this, entriesToRemove.get(trunkEntry), entriesToRemove.get(trunkEntry));
		}
	}

}
