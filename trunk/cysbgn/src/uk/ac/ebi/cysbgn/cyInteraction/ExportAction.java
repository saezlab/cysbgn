package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;
import java.io.File;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

public class ExportAction extends CytoscapeAction{
	
	private static final String EXPORT_MENU = "File.Export";
	
	private CySBGN plugin;
	
	public ExportAction(CySBGN plugin){
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
		File selectedFolder = FileUtil.getFile("Save SBGN network", FileUtil.SAVE, filters);
		
		if( selectedFolder != null ){	
			plugin.writeSBGNDiagram(currentNetwork, currentNetworkView, selectedFolder.getAbsolutePath());
		}
	}

	
}
