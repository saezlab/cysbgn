package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.utils.MessageDialog;
import uk.ac.ebi.cysbgn.utils.SBGNMLValidationTask;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

@SuppressWarnings("serial")
public class SBGNValidator extends CytoscapeAction{

	protected CySBGN plugin;
	
	private TaskMonitor taskMonitor;	
	private static CyLogger logger = CyLogger.getLogger(SBGNValidator.class);
	
	
	public SBGNValidator(CySBGN plugin){
		super("Validate network SBGN-ML...");
		setPreferredMenu(CySBGN.SBGN_MENU);
		
		this.plugin = plugin;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
			CyNetworkView currentNetworkView = Cytoscape.getCurrentNetworkView();
			String sbgnFilePath = plugin.getSbgnML(currentNetwork.getIdentifier());
			
			if( (currentNetwork != null) && (sbgnFilePath != null) ){
				
				SBGNMLValidationTask valiadatorTask = new SBGNMLValidationTask(plugin, sbgnFilePath, currentNetworkView);
				MessagesHandler.executeTask(valiadatorTask, true);
				
			}else{
				String detailedMessage = 
						"Either no network is selected or the network selected is not\nimported from a SBGN-ML " +
						"file.\n\nPlease please select a network imported from a SBGN-ML file.";
				
				new MessageDialog("SBGN Network Simplification", "Invalid network selected", detailedMessage, Icons.ERROR_LOGO.getPath());
			}
		}catch(Exception e){
			e.printStackTrace();
			String detailedMessage = 
					"Either no network is selected or the network selected is not\nimported from a SBGN-ML " +
					"file.\n\nPlease please select a network imported from a SBGN-ML file.";
			
			new MessageDialog("SBGN Network Simplification", "Invalid network selected", detailedMessage, Icons.ERROR_LOGO.getPath());
			
			logger.warn("Error validating SBGN file : " + e.getMessage(), e);
		}
	}

	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}
}
