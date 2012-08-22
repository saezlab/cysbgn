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
package uk.ac.ebi.cysbgn;

import java.io.File;
import java.util.HashMap;

import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.cyInteraction.SBGNConverter;
import uk.ac.ebi.cysbgn.cyInteraction.SBGNValidator;
import uk.ac.ebi.cysbgn.cyInteraction.SBGNWriter;
import uk.ac.ebi.cysbgn.cyInteraction.SbgnFilter;
import uk.ac.ebi.cysbgn.cyInteraction.SimplifyNetwork;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

/**
 * This is the main class of the SBGNplugin and it extends the CytoscapePlugin
 * class.
 * 
 * All diagrams must be loaded and wrote through the readSBGNDiagram and
 * writeSBGNDiagram methods.
 * 
 * Also the displayDiagram method is the one that should be used to show the
 * SBGN diagrams and Cytoscape networks.
 * 
 * The methods convertX and convertY are responsible to enable the correct
 * calculation of X and Y position of nodes in Cytoscape window.Since, in
 * Cytoscape the origin of the axiss is in the center of the node and in libSBGN
 * the origin is in the left top side of the screen.
 * 
 * @author emanuel
 * 
 */

public class CySBGN extends CytoscapePlugin {

	public static boolean SHOW_LIMITATIONS_PANEL = true;
	public static final String SBGN_MENU = "Plugins.CySBGN";
	public static final String SBGN_EXTENSION = ".sbgn";

	private SBGNVisualStyle visualStyle;
	
	
	/** 
	 * Maps the CyNetwork IDs to the SBGN map imported.
	 * i.e: HashMap< CyNetwork.Identifier, Sbgn_Map >
	 */
	private HashMap<String, Sbgn> mapCyNetworkToSbgn;

	/**
	 * Maps the CyNetwork IDs to the SBGN-ML absolute file path used to import the CyNetwork.
	 * i.e: HashMap< CyNetwork.Identifier, SBGM_ML.Absolute_Path >
	 */
	private HashMap<String, String> mapCyNetworkToSBGNML;
	
	
	public CySBGN() {
		System.out.println("Loading CySBGN...");

		mapCyNetworkToSbgn = new HashMap<String, Sbgn>();
		mapCyNetworkToSBGNML = new HashMap<String, String>();
		
		Cytoscape.getImportHandler().addFilter(new SbgnFilter(this));

		SBGNWriter exportAction = new SBGNWriter(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) exportAction);
		
		SimplifyNetwork analysisAction = new SimplifyNetwork(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) analysisAction);
		
		SBGNValidator validator = new SBGNValidator(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) validator);
		
		SBGNConverter test = new SBGNConverter(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) test);
		
		initialiseVisualStyle();
		
//		JPanel testPanel = new JPanel(new BorderLayout());
//		JLabel selected = new JLabel("Nothing selected");
//		testPanel.add(selected, BorderLayout.CENTER);
//		((CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)).add("CySBGN", testPanel);
		
		// testMethod();
	}

	/**
	 * Adds the visual style to the available styles in VizMapper and also adds the nodes and edges attributes.
	 * 
	 */
	private void initialiseVisualStyle(){
		visualStyle = new SBGNVisualStyle(this);
		visualStyle.applyVisualStyle();
		
		String dummyNodeID = "dummy";
		
		// Add Nodes attributes
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.CLASS.getName(), new String());
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.SBGN_ID.getName(), new String());
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_WIDTH.getName(), new Double(0));
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_HEIGHT.getName(), new Double(0));
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_POS_X.getName(), new Integer(0));
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_POS_Y.getName(), new Integer(0));
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_LABEL.getName(), new String());
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_COMPARTMENT.getName(), new String());
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_ORIENTATION.getName(), new String());
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.NODE_CLONE_MARKER.getName(), new Boolean(true));
		Cytoscape.getNodeAttributes().setAttribute(dummyNodeID, SBGNAttributes.VALIDATION.getName(), new String());
		
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.CLASS.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.SBGN_ID.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_WIDTH.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_HEIGHT.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_POS_X.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_POS_Y.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_LABEL.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_COMPARTMENT.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_ORIENTATION.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.NODE_CLONE_MARKER.getName());
		Cytoscape.getNodeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.VALIDATION.getName());
		
		// Add Edges attributes
		Cytoscape.getEdgeAttributes().setAttribute(dummyNodeID, SBGNAttributes.CLASS.getName(), new String());
		Cytoscape.getEdgeAttributes().setAttribute(dummyNodeID, SBGNAttributes.SBGN_ID.getName(), new String());
		Cytoscape.getEdgeAttributes().setAttribute(dummyNodeID, SBGNAttributes.EDGE_ANCHORS.getName(), new String());
		Cytoscape.getEdgeAttributes().setAttribute(dummyNodeID, SBGNAttributes.VALIDATION.getName(), new String());
		
		Cytoscape.getEdgeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.CLASS.getName());
		Cytoscape.getEdgeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.SBGN_ID.getName());
		Cytoscape.getEdgeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.EDGE_ANCHORS.getName());
		Cytoscape.getEdgeAttributes().deleteAttribute(dummyNodeID, SBGNAttributes.VALIDATION.getName());
		
	}
	
	private void testMethod() {
		File sbgnDiagram = new File("/Users/emanuel/SBGNFiles/AF/submap.sbgn");
		LoadNetworkTask.loadFile(sbgnDiagram, true);
	}


	/**
	 * Function called when reading the .sbgn file to convert the libSBGN
	 * coordinates to Cytoscape coordinates.
	 * 
	 * Converts the libSBGN X coordinate.
	 * 
	 * @param xSbgn
	 * @param width
	 * @return
	 */
	public static int convert_X_coord_SBGN_to_Cytoscape(int xSbgn, double width) {
		int xCy = (int) (xSbgn + (width / 2));
		return xCy;
	}

	/**
	 * Function called when reading the .sbgn file to convert the libSBGN
	 * coordinates to Cytoscape coordinates.
	 * 
	 * Converts the libSBGN Y coordinate.
	 * 
	 * @param ySbgn
	 * @param height
	 * @return
	 */
	public static int convert_Y_coord_SBGN_to_Cytoscape(int ySbgn, double height) {
		int yCy = (int) (ySbgn + (height / 2));
		return yCy;
	}

	public static float convert_X_coord_Cytoscape_to_SBGN(int xCy, double width) {
		float xSbgn = (float) (xCy - (width / 2));
		return xSbgn;
	}

	public static float convert_Y_coord_Cytoscape_to_SBGN(int yCy, double height) {
		float ySbgn = (float) (yCy - (height / 2));
		return ySbgn;
	}
	
	/**
	 * After a successful import the cyNetwork is mapped to the correspondent SBGN map and SBGM_ML file used.  
	 * 
	 * @param cyNetwork
	 * @param sbgnMap
	 * @param absolutePath
	 */
	public void addNetwork(CyNetwork cyNetwork, Sbgn sbgnMap, String absolutePath){
		this.mapCyNetworkToSbgn.put(cyNetwork.getIdentifier(), sbgnMap);
		this.mapCyNetworkToSBGNML.put(cyNetwork.getIdentifier(), absolutePath);
	}
	
	
	public Sbgn getSbgn(String cyNetworkId){
		return mapCyNetworkToSbgn.get(cyNetworkId);
	}
	
	public String getSbgnML(String cyNetworkID){
		return mapCyNetworkToSBGNML.get(cyNetworkID);
	}
}
