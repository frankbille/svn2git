package dk.frankbille.svn2git.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.convert.ConversionListener;
import dk.frankbille.svn2git.convert.Converter;
import dk.frankbille.svn2git.model.MappingEntry;
import dk.frankbille.svn2git.model.Project;

public class ConverterDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JLabel gitFastimportFilesizeOutput;
	private JProgressBar overallProgressBar;
	private JProgressBar revisionProgressBar;
	private Converter converter;
	private ConverterWorker converterWorker;
	private JButton actionButton;
	private long gitFastImportFileLength = 0;

	/**
	 * Create the dialog.
	 */
	public ConverterDialog(final Project project) {
		setBounds(100, 100, 450, 300);
		setModal(true);
		getContentPane().setLayout(
				new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"), FormFactory.RELATED_GAP_COLSPEC, FormFactory.BUTTON_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, },
						new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC, FormFactory.GLUE_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, }));

		JLabel overallProgressLabel = new JLabel("Overall progress");
		getContentPane().add(overallProgressLabel, "2, 2, 5, 1");

		final int maxRevisionSteps = project.getMappingEntries().size() + 1;
		final int maxOverallSteps = (1 + (int) (project.getEndRevision() - project.getStartRevision())) * maxRevisionSteps;

		overallProgressBar = new JProgressBar(0, maxOverallSteps);
		overallProgressBar.setValue(0);
		overallProgressBar.setStringPainted(true);
		getContentPane().add(overallProgressBar, "2, 4, 5, 1");

		JLabel revisionProgressLabel = new JLabel("Revision progress");
		getContentPane().add(revisionProgressLabel, "2, 6, 5, 1");

		revisionProgressBar = new JProgressBar(0, maxRevisionSteps);
		revisionProgressBar.setValue(0);
		revisionProgressBar.setStringPainted(true);
		getContentPane().add(revisionProgressBar, "2, 8, 5, 1");

		JSeparator separator = new JSeparator();
		getContentPane().add(separator, "2, 10, 5, 1");

		JLabel gitFastimportFilesizeLabel = new JLabel("Git fast-import filesize:");
		getContentPane().add(gitFastimportFilesizeLabel, "2, 12");

		gitFastimportFilesizeOutput = new JLabel("");
		getContentPane().add(gitFastimportFilesizeOutput, "4, 12, 3, 1");

		actionButton = new JButton();
		getContentPane().add(actionButton, "6, 14");

		File gitFastImportFile = new File(project.getGitFastImportFile());
		if (gitFastImportFile.exists()) {
			gitFastImportFileLength = gitFastImportFile.length();
		}

		converter = new Converter(project);
		converter.addConversionListener(new ConversionListener() {
			@Override
			public void revisionProcessed(long revisionNumber) {
				tick(revisionNumber);
			}

			@Override
			public void mappingEntryUpdated(MappingEntry mappingEntry, long revisionNumber) {
				tick(revisionNumber);
			}

			private void tick(long revisionNumber) {
				if (revisionProgressBar.getValue() >= revisionProgressBar.getMaximum()) {
					revisionProgressBar.setValue(0);
				}

				overallProgressBar.setValue(overallProgressBar.getValue() + 1);
				revisionProgressBar.setValue(revisionProgressBar.getValue() + 1);

				overallProgressBar.setString("" + revisionNumber + "/" + project.getEndRevision());
			}

			@Override
			public void gitFastImportFileChanged(File gitFastImportFile, int numberOfBytesAppended) {
				gitFastImportFileLength += numberOfBytesAppended;

				updateGitFastImportFilesizeLabel();

			}
		});
		converterWorker = new ConverterWorker(converter);
		converterWorker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("state".equals(evt.getPropertyName())) {
					changeActionButton();
				}
			}
		});
		changeActionButton();
		updateGitFastImportFilesizeLabel();
	}

	private void changeActionButton() {
		switch (converterWorker.getState()) {
		case PENDING:
			removeActionListeners();
			actionButton.setText("Start");
			actionButton.setEnabled(true);
			actionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					converterWorker.execute();
				}
			});
			actionButton.setIcon(new ImageIcon(MainWindow.class.getResource("/control.png")));
			break;
		case STARTED:
			removeActionListeners();
			actionButton.setText("Stop");
			actionButton.setEnabled(true);
			actionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					actionButton.setEnabled(false);
					converter.stop();
				}
			});
			actionButton.setIcon(new ImageIcon(MainWindow.class.getResource("/exclamation-red.png")));
			break;
		case DONE:
			removeActionListeners();
			actionButton.setText("Close");
			actionButton.setEnabled(true);
			actionButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ConverterDialog.this.dispose();
				}
			});
			actionButton.setIcon(new ImageIcon(MainWindow.class.getResource("/cross-white.png")));
			break;
		}
	}

	private void removeActionListeners() {
		ActionListener[] actionListeners = actionButton.getActionListeners();
		for (ActionListener actionListener : actionListeners) {
			actionButton.removeActionListener(actionListener);
		}
	}

	private void updateGitFastImportFilesizeLabel() {
		String fileSize = NumberFormat.getIntegerInstance().format(gitFastImportFileLength);
		gitFastimportFilesizeOutput.setText(fileSize + " bytes (" + humanReadableByteCount(gitFastImportFileLength, false) + ")");
	}

	private static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit) {
			return bytes + " B";
		}
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
