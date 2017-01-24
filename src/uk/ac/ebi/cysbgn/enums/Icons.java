/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 ******************************************************************************/
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
