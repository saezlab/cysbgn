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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.mapunits.MapNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyDependency;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * This class encapsulates all the methods used to apply the SBGN visual style to the Cytoscape networks. 
 * It is also responsible to store all the mapping information between the Cytoscape forms and the SBGN syntax. 
 * 
 * @author emanuel
 *
 */

@SuppressWarnings("deprecation")
public class SBGNVisualStyle extends VisualStyle{

	public static final String NAME = "SBGN";
	
	private NodeAppearanceCalculator nac;
	private EdgeAppearanceCalculator eac;
	private GlobalAppearanceCalculator gac;
	
	private DrawCostumNodes costumNodeShapes;
	
	/**
	 * Maps the SBGN target arrow shape to the Cytoscape target arrow shape.
	 */
	private static final Map<String, ArrowShape> targetArrowShapeMap;
	static{
		targetArrowShapeMap = new HashMap<String, ArrowShape>();
		
		targetArrowShapeMap.put(ArcClazz.POSITIVE_INFLUENCE.getClazz(), ArrowShape.DELTA);
		targetArrowShapeMap.put(ArcClazz.NEGATIVE_INFLUENCE.getClazz(), ArrowShape.T);
		targetArrowShapeMap.put(ArcClazz.UNKNOWN_INFLUENCE.getClazz(), ArrowShape.DIAMOND);
		targetArrowShapeMap.put(ArcClazz.NECESSARY_STIMULATION.getClazz(), ArrowShape.DELTA);
		targetArrowShapeMap.put(ArcClazz.LOGIC_ARC.getClazz(), ArrowShape.NONE);
		targetArrowShapeMap.put(ArcClazz.PRODUCTION.getClazz(), ArrowShape.DELTA);
		targetArrowShapeMap.put(ArcClazz.CONSUMPTION.getClazz(), ArrowShape.NONE);
		targetArrowShapeMap.put(ArcClazz.CATALYSIS.getClazz(), ArrowShape.CIRCLE);
		targetArrowShapeMap.put(ArcClazz.MODULATION.getClazz(), ArrowShape.DIAMOND);
		targetArrowShapeMap.put(ArcClazz.STIMULATION.getClazz(), ArrowShape.DELTA);
		targetArrowShapeMap.put(ArcClazz.INHIBITION.getClazz(), ArrowShape.T);
		targetArrowShapeMap.put(ArcClazz.ASSIGNMENT.getClazz(), ArrowShape.ARROW);
		targetArrowShapeMap.put(ArcClazz.INTERACTION.getClazz(), ArrowShape.ARROW);
		targetArrowShapeMap.put(ArcClazz.ABSOLUTE_INHIBITION.getClazz(), ArrowShape.T);
		targetArrowShapeMap.put(ArcClazz.ABSOLUTE_STIMULATION.getClazz(), ArrowShape.DELTA);
	}
	
	/**
	 * Maps the SBGN node shapes to the Cytoscape node shapes.
	 */
	private static final Map<String, NodeShape> nodeShapeMap;
	static{
		nodeShapeMap = new HashMap<String, NodeShape>();
		
		nodeShapeMap.put(GlyphClazz.STATE_VARIABLE.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.BIOLOGICAL_ACTIVITY.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.PHENOTYPE.getClazz(), NodeShape.HEXAGON);
		nodeShapeMap.put(GlyphClazz.MACROMOLECULE_MULTIMER.getClazz(), NodeShape.ROUND_RECT);
		nodeShapeMap.put(GlyphClazz.SIMPLE_CHEMICAL_MULTIMER.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.COMPLEX_MULTIMER.getClazz(), NodeShape.OCTAGON);
		nodeShapeMap.put(GlyphClazz.NUCLEIC_ACID_FEATURE_MULTIMER.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.UNSPECIFIED_ENTITY.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.AND.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.OR.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.NOT.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.DELAY.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.UNIT_OF_INFORMATION.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.TERMINAL.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.SIMPLE_CHEMICAL.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.MACROMOLECULE.getClazz(), NodeShape.ROUND_RECT);
		nodeShapeMap.put(GlyphClazz.NUCLEIC_ACID_FEATURE.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.COMPLEX.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.SOURCE_AND_SINK.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.PERTURBATION.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.PERTURBING_AGENT.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.SUBMAP.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.TAG.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.PROCESS.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.OMITTED_PROCESS.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.UNCERTAIN_PROCESS.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.ASSOCIATION.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.DISSOCIATION.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.STOICHIOMETRY.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.ENTITY.getClazz(), NodeShape.ROUND_RECT);
		nodeShapeMap.put(GlyphClazz.OUTCOME.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.OBSERVABLE.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.INTERACTION.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.ANNOTATION.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.VARIABLE_VALUE.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.IMPLICIT_XOR.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.EXISTENCE.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.LOCATION.getClazz(), NodeShape.ELLIPSE);
		nodeShapeMap.put(GlyphClazz.CARDINALITY.getClazz(), NodeShape.RECT);
		nodeShapeMap.put(GlyphClazz.COMPARTMENT.getClazz(), NodeShape.ROUND_RECT);
	}
	
	
	private Boolean drawCustomNodesShapes = true;
	private Boolean drawCustomEdgesShapes = true;
	
