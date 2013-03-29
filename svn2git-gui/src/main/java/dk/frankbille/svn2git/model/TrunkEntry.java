package dk.frankbille.svn2git.model;

public class TrunkEntry extends Entry {

	private final String checkoutPath;
	private final String sourcePath;
	private final String destinationPath;
	
	public TrunkEntry(String sourcePath, String destinationPath) {
		this(sourcePath, sourcePath, destinationPath);
	}
	
	public TrunkEntry(String checkoutPath, String sourcePath, String destinationPath) {
		this.checkoutPath = checkoutPath;
		this.sourcePath = sourcePath;
		this.destinationPath = destinationPath;
	}
	
	public String getCheckoutPath() {
		return checkoutPath;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	
	public String getDestinationPath() {
		return destinationPath;
	}

	@Override
	public String getLabel() {
		return "Trunk";
	}

}
