package dk.frankbille.svn2git.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JLabel;

public class IconLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	private static final Font FONT;
	private static final Color COLOR_NORMAL = SystemColor.controlText;
	private static final Color COLOR_HIGHLIGHT = SystemColor.textHighlight;

	static {
		try {
			InputStream in = IconLabel.class
					.getResourceAsStream("/fontawesome-webfont.ttf");
			Font ttfBase = Font.createFont(Font.TRUETYPE_FONT, in);
			FONT = ttfBase.deriveFont(Font.BOLD, 14);
		} catch (FontFormatException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public IconLabel() {
		setFont(FONT);
		setForeground(COLOR_NORMAL);
		
		addMouseListener(new MouseAdapter() {
			private Cursor cursor;
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setForeground(COLOR_HIGHLIGHT);
				cursor = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(COLOR_NORMAL);
				setCursor(cursor);
				cursor = null;
			}
		});
	}

}
