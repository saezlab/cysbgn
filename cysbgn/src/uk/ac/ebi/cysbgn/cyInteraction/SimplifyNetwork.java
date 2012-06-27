package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.io.SBGNMLReader;
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
			e.printStackTrace();
		}

	}
}
