/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 *     Martijn van Iersel - co-supervisor
 *     Julio Saez-Rodriguez - supervisor
 ******************************************************************************/
package uk.ac.ebi.cysbgn.io;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

/**
 * This class gathers all methods that should be used to print messages.
 * 
 * @author emanuel
 *
 */

public class MessagesHandler {
	
	/**
	 * Print a message in the command line
	 * 
	 * @param message
	 */
	public static void printMessageCommandLine(String message){
		System.out.println("[SBGNPlugin] "+message+".");
	}

	/**
	 * Print a message in a pop-up screen.
	 * 
	 * @param message
	 * @param title
	 * @param e
	 */
	public static void showErrorMessageDialog(String message, String title, Exception e){
		
		StringBuilder errorMessage = new StringBuilder();
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		errorMessage.append(message);
		errorMessage.append("StackTrace:\n");
		errorMessage.append(sw.toString());
		
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errorMessage.toString(), title, JOptionPane.ERROR_MESSAGE);
	}
	
}
