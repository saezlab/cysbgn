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

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbgn.SbgnUtil;
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
import uk.ac.ebi.cysbgn.mapunits.Diagram;
import uk.ac.ebi.cysbgn.mapunits.MapArc;
import uk.ac.ebi.cysbgn.mapunits.MapNode;
import uk.ac.ebi.cysbgn.methods.ArcSegmentationAlgorithm;
import uk.ac.ebi.cysbgn.methods.SegmentMethods;
import uk.ac.ebi.cysbgn.methods.SegmentationPoint;


/**
 * AbstarctReader class implements all methods shared among the SBGN readers.  
 * 
 * @author emanuel
 *
 */
public class SBGNReader{

	private static final double ABSOLUTE_STIMULATION_DISTANCE = 11.0;
	private static final double ABSOLUTE_INHIBITION_DISTANCE = 3.0;
	private static final double NECESSARY_STIMULATION_DISTANCE = 13.0;

	private ArcSegmentationAlgorithm nextPortCreator;
	
	private CySBGN plugin;
	
	
	public SBGNReader(CySBGN plugin){
		nextPortCreator = new ArcSegmentationAlgorithm();
		this.plugin = plugin;
	}
	
		
	public Diagram read(String diagramFilePath) throws Exception{
		File file = new File(diagramFilePath);

		Sbgn sbgnMap = SbgnUtil.readFromFile(file);

		Diagram diagram = readMap(sbgnMap.getMap(), diagramFilePath); 

		return diagram;
	}

	/**
	 * Creates the Diagram containing all the information of the libSBGN Map.
	 * 
	 * @param diagramMap
	 * @param diagramName
	 * @return
	 * @throws Exception 
	 */
	protected Diagram readMap(Map diagramMap, String diagramName) throws Exception {
		
		Diagram newDiagram = new Diagram(diagramName);
		
		// Load all nodes
		for(Glyph glyph : diagramMap.getGlyph())
			getNode(glyph, newDiagram);
		
		// Load all Arc's Ports and Glyphs 
		for(Arc arc : diagramMap.getArc()){
			for(Port port : arc.getPort())
				addPort(port, newDiagram);

			for(Glyph glyph : arc.getGlyph())
				getNode(glyph, newDiagram);
		}
		
		// Draw all nodes and ports contained inside a ArcGroup
		for(Arcgroup arcgroup : diagramMap.getArcgroup()){
			for(Glyph glyph : arcgroup.getGlyph())
				getNode(glyph, newDiagram);
			
			for(Arc arc : arcgroup.getArc()){
				for(Port port : arc.getPort())
					addPort(port, newDiagram);

				for(Glyph glyph : arc.getGlyph())
					getNode(glyph, newDiagram);
			}

		}
		
		// Draw all edges
		for(Arc arc : diagramMap.getArc())
			getArc(arc, diagramMap, newDiagram);
		
		// Draw all nodes and edges contained inside a ArcGroup
		for(Arcgroup arcgroup : diagramMap.getArcgroup())			
			for(Arc arc : arcgroup.getArc())
				getArc(arc, diagramMap, newDiagram);
		
		return newDiagram;
	}
	
	
	private void getNode(Glyph glyph, Diagram newDiagram){
		
		GlyphClazz nodeClass = GlyphClazz.fromClazz( glyph.getClazz() );
		
		// Create all inner Glyphs
		for(Glyph innerGlyphs : glyph.getGlyph())
			getNode(innerGlyphs, newDiagram);
		
		MapNode newElement = null;
		
		switch(nodeClass){
			case UNIT_OF_INFORMATION : newElement = readUnitOfInformationNode(glyph); break;
			case ANNOTATION :
				newElement = new MapNode(glyph.getId(), nodeClass);
				
				String annotationArcID = glyph.getId() + "callout";
				String targetID = ((Glyph)glyph.getCallout().getTarget()).getId();
				MapArc annotationArc = new MapArc(annotationArcID, 
						ArcClazz.LOGIC_ARC, 
						newElement, 
						newDiagram.getNode(targetID));
				
				newDiagram.add(annotationArc);
				
				break;
			default : newElement = new MapNode(glyph.getId(), nodeClass);
		}
		
		setNodeLabel(glyph, newElement, nodeClass);
		setNodeBbox(glyph, newElement);
		
		// Add node ports
		for(Port glyphPort : glyph.getPort())
			newElement.getPorts().add(glyphPort);
		
		// Set tag and terminal shapes orientation
		if( nodeClass == GlyphClazz.TERMINAL || nodeClass == GlyphClazz.TAG )
			newElement.setOrientation( glyph.getOrientation() );
		
		if( glyph.getClone() != null )
			newElement.setCloneMarker(true);
		
		newDiagram.add(newElement);
	}
	
