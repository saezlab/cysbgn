package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;
import java.io.File;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.io.SBGNMLWriter;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

@SuppressWarnings("serial")
public class SBGNWriter extends CytoscapeAction{
	
	private static final String EXPORT_MENU = "File.Export";
	
	private CySBGN plugin;
	
	public SBGNWriter(CySBGN plugin){
		super("Network as SBGN-ML...");
		setPreferredMenu(EXPORT_MENU);
		
		this.plugin = plugin;
	}

	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		CyNetworkView currentNetworkView = Cytoscape.getCurrentNetworkView();
		
		CyFileFilter[] filters = new CyFileFilter[1];
		filters[0] = new CyFileFilter("sbgn");
		File selectedFolder = FileUtil.getFile("Export SBGN network", FileUtil.SAVE, filters);
		
		if( selectedFolder != null ){	
			SBGNMLWriter sbgnWriter = new SBGNMLWriter(plugin, currentNetwork, currentNetworkView, selectedFolder.getAbsolutePath());
			MessagesHandler.executeTask(sbgnWriter, false);
		}
	}

	
}
