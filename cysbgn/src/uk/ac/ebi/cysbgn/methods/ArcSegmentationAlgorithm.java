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

import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Port;

public class ArcSegmentationAlgorithm {

	private static final Double SAFETY_DISTANCE_NODE_ARC_INTERSECTION = 10.0;
	
	public List<SegmentationPoint> generateSortedPointsList(List<Port> ports, List<Next> nexts, List<Glyph> glyphs, Start startPoint, End endPoint){
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
		
		return segmentPoints;
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
