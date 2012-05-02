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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class SegmentMethods {
	
	public static Vector2D pointOutNodeBoundary(Rectangle2D.Double nodeRectangle, Vector2D start, Vector2D end, double distance){
		// C = A - k ( A - B )
		// k = distance / distance_From_A_to_B
		
		Line arcLine = new Line(start, end);
		
		// Calculate the point here the arc intersects the node boundary rectangle
		Vector2D boundaryPoint = nodeArcIntersectionPoint(nodeRectangle, arcLine);
		
		Double k = distance / boundaryPoint.distance(end);
		
		Double Xc = boundaryPoint.getX() - k * (boundaryPoint.getX() - end.getX());
		Double Xy = boundaryPoint.getY() - k * (boundaryPoint.getY() - end.getY());
		
		return new Vector2D(Xc, Xy);
	}
	
	public static Vector2D calculateLinePointByDistanceToStart(Vector2D A, Vector2D B, double distance){
		// C = A - k ( A - B )
		// k = distance / distance_From_A_to_B
	
		Double k = distance / A.distance(B);
		
		Double Cx = A.getX() - k * (A.getX() - B.getX());
		Double Cy = A.getY() - k * (A.getY() - B.getY());
		
		Vector2D C = new Vector2D(Cx, Cy); 
		
		return C;
	}
	
	public static Vector2D calculateOutSidePointByDistanceToStart(Vector2D C, Vector2D B, double distance){
		// A = ( C - k B ) / ( 1 - k )
		// k = distance / distance_From_A_to_B
		// distance_From_A_to_B = distance + distance_From_C_to_B
	
		Double k = distance / ( C.distance(B) + distance );
		
		Double Ax = ( C.getX() - k * B.getX() ) / ( 1 - distance );
		Double Ay = ( C.getY() - k * B.getY() ) / ( 1 - distance );
		
		Vector2D A = new Vector2D(Ax, Ay);
		
		return A;
	}
	
	
	
	/**
	 * IMPORTANT: the y axis is inverted since the origin of the axis in Cytoscape
	 * is located in the top left corner of the screen.
	 * 
	 * @param nodeRectangle
	 * @param arcLine
	 * @return
	 */
	public static Vector2D nodeArcIntersectionPoint(Rectangle2D.Double nodeRectangle, Line arcLine){
		
		Line topLine = new Line(
				new Vector2D(nodeRectangle.getMinX(), nodeRectangle.getMinY()),
				new Vector2D(nodeRectangle.getMaxX(), nodeRectangle.getMinY()));
		
		Line bottomLine = new Line(
				new Vector2D(nodeRectangle.getMinX(), nodeRectangle.getMaxY()),
				new Vector2D(nodeRectangle.getMaxX(), nodeRectangle.getMaxY()));
		
		Line leftLine = new Line(
				new Vector2D(nodeRectangle.getMinX(), nodeRectangle.getMinY()),
				new Vector2D(nodeRectangle.getMinX(), nodeRectangle.getMaxY()));
		
		Line rightLine = new Line(
				new Vector2D(nodeRectangle.getMaxX(), nodeRectangle.getMinY()),
				new Vector2D(nodeRectangle.getMaxX(), nodeRectangle.getMaxY()));
		
		
		Vector2D topLineIntersection = topLine.intersection(arcLine);
		if( topLineIntersection != null ) return topLineIntersection;
		
		Vector2D bottomLineIntersection = bottomLine.intersection(arcLine);
		if( bottomLineIntersection != null ) return bottomLineIntersection;
		
		Vector2D leftLineIntersection = leftLine.intersection(arcLine);
		if( leftLineIntersection != null ) return leftLineIntersection;
		
		Vector2D rightLineIntersection = rightLine.intersection(arcLine);
		if( rightLineIntersection != null ) return rightLineIntersection;
		
		return null;
	}
	
	
	public static boolean containsPoint(Double squareCenterX, Double squareCenterY, Double squareWidth, Point2D.Double point){
		Rectangle2D.Double square = new Rectangle2D.Double(
				squareCenterX-squareWidth/2, 
				squareCenterY-squareWidth/2, 
				squareWidth, 
				squareWidth);
		
		return square.contains(point);
	}
	
	
}
