package dk.frankbille.svn2git.gui;

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
	}

	@Override
	public int getSize() {
		return project.getTrunkEntries().size();
	}

	@Override
	public TrunkEntry getElementAt(int index) {
		return project.getTrunkEntries().get(index);
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
