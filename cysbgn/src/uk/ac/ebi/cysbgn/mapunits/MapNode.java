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

import java.util.ArrayList;
import java.util.List;

import org.sbgn.GlyphClazz;
import org.sbgn.bindings.Port;

/**
 * Represents the SBGN plug-in map node.
 * 
 * @author emanuel
 *
 */
public class MapNode{

	public static final String INVISIBLE_NODE = "invisible";
	
	protected String id;

	protected GlyphClazz type;

	protected String label;
	
	protected int width = 1;

	protected int height = 1;
	
	protected int x = 0;

	protected int y = 0;
	
	protected String orientation;
	
	protected boolean cloneMarker = false;
	
	protected List<Port> ports;
	
	protected boolean isInvisible = false;
	
	
	public MapNode(String id, GlyphClazz type){
		this.id = id;
		this.type = type;
		
		this.ports = new ArrayList<Port>();
	}
	
	
	public MapNode(String id, String invisibleNode){
		this.id = id;
		this.width = 1;
		this.height = 1;
		
		this.type = null;
		this.isInvisible = true;
		this.ports = new ArrayList<Port>();
	}


	// Getters and Setters
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public GlyphClazz getType() {
		return type;
	}


	public void setType(GlyphClazz type) {
		this.type = type;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public int getWidth() {
		return width;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public int getHeight() {
		return height;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public String getOrientation() {
		return orientation;
	}


	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}


	public Boolean getCloneMarker() {
		return cloneMarker;
	}


	public void setCloneMarker(Boolean clone) {
		this.cloneMarker = clone;
	}


	public List<Port> getPorts() {
		return ports;
	}


	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}


	public boolean isInvisible() {
		return isInvisible;
	}

	public void setInvisible(boolean isInvisible) {
		this.isInvisible = isInvisible;
	}
	
	
}
