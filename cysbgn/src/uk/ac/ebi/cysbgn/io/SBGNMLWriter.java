/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 *     Martijn van Iersel - co-supervisor
 *     Julio Saez-Rodriguez - supervisor
 ******************************************************************************/
package uk.ac.ebi.cysbgn.io;

import java.io.File;
import java.util.Iterator;

import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Glyph.Clone;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
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
	
	
	public SBGNMLWriter(CySBGN plugin, CyNetwork cyNetwork, CyNetworkView cyNetworkView, String filePath){
		this.plugin = plugin;
		this.filePath = filePath + CySBGN.SBGN_EXTENSION;
		this.cyNetwork = cyNetwork;
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
			
			// Apply nodes modifications
			Iterator<CyNode> nodeIterator = cyNetwork.nodesIterator();
			while( nodeIterator.hasNext() ){
				CyNode cyNode = nodeIterator.next();
				String nodeSbgnID = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
				
				Glyph nodeGlyph = getGlyph(nodeSbgnID, importedSbgn.getMap());
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
		
		Bbox bbox = new Bbox();
		bbox.setW((float) width);
		bbox.setH((float) height);
		bbox.setX( CySBGN.convert_X_coord_Cytoscape_to_SBGN(x, width) );
		bbox.setY( CySBGN.convert_Y_coord_Cytoscape_to_SBGN(y, height) );
		glyph.setBbox(bbox);
		
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
	
	
	private Glyph getGlyph(String glyphID, Map diagram){
		for(Glyph glyph : diagram.getGlyph())
			if( glyph.getId().equals(glyphID) )
				return glyph;
		return null;
	}
	
	private Arc getArc(String arcID, Map diagram){
		for(Arc arc : diagram.getArc())
			if( arc.getId().equals(arcID) )
				return arc;
		return null;
	}
}
