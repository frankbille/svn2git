package dk.frankbille.svn2git.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
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

import dk.frankbille.svn2git.model.AuthorMapping;
import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;
import dk.frankbille.svn2git.model.ProjectUtils;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel mappingsPanel = new JPanel();
	private JTextField subversionUrlField;
	private JTextField gitFastImportFileField;
	private JTable authorsTable;
	private JTable mappingEntryList;
	private MappingEntryListModel mappingEntryListModel;
	private final JPanel contentPanel = new JPanel();
	private File projectFile;
	private Project project;
	private JSpinner endRevisionField;
	private JSpinner startRevisionField;
	private JCheckBox endIsHead;
	private JTextField workspaceField;
	private JButton removeMappingEntry;
	private JButton removeAuthorMappingButton;
	private AuthorsTableModel authorsModel;

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
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPanel.add(tabbedPane, "1, 1, 7, 1, fill, fill");

		JPanel generalPanel = new JPanel();
		tabbedPane.addTab("General", null, generalPanel, null);
		// @formatter:off
		generalPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("left:default"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
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
		generalPanel.add(subversionUrlField, "4, 2, 5, 1, fill, default");
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
		generalPanel.add(gitFastImportFileField, "4, 4, 3, 1, fill, default");
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
		generalPanel.add(locateGitFastImportFileButton, "8, 4");

		JLabel workspaceLabel = new JLabel("Workspace");
		workspaceLabel.setToolTipText("The folder where the subversion locations will be checked out and processed.");
		generalPanel.add(workspaceLabel, "2, 6, left, default");

		workspaceField = new JTextField();
		workspaceField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				project.setWorkspaceFolder(workspaceField.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		generalPanel.add(workspaceField, "4, 6, 3, 1, fill, default");
		workspaceField.setColumns(10);

		JButton locateWorkspaceButton = new JButton("...");
		{
			Dimension preferredSize = locateWorkspaceButton.getPreferredSize();
			locateWorkspaceButton.setPreferredSize(new Dimension(45, preferredSize.height));
		}
		locateWorkspaceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser workspaceChooser = new JFileChooser();
				workspaceChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				workspaceChooser.setMultiSelectionEnabled(false);
				if (StringUtils.isNotEmpty(project.getWorkspaceFolder())) {
					workspaceChooser.setSelectedFile(new File(project.getWorkspaceFolder()));
				}
				if (workspaceChooser.showDialog(MainWindow.this, "Select") == JFileChooser.APPROVE_OPTION) {
					workspaceField.setText(workspaceChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		generalPanel.add(locateWorkspaceButton, "8, 6");

		JLabel startRevisionLabel = new JLabel("Start revision");
		generalPanel.add(startRevisionLabel, "2, 8, left, default");

		startRevisionField = new JSpinner();
		startRevisionField.setEditor(new JSpinner.NumberEditor(startRevisionField));
		generalPanel.add(startRevisionField, "4, 8");

		JLabel lblEndRevision = new JLabel("End revision");
		generalPanel.add(lblEndRevision, "2, 10, left, default");

		endRevisionField = new JSpinner();
		endRevisionField.setEditor(new JSpinner.NumberEditor(endRevisionField));
		generalPanel.add(endRevisionField, "4, 10");

		endIsHead = new JCheckBox("HEAD");
		endIsHead.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				project.setEndHeadRevision(endIsHead.isSelected());
				setEndRevisionEnabledState();
			}
		});
		generalPanel.add(endIsHead, "6, 10");

		JPanel authorsPanel = new JPanel();
		tabbedPane.addTab("Authors", null, authorsPanel, null);
		// @formatter:off
		authorsPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		JScrollPane authorsTableScrollPane = new JScrollPane();
		authorsPanel.add(authorsTableScrollPane, "2, 2, 4, 1, fill, fill");
		authorsTable = new JTable();
		authorsModel = new AuthorsTableModel();
		authorsTable.setModel(authorsModel);
		TableRowSorter<AuthorsTableModel> authorSorter = new TableRowSorter<AuthorsTableModel>(authorsModel);
		authorsTable.setRowSorter(authorSorter);
		List<SortKey> authorSortKeys = new ArrayList<>(authorSorter.getSortKeys());
		authorSortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		authorSorter.setSortKeys(authorSortKeys);
		authorsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (false == e.getValueIsAdjusting()) {
					removeAuthorMappingButton.setEnabled(authorsTable.getSelectedRowCount() > 0);
				}
			}
		});
		authorsTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = authorsTable.getSelectedRow();
					if (selectedRow > -1) {
						AuthorMapping selectedValue = project.getAuthors().get(authorsTable.convertRowIndexToModel(selectedRow));
						if (selectedValue != null) {
							AuthorMappingDialog editDialog = new AuthorMappingDialog(selectedValue);
							editDialog.setVisible(true);
						}
					}
				}
			}
		});
		authorsTableScrollPane.setViewportView(authorsTable);

		JButton addAuthorMappingButton = new JButton("+");
		addAuthorMappingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AuthorMapping newEntry = new AuthorMapping();
				AuthorMappingDialog editDialog = new AuthorMappingDialog(newEntry);
				editDialog.setVisible(true);
				if (editDialog.isOkPressed()) {
					project.addAuthor(newEntry);
				}
			}
		});
		authorsPanel.add(addAuthorMappingButton, "3, 4");

		removeAuthorMappingButton = new JButton("-");
		removeAuthorMappingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selectedViewRows = authorsTable.getSelectedRows();
				int[] selectedModelRows = new int[selectedViewRows.length];
				for (int i = 0; i < selectedViewRows.length; i++) {
					selectedModelRows[i] = authorsTable.convertRowIndexToModel(selectedViewRows[i]);
				}
				authorsModel.removeAuthors(selectedModelRows);
				authorsTable.clearSelection();
			}
		});
		removeAuthorMappingButton.setEnabled(false);
		authorsPanel.add(removeAuthorMappingButton, "5, 4");
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
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		JScrollPane mappingEntryListScrollPane = new JScrollPane();
		mappingsPanel.add(mappingEntryListScrollPane, "2, 2, 4, 1, fill, fill");

		mappingEntryList = new JTable();
		mappingEntryListModel = new MappingEntryListModel();
		mappingEntryList.setModel(mappingEntryListModel);
		TableRowSorter<MappingEntryListModel> mappingEntrySorter = new TableRowSorter<MappingEntryListModel>(mappingEntryListModel);
		mappingEntryList.setRowSorter(mappingEntrySorter);
		List<SortKey> sortKeys = new ArrayList<>(mappingEntrySorter.getSortKeys());
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		mappingEntrySorter.setSortKeys(sortKeys);
		mappingEntryList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (false == e.getValueIsAdjusting()) {
					removeMappingEntry.setEnabled(mappingEntryList.getSelectedRowCount() > 0);
				}
			}
		});
		mappingEntryList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int selectedRow = mappingEntryList.getSelectedRow();
					if (selectedRow > -1) {
						MappingEntry selectedValue = project.getMappingEntries().get(mappingEntryList.convertRowIndexToModel(selectedRow));
						if (selectedValue != null) {
							MappingEntryDialog editDialog = new MappingEntryDialog(selectedValue);
							editDialog.setVisible(true);
						}
					}
				}
			}
		});
		mappingEntryListScrollPane.setViewportView(mappingEntryList);

		JButton addMappingEntry = new JButton("+");
		addMappingEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MappingEntry newEntry = new MappingEntry();
				MappingEntryDialog editDialog = new MappingEntryDialog(newEntry);
				editDialog.setVisible(true);
				if (editDialog.isOkPressed()) {
					project.addMappingEntry(newEntry);
				}
			}
		});
		addMappingEntry.setToolTipText("Add new mapping mapping");
		mappingsPanel.add(addMappingEntry, "3, 4");

		removeMappingEntry = new JButton("-");
		removeMappingEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] selectedViewRows = mappingEntryList.getSelectedRows();
				int[] selectedModelRows = new int[selectedViewRows.length];
				for (int i = 0; i < selectedViewRows.length; i++) {
					selectedModelRows[i] = mappingEntryList.convertRowIndexToModel(selectedViewRows[i]);
				}
				mappingEntryListModel.removeMappingEntries(selectedModelRows);
			}
		});
		removeMappingEntry.setEnabled(false);
		removeMappingEntry.setToolTipText("Remove selected mapping entry");
		mappingsPanel.add(removeMappingEntry, "5, 4");

		JButton quitButton = new JButton("Quit");
		quitButton.setEnabled(false);
		quitButton.setToolTipText("Not implemented yet");
		quitButton.setIcon(new ImageIcon(MainWindow.class.getResource("/door-open-in.png")));
		contentPanel.add(quitButton, "2, 3");

		JButton executeButton = new JButton("Execute");
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConverterDialog converterDialog = new ConverterDialog(project);
				converterDialog.setVisible(true);
			}
		});
		executeButton.setIcon(new ImageIcon(MainWindow.class.getResource("/control.png")));
		contentPanel.add(executeButton, "6, 3");

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
		workspaceField.setText(project.getWorkspaceFolder());
		endIsHead.setSelected(project.isEndHeadRevision());

		new RevisionModel(true, project, startRevisionField);
		new RevisionModel(false, project, endRevisionField);
		
		authorsModel.setProject(project);
		mappingEntryListModel.setProject(project);

		setEndRevisionEnabledState();
	}

	private void setEndRevisionEnabledState() {
		endRevisionField.setEnabled(false == project.isEndHeadRevision());
	}
}
