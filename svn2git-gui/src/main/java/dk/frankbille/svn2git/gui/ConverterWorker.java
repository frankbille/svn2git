package dk.frankbille.svn2git.gui;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.frankbille.svn2git.convert.Converter;

public class ConverterWorker extends SwingWorker<Converter, Object> {

	private static final Logger logger = LoggerFactory.getLogger(ConverterWorker.class);
	
	private final Converter converter;
	
	public ConverterWorker(Converter converter) {
		this.converter = converter;
	}

	@Override
	protected Converter doInBackground() throws Exception {
		try {
			converter.convert();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return converter;
	}

}
