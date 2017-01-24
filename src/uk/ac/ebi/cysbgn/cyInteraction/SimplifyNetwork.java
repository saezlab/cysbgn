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

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.io.SBGNMLReader;
import uk.ac.ebi.cysbgn.utils.MessageDialog;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

@SuppressWarnings("serial")
public class SimplifyNetwork extends CytoscapeAction{

	private CySBGN plugin;

	public SimplifyNetwork(CySBGN plugin){
		super("Create simplified network...");
		setPreferredMenu(CySBGN.SBGN_MENU);

		this.plugin = plugin;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			String currentNetworkID = Cytoscape.getCurrentNetwork().getIdentifier();
			
			if( (currentNetworkID != null) && (plugin.getSbgn(currentNetworkID) != null) ){ 
				SBGNMLReader reader = new SBGNMLReader(true);

				String simplifiedNetworkTitle = Cytoscape.getCurrentNetwork().getTitle() + "Simp";
				CyNetwork simplifiedNetwork = Cytoscape.createNetwork(simplifiedNetworkTitle, true);

				reader.readNetwork(plugin.getSbgn(currentNetworkID).getMap(), simplifiedNetwork);
				
				SBGNVisualStyle visualStyle = new SBGNVisualStyle(plugin);
				visualStyle.applyVisualStyle();	
			}
			
		}catch(Exception e){
			new MessageDialog("Network simplification", e.getMessage(), MessagesHandler.getStackTrace(e), Icons.ERROR_LOGO.getPath());
			e.printStackTrace();
		}

	}
}
