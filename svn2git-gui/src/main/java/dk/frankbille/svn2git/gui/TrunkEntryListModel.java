package dk.frankbille.svn2git.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractListModel;

import dk.frankbille.svn2git.model.Project;
import dk.frankbille.svn2git.model.TrunkEntry;

public class TrunkEntryListModel extends AbstractListModel<TrunkEntry> {
	private static final long serialVersionUID = 1L;

	private final Project project;

	public TrunkEntryListModel(Project project) {
		this.project = project;
		this.project.addPropertyChangeListener("trunkEntries", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				TrunkEntryListModel.this.fireContentsChanged(TrunkEntryListModel.this, 0, TrunkEntryListModel.this.project.getTrunkEntries().size()-1);
			}
		});
	}

	@Override
	public int getSize() {
		return project.getTrunkEntries().size();
	}

	@Override
	public TrunkEntry getElementAt(final int index) {
		TrunkEntry trunkEntry = project.getTrunkEntries().get(index);
		trunkEntry.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				TrunkEntryListModel.this.fireContentsChanged(TrunkEntryListModel.this, index, index);
			}
		});
		return trunkEntry;
	}
	
	public void removeTrunkEntries(int... indices) {
		Map<TrunkEntry, Integer> entriesToRemove = new HashMap<>();
		for (int selectedIndex : indices) {
			entriesToRemove.put(getElementAt(selectedIndex), selectedIndex);
		}
		for (TrunkEntry trunkEntry : entriesToRemove.keySet()) {
			project.removeTrunkEntry(trunkEntry);
			fireIntervalRemoved(this, entriesToRemove.get(trunkEntry), entriesToRemove.get(trunkEntry));
		}
	}

}
