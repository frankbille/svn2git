package dk.frankbille.svn2git.gui;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.model.BranchEntry;
import dk.frankbille.svn2git.model.TagEntry;
import dk.frankbille.svn2git.model.TrunkEntry;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTable;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel mappingsPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTable authorsTable;
	
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel = new JLabel("Subversion URL");
		generalPanel.add(lblNewLabel, "2, 2, left, default");
		
		textField = new JTextField();
		generalPanel.add(textField, "4, 2, 2, 1, fill, default");
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Git Repository");
		generalPanel.add(lblNewLabel_1, "2, 4, left, default");
		
		textField_1 = new JTextField();
		generalPanel.add(textField_1, "4, 4, fill, default");
		textField_1.setColumns(10);
		
		JButton button = new JButton("...");
		generalPanel.add(button, "5, 4");
		
		JLabel lblNewLabel_2 = new JLabel("Authors File");
		generalPanel.add(lblNewLabel_2, "2, 6, left, default");
		
		textField_2 = new JTextField();
		generalPanel.add(textField_2, "4, 6, fill, default");
		textField_2.setColumns(10);
		
		JButton button_1 = new JButton("...");
		generalPanel.add(button_1, "5, 6");
		
		JPanel authorsPanel = new JPanel();
		tabbedPane.addTab("Authors", null, authorsPanel, null);
		authorsPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane authorsTableScrollPane = new JScrollPane();
		authorsPanel.add(authorsTableScrollPane, BorderLayout.CENTER);
		
		authorsTable = new JTable();
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
		
		IconLabel addTrunkEntry = new IconLabel();
		addTrunkEntry.setText("\uf067");
		mappingsPanel.add(addTrunkEntry, "3, 2");
		
		IconLabel removeTrunkEntry = new IconLabel();
		removeTrunkEntry.setText("\uf068");
		mappingsPanel.add(removeTrunkEntry, "5, 2");
		
		JScrollPane trunkEntryListScrollPane = new JScrollPane();
		mappingsPanel.add(trunkEntryListScrollPane, "2, 4, 4, 1, fill, fill");
		
		JList<TrunkEntry> trunkEntryList = new JList<>();
		trunkEntryListScrollPane.setViewportView(trunkEntryList);
		
		JLabel branchEntriesLabel = new JLabel("Branch entries:");
		mappingsPanel.add(branchEntriesLabel, "2, 6");
		
		IconLabel addBranchEntry = new IconLabel();
		addBranchEntry.setText("\uF067");
		mappingsPanel.add(addBranchEntry, "3, 6");
		
		IconLabel removeBranchEntry = new IconLabel();
		removeBranchEntry.setText("\uF068");
		mappingsPanel.add(removeBranchEntry, "5, 6");
		
		JScrollPane branchEntryListScrollPane = new JScrollPane();
		mappingsPanel.add(branchEntryListScrollPane, "2, 8, 4, 1, fill, fill");
		
		JList<BranchEntry> branchEntryList = new JList<>();
		branchEntryListScrollPane.setViewportView(branchEntryList);
		
		JLabel tagEntriesLabel = new JLabel("Tag entries:");
		mappingsPanel.add(tagEntriesLabel, "2, 10");
		
		IconLabel addTagEntry = new IconLabel();
		addTagEntry.setText("\uF067");
		mappingsPanel.add(addTagEntry, "3, 10");
		
		IconLabel removeTagEntry = new IconLabel();
		removeTagEntry.setText("\uF068");
		mappingsPanel.add(removeTagEntry, "5, 10");
		
		JScrollPane tagEntryListScrollPane = new JScrollPane();
		mappingsPanel.add(tagEntryListScrollPane, "2, 12, 4, 1, fill, fill");
		
		JList<TagEntry> tagEntryList = new JList<>();
		tagEntryListScrollPane.setViewportView(tagEntryList);
		
		JButton quitButton = new JButton("Quit");
		getContentPane().add(quitButton, "2, 4");
		
		JButton testButton = new JButton("Test");
		getContentPane().add(testButton, "6, 4");
		
		JButton executeButton = new JButton("Execute");
		getContentPane().add(executeButton, "8, 4");
	}
}