	private void getArc(Arc arc, Map diagramMap, Diagram newDiagram) throws Exception{
		try{
			// Generate the arc and/or sub arcs 
			List<MapArc> mapArcs = generateArc(arc, diagramMap, newDiagram);
			
			for(MapArc mapAcr : mapArcs)
				newDiagram.add(mapAcr);
			
		}catch(Exception e){
			throw new Exception("Arc "+ arc.getId() +": Invalid target or source node.\n\n", e);
//			String errorMessage = "Arc "+ arc.getId() +": Invalid target or source node.\n\n";
//			MessagesHandler.showErrorMessageDialog(errorMessage, "SBGN arc import error", e);
//			e.printStackTrace();
		}
	}
	
	private List<MapArc> generateArc(Arc arc, Map diagramMap, Diagram newDiagram){
		
		List<MapArc> arcs = new ArrayList<MapArc>();
		
		MapArc newElement = new MapArc(arc.getId());
		
		ArcClazz arcClass = ArcClazz.fromClazz( arc.getClazz() );
		
		// Get segment list points of the arc
		List<SegmentationPoint> arcPoints = nextPortCreator.generateSortedPointsList(arc.getPort(), arc.getNext(), arc.getGlyph(), arc.getStart(), arc.getEnd());
		
		for(int i=0; i<arcPoints.size(); i++){
			
			Object currentPoint = arcPoints.get(i).getPoint(); 
			
			if( currentPoint instanceof Start){ // If the segment point is instance of Start class add it to the source of the arc
				String sourceNodeID;
				// Source can be a Glyph or a Port 
				if( arc.getSource() instanceof Glyph)
					sourceNodeID = ((Glyph)arc.getSource()).getId();
				else
					sourceNodeID = ((Port)arc.getSource()).getId();
				
				MapNode source = newDiagram.getNode( sourceNodeID );
				
				if(source == null){
					String glyphId = getNodeByPort((Port)arc.getSource(), diagramMap);
					source = newDiagram.getNode(glyphId);
					
					newElement.addAnchorPoint( ((Port)arc.getSource()) );
				}

				// Arcs that point into the same node have anchors in start and end point
				if( arc.getSource().equals(arc.getTarget()) )
					newElement.addAnchorPoint((Start)currentPoint);
				
				newElement.setSourceNode(source);
				
			} else if( currentPoint instanceof Next ){ // Add the anchor point of the segment
				newElement.addAnchorPoint( (Next)currentPoint );
				
			}
			else if( currentPoint instanceof Port ){ // If it's a port a sub arc must be created.
				String targetNodeID = ((Port)currentPoint).getId();
				MapNode target = newDiagram.getNode( targetNodeID );
								
				newElement.setTargetNode(newElement.getSourceNode());
				
				newElement.setSourceNode(target);
				
				newElement.setType(ArcClazz.LOGIC_ARC);
				
				arcs.add(newElement);
				
				newElement = new MapArc(arc.getId() + targetNodeID);
				newElement.setSourceNode(target);
				
			} else if( currentPoint instanceof End){
				String targetNodeID;
				if(arc.getTarget() instanceof Glyph)
					targetNodeID = ((Glyph)arc.getTarget()).getId();
				else
					targetNodeID = ((Port)arc.getTarget()).getId();
				
				MapNode target = newDiagram.getNode( targetNodeID );
				
				if(target == null){
					String glyphID = getNodeByPort((Port)arc.getTarget(), diagramMap);
					target = newDiagram.getNode(glyphID);
					
					newElement.addAnchorPoint( ((Port)arc.getTarget()) );
				}
				
				newElement.setTargetNode(target);
				
				newElement.setType(ArcClazz.fromClazz(arc.getClazz()));
				
				if(arc.getTarget() instanceof Glyph){
					if( ((Glyph)arc.getTarget()).getClazz().equals(GlyphClazz.IMPLICIT_XOR.getClazz()) )
							newElement.setType(ArcClazz.LOGIC_ARC);
				}
				
				// Arcs that point into the same node have anchors in start and end point
				if( arc.getSource().equals(arc.getTarget()) )
					newElement.addAnchorPoint((End)currentPoint);
					
				arcs.add(newElement);
				
			} else { // currentPoint instanceof Glyph
				String targetNodeID = ((Glyph)currentPoint).getId();
				MapNode target = newDiagram.getNode( targetNodeID );
				
				newElement.setTargetNode(newElement.getSourceNode());
				
				newElement.setSourceNode(target);
				
				newElement.setType(ArcClazz.LOGIC_ARC);
				
				arcs.add(newElement);
				
				newElement = new MapArc(arc.getId() + targetNodeID);
				newElement.setSourceNode(target);
			}
		}
		
		switch( arcClass ){
			case NECESSARY_STIMULATION : customEdges( arcs, newDiagram, ArcClazz.INHIBITION, NECESSARY_STIMULATION_DISTANCE); break;
			case ABSOLUTE_INHIBITION : customEdges( arcs, newDiagram, ArcClazz.ABSOLUTE_INHIBITION, ABSOLUTE_INHIBITION_DISTANCE); break;
			case ABSOLUTE_STIMULATION : customEdges( arcs, newDiagram, ArcClazz.STIMULATION, ABSOLUTE_STIMULATION_DISTANCE); break;
			case INTERACTION :
				for(int i=0; i<arcs.size(); i++){
					
					arcs.get(i).setType( ArcClazz.LOGIC_ARC );
					
					if( i==0 ){
						arcs.get(i).setType( ArcClazz.INTERACTION );
						
						if( arcs.get(i).getTargetNode().isInvisible() )
							arcs.get(i).setType(ArcClazz.LOGIC_ARC);
						else
							if( arcs.get(i).getTargetNode().getType().equals( GlyphClazz.INTERACTION ) )
								arcs.get(i).setType( ArcClazz.LOGIC_ARC );
						
					}
					
					if( i==(arcs.size()-1) ){
						arcs.get(i).setType( ArcClazz.INTERACTION );
						
						if( arcs.get(i).getTargetNode().isInvisible() )
							arcs.get(i).setType( ArcClazz.LOGIC_ARC );
						else
							if( arcs.get(i).getTargetNode().getType().equals( GlyphClazz.INTERACTION ) )
								arcs.get(i).setType( ArcClazz.LOGIC_ARC );
					}
				}
				
				break;
			default : ;
		}
		
		return arcs;
	}
	
	
	// Auxiliary methods
	private void customEdges(List<MapArc> arcs, Diagram diagram, ArcClazz auxArcClazz, Double distance){
		
		// Is draw custom edges option is set to false just do nothing
		if(!plugin.getDrawCustomEdgesShapes())
			return;
		
		// Get target arc
		MapArc arc = arcs.get( arcs.size()-1 );
		
		// Create port and calculate port position
		Port auxPort = new Port();		
		auxPort.setId(arc.getId() + "AuxPort");
		calculateAuxPortPosition(arc, auxPort, distance);
		
		MapNode auxTargetNode = addPort(auxPort, diagram);
		
		// Create auxiliary arc
		MapArc auxArc = new MapArc(arc.getId() + "Aux");
		auxArc.setType(auxArcClazz);
		
		// Set anchors
		auxArc.setAnchors(arc.getAnchors());
		
		// Set source
		auxArc.setSourceNode(arc.getSourceNode());
		
		// Set target
		auxArc.setTargetNode(auxTargetNode);
		
		// Re set the source node of the original arc
		arc.setSourceNode(auxTargetNode);
		
		arc.setAnchors(new ArrayList<Point>());
		
		// Add arc to the list
		arcs.add(auxArc);
		
	}
	
