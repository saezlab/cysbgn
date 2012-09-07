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
package uk.ac.ebi.cysbgn.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.sbgn.schematron.Issue;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.cytopanels.CytoPanelImp;

@SuppressWarnings("serial")
public class ValidationPanel extends JPanel{

	private static final int[] ISSUE_TABLE_HEADERS_WIDTH = {80, 80, 500, 80};
	private static final String[] ISSUE_TABLE_HEADERS = {"Severity", "SBGN ID", "Message", "Rule ID"};
	private static final String[] RULES_TO_IGNORE = {"af10114"};
	private static final String VALIDATION_PANEL_TITLE =  "CySBGN - Validation";
	
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
		refreshCyNetworkAttributes();
		
		showPanel();
		showDialog();
	}
	
	private void showPanel(){
		int index = ((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).indexOfComponent(VALIDATION_PANEL_TITLE);
		
		if( index != -1 )
			((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).remove(index);
		
		((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).add(VALIDATION_PANEL_TITLE, ValidationPanel.this);
		index = ((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).indexOfComponent(VALIDATION_PANEL_TITLE);
		((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)).setSelectedIndex(index);
		
	}
	
	private void showDialog(){
		if( issues.size() == 0 ){
			new MessageDialog("SBGN-ML Validation", "No issues were found!", null, Icons.CORRECT_LOGO.getPath());
		}else{
			String detailedMessage = "Check please CySBGN validation panel below.";
			new MessageDialog("SBGN-ML Validation", "The validation found " + numberOfIssuesToConsider() + " issues.\n Check CySBGN validation panel below.", null, Icons.ERROR_LOGO.getPath());
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
	
		issuesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		issuesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if( !event.getValueIsAdjusting() )
					issueRowSelected();
			}
		});
		issuesTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event){}
			
			@Override
			public void mousePressed(MouseEvent event){}
			
			@Override
			public void mouseExited(MouseEvent event){}
			
			@Override
			public void mouseEntered(MouseEvent event){}

			@Override
			public void mouseClicked(MouseEvent event){
//				if( event.getButton() == MouseEvent.NOBUTTON )
					issueRowSelected();
			}
		});
		
		// Set table headers size
		issuesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int index = 0;
        while (index < ISSUE_TABLE_HEADERS.length) {
            TableColumn a = issuesTable.getColumnModel().getColumn(index);
            a.setPreferredWidth(ISSUE_TABLE_HEADERS_WIDTH[index]);
            index++;
        }
		
		issuesPanel.add(issuesTableScrollPane, BorderLayout.CENTER);
		
		// Main panel
		setLayout(new BorderLayout());
		add(titlePanel, BorderLayout.NORTH);
		add(issuesPanel, BorderLayout.CENTER);
	}
	
	
	private void issueRowSelected(){
		cyNetworkView.getNetwork().unselectAllEdges();
		cyNetworkView.getNetwork().unselectAllNodes();

		List<CyNode> nodesToSelect = new ArrayList<CyNode>();
		List<CyEdge> edgesToSelect = new ArrayList<CyEdge>();

		int[] selectedRows = issuesTable.getSelectedRows();
		for(int selectedRow : selectedRows){
			String sbgnID = (String) issuesTable.getValueAt(selectedRow, 1);

			nodesToSelect.addAll( CyNetworkUtils.getCyNodesBySbgnId(cyNetworkView.getNetwork(), sbgnID) );
			edgesToSelect.addAll( CyNetworkUtils.getCyEdgesBySbgnId(cyNetworkView.getNetwork(), sbgnID) );
		}

		cyNetworkView.getNetwork().setSelectedNodeState(nodesToSelect, true);
		cyNetworkView.getNetwork().setSelectedEdgeState(edgesToSelect, true);

		cyNetworkView.redrawGraph(true, true);
		cyNetworkView.updateView();
	}
	
	private void fillIssuesTable(){
		for(Issue issue : issues){
			if( !ignoreIssue(issue) ){
				String[] issueAttributes = {issue.getSeverity().name(), issue.getAboutId(), issue.getRuleDescription(), issue.getRuleId()};
				issuesTableModel.addRow(issueAttributes);
			}
		}
	}
	
	private void refreshCyNetworkAttributes(){
		CyNetwork cyNetwork = cyNetworkView.getNetwork();
		
		// Refresh nodes validation attribute
		Iterator<CyNode> nodesIterator = cyNetwork.nodesIterator();
		while( nodesIterator.hasNext() ){
			CyNode cyNode = nodesIterator.next();
			String sbgnID = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			boolean isCorrect = true;
			for(int i=0; i<issues.size() && isCorrect; i++)
				if( issues.get(i).getAboutId().equals(sbgnID) && ignoreIssue(issues.get(i)) )
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
				if( issues.get(i).getAboutId().equals(sbgnID) && ignoreIssue(issues.get(i)) )
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
	
	private int numberOfIssuesToConsider(){
		int count = 0;
		for(Issue issue : issues)
			if( !ignoreIssue(issue) )
				count++;
		return count;
	}
}
