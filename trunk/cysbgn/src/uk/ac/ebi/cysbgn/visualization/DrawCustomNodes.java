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
package uk.ac.ebi.cysbgn.visualization;

import giny.view.GraphView;
import giny.view.NodeView;

import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.sbgn.GlyphClazz;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.render.stateful.NodeDetails;
import ding.view.DNodeView;


/**
 * This class is responsible to draw the custom nodes shapes.
 * 
 * @author emanuel
 *
 */
public class DrawCustomNodes {

	private CySBGN plugin;
	
	private static HashMap<String,Paint> customShapesMap;
	

	public DrawCustomNodes(CySBGN plugin){
		this.plugin = plugin;
		this.customShapesMap = new HashMap<String, Paint>();
	}

	public void drawCustomNodes(CyNetwork cyNetwork, GraphView gview){
		Iterator<CyNode> iter = cyNetwork.nodesIterator();

		while(iter.hasNext()){
			CyNode cyNode = iter.next();
			String nodeClassName = (String) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.CLASS.getName());
			
			if( (nodeClassName != null) && (!nodeClassName.equals(SBGNAttributes.CLASS_INVISIBLE.getName())) ){
				GlyphClazz nodeClass = GlyphClazz.fromClazz(nodeClassName);

				Boolean hasCloneMarker = false;
				switch(nodeClass){
					case NUCLEIC_ACID_FEATURE : 
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker )
							drawCustomShape(cyNode, gview, "/cloneNucleicAcidFeatureMarker.png");
						else
							drawCustomShape(cyNode, gview, "/nucleic_acid_feature.png"); 
						break;
					case PERTURBATION : drawCustomShape(cyNode, gview, "/perturbation.png"); break;
					case PERTURBING_AGENT : drawCustomShape(cyNode, gview, "/perturbation.png"); break;
					case TERMINAL : ;
					case TAG : 
						String orientation = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_ORIENTATION.getName());
						
						if(orientation.equals(SBGNAttributes.NODE_ORIENTATION_UP.getName()) )
							drawCustomShape(cyNode, gview, "/tag_up.png");
						else if (orientation.equals(SBGNAttributes.NODE_ORIENTATION_DOWN.getName())) 
							drawCustomShape(cyNode, gview, "/tag_down.png");
						else if (orientation.equals(SBGNAttributes.NODE_ORIENTATION_RIGHT.getName())) 
							drawCustomShape(cyNode, gview, "/tag_right.png");
						else 
							drawCustomShape(cyNode, gview, "/tag_left.png");
						
						break;
					case ANNOTATION : drawCustomShape(cyNode, gview, "/annotation.png"); break;
					case COMPLEX : 
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker )
							drawCustomShape(cyNode, gview, "/cloneComplexMarker.png");
						else
							drawCustomShape(cyNode, gview, "/complex.png");
						break;
					case EXISTENCE : drawCustomShape(cyNode, gview, "/existence.png"); break;
					case LOCATION : drawCustomShape(cyNode, gview, "/location.png"); break;
					case SOURCE_AND_SINK : drawCustomShape(cyNode, gview, "/sourceSlink.png"); break;
					case SIMPLE_CHEMICAL :
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker )
							drawCustomShape(cyNode, gview, "/cloneCircleMarker.png"); 
						break;
					case MACROMOLECULE :
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker )
							drawCustomShape(cyNode, gview, "/cloneMacromoleculeMarker.png");
						
						break;
					case NUCLEIC_ACID_FEATURE_MULTIMER : drawCustomShape(cyNode, gview, "/nucleic_multimer.png"); break;
					case MACROMOLECULE_MULTIMER : drawCustomShape(cyNode, gview, "/macromolecule_multimer.png"); break;
					case COMPLEX_MULTIMER: drawCustomShape(cyNode, gview, "/complex_multimer.png"); break;
					case SIMPLE_CHEMICAL_MULTIMER : drawCustomShape(cyNode, gview, "/simple_chemical_multimer.png"); break;
					default :;
				}
			}
		}

		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
	}

	
	// Drawing methods
	private void drawCustomShape(CyNode cyNode, GraphView gview, String nodeShapePath){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());

		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);

		NodeView nv = gview.getNodeView(cyNode);

		Paint image = customShapesMap.get(nodeShapePath);
		if( image == null ){
			try {
				InputStream input = getClass().getResourceAsStream(nodeShapePath);
				image = new TexturePaint( ImageIO.read(input), shape );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			customShapesMap.put(nodeShapePath, image);
		}

		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);

	}

}
