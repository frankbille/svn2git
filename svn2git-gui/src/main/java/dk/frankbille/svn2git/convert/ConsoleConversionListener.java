package dk.frankbille.svn2git.convert;

import java.io.File;

import dk.frankbille.svn2git.model.MappingEntry;

public class ConsoleConversionListener implements ConversionListener {

	@Override
	public void revisionProcessed(long revisionNumber) {
		System.out.println("Finished processing the revision: "+revisionNumber);
	}

	@Override
	public void mappingEntryUpdated(MappingEntry mappingEntry, long revisionNumber) {
		System.out.println("Finished updating for the mapping entry: "+mappingEntry.getSourcePath());
	}

	@Override
	public void gitFastImportFileChanged(File gitFastImportFile, int numberOfBytesAppended) {
		System.out.println("Git fast-import file changed. File size: "+gitFastImportFile.length());
	}

}
