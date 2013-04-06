package dk.frankbille.svn2git.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import dk.frankbille.svn2git.model.MappingEntry;

public class MappingEntryDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField sourcePathField;
	private JTextField destinationPathField;
	private JTextField destinationRefField;

	private boolean okPressed = false;

	/**
	 * Create the dialog.
	 */
	public MappingEntryDialog(final MappingEntry mappingEntry) {
		setBounds(100, 100, 472, 202);
		setModal(true);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// @formatter:off
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.BUTTON_COLSPEC,
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
				RowSpec.decode("3dlu:grow"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		{
			JLabel sourcePathLabel = new JLabel("Source Path");
			sourcePathLabel.setToolTipText("The path in the SVN repository to migrate");
			contentPanel.add(sourcePathLabel, "2, 2, left, default");
		}
		{
			sourcePathField = new JTextField(mappingEntry.getSourcePath());
			contentPanel.add(sourcePathField, "4, 2, 4, 1, fill, default");
		}
		{
			JLabel destinationPathLabel = new JLabel("Destination Path");
			destinationPathLabel.setToolTipText("The path in the Git repository");
			contentPanel.add(destinationPathLabel, "2, 4, left, default");
		}
		{
			destinationPathField = new JTextField(mappingEntry.getDestinationPath());
			contentPanel.add(destinationPathField, "4, 4, 4, 1, fill, default");
		}
		{
			JLabel destinationRefLabel = new JLabel("Destination Ref");
			destinationRefLabel.setToolTipText("The ref spec in the Git repository");
			contentPanel.add(destinationRefLabel, "2, 6, left, default");
		}
		{
			destinationRefField = new JTextField(mappingEntry.getDestinationRef());
			contentPanel.add(destinationRefField, "4, 6, 4, 1, fill, default");
			destinationRefField.setColumns(10);
		}
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mappingEntry.setSourcePath(sourcePathField.getText());
					mappingEntry.setDestinationPath(destinationPathField.getText());
					mappingEntry.setDestinationRef(destinationRefField.getText());

					okPressed = true;

					MappingEntryDialog.this.dispose();
				}
			});
			contentPanel.add(okButton, "5, 9");
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					MappingEntryDialog.this.dispose();
				}
			});
			contentPanel.add(cancelButton, "7, 9");
		}
	}

	public boolean isOkPressed() {
		return okPressed;
	}

}
