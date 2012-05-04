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
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.sbgn.GlyphClazz;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.mapunits.MapNode;
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
public class DrawCostumNodes {

	private CySBGN plugin;
	
	public DrawCostumNodes(CySBGN plugin){
		this.plugin = plugin;
	}
	
	public void drawCustomNodes(CyNetwork cyNetwork, GraphView gview){
		Iterator<CyNode> iter = cyNetwork.nodesIterator();

		while(iter.hasNext()){
			CyNode cyNode = iter.next();

			String nodeClassName = (String) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.CLASS.getName());

			if( !nodeClassName.equals(MapNode.INVISIBLE_NODE) )
			{

				if( !plugin.getDrawCustomNodesShapes() ){
					
					GlyphClazz nodeClass = GlyphClazz.fromClazz(nodeClassName);

					switch(nodeClass){
						case NUCLEIC_ACID_FEATURE : ;
						case PERTURBATION : ;
						case PERTURBING_AGENT : ;
						case TERMINAL : ;
						case TAG : ;
						case ANNOTATION : ;
						case COMPLEX : ;
						case EXISTENCE : ;
						case LOCATION : ;
						case SOURCE_AND_SINK : ;
						case SIMPLE_CHEMICAL : ;
						case MACROMOLECULE : ;
						case NUCLEIC_ACID_FEATURE_MULTIMER : ;
						case MACROMOLECULE_MULTIMER : ;
						case COMPLEX_MULTIMER: ;
						case SIMPLE_CHEMICAL_MULTIMER : 
							NodeView nv = gview.getNodeView(cyNode);
							DNodeView dnv = (DNodeView) nv;
							dnv.removeAllCustomGraphics();
							break;
						default : ;
					}

				}else{
					GlyphClazz nodeClass = GlyphClazz.fromClazz(nodeClassName);

					Boolean hasCloneMarker = false;
					switch(nodeClass){
					case NUCLEIC_ACID_FEATURE : 
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker ){
							drawNucleicAcidCloneMarkerNodes(cyNode, gview);
						}
						else{
							drawNucleicAcidFeature(cyNode, gview); 
						}
						break;
					case PERTURBATION : drawPerturbationNodes(cyNode, gview); break;
					case PERTURBING_AGENT : drawPerturbationNodes(cyNode, gview); break;
					case TERMINAL : drawTerminalandTagNodes(cyNode, gview); break;
					case TAG : drawTerminalandTagNodes(cyNode, gview); break;
					case ANNOTATION : drawAnnotationNodes(cyNode, gview); break;
					case COMPLEX : 
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker ){
							drawComplexCloneMarkerNodes(cyNode, gview);
						}
						else{
							drawComplexNodes(cyNode, gview);
						}
						break;
					case EXISTENCE : drawExistenceNodes(cyNode, gview); break;
					case LOCATION : drawLocationNodes(cyNode, gview); break;
					case SOURCE_AND_SINK : drawSourceSLinkNodes(cyNode, gview); break;
					case SIMPLE_CHEMICAL :
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker ){
							drawSimpleChemicalCloneMarkerNodes(cyNode, gview); 
						}
						break;
					case MACROMOLECULE :
						hasCloneMarker = (Boolean) Cytoscape.getNodeAttributes().getAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_CLONE_MARKER.getName());
						if( hasCloneMarker ){
							drawMacromoleculeCloneMarkerNodes(cyNode, gview); 
						}
						break;
					case NUCLEIC_ACID_FEATURE_MULTIMER : drawNucleicAcidFeatureMultimerNodes(cyNode, gview); break;
					case MACROMOLECULE_MULTIMER : drawMacromoleculeMultimer(cyNode, gview); break;
					case COMPLEX_MULTIMER: drawComplexMultimer(cyNode, gview); break;
					case SIMPLE_CHEMICAL_MULTIMER : drawSimpleChemicalMultimer(cyNode, gview); break;
					default :;
					}
				}
			}
		}

		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
	}
	
	private void drawSimpleChemicalMultimer(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/simple_chemical_multimer.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawComplexMultimer(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/complex_multimer.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawMacromoleculeMultimer(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/macromolecule_multimer.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawSourceSLinkNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/sourceSlink.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawLocationNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/location.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawExistenceNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/existence.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawComplexNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/complex.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawNucleicAcidCloneMarkerNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/cloneNucleicAcidFeatureMarker.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawMacromoleculeCloneMarkerNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/cloneMacromoleculeMarker.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawComplexCloneMarkerNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/cloneComplexMarker.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawSimpleChemicalCloneMarkerNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/cloneCircleMarker.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawAnnotationNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/annotation.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawNucleicAcidFeatureMultimerNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/nucleic_multimer.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawNucleicAcidFeature(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/nucleic_acid_feature.png");
			image = new TexturePaint( ImageIO.read(input), shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawPerturbationNodes(CyNode cyNode, GraphView gview){
		
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			InputStream input = getClass().getResourceAsStream("/perturbation.png");
			image = new TexturePaint( ImageIO.read(input) , shape );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
	
	private void drawTerminalandTagNodes(CyNode cyNode, GraphView gview){
		Double height = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName());
		Double width = Cytoscape.getNodeAttributes().getDoubleAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_WIDTH.getName());
		
		String orientation = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.NODE_ORIENTATION.getName());
		
		Rectangle2D shape = new Rectangle2D.Double(-(width/2), -(height/2), width, height);
		
		NodeView nv = gview.getNodeView(cyNode);
		
		Paint image = null;
		try {
			
			InputStream input = null;
			
			if(orientation.equals(SBGNAttributes.NODE_ORIENTATION_UP.getName()) ){
				input = getClass().getResourceAsStream("/tag_up.png");
			}else if (orientation.equals(SBGNAttributes.NODE_ORIENTATION_DOWN.getName())) {
				input = getClass().getResourceAsStream("/tag_down.png");
			}else if (orientation.equals(SBGNAttributes.NODE_ORIENTATION_RIGHT.getName())) {
				input = getClass().getResourceAsStream("/tag_right.png");
			}else {
				input = getClass().getResourceAsStream("/tag_left.png");
			}
			
			image = new TexturePaint( ImageIO.read(input), shape );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DNodeView dnv = (DNodeView) nv;
		dnv.addCustomGraphic(shape, image, NodeDetails.ANCHOR_CENTER);
	}
}
