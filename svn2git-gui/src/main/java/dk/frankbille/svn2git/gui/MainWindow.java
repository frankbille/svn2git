package dk.frankbille.svn2git.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.model.BranchEntry;
import dk.frankbille.svn2git.model.TagEntry;
import dk.frankbille.svn2git.model.TrunkEntry;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 476);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel trunkEntriesLabel = new JLabel("Trunk entries:");
		getContentPane().add(trunkEntriesLabel, "2, 2");
		
		IconLabel addTrunkEntry = new IconLabel();
		addTrunkEntry.setText("\uf067");
		getContentPane().add(addTrunkEntry, "4, 2");
		
		IconLabel removeTrunkEntry = new IconLabel();
		removeTrunkEntry.setText("\uf068");
		getContentPane().add(removeTrunkEntry, "6, 2");
		
		JScrollPane trunkEntryListScrollPane = new JScrollPane();
		getContentPane().add(trunkEntryListScrollPane, "2, 4, 5, 1, fill, fill");
		
		JList<TrunkEntry> trunkEntryList = new JList<>();
		trunkEntryListScrollPane.setViewportView(trunkEntryList);
		
		JLabel branchEntriesLabel = new JLabel("Branch entries:");
		getContentPane().add(branchEntriesLabel, "2, 6");
		
		IconLabel addBranchEntry = new IconLabel();
		addBranchEntry.setText("\uF067");
		getContentPane().add(addBranchEntry, "4, 6");
		
		IconLabel removeBranchEntry = new IconLabel();
		removeBranchEntry.setText("\uF068");
		getContentPane().add(removeBranchEntry, "6, 6");
		
		JScrollPane branchEntryListScrollPane = new JScrollPane();
		getContentPane().add(branchEntryListScrollPane, "2, 8, 5, 1, fill, fill");
		
		JList<BranchEntry> branchEntryList = new JList<>();
		branchEntryListScrollPane.setViewportView(branchEntryList);
		
		JLabel tagEntriesLabel = new JLabel("Tag entries:");
		getContentPane().add(tagEntriesLabel, "2, 10");
		
		IconLabel addTagEntry = new IconLabel();
		addTagEntry.setText("\uF067");
		getContentPane().add(addTagEntry, "4, 10");
		
		IconLabel removeTagEntry = new IconLabel();
		removeTagEntry.setText("\uF068");
		getContentPane().add(removeTagEntry, "6, 10");
		
		JScrollPane tagEntryListScrollPane = new JScrollPane();
		getContentPane().add(tagEntryListScrollPane, "2, 12, 5, 1, fill, fill");
		
		JList<TagEntry> tagEntryList = new JList<>();
		tagEntryListScrollPane.setViewportView(tagEntryList);
	}
}
