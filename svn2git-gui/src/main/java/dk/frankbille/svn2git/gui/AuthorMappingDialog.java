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

import dk.frankbille.svn2git.model.AuthorMapping;

public class AuthorMappingDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private JTextField svnUsernameField;
	private JTextField gitAuthorField;

	private boolean okPressed = false;

	/**
	 * Create the dialog.
	 */
	public AuthorMappingDialog(final AuthorMapping authorMapping) {
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
				RowSpec.decode("3dlu:grow"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		// @formatter:on

		{
			JLabel svnUsernameLabel = new JLabel("SVN Username");
			contentPanel.add(svnUsernameLabel, "2, 2, left, default");
		}
		{
			svnUsernameField = new JTextField(authorMapping.getSvnUsername());
			contentPanel.add(svnUsernameField, "4, 2, 4, 1, fill, default");
		}
		{
			JLabel gitAuthorLabel = new JLabel("Git Author");
			contentPanel.add(gitAuthorLabel, "2, 4, left, default");
		}
		{
			gitAuthorField = new JTextField(authorMapping.getGitAuthor());
			contentPanel.add(gitAuthorField, "4, 4, 4, 1, fill, default");
		}
		{
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					authorMapping.setSvnUsername(svnUsernameField.getText());
					authorMapping.setGitAuthor(gitAuthorField.getText());

					okPressed = true;

					AuthorMappingDialog.this.dispose();
				}
			});
			contentPanel.add(okButton, "5, 6");
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AuthorMappingDialog.this.dispose();
				}
			});
			contentPanel.add(cancelButton, "7, 6");
		}
	}

	public boolean isOkPressed() {
		return okPressed;
	}

}
