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
package uk.ac.ebi.cysbgn.io.messages;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/**
 * This class gathers all methods that should be used to print messages.
 * 
 * @author emanuel
 *
 */

public class MessagesHandler {
	
	public static void printMessageCommandLine(String message){
		System.out.println("[SBGNPlugin] "+message+".");
	}

	public static void showErrorMessageDialog(String message, String title, Exception e){
		
		StringBuilder errorMessage = new StringBuilder();
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		errorMessage.append(message);
		errorMessage.append("StackTrace:\n");
		errorMessage.append(sw.toString());
		
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), errorMessage.toString(), title, JOptionPane.ERROR_MESSAGE);
	}
	
	
	public static void executeTask(Task task, boolean autoDispose){
		executeTask(task, autoDispose, true, false);
	}
	
	public static void executeTask(Task task, boolean autoDispose, boolean displayCancelButton, boolean displayTimeRemaining){
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(displayCancelButton);

		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(autoDispose);
		
		jTaskConfig.displayTimeRemaining(displayTimeRemaining);
		
		TaskManager.executeTask(task, jTaskConfig);
	}
	
	public static String getStackTrace(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