	public void calculateAuxPortPosition(MapArc arc, Port auxPort, Double distance){
		int arcLastX;
		int arcLastY;
		
		boolean isAnchor = false;
		
		// Get previous last point of the arc line
		if( arc.getAnchors().size() > 0 ){
			arcLastX = (int) arc.getAnchors().get( (arc.getAnchors().size()-1) ).getX();
			arcLastY = (int) arc.getAnchors().get( (arc.getAnchors().size()-1) ).getY();
			
			isAnchor = true;
		}
		else{
			arcLastX = arc.getStartX();
			arcLastY = arc.getStartY();
		}
		
		// Get arc end point
		int arcEndX = arc.getEndX();
		int arcEndY = arc.getEndY();
		
		// Node boundary rectangle
		Rectangle2D.Double nodeRectangle = new Rectangle2D.Double(
				arc.getTargetNode().getX() - arc.getTargetNode().getWidth()/2, 
				arc.getTargetNode().getY() - arc.getTargetNode().getHeight()/2, 
				arc.getTargetNode().getWidth(), 
				arc.getTargetNode().getHeight());
		
		Vector2D portPosition = SegmentMethods.pointOutNodeBoundary(nodeRectangle, new Vector2D(arcEndX, arcEndY), new Vector2D(arcLastX, arcLastY), distance);

		if( isAnchor ){
			Rectangle2D.Double portBoundaries = new Rectangle2D.Double(portPosition.getX(), portPosition.getY(), 3, 3);
			
			if( portBoundaries.contains(arcLastX, arcLastY) ){
				arc.getAnchors().remove((arc.getAnchors().size()-1));
				
				calculateAuxPortPosition(arc, auxPort, distance);
				
				return;
			}
		}
		
		auxPort.setX( (float) portPosition.getX() );
		auxPort.setY( (float) portPosition.getY() );
	}

	
	private MapNode addPort(Port port, Diagram diagram){
		MapNode mapPort = createInvisibleNode(port.getId());

		int width = 1;
		int height = 1;
		
		mapPort.setX( CySBGN.convert_X_coord_SBGN_to_Cytoscape((int) port.getX(), width) );
		mapPort.setY( CySBGN.convert_Y_coord_SBGN_to_Cytoscape((int) port.getY(), height) );
		
		mapPort.setWidth( width );
		mapPort.setHeight( height );
		
		mapPort.setLabel("");
		
		diagram.add(mapPort);
		
		return mapPort;
	}
	
