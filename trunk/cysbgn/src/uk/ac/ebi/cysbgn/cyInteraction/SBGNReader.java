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
package uk.ac.ebi.cysbgn.cyInteraction;

import java.io.IOException;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.io.SBGNMLReader;
import uk.ac.ebi.cysbgn.utils.LimitationDialog;
import uk.ac.ebi.cysbgn.utils.MessageDialog;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;

/**
 * Class responsible to call the SBGN reader when the SBGN diagram file is selected from the import menu of Cytoscape.
 * 
 * @author emanuel
 *
 */
public class SBGNReader extends AbstractGraphReader{

	protected CySBGN plugin;
	protected SBGNVisualStyle visualStyle;
	
	private TaskMonitor taskMonitor;
	private static CyLogger logger = CyLogger.getLogger(SBGNReader.class);
	
	
	public SBGNReader(String fileName, CySBGN plugin){
		this(fileName, plugin, null);
	}

	public SBGNReader(String fileName, CySBGN plugin, final TaskMonitor monitor){
		super(fileName);
		this.plugin = plugin;
		this.visualStyle = new SBGNVisualStyle(plugin);
		setTaskMonitor(monitor);
	}
	
	@Override
	public void read() throws IOException {
		
	}
	
	public void doPostProcessing(CyNetwork network){
		SBGNMLReader newReader = new SBGNMLReader(false);
		try {
			network = newReader.read(fileName, network);
			
			visualStyle.applyVisualStyle();
			
			plugin.addNetwork(network, newReader.getMap(), fileName);
			
			if( CySBGN.SHOW_LIMITATIONS_PANEL )
				new LimitationDialog();
			
		} catch (Exception e) {
			Cytoscape.destroyNetwork(network);
			if( taskMonitor != null){
				taskMonitor.setStatus(e.getMessage());
				taskMonitor.setException(e, "Error reading SBGN file.");
			}else{
				new MessageDialog("Rendering Limitations", e.getMessage(), MessagesHandler.getStackTrace(e), Icons.ERROR_LOGO.getPath());
				logger.warn("Error reading SBGN file " + network.getTitle() + ": " + e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			}
			
		}
	}
		
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}
	
}
