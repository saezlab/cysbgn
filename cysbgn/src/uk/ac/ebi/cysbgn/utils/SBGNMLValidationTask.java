package uk.ac.ebi.cysbgn.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.sbgn.schematron.Issue;
import org.sbgn.schematron.SchematronValidator;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;
import cytoscape.view.CyNetworkView;

public class SBGNMLValidationTask implements Task{

	private CySBGN plugin;
	private TaskMonitor taskMonitor;
	private static CyLogger logger = CyLogger.getLogger(SBGNMLValidationTask.class);
	private Thread myThread = null;

	private String sbgnFile;
	private CyNetworkView cyNetworkView;
	private List<Issue> issues;
	
	public SBGNMLValidationTask(CySBGN plugin, String sbgnFile, CyNetworkView cyNetworkView){
		this.plugin = plugin;
		this.sbgnFile = sbgnFile;
		this.cyNetworkView = cyNetworkView;
		this.issues = new ArrayList<Issue>();
	}

	@Override
	public String getTitle() {
		return "SBGN-ML validation";
	}

	@Override
	public void halt() {
		
		CyLogger.getLogger().info("Halt called");

		if (myThread != null) {
			myThread.interrupt();
			((JTask) taskMonitor).setDone();
			
			issues = new ArrayList<Issue>();
		}
	}

	@Override
	public void run() {

		taskMonitor.setPercentCompleted(-1);
		taskMonitor.setStatus("Validating network SBGN-ML file...");
		
		myThread = Thread.currentThread();
		try{
			File file = new File(sbgnFile);

			issues = SchematronValidator.validate(file);

			// Create and show the validation panel
			ValidationPanel validationPanel = new ValidationPanel(plugin, issues, cyNetworkView);

		}catch (Exception e) {
			taskMonitor.setException(e, "Error running the validation process.\nResults may not be shown.");
			return;
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Validation finished!");
	}

	
	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		this.taskMonitor = monitor;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
}
