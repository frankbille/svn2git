package dk.frankbille.svn2git.convert;

import java.io.File;

import dk.frankbille.svn2git.model.MappingEntry;

public interface ConversionListener {
	
	void revisionProcessed(long revisionNumber);
	
	void mappingEntryUpdated(MappingEntry mappingEntry);
	
	void gitFastImportFileChanged(File gitFastImportFile);
	
}
