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
package uk.ac.ebi.cysbgn.mapunits;

import java.awt.Point;
import java.util.ArrayList;

import org.sbgn.ArcClazz;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Port;

/**
 * Represents an arc/edge of the SBGN plug-in diagrams.
 * 
 * @author emanuel
 *
 */
public class MapArc {
	
	protected String id;

	protected ArcClazz type;
	
	protected MapNode sourceNode;

	protected MapNode targetNode;
		
	protected ArrayList<Point> anchors;
	
	protected ArcClazz sourceArrowShape;
	
	protected ArcClazz targetArrowShape;
	
	
	public MapArc(String id){
		this(id, ArcClazz.LOGIC_ARC, null, null);
	}
	
	public MapArc(String id, ArcClazz type, MapNode sourceNode, MapNode targetNode){
		this.id = id;
		this.type = type;
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		anchors = new ArrayList<Point>();
	}

	public void addAnchorPoint(Point anchor){
		anchors.add(anchor);
	}
	
	public void addAnchorPoint(Next next){
		Point anchor = new Point((int)next.getX(), (int)next.getY());
		anchors.add(anchor);
	}
	
	public void addAnchorPoint(Port port){
		Point anchor = new Point((int)port.getX(), (int)port.getY());
		anchors.add(anchor);
	}
	
	public void addAnchorPoint(Start port){
		Point anchor = new Point((int)port.getX(), (int)port.getY());
		anchors.add(anchor);
	}
	
	public void addAnchorPoint(End port){
		Point anchor = new Point((int)port.getX(), (int)port.getY());
		anchors.add(anchor);
	}
	
	// Getters and Setters
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public ArcClazz getType() {
		return type;
	}


	public void setType(ArcClazz type) {
		this.type = type;
	}


	public MapNode getSourceNode() {
		return sourceNode;
	}


	public void setSourceNode(MapNode sourceNode) {
		this.sourceNode = sourceNode;
	}


	public MapNode getTargetNode() {
		return targetNode;
	}


	public void setTargetNode(MapNode targetNode) {
		this.targetNode = targetNode;
	}


	public int getStartX() {
		return this.sourceNode.getX();
	}

	public int getStartY() {
		return this.sourceNode.getY();
	}


	public int getEndX() {
		return this.targetNode.getX();
	}

	public int getEndY() {
		return this.targetNode.getY();
	}


	public ArrayList<Point> getAnchors() {
		return anchors;
	}


	public void setAnchors(ArrayList<Point> anchors) {
		this.anchors = anchors;
	}

	public ArcClazz getSourceArrowShape() {
		return sourceArrowShape;
	}

	public void setSourceArrowShape(ArcClazz sourceArrowShape) {
		this.sourceArrowShape = sourceArrowShape;
	}

	public ArcClazz getTargetArrowShape() {
		return targetArrowShape;
	}

	public void setTargetArrowShape(ArcClazz targetArrowShape) {
		this.targetArrowShape = targetArrowShape;
	}

	
	
}
