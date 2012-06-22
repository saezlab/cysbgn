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

import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Port;

public class SegmentationPoint implements Comparable<SegmentationPoint>{
	
	private Object point;
	private Double distance;

	private PointType pointType;
	
	
	public SegmentationPoint(Object point){
		this(point, 0.0);
	}
	
	public SegmentationPoint(Object point, Double distance){
		this.point = point;
		this.distance = distance;
		
		if( point instanceof Start)
			pointType = PointType.START;
		else if( point instanceof Next)
			pointType = PointType.NEXT;
		else if( point instanceof Port)
			pointType = PointType.PORT;
		else if( point instanceof End)
			pointType = PointType.END;
		else if( point instanceof Glyph)
			pointType = PointType.GLYPH;
		else
			pointType = PointType.CUSTOM;
	}

	@Override
	public int compareTo(SegmentationPoint oneDimensionPoint) {
		if( distance > oneDimensionPoint.getDistance() )
			return 1;
		if( distance < oneDimensionPoint.getDistance() )
			return -1;
		
		return 0;
	}
	
	public double calculateDistance(SegmentationPoint point){
	
		Point2D.Double startPoint = new Point2D.Double(this.getX(), this.getY());
		Point2D.Double endPoint = new Point2D.Double(point.getX(), point.getY());
		
		return startPoint.distance(endPoint);
	}
	
	public Boolean intersectsLine(SegmentationPoint startLinePoint, SegmentationPoint endLinePoint){
		Double distance = distanceFromLine(startLinePoint, endLinePoint);

		if( distance == 0 )
			return true;
		else
			return false;
	}
	
	public Double distanceFromLine(SegmentationPoint startLinePoint, SegmentationPoint endLinePoint){
		Line2D.Float line = new Line2D.Float(startLinePoint.getX(), startLinePoint.getY(), endLinePoint.getX(), endLinePoint.getY());
		
		Point2D.Float point = new Point2D.Float(this.getX(), this.getY());
		
		Double distance = line.ptLineDist(point);
		
		return distance;
	}
	
	/**
	 * Calculates the distance based on a previous point.
	 * 
	 * @param previousPoint
	 */
	public void setDistanceFromPoint(SegmentationPoint previousPoint){
		double distance = previousPoint.calculateDistance(this);
		distance += previousPoint.getDistance();

		this.setDistance(distance);
	}
	
	public boolean isStartPoint(){
		if( pointType == PointType.START )
			return true;
		else
			return false;
	}
	
	public boolean isNextPoint(){
		if( pointType == PointType.NEXT )
			return true;
		else
			return false;
	}
	
	public boolean isPortPoint(){
		if( pointType == PointType.PORT )
			return true;
		else
			return false;
	}
	
	public boolean isEndPoint(){
		if( pointType == PointType.END )
			return true;
		else
			return false;
	}
	
	public boolean isGlyphPoint(){
		if( pointType == PointType.GLYPH )
			return true;
		else
			return false;
	}
	
	public boolean isCustomEgdePoint(){
		if( pointType == PointType.CUSTOM )
			return true;
		else
			return false;
	}

	// Getters and Setters
	public float getX(){
		switch(pointType){
			case START : return ((Start) point).getX();
			case NEXT : return ((Next) point).getX();
			case PORT : return ((Port) point).getX();
			case GLYPH : return ((Glyph) point).getBbox().getX();
			case CUSTOM : return ((CustomEdgePoint) point).getX();
			default : return ((End) point).getX();
		}
	}
	
	public float getY(){
		switch(pointType){
			case START : return ((Start) point).getY();
			case NEXT : return ((Next) point).getY();
			case PORT : return ((Port) point).getY();
			case GLYPH : return ((Glyph) point).getBbox().getY();
			case CUSTOM : return ((CustomEdgePoint) point).getY();
			default : return ((End) point).getY();
		}
	}
	
	public Object getPoint() {
		return point;
	}

	public void setPoint(Object point) {
		this.point = point;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}
	
	@Override
	public String toString(){
		StringBuilder output = new StringBuilder();
		
		switch(pointType){
			case START : output.append("Start"); break;
			case NEXT : output.append("Next"); break;
			case PORT : output.append("Port"); break;
			case GLYPH : output.append("Glyph"); break;
			case CUSTOM : output.append("Custom"); break;
			default : output.append("End"); break;
		}
		
		output.append("[");
		output.append(getDistance());
		output.append("]");
		
		return output.toString();
	}

	// Enum type of the point
	private enum PointType{
		START,
		END,
		PORT,
		NEXT,
		CUSTOM,
		GLYPH;
	}
	
}