	private void setNodeBbox(Glyph glyph, MapNode newElement){
		int height = (int) glyph.getBbox().getH();
		int width = (int) glyph.getBbox().getW();
		
		if( height <= 0 ) 
			height = 1;
		if( width <= 0 ) 
			width = 1;
		
		newElement.setHeight(height);
		newElement.setWidth(width);
		
		int xSBGN = (int) glyph.getBbox().getX();
		int ySBGN = (int) glyph.getBbox().getY();
		newElement.setX( CySBGN.convert_X_coord_SBGN_to_Cytoscape(xSBGN, width) );
		newElement.setY( CySBGN.convert_Y_coord_SBGN_to_Cytoscape(ySBGN, height) );
	}
	
	private void setNodeLabel(Glyph glyph, MapNode newElement, GlyphClazz nodeClass){
		
		switch( nodeClass ){
			case AND : newElement.setLabel( "AND" ); break;
			case OR : newElement.setLabel( "OR" ); break;
			case NOT : newElement.setLabel( "NOT" ); break;
			case OMITTED_PROCESS : newElement.setLabel( "\\\\" ); break;
			case UNCERTAIN_PROCESS : newElement.setLabel( "?" ); break;
			case DISSOCIATION : newElement.setLabel( "O" ); break;
			case DELAY : newElement.setLabel( "\u03C4" ); break;
			case STATE_VARIABLE : 
				if( glyph.getState() == null ) return;
				StringBuilder label = null;
				
				if( glyph.getState().getValue() != null ){
					label = new StringBuilder();
					label.append(glyph.getState().getValue());
				}
				
				if( glyph.getState().getVariable() != null ){
					if(label == null) 
						label = new StringBuilder();
					else 
						label.append("@");
					
					label.append(glyph.getState().getVariable());
				}
					
				newElement.setLabel(label.toString()); 
				break;
			case LOCATION : newElement.setLabel(""); break;
			case ASSOCIATION : newElement.setLabel(""); break;
			case PROCESS : newElement.setLabel(""); break;
			case SOURCE_AND_SINK : newElement.setLabel(""); break;
			case OUTCOME : newElement.setLabel(""); break;
			case INTERACTION : newElement.setLabel(""); break;
			case EXISTENCE : newElement.setLabel(""); break;
			default :
				if( glyph.getLabel() != null )
					newElement.setLabel( glyph.getLabel().getText() );
				break;
		}
		
	}
	
	private MapNode readUnitOfInformationNode(Glyph glyph){
		String newNodeClass;
		
		if( glyph.getEntity() != null ){
			newNodeClass = glyph.getEntity().getName();
		}
		else{
			newNodeClass = glyph.getClazz();
		}
		
		return new MapNode(glyph.getId(), GlyphClazz.fromClazz(newNodeClass) );
	}
	
	private MapNode createInvisibleNode(String nodeID){
		MapNode invisibleNode = new MapNode(nodeID, MapNode.INVISIBLE_NODE);
				
		return invisibleNode;
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
	
}
