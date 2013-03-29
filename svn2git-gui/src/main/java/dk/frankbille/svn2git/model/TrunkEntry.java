package dk.frankbille.svn2git.model;

public class TrunkEntry extends Entry {

	private String checkoutPath;
	private String sourcePath;
	private String destinationPath;
	
	public TrunkEntry() {
	}
	
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
	
	public void setCheckoutPath(String checkoutPath) {
		this.checkoutPath = checkoutPath;
	}
	
	public String getSourcePath() {
		return sourcePath;
	}
	
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	public String getDestinationPath() {
		return destinationPath;
	}
	
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
	
	@Override
	public String toString() {
		return sourcePath+" => "+destinationPath;
	}

}
