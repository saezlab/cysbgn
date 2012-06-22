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
package uk.ac.ebi.cysbgn.io.readers;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sbgn.ArcClazz;
import org.sbgn.ConvertMilestone1to2;
import org.sbgn.GlyphClazz;
import org.sbgn.SbgnUtil;
import org.sbgn.SbgnVersionFinder;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Arcgroup;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Port;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.methods.ArcSegmentationAlgorithm;
import uk.ac.ebi.cysbgn.methods.CustomEdgePoint;
import uk.ac.ebi.cysbgn.methods.SegmentationPoint;
import uk.ac.ebi.cysbgn.utils.CyEdgeAttrUtils;
import uk.ac.ebi.cysbgn.utils.CyNetworkUtils;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;


/**
 * AbstarctReader class implements all methods shared among the SBGN readers.  
 * 
 * @author emanuel
 *
 */
public class SBGNReader{

	
	private ArcSegmentationAlgorithm nextPortCreator;
	
	private Sbgn map;
	
	/**
	 * HashMap<SBGN_ID, Cytoscape_ID>
	 */
	private HashMap<String,String> nodesIDs;
	
	
	public SBGNReader(){
		nextPortCreator = new ArcSegmentationAlgorithm();
		nodesIDs = new HashMap<String, String>();
	}
	
		
	public CyNetwork read(String networkFilePath, Boolean isAnalysisStyle, CyNetwork cyNetwork) throws Exception{
		File file = new File(networkFilePath);
		File targetFile = file;
		
		// Check file version
		int version = SbgnVersionFinder.getVersion(targetFile);
		if (version == 1){
			 targetFile = File.createTempFile(file.getName(), ".sbgn");
             System.out.println ("Converted to " + targetFile);
             ConvertMilestone1to2.convert (file, targetFile);
		}
		
		map = SbgnUtil.readFromFile(targetFile);
		CyNetwork network = readNetwork(map.getMap(), isAnalysisStyle, cyNetwork);

		return network;
	}
	
	/**
	 * Creates the Diagram containing all the information of the libSBGN Map.
	 * 
	 * @param diagramMap
	 * @param networkName
	 * @return
	 * @throws Exception 
	 */
	protected CyNetwork readNetwork(Map diagramNetwork, Boolean isAnalysisStyle, CyNetwork newNetwork) throws Exception {
		
		// Load all nodes
		for(Glyph glyph : diagramNetwork.getGlyph())
			createNode(glyph, newNetwork);
		
		// Load all Arc's Ports and Glyphs 
		for(Arc arc : diagramNetwork.getArc()){
			for(Port port : arc.getPort())
				createNode(port, newNetwork);

			for(Glyph glyph : arc.getGlyph())
				createNode(glyph, newNetwork);
		}
		
		// Draw all nodes and ports contained inside a ArcGroup
		for(Arcgroup arcgroup : diagramNetwork.getArcgroup()){
			for(Glyph glyph : arcgroup.getGlyph())
				createNode(glyph, newNetwork);
			
			for(Arc arc : arcgroup.getArc()){
				for(Port port : arc.getPort())
					createNode(port, newNetwork);

				for(Glyph glyph : arc.getGlyph())
					createNode(glyph, newNetwork);
			}

		}
		
		// Draw all edges
		for(Arc arc : diagramNetwork.getArc())
			getArc(arc, diagramNetwork, newNetwork);
		
		// Draw all nodes and edges contained inside a ArcGroup
		for(Arcgroup arcgroup : diagramNetwork.getArcgroup())			
			for(Arc arc : arcgroup.getArc())
				getArc(arc, diagramNetwork, newNetwork);
		
		return newNetwork;
	}
	
