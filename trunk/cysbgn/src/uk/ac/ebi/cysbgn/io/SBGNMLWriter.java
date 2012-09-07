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
package uk.ac.ebi.cysbgn.io;

import java.io.File;
import java.util.Iterator;

import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Glyph.Clone;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.utils.CyNetworkViewUtils;
import uk.ac.ebi.cysbgn.utils.SbgnDiagramUtils;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

/**
 * AbstarctWriter class implements all methods shared among the SBGN writers.  
 * 
 * @author emanuel
 *
 */
public class SBGNMLWriter implements Task{

	private CySBGN plugin;
	
	private String filePath;
	private CyNetwork cyNetwork;
	private CyNetworkView cyNetworkView;
	
	private TaskMonitor taskMonitor;
	
	
	public SBGNMLWriter(CySBGN plugin, CyNetworkView cyNetworkView, String filePath){
		this.plugin = plugin;
		this.filePath = filePath + CySBGN.SBGN_EXTENSION;
		this.cyNetwork = cyNetworkView.getNetwork();
		this.cyNetworkView = cyNetworkView;
	}
	
	@Override
	public String getTitle() {
		return "Saving SBGN-ML network";
	}

	@Override
	public void halt() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		taskMonitor = monitor;
	}
	
	
	public void run(){
		
		taskMonitor.setStatus("Saving ...");
		taskMonitor.setPercentCompleted(-1);
		
		Sbgn importedSbgn = plugin.getSbgn(cyNetwork.getIdentifier());
		if( importedSbgn == null ) return;
		
		try{
			// Create SBGN-ML file
			File changedFile = new File(filePath);
			
			//
			CyNetworkViewUtils.refreshNodesAttributes(cyNetworkView);
			
			// Apply nodes modifications
			Iterator<CyNode> nodeIterator = cyNetwork.nodesIterator();
			while( nodeIterator.hasNext() ){
				CyNode cyNode = nodeIterator.next();
				String nodeSbgnID = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
				
				Glyph nodeGlyph = SbgnDiagramUtils.getGlyph(nodeSbgnID, importedSbgn.getMap());
				if( nodeGlyph != null ){
					saveCyNodeAttributes(cyNode, nodeGlyph);
				}
			}		
			
			SbgnUtil.writeToFile(importedSbgn, changedFile);
		}
		catch( Exception e ){	
			taskMonitor.setException(e, "Error while saving SBGN file!\n");
			return;
		}
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Network saved successfully!");
	}
	
	private void saveCyNodeAttributes(CyNode cyNode, Glyph glyph){
		// Store node size and position
		double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		int x = Cytoscape.getNodeAttributes().getIntegerAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_POS_X.getName());
		int y = Cytoscape.getNodeAttributes().getIntegerAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_POS_Y.getName());
		
		glyph.getBbox().setW(( float) width);
		glyph.getBbox().setH( (float) height);
		glyph.getBbox().setX( CySBGN.convert_X_coord_Cytoscape_to_SBGN(x, width) );
		glyph.getBbox().setY( CySBGN.convert_Y_coord_Cytoscape_to_SBGN(y, height) );
		
		// Store clone marker
		if( Cytoscape.getNodeAttributes().getBooleanAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName()) )
			glyph.setClone(new Clone());
		
		// Store label
		if( glyph.getLabel() != null){
			String labelString = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_LABEL.getName());
			Label label = new Label();
			label.setText(labelString);
			glyph.setLabel(label);	
		}
	}

}
