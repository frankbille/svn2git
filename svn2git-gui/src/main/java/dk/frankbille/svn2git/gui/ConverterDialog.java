package dk.frankbille.svn2git.gui;

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

	/**
	 * Create the dialog.
	 */
	public ConverterDialog(final Project project) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.BUTTON_COLSPEC,
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
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JLabel overallProgressLabel = new JLabel("Overall progress");
		getContentPane().add(overallProgressLabel, "2, 2, 5, 1");
		
		final int maxRevisionSteps = project.getMappingEntries().size()+1;
		final int maxOverallSteps = (1+(int)(project.getEndRevision()-project.getStartRevision()))*maxRevisionSteps;
		
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
		
		JButton btnStop = new JButton("Stop");
		btnStop.setToolTipText("Not implemented yet");
		btnStop.setEnabled(false);
		btnStop.setIcon(new ImageIcon(MainWindow.class.getResource("/exclamation-red.png")));
		getContentPane().add(btnStop, "6, 14");

		Converter converter = new Converter(project);
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

				overallProgressBar.setValue(overallProgressBar.getValue()+1);
				revisionProgressBar.setValue(revisionProgressBar.getValue()+1);
				
				overallProgressBar.setString(""+revisionNumber+"/"+project.getEndRevision());
			}
			
			@Override
			public void gitFastImportFileChanged(File gitFastImportFile) {
				String fileSize = NumberFormat.getIntegerInstance().format(gitFastImportFile.length());
				gitFastimportFilesizeOutput.setText(fileSize+" bytes");
				
			}
		});
		ConverterWorker converterWorker = new ConverterWorker(converter);
		converterWorker.execute();
	}

}
