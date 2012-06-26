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

import javax.swing.JOptionPane;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.io.SBGNReader;
import uk.ac.ebi.cysbgn.utils.LimitationPanel;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
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
		
		
		if( CySBGN.SHOW_LIMITATIONS_PANEL )
			showLimitationDialog();
		
	}
	
	private void showLimitationDialog(){
		String[] options = {"Ok, don't show me again.", "Ok, I understand."};
		int answer = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new LimitationPanel(), "Rendering limitations", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
		
		if( answer == 0 )
			CySBGN.SHOW_LIMITATIONS_PANEL = false;
	}
	
	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		taskMonitor = monitor;
	}
	
}
