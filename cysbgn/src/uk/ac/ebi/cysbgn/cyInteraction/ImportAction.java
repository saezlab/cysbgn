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
import uk.ac.ebi.cysbgn.io.SBGNReader;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.task.TaskMonitor;

/**
 * Class responsible to call the SBGN reader when the SBGN diagram file is selected from the import menu of Cytoscape.
 * 
 * @author emanuel
 *
 */
public class ImportAction extends AbstractGraphReader{

	private TaskMonitor taskMonitor;
	
	protected CySBGN plugin;
	protected SBGNVisualStyle visualStyle;
	
	
	public ImportAction(String fileName, CySBGN plugin){
		super(fileName);
		this.plugin = plugin;
		this.visualStyle = new SBGNVisualStyle(plugin);
	}

	@Override
	public void read() throws IOException {
		
	}
	
	public void doPostProcessing(CyNetwork network){
		SBGNReader newReader = new SBGNReader(false);
		try {
			network = newReader.read(fileName, network);
			
			visualStyle.applyVisualStyle();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}		
		plugin.addNetwork(network, newReader.getMap(), fileName);
	}
	
	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		taskMonitor = monitor;
	}

}
