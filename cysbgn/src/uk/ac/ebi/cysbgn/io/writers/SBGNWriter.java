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
package uk.ac.ebi.cysbgn.io.writers;

import java.io.File;
import java.util.Iterator;

import org.sbgn.ArcClazz;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Glyph.Clone;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualPropertyType;

/**
 * AbstarctWriter class implements all methods shared among the SBGN writers.  
 * 
 * @author emanuel
 *
 */
public class SBGNWriter implements Task{

	private String filePath;
	private CyNetwork cyNetwork;
	private CyNetworkView cyNetworkView;
	
	private TaskMonitor taskMonitor;
	
	
	public SBGNWriter(CyNetwork cyNetwork, CyNetworkView cyNetworkView, String filePath){
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
		
		try{
			save(cyNetwork, filePath);
		}
		catch( Exception e ){	
			taskMonitor.setException(e, "Error while saving SBGN file!\n");
			return;
		}
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Network saved successfully!");
	}
	
	
	public void save(CyNetwork cyNetwork, String filePath) throws Exception{
		// Create sbgn map file
		File sbgnFile = new File(filePath);

		// Create map
		Sbgn sbgnDiagram = new Sbgn();
		Map map = new Map();
		sbgnDiagram.setMap(map);

		// Add nodes
		Iterator<CyNode> nodeIterator = cyNetwork.nodesIterator();
		while( nodeIterator.hasNext() ){
			CyNode node = nodeIterator.next();
			saveNodes(node, map);
		}

		// Add nodes
		Iterator<CyEdge> edgesIterator = cyNetwork.edgesIterator();
		while( edgesIterator.hasNext() ){
			CyEdge edge = edgesIterator.next();
			saveEdges(edge, map);
		}

		// Store file
		SbgnUtil.writeToFile(sbgnDiagram, sbgnFile);
	}
	
	public void saveNodes(CyNode node, Map diagram){
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
		// Create glyph
		Glyph glyph = new Glyph();
		
		// Set ID
		glyph.setId( node.getIdentifier() );
		
		// Set glyph class
		String glyphClass = nodeAttributes.getStringAttribute(node.getIdentifier(), SBGNAttributes.CLASS.getName());
		glyph.setClazz( glyphClass );
		
		
		// Set glyph bbox
		double glyphWidth = cyNetworkView.getNodeView(node).getWidth();
		double glyphHeight = cyNetworkView.getNodeView(node).getHeight();
		double glyphX = cyNetworkView.getNodeView(node).getXPosition();
		double glyphY = cyNetworkView.getNodeView(node).getYPosition();
		
		Bbox glyphBBox = new Bbox();
		glyphBBox.setW( (float) glyphWidth );
		glyphBBox.setH( (float) glyphHeight );
		glyphBBox.setX( CySBGN.convert_X_coord_Cytoscape_to_SBGN( (int)glyphX, (int)glyphWidth ) );
		glyphBBox.setY( CySBGN.convert_Y_coord_Cytoscape_to_SBGN( (int)glyphY, (int)glyphHeight ) );
		
		glyph.setBbox( glyphBBox );
		
		// Set clone marker
		boolean glyphCloneMarker = nodeAttributes.getBooleanAttribute(node.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
		if( glyphCloneMarker ) glyph.setClone(new Clone());
		
		// Set label
		String label = nodeAttributes.getStringAttribute(node.getIdentifier(), VisualPropertyType.NODE_LABEL.getBypassAttrName());
		if( label != null ){
			Label glyphLabel = new Label();
			glyphLabel.setText(label);
			glyph.setLabel(glyphLabel);
		}
		
		// Add glyph
		diagram.getGlyph().add(glyph);
	}
	
	public void saveEdges(CyEdge edge, Map diagram){
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		// Create Arc
		Arc arc = new Arc();
		
		// Set ID
		arc.setId( edge.getIdentifier() );
		
		// Set arc class
		String glyphClass = edgeAttributes.getStringAttribute(edge.getIdentifier(), SBGNAttributes.CLASS.getName());
		arc.setClazz( ArcClazz.fromClazz(glyphClass).getClazz() );
		
		// Set Source
		Glyph sourceGlyph = getGlyph(edge.getSource().getIdentifier(), diagram);
		arc.setSource( sourceGlyph );
		
		// Set Source
		Glyph targetGlyph = getGlyph(edge.getTarget().getIdentifier(), diagram);
		arc.setTarget( targetGlyph );
		
		// Set Start
		Start glyphStart = new Start();
		glyphStart.setX(sourceGlyph.getBbox().getX());
		glyphStart.setY(sourceGlyph.getBbox().getY());
		arc.setStart(glyphStart);
		
		// Set End
		End glyphEnd = new End();
		glyphEnd.setX(targetGlyph.getBbox().getX());
		glyphEnd.setY(targetGlyph.getBbox().getY());
		arc.setEnd(glyphEnd);
		
		// Add arc
		diagram.getArc().add(arc);
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
