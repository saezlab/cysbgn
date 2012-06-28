package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.SwingConstants;

import org.sbgn.schematron.Issue;
import org.sbgn.schematron.SchematronValidator;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.utils.ValidationPanel;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanelImp;

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
			
			if( currentNetwork != null){
				String sbgnFilePath = plugin.getSbgnML(currentNetwork.getIdentifier());
				System.out.println(sbgnFilePath);
				File sbgnFile = new File(sbgnFilePath);
				
				List<Issue> issues = SchematronValidator.validate(sbgnFile);
				
				for( Issue issue : issues){
					System.out.print(issue.getRuleId()+": ");
					System.out.println(issue);
				}
				
				// Create and show the validation panel
				ValidationPanel validationPanel = new ValidationPanel(plugin, issues, currentNetworkView);
				((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).add("CySBGN", validationPanel);
				
				int index = ((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).indexOfComponent("CySBGN");
				((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).setSelectedIndex(index);
				
			}
		}catch(Exception e){
			e.printStackTrace();
//			new MessageDialog(e.getMessage(), MessagesHandler.getStackTrace(e), Icons.ERROR_LOGO.getPath());
			logger.warn("Error validating SBGN file : " + e.getMessage(), e);
		}
	}

	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}
}
