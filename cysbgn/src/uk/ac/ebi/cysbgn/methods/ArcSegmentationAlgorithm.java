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
package uk.ac.ebi.cysbgn.methods;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.sbgn.ArcClazz;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Port;

public class ArcSegmentationAlgorithm {

	
	private static final double ABSOLUTE_STIMULATION_DISTANCE = 11.0;
	private static final double ABSOLUTE_INHIBITION_DISTANCE = 0.5;
	private static final double NECESSARY_STIMULATION_DISTANCE = 13.0;
	private static final double SAFETY_DISTANCE_NODE_ARC_INTERSECTION = 10.0;
	
	
	public List<SegmentationPoint> generateSortedPointsList(Arc arc, boolean isAnalysisMethod){
		List<Port> ports = arc.getPort(); 
		List<Next> nexts = arc.getNext();
		List<Glyph> glyphs = arc.getGlyph();
		Start startPoint = arc.getStart();
		End endPoint = arc.getEnd();
		
		List<SegmentationPoint> segmentPoints = new ArrayList<SegmentationPoint>();

		// Add starting point
		SegmentationPoint start = new SegmentationPoint(startPoint, 0.0) ;
		segmentPoints.add(start);
		
		// Add next points following the file order
		for(Next next : nexts){
			SegmentationPoint currentPoint = new SegmentationPoint(next);
			SegmentationPoint previousPoint = segmentPoints.get(segmentPoints.size()-1); 

			currentPoint.setDistanceFromPoint(previousPoint);

			segmentPoints.add(currentPoint);
		}

		// Add ending point
		SegmentationPoint end = new SegmentationPoint(endPoint);
		SegmentationPoint previousPoint = segmentPoints.get(segmentPoints.size()-1);

		end.setDistanceFromPoint(previousPoint);

		segmentPoints.add(end);

		// Add the arcs ports that intersect with the segments
		for(Port port : ports){
			SegmentationPoint portPoint = new SegmentationPoint(port);
			checkPortIntersection(segmentPoints, portPoint);
		}
		
		// Sort the segment list by distance to the origin
		Collections.sort(segmentPoints);
		
		// Add the arcs glyphs that intersect with the segments
		for(Glyph glyph : glyphs){
			SegmentationPoint glyphPoint = new SegmentationPoint(glyph);
			checkGlyphIntersection(segmentPoints, glyphPoint);
		}

		// Sort the segment list by distance to the origin
		Collections.sort(segmentPoints);
		
		// Add custom edges
		
		if( !isAnalysisMethod ){
			ArcClazz arcClass = ArcClazz.fromClazz(arc.getClazz());
	
			switch( arcClass ){
				case NECESSARY_STIMULATION : 
					customEdges(arc, segmentPoints, ArcClazz.INHIBITION, NECESSARY_STIMULATION_DISTANCE); break;
				case ABSOLUTE_INHIBITION : 
					customEdges(arc, segmentPoints, ArcClazz.INHIBITION, ABSOLUTE_INHIBITION_DISTANCE); break;
				case ABSOLUTE_STIMULATION : 
					customEdges(arc, segmentPoints, ArcClazz.STIMULATION, ABSOLUTE_STIMULATION_DISTANCE); break;
				default:;
			}
			
			Collections.sort(segmentPoints);
		}
		return segmentPoints;
	}
	
	
	private void customEdges(Arc arc, List<SegmentationPoint> segmentationPoints, ArcClazz auxArcClazz, Double distance){
		
		// Get target arc
		SegmentationPoint arcTarget = segmentationPoints.get( segmentationPoints.size()-1 );
		
		// Create port and calculate port position
		CustomEdgePoint customEdgePoint = new CustomEdgePoint(auxArcClazz);		
		customEdgePoint.setID( arc.getId() + "CE" );
		calculateAuxPortPosition(segmentationPoints, customEdgePoint, distance);
		
		SegmentationPoint customPoint = new SegmentationPoint(customEdgePoint);
		customPoint.setDistanceFromPoint(segmentationPoints.get(segmentationPoints.size()-2));

		segmentationPoints.add(customPoint);
		
	}
	
