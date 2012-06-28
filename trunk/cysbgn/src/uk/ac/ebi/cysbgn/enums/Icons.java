package uk.ac.ebi.cysbgn.enums;

public enum Icons {

	LOGO ("/logo.png"),
	WARNING_LOGO ("/warningLogo.png"),
	ERROR_LOGO ("/errorLogo.png"),
	CORRECT_LOGO ("/correctLogo.png");
	
	
	private Icons(String iconPath){
		this.iconPath = iconPath;
	}
	
	private final String iconPath;
	
	public String getPath() { return iconPath; }
}
