package uk.ac.ebi.cysbgn.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.sbgn.schematron.Issue;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

@SuppressWarnings("serial")
public class ValidationPanel extends JPanel{

	private static final String[] ISSUE_TABLE_HEADERS = {"Severity", "SBGN ID", "Message", "Rule ID"};
	private static final String[] RULES_TO_IGNORE = {"af10114"};
	
	private CySBGN plugin;
	private List<Issue> issues;
	private CyNetworkView cyNetworkView;
	
	private JPanel titlePanel;
	private JLabel networkLabel;
	private JLabel networkTitleLabel;
	
	private JPanel issuesPanel;
	private JScrollPane issuesTableScrollPane;
	private JTable issuesTable;
	private DefaultTableModel issuesTableModel;
	
	
	public ValidationPanel(CySBGN plugin, List<Issue> issues, CyNetworkView cyNetworkView){
		super();
		
		this.plugin = plugin;
		this.issues = issues;
		this.cyNetworkView = cyNetworkView;
		
		initComponents();
		
		fillIssuesTable();
		validateCyNetworkAttributes();
		
		showDialog();
	}
	
	private void showDialog(){
		if( issues.size() == 0 ){
			String detailedMessage = "No issues were found for " + cyNetworkView.getTitle() + ".";
			new MessageDialog("SBGN-ML Validation", "No issues were found!", detailedMessage, Icons.CORRECT_LOGO.getPath());
		}else{
			String detailedMessage = "Check please CySBGN validation panel below.";
			new MessageDialog("SBGN-ML Validation", issues.size() + " issues were found for " + cyNetworkView.getTitle(), detailedMessage, Icons.ERROR_LOGO.getPath());
		}
	}

	private void initComponents(){
		
		// Title panel
		titlePanel = new JPanel(new BorderLayout());
		networkLabel = new JLabel("Network title: ");
		networkTitleLabel = new JLabel( cyNetworkView.getTitle() );
		
		titlePanel.add(networkLabel, BorderLayout.WEST);
		titlePanel.add(networkTitleLabel, BorderLayout.CENTER);
		
		// Issues panel
		issuesPanel = new JPanel(new BorderLayout());
		issuesPanel.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Issues found"));
		
		issuesTableModel = new DefaultTableModel(ISSUE_TABLE_HEADERS, 0){
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};

		issuesTable = new JTable(issuesTableModel);
		issuesTableScrollPane = new JScrollPane(issuesTable);
	
		issuesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				
				List<CyNode> nodesToSelect = new ArrayList<CyNode>();
				List<CyEdge> edgesToSelect = new ArrayList<CyEdge>();
				
				int[] selectedRows = issuesTable.getSelectedRows();
				for(int selectedRow : selectedRows){
					String sbgnID = (String) issuesTable.getValueAt(selectedRow, 1);
					System.out.println(sbgnID);
					
					nodesToSelect.addAll( CyNetworkUtils.getCyNodesBySbgnId(cyNetworkView.getNetwork(), sbgnID) );
					edgesToSelect.addAll( CyNetworkUtils.getCyEdgesBySbgnId(cyNetworkView.getNetwork(), sbgnID) );
				}
				
				cyNetworkView.getNetwork().unselectAllEdges();
				cyNetworkView.getNetwork().unselectAllNodes();
				
				cyNetworkView.getNetwork().setSelectedNodeState(nodesToSelect, true);
				cyNetworkView.getNetwork().setSelectedEdgeState(edgesToSelect, true);
			}
		});
		
		issuesPanel.add(issuesTableScrollPane, BorderLayout.CENTER);
		
		// Main panel
		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(issuesPanel, BorderLayout.CENTER);
	}
	
	private void fillIssuesTable(){
		for(Issue issue : issues){
			if( !ignoreIssue(issue) ){
				String[] issueAttributes = {issue.getSeverity().name(), issue.getAboutId(), issue.getRuleDescription(), issue.getRuleId()};
				issuesTableModel.addRow(issueAttributes);
			}
		}
	}
	
	private void validateCyNetworkAttributes(){
		CyNetwork cyNetwork = cyNetworkView.getNetwork();
		
		// Refresh nodes validation attribute
		Iterator<CyNode> nodesIterator = cyNetwork.nodesIterator();
		while( nodesIterator.hasNext() ){
			CyNode cyNode = nodesIterator.next();
			String sbgnID = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			boolean isCorrect = true;
			for(int i=0; i<issues.size() && isCorrect; i++)
				if( issues.get(i).getAboutId().equals(sbgnID) )
					isCorrect = false;
			
			if( isCorrect )
				Cytoscape.getNodeAttributes().setAttribute(cyNode.getIdentifier(), SBGNAttributes.VALIDATION.getName(), SBGNAttributes.VALIDATION_CORRECT.getName());
			else 
				Cytoscape.getNodeAttributes().setAttribute(cyNode.getIdentifier(), SBGNAttributes.VALIDATION.getName(), SBGNAttributes.VALIDATION_INCORRECT.getName());
		}
		
		// Refresh edges validation attribute
		Iterator<CyEdge> edgesIterator = cyNetwork.edgesIterator();
		while( edgesIterator.hasNext() ){
			CyEdge cyEdge = edgesIterator.next();
			String sbgnID = Cytoscape.getEdgeAttributes().getStringAttribute(cyEdge.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			boolean isCorrect = true;
			for(int i=0; i<issues.size() && isCorrect; i++)
				if( issues.get(i).getAboutId().equals(sbgnID) )
					isCorrect = false;
			
			if( isCorrect )
				Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), SBGNAttributes.VALIDATION.getName(), SBGNAttributes.VALIDATION_CORRECT.getName());
			else 
				Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), SBGNAttributes.VALIDATION.getName(), SBGNAttributes.VALIDATION_INCORRECT.getName());
		}
		
	}
	
	private boolean ignoreIssue(Issue issue){
		for(String rule : RULES_TO_IGNORE)
			if( issue.getRuleId().equals(rule) )
				return true;
		
		return false;
	}
}