	public void calculateAuxPortPosition(List<SegmentationPoint> points, CustomEdgePoint customPoint, Double distance){
		
//		boolean isAnchor = false;
//		if( points.size() > 2 )	
//			isAnchor = true;
		
		// Get point before last
		int pointBeforeLastX = (int) points.get( points.size()-2 ).getX();
		int pointBeforeLastY = (int) points.get( points.size()-2 ).getY();
		
		// Get last point
		int lastPointX = (int) points.get( points.size()-1 ).getX();
		int lastPointY = (int) points.get( points.size()-1 ).getY();
		
		Vector2D portPosition = SegmentMethods.calculateLinePointByDistanceToStart(new Vector2D(lastPointX, lastPointY), new Vector2D(pointBeforeLastX, pointBeforeLastY), distance);

//		if( isAnchor ){
//			Rectangle2D.Double portBoundaries = new Rectangle2D.Double(portPosition.getX(), portPosition.getY(), 3, 3);
//			
//			if( portBoundaries.contains(arcLastX, arcLastY) ){
//				edgeAnchors.remove( (edgeAnchors.size()-1) );
//				
//				Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), SBGNAttributes.EDGE_ANCHORS.getName(), CyEdgeAttrUtils.getAnchorAttribute(edgeAnchors));
//				
//				calculateAuxPortPosition(arc, edge, auxPort, distance);
//				
//				return;
//			}
//		}
		
		customPoint.setX( (float) portPosition.getX() );
		customPoint.setY( (float) portPosition.getY() );
	}
	
	public void checkGlyphIntersection(List<SegmentationPoint> segmentPoints, SegmentationPoint point){
		
		for(int i=0; i<(segmentPoints.size()-1); i++){
			SegmentationPoint startSegmentPoint = segmentPoints.get(i);
			SegmentationPoint endSegmentPoint = segmentPoints.get((i+1));
			
			
			Rectangle2D.Double glyphBBox = new Rectangle2D.Double(
					((Glyph)point.getPoint()).getBbox().getX(), 
					((Glyph)point.getPoint()).getBbox().getY(), 
					((Glyph)point.getPoint()).getBbox().getW() + SAFETY_DISTANCE_NODE_ARC_INTERSECTION, 
					((Glyph)point.getPoint()).getBbox().getH() + SAFETY_DISTANCE_NODE_ARC_INTERSECTION);
			
			Point2D.Double startPoint = new Point2D.Double(startSegmentPoint.getX(), startSegmentPoint.getY());
			Point2D.Double endPoint = new Point2D.Double(endSegmentPoint.getX(), endSegmentPoint.getY());
			Line2D.Double segmentLine = new Line2D.Double(startPoint, endPoint);
			
			
			if( segmentLine.intersects(glyphBBox) ){
				
				double portDistance = point.calculateDistance(startSegmentPoint);
				portDistance += startSegmentPoint.getDistance();
				
				point.setDistance(portDistance);
				
				segmentPoints.add(point);
				
				return;
			}
		}
		
	}
	
	public void checkPortIntersection(List<SegmentationPoint> segmentPoints, SegmentationPoint point){
		
		for(int i=0; i<(segmentPoints.size()-1); i++){
			SegmentationPoint startSegmentPoint = segmentPoints.get(i);
			SegmentationPoint endSegmentPoint = segmentPoints.get((i+1));
			
			if( point.intersectsLine(startSegmentPoint, endSegmentPoint ) ){
				
				double portDistance = point.calculateDistance(startSegmentPoint);
				portDistance += startSegmentPoint.getDistance();
				
				point.setDistance(portDistance);
				
				segmentPoints.add(point);
				
				return;
			}
		}
		
	}
	
}
