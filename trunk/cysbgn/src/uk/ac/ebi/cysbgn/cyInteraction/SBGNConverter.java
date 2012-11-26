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
package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;
import java.io.File;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.utils.MessageDialog;
import uk.ac.ebi.cysbgn.utils.Sbml2SbgnTask;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CytoscapeDesktop;

@SuppressWarnings("serial")
public class SBGNConverter extends CytoscapeAction{

	protected CySBGN plugin;
	
	private TaskMonitor taskMonitor;	
	private static CyLogger logger = CyLogger.getLogger(SBGNConverter.class);
	
	
	public SBGNConverter(CySBGN plugin){
		super("SBML to SBGN...");
		setPreferredMenu(CySBGN.SBGN_MENU);
		
		this.plugin = plugin;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			//Check if CySBML is installed if not warn the user
			if( plugin.isCySBML_Installed() ){
				
				// Browse for sbml file
				CyFileFilter[] filters = new CyFileFilter[1];
				filters[0] = new CyFileFilter("xml");
				File sbmlFile = FileUtil.getFile("Select SBML model file", FileUtil.LOAD, filters);
	
				if( sbmlFile != null){
					Sbml2SbgnTask converterTask = new Sbml2SbgnTask(plugin, sbmlFile);
					MessagesHandler.executeTask(converterTask, false);
				}
				
			}else{
				String detailedMessage = "To use this feature CySBML plug-in needs to be installed.\nFor more details see on how to install CySBGN webpage.\nhttp://www.ebi.ac.uk/saezrodriguez/cysbgn/";
				new MessageDialog("CySBML plug-in not installed", "CySBML plug-in not installed", detailedMessage, Icons.WARNING_LOGO.getPath());
				logger.warn("SBML to SBGN conversion requires CySBML plug-in installed.");
			}
			
		}catch(Exception e){
			e.printStackTrace();
			String detailedMessage = "No details.";
			
			new MessageDialog("Error importing sbml file", "Error importing sbml file", detailedMessage, Icons.ERROR_LOGO.getPath());
			logger.warn("Error SBML file : " + e.getMessage(), e);
		}
	}

	
	
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}

}
