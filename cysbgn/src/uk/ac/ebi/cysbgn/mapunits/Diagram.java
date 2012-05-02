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

import java.util.HashMap;
import java.util.Map;

import org.sbgn.Language;

public class Diagram{

	protected String name;
	protected Language sbgnLanguage;
	protected Map<String,MapNode> mapNodes;
	protected Map<String,MapArc> mapArcs;
	
	public Diagram(){
		this("No named network");
	}
	
	public Diagram(String networkName){
		name = networkName;
		mapNodes = new HashMap<String, MapNode>();
		mapArcs = new HashMap<String, MapArc>();
	}
	
	public Diagram(Diagram diagram){
		this.name = diagram.getName();
		this.mapArcs = new HashMap<String, MapArc>(diagram.getMapArcs());
		this.mapNodes = new HashMap<String, MapNode>(diagram.getMapNodes());
	}
	
	public void addNode(MapNode node){ mapNodes.put(node.getId(), node); }
	public void addArc(MapArc arc){ mapArcs.put(arc.getId(), arc); }
	
	public void removeNode(String nodeID){ mapNodes.remove(nodeID); }
	public void removeArc(String arcsID){ mapArcs.remove(arcsID); }
	
	public void removeAllNodes(){ mapNodes.clear(); }
	public void removeAllArcs(){ mapArcs.clear(); }
	
	public Map<String,MapNode> getMapNodes(){ return mapNodes; }
	public Map<String,MapArc> getMapArcs(){ return mapArcs; }
	
	public String getName(){ return name; }
	public void setName(String networkName){ name = networkName; }
	
	public MapNode getNode(String id){ return mapNodes.get(id); }
	public MapArc getArc(String id){ return mapArcs.get(id); }
	
	public Language getDiagramType(){ return sbgnLanguage; }
	public void setDiagramType(Language sbgnLanguage){ this.sbgnLanguage = sbgnLanguage; }

	public void add(MapNode node) { addNode(node); }
	public void add(MapArc node) { addArc(node); }
	
	public Map<String, MapNode> getNodeList(){ return mapNodes; }
	public Map<String, MapArc> getArcList(){ return mapArcs; }
}
