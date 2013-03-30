package dk.frankbille.svn2git.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.lang3.StringUtils;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.model.BranchEntry;
import dk.frankbille.svn2git.model.Project;
import dk.frankbille.svn2git.model.ProjectUtils;
import dk.frankbille.svn2git.model.TagEntry;
import dk.frankbille.svn2git.model.TrunkEntry;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel mappingsPanel = new JPanel();
	private JTextField subversionUrlField;
	private JTextField gitFastImportFileField;
	private JTable authorsTable;
	private JList<TrunkEntry> trunkEntryList;
	private TrunkEntryListModel trunkEntryListModel;
	private final JPanel contentPanel = new JPanel();
	private File projectFile;
	private Project project;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new NimbusLookAndFeel());
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
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		// @formatter:off
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
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
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPanel.add(tabbedPane, "1, 1, 9, 1, fill, fill");

		JPanel generalPanel = new JPanel();
		tabbedPane.addTab("General", null, generalPanel, null);
		// @formatter:off
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
		// @formatter:on

		JLabel subversionUrlLabel = new JLabel("Subversion URL");
		generalPanel.add(subversionUrlLabel, "2, 2, left, default");

		subversionUrlField = new JTextField();
		subversionUrlField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				project.setSvnUrl(subversionUrlField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		generalPanel.add(subversionUrlField, "4, 2, 2, 1, fill, default");
		subversionUrlField.setColumns(10);

		JLabel gitFastImportFileLabel = new JLabel("Git fast-import file");
		generalPanel.add(gitFastImportFileLabel, "2, 4, left, default");

		gitFastImportFileField = new JTextField();
		gitFastImportFileField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				project.setGitFastImportFile(gitFastImportFileField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		generalPanel.add(gitFastImportFileField, "4, 4, fill, default");
		gitFastImportFileField.setColumns(10);

		JButton locateGitFastImportFileButton = new JButton("...");
		{
			Dimension preferredSize = locateGitFastImportFileButton.getPreferredSize();
			locateGitFastImportFileButton.setPreferredSize(new Dimension(45, preferredSize.height));
		}
		locateGitFastImportFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser gitFastImportFileChooser = new JFileChooser();
				gitFastImportFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				gitFastImportFileChooser.setMultiSelectionEnabled(false);
				if (StringUtils.isNotEmpty(project.getGitFastImportFile())) {
					gitFastImportFileChooser.setSelectedFile(new File(project.getGitFastImportFile()));
				}
				if (gitFastImportFileChooser.showDialog(MainWindow.this, "Select") == JFileChooser.APPROVE_OPTION) {
					gitFastImportFileField.setText(gitFastImportFileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		generalPanel.add(locateGitFastImportFileButton, "5, 4");

		JPanel authorsPanel = new JPanel();
		tabbedPane.addTab("Authors", null, authorsPanel, null);
		// @formatter:off
		authorsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		JScrollPane authorsTableScrollPane = new JScrollPane();
		authorsPanel.add(authorsTableScrollPane, "2, 2, fill, fill");
		authorsTable = new JTable();
		authorsTableScrollPane.setViewportView(authorsTable);
		tabbedPane.addTab("Mappings", null, mappingsPanel, null);
		// @formatter:off
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
		// @formatter:on

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
		quitButton.setEnabled(false);
		quitButton.setToolTipText("Not implemented yet");
		quitButton.setIcon(new ImageIcon(MainWindow.class.getResource("/door-open-in.png")));
		contentPanel.add(quitButton, "2, 3");

		JButton testButton = new JButton("Test");
		testButton.setToolTipText("Not implemented yet");
		testButton.setEnabled(false);
		testButton.setIcon(new ImageIcon(MainWindow.class.getResource("/target.png")));
		contentPanel.add(testButton, "6, 3");

		JButton executeButton = new JButton("Execute");
		executeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/control.png")));
		contentPanel.add(executeButton, "8, 3");

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton loadProjectButton = new JButton((String) null);
		loadProjectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser projectFileChooser = new JFileChooser();
				projectFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				projectFileChooser.setMultiSelectionEnabled(false);
				if (projectFileChooser.showDialog(MainWindow.this, "Select project file") == JFileChooser.APPROVE_OPTION) {
					try {
						projectFile = projectFileChooser.getSelectedFile();
						setProject(ProjectUtils.load(projectFile));
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}
			}
		});
		loadProjectButton.setToolTipText("Open Project");
		loadProjectButton.setIcon(new ImageIcon(MainWindow.class.getResource("/folder-horizontal-open.png")));
		toolBar.add(loadProjectButton);

		JButton saveProjectButton = new JButton((String) null);
		saveProjectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (projectFile == null) {
					JFileChooser projectFileChooser = new JFileChooser();
					projectFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					projectFileChooser.setMultiSelectionEnabled(false);
					if (projectFileChooser.showDialog(MainWindow.this, "Select project file") == JFileChooser.APPROVE_OPTION) {
						projectFile = projectFileChooser.getSelectedFile();
					}
				}

				if (projectFile != null) {
					try {
						ProjectUtils.save(project, projectFile);
					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}
			}
		});
		saveProjectButton.setToolTipText("Save project");
		saveProjectButton.setIcon(new ImageIcon(MainWindow.class.getResource("/disk.png")));
		toolBar.add(saveProjectButton);

		setProject(new Project());
	}

	private void setProject(Project project) {
		this.project = project;

		subversionUrlField.setText(project.getSvnUrl());
		gitFastImportFileField.setText(project.getGitFastImportFile());

		trunkEntryListModel = new TrunkEntryListModel(project);
		trunkEntryList.setModel(trunkEntryListModel);

		AuthorsTableModel authorsModel = new AuthorsTableModel(project);
		authorsTable.setModel(authorsModel);
		authorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TableRowSorter<AuthorsTableModel> sorter = new TableRowSorter<AuthorsTableModel>(authorsModel);
		authorsTable.setRowSorter(sorter);
		List<SortKey> sortKeys = new ArrayList<>(sorter.getSortKeys());
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
	}
}