	private CyNode createNode(Glyph glyph, CyNetwork newCyNetwork){

		// Node attributes
		GlyphClazz 	nodeClass = GlyphClazz.fromClazz( glyph.getClazz() );
		String 		sbgnID = glyph.getId();
		Double 		width = 1.0;
		Double		height = 1.0;
		Integer 	x = (int) glyph.getBbox().getX();
		Integer 	y = (int) glyph.getBbox().getY();
		String 		label = "";
		String		compartment = SBGNAttributes.NODE_COMPARTMENT_NA.getName();
		String 		orientation = SBGNAttributes.NODE_ORIENTATION_NA.getName();
		Boolean		clone = false;
		
		
		// Create all inner Glyphs
		for(Glyph innerGlyphs : glyph.getGlyph())
			createNode(innerGlyphs, newCyNetwork);
		
		CyNode newCyNode = Cytoscape.getCyNode(glyph.getId(), true);

		switch(nodeClass){
			case UNIT_OF_INFORMATION : 
				String newNodeClass;
				if( glyph.getEntity() != null )
					newNodeClass = glyph.getEntity().getName();
				else
					newNodeClass = glyph.getClazz();
	
				break;	
			case ANNOTATION :
				String annotationArcID = glyph.getId() + "callout";
				
				CyNode target = CyNetworkUtils.getNode(newCyNetwork, nodesIDs.get( ((Glyph)glyph.getCallout().getTarget()).getId()) ); 
				CyEdge cyEdge = createEdge(newCyNode, target, ArcClazz.LOGIC_ARC, annotationArcID, new ArrayList<Point2D>());
				
				newCyNetwork.addEdge(cyEdge);
				break;
			default : break; 
		}

		switch( nodeClass ){
			case AND : label = "AND"; break;
			case OR : label = "OR"; break;
			case NOT : label = "NOT"; break;
			case OMITTED_PROCESS : label = "\\\\"; break;
			case UNCERTAIN_PROCESS : label = "?"; break;
			case DISSOCIATION : label = "O"; break;
			case DELAY : label = "\u03C4"; break;
			case STATE_VARIABLE : 
				if( glyph.getState() != null ){
					label = "";
					
					String value = null;
					String variable = null;
					
					if( glyph.getState().getValue() != null )
						value = glyph.getState().getValue();
					if( glyph.getState().getVariable() != null )
						variable = glyph.getState().getVariable();
					
					if( (value != null) && (variable != null) )
						label = value + "@" + variable;
					if( (value != null) && (variable == null) )
						label = value;
					if( (value == null) && (variable != null) )
						label = variable;
				}
				break;
			case LOCATION : label = ""; break;
			case ASSOCIATION : label = ""; break;
			case PROCESS : label = ""; break;
			case SOURCE_AND_SINK : label = ""; break;
			case OUTCOME : label = ""; break;
			case INTERACTION : label = ""; break;
			case EXISTENCE : label = ""; break;
			default :
				if( glyph.getLabel() != null )
					label =  glyph.getLabel().getText();
				break;
		}
		
		width = (double) glyph.getBbox().getW();
		height = (double) glyph.getBbox().getH();
		
		if( height <= 0 ) height = 1.0;
		if( width <= 0 ) width = 1.0;
		
		x = CySBGN.convert_X_coord_SBGN_to_Cytoscape(x, width);
		y = CySBGN.convert_Y_coord_SBGN_to_Cytoscape(y, height);

		// Set tag and terminal shapes orientation
		if( nodeClass == GlyphClazz.TERMINAL || nodeClass == GlyphClazz.TAG )
			orientation = glyph.getOrientation();

		if( glyph.getClone() != null )
			clone = true;

		// Add the node and attributes
		newCyNetwork.addNode(newCyNode);
		
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.CLASS.getName(), nodeClass.getClazz());
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName(), sbgnID);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), width);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), height);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_POS_X.getName(), x);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_POS_Y.getName(), y);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_LABEL.getName(), label);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_COMPARTMENT.getName(), compartment);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_ORIENTATION.getName(), orientation);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName(), clone);
		
		nodesIDs.put(sbgnID, newCyNode.getIdentifier());
		
		return newCyNode;
	}
	
	private CyNode createNode(Port port, CyNetwork newCyNetwork){
		// Node attributes 
		String 		nodeClass = SBGNAttributes.CLASS_INVISIBLE.getName();
		String 		sbgnID = port.getId();
		Double 		width = 1.0;
		Double		height = 1.0;
		Integer 	x = (int) port.getX();
		Integer 	y = (int) port.getY();
		String 		label = "";
		String		compartment = SBGNAttributes.NODE_COMPARTMENT_NA.getName();
		String 		orientation = SBGNAttributes.NODE_ORIENTATION_NA.getName();
		Boolean		clone = false;
		
		CyNode newCyNode = Cytoscape.getCyNode(sbgnID, true);
		
		x = CySBGN.convert_X_coord_SBGN_to_Cytoscape(x, width);
		y = CySBGN.convert_Y_coord_SBGN_to_Cytoscape(y, height);
		
		// Add the node and attributes
		newCyNetwork.addNode(newCyNode);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.CLASS.getName(), nodeClass);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName(), sbgnID);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), width);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), height);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_POS_X.getName(), x);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_POS_Y.getName(), y);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_LABEL.getName(), label);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_COMPARTMENT.getName(), compartment);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_ORIENTATION.getName(), orientation);
		Cytoscape.getNodeAttributes().setAttribute(newCyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName(), clone);
		
		nodesIDs.put(sbgnID, newCyNode.getIdentifier());
		
		return newCyNode;
	}

	
	private void getArc(Arc arc, Map diagramMap, CyNetwork cyNetwork) throws Exception{
		try{
			// Edges attributes
			ArcClazz		arcClass = ArcClazz.fromClazz( arc.getClazz() );
			String 			sbgnID = arc.getId();
			CyNode			source = null;
			CyNode			target = null;
			List<Point2D> 	bendPoints = new ArrayList<Point2D>();
			

			// Get segment list points of the arc
			List<SegmentationPoint> arcPoints = nextPortCreator.generateSortedPointsList(arc);
			
			System.out.print(arc.getId()+": ");
			System.out.println(arcPoints);
			
			for(int i=0; i<arcPoints.size(); i++){
				
				Object currentPoint = arcPoints.get(i).getPoint(); 
				
				if( currentPoint instanceof Start){
					String sourceNodeID;

					if( arc.getSource() instanceof Glyph)
						sourceNodeID = ((Glyph)arc.getSource()).getId();
					else
						sourceNodeID = ((Port)arc.getSource()).getId();
					
					source = CyNetworkUtils.getNode(cyNetwork, nodesIDs.get(sourceNodeID));
					
					if(source == null){ // No Node found, search node by port
						String glyphID = getNodeByPort((Port)arc.getSource(), diagramMap);
						source = CyNetworkUtils.getNode(cyNetwork, nodesIDs.get(glyphID));
						addBendPoint( bendPoints, arc.getSource() );
					}

					// Arcs that point into the same node have anchors in start and end point
					if( arc.getSource().equals(arc.getTarget()) )
						addBendPoint( bendPoints, currentPoint );
					
				} else if( currentPoint instanceof Next ){ // Add the anchor point of the segment
					addBendPoint( bendPoints, currentPoint );
					
				}
				else if( currentPoint instanceof Port ){ // If it's a port a sub arc must be created.
					target = createNode(((Port)currentPoint), cyNetwork);
					
					CyEdge cyEdge;
					switch( arcClass ){
						case INTERACTION : 
							if( linksToStart(arcPoints, i) ){
								cyEdge = createEdge(target, source, ArcClazz.INTERACTION, sbgnID, bendPoints);
								break;
							}
						default : cyEdge = createEdge(source, target, ArcClazz.LOGIC_ARC, sbgnID, bendPoints);
							
					}
					cyNetwork.addEdge(cyEdge);
					
					source = target;
					bendPoints = new ArrayList<Point2D>();
					
				} else if( currentPoint instanceof End){
					String targetNodeID;
					if(arc.getTarget() instanceof Glyph)
						targetNodeID = ((Glyph)arc.getTarget()).getId();
					else
						targetNodeID = ((Port)arc.getTarget()).getId();
					
					target = CyNetworkUtils.getNode(cyNetwork, nodesIDs.get(targetNodeID));
					
					if(target == null){
						String glyphID = getNodeByPort((Port)arc.getTarget(), diagramMap);
						target = CyNetworkUtils.getNode(cyNetwork, nodesIDs.get(glyphID));
						
						addBendPoint( bendPoints, arc.getTarget() );
					}
						
					if(arc.getTarget() instanceof Glyph)
						if( ((Glyph)arc.getTarget()).getClazz().equals(GlyphClazz.IMPLICIT_XOR.getClazz()) )
							arcClass = ArcClazz.LOGIC_ARC;
					
					// Arcs that point into the same node have anchors in start and end point
					if( arc.getSource().equals(arc.getTarget()) )
						addBendPoint( bendPoints, currentPoint );
						
					CyEdge cyEdge = createEdge(source, target, arcClass, sbgnID, bendPoints);
					cyNetwork.addEdge(cyEdge);
					
					bendPoints = new ArrayList<Point2D>();
					
				} else if( currentPoint instanceof Glyph){ 
					sbgnID = ((Glyph)currentPoint).getId();
					target = CyNetworkUtils.getNode(cyNetwork, nodesIDs.get(sbgnID));
					
					GlyphClazz glyphClass = GlyphClazz.fromClazz( ((Glyph) currentPoint).getClazz() );
					switch( glyphClass ){
						case INTERACTION: ;
						case CARDINALITY: ;
						case OUTCOME : arcClass = ArcClazz.LOGIC_ARC; break;
						default : break;
					}
					
					CyEdge cyEdge;
					switch( ArcClazz.fromClazz(arc.getClazz()) ){
						case INTERACTION : 
							if( linksToStart(arcPoints, i) ){
								cyEdge = createEdge(target, source, ArcClazz.INTERACTION, sbgnID, bendPoints);
								break;
							}
						default : 
							cyEdge = createEdge(source, target, arcClass, sbgnID, bendPoints);
					}
					cyNetwork.addEdge(cyEdge);
					
					arcClass = ArcClazz.fromClazz( arc.getClazz() );
					source = target;
					bendPoints = new ArrayList<Point2D>();
					
				} else if( currentPoint instanceof CustomEdgePoint){
					target = createNode(((CustomEdgePoint) currentPoint).getPort(), cyNetwork);
					
					CyEdge cyEdge = createEdge(source, target, ((CustomEdgePoint) currentPoint).getArcClazz(), sbgnID, bendPoints);
					cyNetwork.addEdge(cyEdge);
					
					source = target;
					bendPoints = new ArrayList<Point2D>();
				}
			}
			
		}catch(Exception e){
			throw new Exception("Arc "+ arc.getId() +": Invalid target or source node.\n\n"+e.getMessage(), e.getCause());
		}
	}
	
	private CyEdge createEdge(CyNode source, CyNode target, ArcClazz arcClazz, String sbgnID, List<Point2D> bendPoints){
		
		String interaction;
		switch(arcClazz){
			case ABSOLUTE_INHIBITION: 
			case INHIBITION: interaction = "-1"; break;
			default : interaction = "1";
		}
		
		CyEdge cyEdge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, interaction, true);
		
		Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), SBGNAttributes.CLASS.getName(), arcClazz.getClazz());
		Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), SBGNAttributes.SBGN_ID.getName(), sbgnID);
		Cytoscape.getEdgeAttributes().setAttribute(cyEdge.getIdentifier(), SBGNAttributes.EDGE_ANCHORS.getName(), CyEdgeAttrUtils.getAnchorAttribute(bendPoints));
		
		return cyEdge;
	}
	
	
	private boolean linksToStart(List<SegmentationPoint> arcPoints, int currentPointIndex){
		
		for( int i=0; i<currentPointIndex; i++){
			if(arcPoints.get(i).getPoint() instanceof Port)
				return false;
			
			if(arcPoints.get(i).getPoint() instanceof Glyph)
				return false;
		}
		
		return true;
	}
	
	private String getNodeByPort(Port port, Map diagramMap){
		// Search Glyphs
		for(Glyph node : diagramMap.getGlyph())
			for(Port nodePort : node.getPort())
				if(nodePort.getId().equals(port.getId()))
					return node.getId();
	
		// Search ArcGroups
		for(Arcgroup arcgroup : diagramMap.getArcgroup()){
			// Search ArcGroup's Glyphs
			for(Glyph node : arcgroup.getGlyph())
				for(Port nodePort : node.getPort())
					if(nodePort.getId().equals(port.getId()))
						return node.getId();
			
			// Search ArcGroup's Arcs
			for(Arc arc : arcgroup.getArc())
				for(Glyph node : arc.getGlyph())
					for(Port nodePort : node.getPort())
						if(nodePort.getId().equals(port.getId()))
							return node.getId();
		}
		
		return null;
	}
	
	
	public void addBendPoint(List<Point2D> list, Object point){
		if( point instanceof Glyph )
			addBendPoint(list, ((Glyph)point));
		if( point instanceof Port )
			addBendPoint(list, ((Port)point));
		if( point instanceof End )
			addBendPoint(list, ((End)point));
		if( point instanceof Start )
			addBendPoint(list, ((Start)point));
		if( point instanceof Next )
			addBendPoint(list, ((Next)point));
	}
	
	public void addBendPoint(List<Point2D> list, Glyph point){
		list.add( new Point2D.Float(point.getBbox().getX(), point.getBbox().getY()) );
	}
	
	public void addBendPoint(List<Point2D> list, Port point){
		list.add( new Point2D.Float(point.getX(), point.getY()) );
	}
	
	public void addBendPoint(List<Point2D> list, Start point){
		list.add( new Point2D.Float(point.getX(), point.getY()) );
	}
	
	public void addBendPoint(List<Point2D> list, End point){
		list.add( new Point2D.Float(point.getX(), point.getY()) );
	}
	
	public void addBendPoint(List<Point2D> list, Next point){
		list.add( new Point2D.Float(point.getX(), point.getY()) );
	}

	// Getters and Setters
	public Sbgn getMap() {
		return map;
	}

	public void setMap(Sbgn map) {
		this.map = map;
	}
	
	
}
