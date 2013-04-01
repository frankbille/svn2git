package dk.frankbille.svn2git.gui;

import javax.swing.SwingWorker;

import dk.frankbille.svn2git.convert.Converter;

public class ConverterWorker extends SwingWorker<Converter, Object> {

	private final Converter converter;
	
	public ConverterWorker(Converter converter) {
		this.converter = converter;
	}

	@Override
	protected Converter doInBackground() throws Exception {
		converter.convert();
		return converter;
	}

}
