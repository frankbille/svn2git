package dk.frankbille.svn2git.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.model.BranchEntry;
import dk.frankbille.svn2git.model.Project;
import dk.frankbille.svn2git.model.TagEntry;
import dk.frankbille.svn2git.model.TrunkEntry;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel mappingsPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTable authorsTable;
	private Project project;
	private JList<TrunkEntry> trunkEntryList;
	private TrunkEntryListModel trunkEntryListModel;
	
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
		this.project = new Project();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 718, 476);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, "2, 2, 7, 1, fill, fill");
		
		JPanel generalPanel = new JPanel();
		tabbedPane.addTab("General", null, generalPanel, null);
		generalPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel = new JLabel("Subversion URL");
		generalPanel.add(lblNewLabel, "2, 2, left, default");
		
		textField = new JTextField();
		generalPanel.add(textField, "4, 2, 2, 1, fill, default");
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Git fast-import file");
		generalPanel.add(lblNewLabel_1, "2, 4, left, default");
		
		textField_1 = new JTextField();
		generalPanel.add(textField_1, "4, 4, fill, default");
		textField_1.setColumns(10);
		
		JButton button = new JButton("...");
		generalPanel.add(button, "5, 4");
		
		JPanel authorsPanel = new JPanel();
		tabbedPane.addTab("Authors", null, authorsPanel, null);
		authorsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JScrollPane authorsTableScrollPane = new JScrollPane();
		authorsPanel.add(authorsTableScrollPane, "2, 2, fill, fill");
		
		AuthorsTableModel authorsModel = new AuthorsTableModel(project);
		authorsTable = new JTable(authorsModel);
		authorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableRowSorter<AuthorsTableModel> sorter = new TableRowSorter<AuthorsTableModel>(authorsModel);
		List<SortKey> sortKeys = new ArrayList<>(sorter.getSortKeys());
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		authorsTable.setRowSorter(sorter);
		authorsTableScrollPane.setViewportView(authorsTable);
		tabbedPane.addTab("Mappings", null, mappingsPanel, null);
		mappingsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
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
		mappingsPanel.add(trunkEntriesLabel, "2, 2");
		
		JButton addTrunkEntry = new JButton("+");
		addTrunkEntry.setToolTipText("Add new trunk mapping");
		{
			Dimension preferredSize = addTrunkEntry.getPreferredSize();
			addTrunkEntry.setPreferredSize(new Dimension(45, preferredSize.height));
		}
		mappingsPanel.add(addTrunkEntry, "3, 2");
		
		final JButton removeTrunkEntry = new JButton("-");
		removeTrunkEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selectedIndices = trunkEntryList.getSelectedIndices();
				trunkEntryListModel.removeTrunkEntries(selectedIndices);
				trunkEntryList.clearSelection();
			}
		});
		removeTrunkEntry.setEnabled(false);
		removeTrunkEntry.setToolTipText("Remove selected trunk mapping");
		{
			Dimension preferredSize = removeTrunkEntry.getPreferredSize();
			removeTrunkEntry.setPreferredSize(new Dimension(45, preferredSize.height));
		}
		mappingsPanel.add(removeTrunkEntry, "5, 2");
		
		JScrollPane trunkEntryListScrollPane = new JScrollPane();
		mappingsPanel.add(trunkEntryListScrollPane, "2, 4, 4, 1, fill, fill");
		
		trunkEntryList = new JList<>();
		trunkEntryListModel = new TrunkEntryListModel(project);
		trunkEntryList.setModel(trunkEntryListModel);
		trunkEntryList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (false == e.getValueIsAdjusting()) {
					removeTrunkEntry.setEnabled(false == trunkEntryList.isSelectionEmpty());
				}
			}
		});
		trunkEntryListScrollPane.setViewportView(trunkEntryList);
		
		JLabel branchEntriesLabel = new JLabel("Branch entries:");
		branchEntriesLabel.setToolTipText("Not implemented yet");
		branchEntriesLabel.setEnabled(false);
		mappingsPanel.add(branchEntriesLabel, "2, 6");
		
		JScrollPane branchEntryListScrollPane = new JScrollPane();
		branchEntryListScrollPane.setEnabled(false);
		mappingsPanel.add(branchEntryListScrollPane, "2, 8, 4, 1, fill, fill");
		
		JList<BranchEntry> branchEntryList = new JList<>();
		branchEntryList.setToolTipText("Not implemented yet");
		branchEntryList.setEnabled(false);
		branchEntryListScrollPane.setViewportView(branchEntryList);
		
		JLabel tagEntriesLabel = new JLabel("Tag entries:");
		tagEntriesLabel.setEnabled(false);
		mappingsPanel.add(tagEntriesLabel, "2, 10");
		
		JScrollPane tagEntryListScrollPane = new JScrollPane();
		tagEntryListScrollPane.setEnabled(false);
		mappingsPanel.add(tagEntryListScrollPane, "2, 12, 4, 1, fill, fill");
		
		JList<TagEntry> tagEntryList = new JList<>();
		tagEntryList.setToolTipText("Not implemented yet");
		tagEntryList.setEnabled(false);
		tagEntryListScrollPane.setViewportView(tagEntryList);
		
		JButton quitButton = new JButton("Quit");
		getContentPane().add(quitButton, "2, 4");
		
		JButton testButton = new JButton("Test");
		getContentPane().add(testButton, "6, 4");
		
		JButton executeButton = new JButton("Execute");
		getContentPane().add(executeButton, "8, 4");
	}
}
