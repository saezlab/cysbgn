package uk.ac.ebi.cysbgn.cyInteraction;

import java.awt.event.ActionEvent;
import java.io.File;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.utils.MessageDialog;
import uk.ac.ebi.cysbgn.utils.Sbml2SbgnTask;
import cytoscape.logger.CyLogger;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

@SuppressWarnings("serial")
public class SBGNTest extends CytoscapeAction{

	protected CySBGN plugin;
	
	private TaskMonitor taskMonitor;	
	private static CyLogger logger = CyLogger.getLogger(SBGNTest.class);
	
	
	public SBGNTest(CySBGN plugin){
		super("SBML to SBGN...");
		setPreferredMenu(CySBGN.SBGN_MENU);
		
		this.plugin = plugin;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try{
			// Browse for sbml file
			CyFileFilter[] filters = new CyFileFilter[1];
			filters[0] = new CyFileFilter("xml");
			File sbmlFile = FileUtil.getFile("Select SBML model file", FileUtil.LOAD, filters);

			if( sbmlFile != null){
				Sbml2SbgnTask convert = new Sbml2SbgnTask(plugin, sbmlFile);
				MessagesHandler.executeTask(convert, false);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			String detailedMessage = "Error importing hepatonet1 sbml file!";
			
			new MessageDialog("Error importing hepatonet1 sbml file", "Error importing hepatonet1 sbml file", detailedMessage, Icons.ERROR_LOGO.getPath());
			logger.warn("Error Hepatonet1 SBML file : " + e.getMessage(), e);
		}
	}

	
	
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}

}
