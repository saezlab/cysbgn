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
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.mapunits.Diagram;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.view.CyNetworkView;

/**
 * Class responsible to call the SBGN reader when the SBGN diagram file is selected from the import menu of Cytoscape.
 * 
 * @author emanuel
 *
 */
public class ImportAction extends AbstractGraphReader{

	protected CySBGN plugin;
	protected Diagram diagram;
	
	
	public ImportAction(String fileName, CySBGN plugin){
		super(fileName);
		this.plugin = plugin;
	}

	@Override
	public void read() throws IOException {
		diagram = plugin.readSBGNDiagram(fileName);
		
		if(diagram == null) 
			MessagesHandler.showErrorMessageDialog("Could not load file!", "Error importing SBGN file", new Exception());
	}
	
	public void doPostProcessing(CyNetwork network){
		CyNetworkView cyNetworkView = Cytoscape.getNetworkView(network.getIdentifier());
		plugin.displayDiagram(diagram, cyNetworkView);
	}

}
