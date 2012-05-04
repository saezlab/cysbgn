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
package uk.ac.ebi.cysbgn.cyInteraction;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.mapunits.Diagram;
import uk.ac.ebi.cysbgn.mapunits.MapArc;
import uk.ac.ebi.cysbgn.mapunits.MapNode;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualPropertyType;

/**
 * SBGN converter class. Enable the conversion for a Diagram map into a Cytoscape network.
 * 
 * @author emanuel
 *
 */
public class SBGNConverter {

	public void displayDiagram(Diagram diagram, CyNetworkView cyNetworkView){
		CyNetwork cyNetwork = cyNetworkView.getNetwork();
		
		for(MapNode node : diagram.getMapNodes().values())
			loadMapNode(node, cyNetwork, diagram.getName());

		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
		
		for(MapArc arc : diagram.getMapArcs().values())
			loadMapArc(arc, cyNetwork, diagram.getName());

		
		loadCyNodeViewAttributes(cyNetwork, diagram);
		loadCyEdgeViewAttributes(cyNetwork, diagram);
	}
	

	private void loadMapNode(MapNode mapNode, CyNetwork cyNetwork, String diagramName){
		CyNode cyNode = Cytoscape.getCyNode(mapNode.getId(), true);
		
		CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
		
		// Load attributes to Cytoscape
		cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName(), new String(mapNode.getId()) );
		
		cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_POS_X.getName(), new Double(mapNode.getX()) );
		cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_POS_Y.getName(), new Double(mapNode.getY()) );
		
		cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(mapNode.getWidth()) );
		cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(mapNode.getHeight()) );
		
		if( mapNode.getType() == null){
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.CLASS.getName(), new String(MapNode.INVISIBLE_NODE) );
		}else{
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.CLASS.getName(), new String(mapNode.getType().getClazz()) );
		}
		
		// Check if is to add clone marker
		if( mapNode.getCloneMarker() )
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName(), new Boolean(true) );
		else
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName(), new Boolean(false) );
		
		// Set Label
		if( mapNode.getLabel() != null ){
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), VisualPropertyType.NODE_LABEL.getBypassAttrName(), mapNode.getLabel());
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), "canonicalName",  mapNode.getLabel());
		}
		
		// Set Tag orientation
		if( mapNode.getOrientation() != null)
			cyNodeAttrs.setAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_ORIENTATION.getName(), new String(mapNode.getOrientation()) );
		
		cyNetwork.addNode(cyNode);
	}
	
	private void loadMapArc(MapArc arc, CyNetwork cyNetwork, String diagramName){
		if( arc.getSourceNode()==null || arc.getTargetNode()==null )
			return;
		
		CyNode source = mapMapNode2CyNode(arc.getSourceNode().getId(), cyNetwork);
		CyNode target = mapMapNode2CyNode(arc.getTargetNode().getId(), cyNetwork);
		
		String interaction;
		switch( arc.getType() ){
			case INHIBITION : interaction = "-1"; break;
			case ABSOLUTE_INHIBITION : interaction = "-1"; break;
			default : interaction = "1";
		}
		
		CyEdge cyEdge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, interaction, true, true);
		
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
		cyEdgeAttrs.setAttribute(cyEdge.getIdentifier(), SBGNAttributes.SBGN_ID.getName(), new String(arc.getId()) );
		cyEdgeAttrs.setAttribute(cyEdge.getIdentifier(), SBGNAttributes.CLASS.getName(), new String(arc.getType().getClazz()) );
		
		CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
		String sourceCanonicalName = cyNodeAttrs.getStringAttribute(source.getIdentifier(), "canonicalName");
		String targetCanonicalName = cyNodeAttrs.getStringAttribute(target.getIdentifier(), "canonicalName");
		String canonicalName = sourceCanonicalName + " (" + interaction + ") " + targetCanonicalName;
		cyEdgeAttrs.setAttribute(cyEdge.getIdentifier(), "canonicalName", canonicalName );
		
		cyNetwork.addEdge(cyEdge);
	}
	

	
	@SuppressWarnings("unchecked")
	private void loadCyNodeViewAttributes(CyNetwork cyNetwork, Diagram diagram){
		
		Iterator<CyNode> cyNodesIter = cyNetwork.nodesIterator();
		
		while( cyNodesIter.hasNext() ){
			CyNode currentNode = cyNodesIter.next();
			
			CyAttributes cyNodesAttrs = Cytoscape.getNodeAttributes();
			String sbgnID = cyNodesAttrs.getStringAttribute(currentNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			NodeView currentNodeView = Cytoscape.getCurrentNetworkView().getNodeView(currentNode);
	
			// Load x and y position
			double posX = diagram.getNode(sbgnID).getX();
			double posY = diagram.getNode(sbgnID).getY();

			currentNodeView.setXPosition(posX);
			currentNodeView.setYPosition(posY);
			
		}
	}

	@SuppressWarnings("unchecked")
	private void loadCyEdgeViewAttributes(CyNetwork cyNetwork, Diagram diagram){
		
		Iterator<CyEdge> cyEdgesIter = cyNetwork.edgesIterator();
		
		while( cyEdgesIter.hasNext() ){
			CyEdge currentEdge = cyEdgesIter.next();
			
			CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();
			String sbgnID = cyEdgeAttrs.getStringAttribute(currentEdge.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			EdgeView currentNodeView = Cytoscape.getCurrentNetworkView().getEdgeView(currentEdge);

			// Load edge anchor points
			ArrayList<Point> anchors = diagram.getArc(sbgnID).getAnchors();
			for(Point anchor : anchors)
				currentNodeView.getBend().addHandle(anchor);
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private CyNode mapMapNode2CyNode(String nodeID, CyNetwork network){
		
		Iterator<CyNode> cyNodesIter = network.nodesIterator();
		
		while( cyNodesIter.hasNext() ){
			CyNode currentNode = cyNodesIter.next(); 
			if(currentNode.getIdentifier().equals(nodeID))
				return currentNode;
		}
		
		return null;
	}
}