	/**
	 * Class constructor.
	 * 
	 */
	public SBGNVisualStyle(CySBGN plugin) {
		super(NAME);
		costumNodeShapes = new DrawCostumNodes(plugin);
	}
	
	
	/**
	 * Creates SBGN visual style.
	 * 
	 * @param cyNetwork
	 * @return
	 */
	public VisualStyle init(CyNetwork cyNetwork){
		nac = new NodeAppearanceCalculator();
		eac = new EdgeAppearanceCalculator();
		gac = new GlobalAppearanceCalculator();

		// Features
		setDefaultAppearanceAspects();

		// Node features
		if( drawCustomNodesShapes )
			setNodeBorderOpacity(cyNetwork);
		setNodeOpacity(cyNetwork);
		setNodeLabels();
		setNodeWidth();
		setNodeHeight();
		setNodeShape(cyNetwork);
		setNodeFontSize(cyNetwork);
		setNodeColour(cyNetwork);
		setNodeBorderWidthPosition(cyNetwork);
//		setNodeLabelPosition(cyNetwork); NOT WORKING: Cytoscape bug, no effect is taken

		// Edge features
		setEdgeTargetArrowShape(cyNetwork);
//		setEdgeSourceArrowShape(cyNetwork);
		
		VisualStyle sbgnVisualStyle = new VisualStyle(NAME, nac, eac, gac);
		sbgnVisualStyle.getDependency().set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED, false);
		return sbgnVisualStyle;
	}

	// DEFAULT ASPECTS OF THE VISUAL STYLE
	private void setDefaultAppearanceAspects(){

		// Global default aspects
		gac.setDefaultBackgroundColor(Color.WHITE);
		
		// Nodes default aspects
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ROUND_RECT);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, Color.WHITE);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FONT_SIZE, new Integer(11));

		// Edges default aspects
		eac.getDefaultAppearance().set(VisualPropertyType.EDGE_COLOR,  Color.BLACK);
	}

	// Nodes attributes methods
	private void setNodeBorderWidthPosition(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_LINE_WIDTH), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(GlyphClazz.COMPARTMENT.getClazz(), new Double(6));
		Calculator nodeBorderOpacity = new BasicCalculator("Node border width calculator", disMapping, VisualPropertyType.NODE_LINE_WIDTH);
		nac.setCalculator(nodeBorderOpacity);
	}
	
	private void setNodeLabelPosition(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_LABEL_POSITION), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(GlyphClazz.COMPARTMENT.getClazz(), VisualPropertyType.NODE_LABEL_POSITION);
		
		Calculator nodeBorderOpacity = new BasicCalculator("Node label position calculator", disMapping, VisualPropertyType.NODE_LABEL_POSITION);
		nac.setCalculator(nodeBorderOpacity);
	}
	
	private void setNodeColour(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_FILL_COLOR), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(GlyphClazz.OUTCOME.getClazz(), Color.BLACK);
		disMapping.putMapValue(GlyphClazz.ASSOCIATION.getClazz(), Color.BLACK);
		
		Calculator nodeBorderOpacity = new BasicCalculator("Node fill colour calculator", disMapping, VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(nodeBorderOpacity);
	}
	
	private void setNodeBorderOpacity(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(new Integer(255), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(MapNode.INVISIBLE_NODE, new Double(0));
		disMapping.putMapValue(GlyphClazz.NUCLEIC_ACID_FEATURE.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.PERTURBATION.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.TERMINAL.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.PERTURBING_AGENT.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.TAG.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.ANNOTATION.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.COMPLEX.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.EXISTENCE.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.LOCATION.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.SOURCE_AND_SINK.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.SIMPLE_CHEMICAL_MULTIMER.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.NUCLEIC_ACID_FEATURE_MULTIMER.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.COMPLEX_MULTIMER.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.MACROMOLECULE_MULTIMER.getClazz(), new Double(0));
		
		Calculator nodeBorderOpacity = new BasicCalculator("Node border opacity", disMapping, VisualPropertyType.NODE_BORDER_OPACITY);
		nac.setCalculator(nodeBorderOpacity);
	}
	
	private void setNodeFontSize(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(new Integer(12), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(GlyphClazz.DELAY.getClazz(), new Double(22));
		
		Calculator nodeFontSizeCalculator = new BasicCalculator("Node Font Size Calculator", disMapping, VisualPropertyType.NODE_FONT_SIZE);
		nac.setCalculator(nodeFontSizeCalculator);
	}

	private void setNodeOpacity(CyNetwork cyNetwork){
		DiscreteMapping disMapping = new DiscreteMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_OPACITY), ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		disMapping.putMapValue(GlyphClazz.COMPARTMENT.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.COMPLEX.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.COMPLEX_MULTIMER.getClazz(), new Double(0));
		disMapping.putMapValue(GlyphClazz.SUBMAP.getClazz(), new Double(0));
		
		Calculator opacityCalculator = new BasicCalculator("Node Opacity calculator", disMapping, VisualPropertyType.NODE_OPACITY);
		nac.setCalculator(opacityCalculator);
	}
	
	private void setNodeLabels(){
		PassThroughMapping pmLabel = new PassThroughMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_LABEL), SBGNAttributes.NODE_LABEL.getName());
		Calculator labelCalculator = new BasicCalculator("Node Label Calculator", pmLabel, VisualPropertyType.NODE_LABEL);
		nac.setCalculator(labelCalculator);
	}
	
	private void setNodeWidth(){
		PassThroughMapping pmWidth = new PassThroughMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_WIDTH), SBGNAttributes.NODE_WIDTH.getName());
		Calculator widthCalculator = new BasicCalculator("Node Width Calculator", pmWidth, VisualPropertyType.NODE_WIDTH);
		nac.setCalculator(widthCalculator);
	}
	
	private void setNodeHeight(){
		PassThroughMapping pmHeight = new PassThroughMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_HEIGHT), SBGNAttributes.NODE_HEIGHT.getName());
		Calculator heightCalculator = new BasicCalculator("Node Height Calculator", pmHeight, VisualPropertyType.NODE_HEIGHT);
		nac.setCalculator(heightCalculator);
	}
		
	private void setNodeShape(CyNetwork cyNetwork){
		DiscreteMapping nodeShapeMapping = new DiscreteMapping(nac.getDefaultAppearance().get(VisualPropertyType.NODE_SHAPE), ObjectMapping.NODE_MAPPING);
		
		nodeShapeMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		nodeShapeMapping.putAll(nodeShapeMap);
		
		Calculator nodeShapeCalculator = new BasicCalculator("Node Shape Calculator", nodeShapeMapping, VisualPropertyType.NODE_SHAPE);
		nac.setCalculator(nodeShapeCalculator);
	}
	
	// Edges attributes methods
	private void setEdgeTargetArrowShape(CyNetwork cyNetwork){
		DiscreteMapping targetArrowMapping = new DiscreteMapping( eac.getDefaultAppearance().get(VisualPropertyType.EDGE_TGTARROW_SHAPE), ObjectMapping.EDGE_MAPPING);
		
		targetArrowMapping.setControllingAttributeName(SBGNAttributes.CLASS.getName(), cyNetwork, false);
		targetArrowMapping.putAll(targetArrowShapeMap);
		
		Calculator targetArrowCalculator = new BasicCalculator("Target Arrow Calculator", targetArrowMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
		eac.setCalculator(targetArrowCalculator);
	}
	
	// Apply style methods
	public void applyVisualStyle(){
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();

        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        CalculatorCatalog catalog = manager.getCalculatorCatalog();

        VisualStyle vs = catalog.getVisualStyle(NAME);
        if (vs == null) {
        	vs = init(network);
        	catalog.addVisualStyle(vs);
        }

        networkView.setVisualStyle(vs.getName());
        manager.setVisualStyle(vs);
        
        costumNodeShapes.drawCustomNodes(network, networkView);
	}
	
	public void refreshVisualStyle(CyNetwork network){
		CyNetworkView cyNetworkView = Cytoscape.getNetworkView(network.getIdentifier());

		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		catalog.removeVisualStyle(NAME);

		VisualStyle	vs = init(network);
		catalog.addVisualStyle(vs);

		cyNetworkView.setVisualStyle(vs.getName());
		manager.setVisualStyle(vs);

		costumNodeShapes.drawCustomNodes(network, cyNetworkView);
	}


	// Getters and setters
	
	public Boolean getDrawCustomNodesShapes() {
		return drawCustomNodesShapes;
	}


	public void setDrawCustomNodesShapes(Boolean drawCustomNodesShapes) {
		this.drawCustomNodesShapes = drawCustomNodesShapes;
	}


	public Boolean getDrawCustomEdgesShapes() {
		return drawCustomEdgesShapes;
	}


	public void setDrawCustomEdgesShapes(Boolean drawCustomEdgesShapes) {
		this.drawCustomEdgesShapes = drawCustomEdgesShapes;
	}
	
	
}
