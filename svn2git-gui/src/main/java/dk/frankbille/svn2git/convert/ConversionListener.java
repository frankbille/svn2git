package dk.frankbille.svn2git.convert;

import java.io.File;

import dk.frankbille.svn2git.model.MappingEntry;

public interface ConversionListener {
	
	void revisionProcessed(long revisionNumber, long endRevisionNumber);
	
	void mappingEntryUpdated(MappingEntry mappingEntry, long revisionNumber, long endRevisionNumber);
	
	void gitFastImportFileChanged(File gitFastImportFile, int numberOfBytesAppended);
	
}
