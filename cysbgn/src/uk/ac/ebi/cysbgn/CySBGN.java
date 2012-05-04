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

import uk.ac.ebi.cysbgn.cyInteraction.ExportAction;
import uk.ac.ebi.cysbgn.cyInteraction.SBGNConverter;
import uk.ac.ebi.cysbgn.cyInteraction.SbgnFilter;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.io.readers.SBGNReader;
import uk.ac.ebi.cysbgn.io.writers.SBGNWriter;
import uk.ac.ebi.cysbgn.mapunits.Diagram;
import uk.ac.ebi.cysbgn.menu.CustomEdgesOption;
import uk.ac.ebi.cysbgn.menu.NodeShapesOption;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;


/**
 * This is the main class of the SBGNplugin and it extends the CytoscapePlugin class.  
 * 
 * All diagrams must be loaded and wrote through the readSBGNDiagram and writeSBGNDiagram methods. 
 * 
 * Also the displayDiagram method is the one that should be used to show the SBGN diagrams and Cytoscape networks.
 * 
 * The methods convertX and convertY are responsible to enable the correct calculation of X and Y position 
 * of nodes in Cytoscape window.Since, in Cytoscape the origin of the axiss is in the center of the node and
 * in libSBGN the origin is in the left top side of the screen.
 * 
 * @author emanuel
 *
 */

public class CySBGN extends CytoscapePlugin {

	public static final String SBGN_MENU = "Plugins.CySBGN";
	public static final String SBGN_EXTENSION = ".sbgn";
	
	private SBGNVisualStyle visualStyle;
	private SBGNConverter converter; 
	
	
	public CySBGN(){
		System.out.println("Loading CySBGN...");
		
		visualStyle = new SBGNVisualStyle(this);
		converter = new SBGNConverter();
		
		Cytoscape.getImportHandler().addFilter(new SbgnFilter(this));

		NodeShapesOption nodeShapesMenuAction = new NodeShapesOption(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction)nodeShapesMenuAction);
		
		CustomEdgesOption edgesShapesMenuAction = new CustomEdgesOption(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction)edgesShapesMenuAction);

		ExportAction exportAction = new ExportAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction)exportAction);
//		testMethod();
	}
	
	
	private void testMethod(){
		File sbgnDiagram = new File("/Users/emanuel/SBGNFiles/AF/activity-nodes.sbgn");
		LoadNetworkTask.loadFile(sbgnDiagram, true);
	}
	
	public void displayDiagram(Diagram diagram, CyNetworkView cyNetworkView){
		converter.displayDiagram(diagram, cyNetworkView);
    	
		visualStyle.applyVisualStyle();
    	
    	cyNetworkView.redrawGraph(true, true);
	}

	public void refreshCurrentNetworkVisualStyle(CyNetworkView cyNetworkView){
		
		visualStyle.refreshVisualStyle(cyNetworkView.getNetwork());
    	
    	cyNetworkView.redrawGraph(true, true);
	}
	
	public Diagram readSBGNDiagram(String filePath) throws Exception{
		SBGNReader newReader = new SBGNReader(this);
		return newReader.read(filePath);
	}
	

	public void writeSBGNDiagram(CyNetwork network, CyNetworkView cyNetworkView, String filePath){
		SBGNWriter sbgnWriter = new SBGNWriter(network, cyNetworkView, filePath);
		MessagesHandler.executeTask(sbgnWriter, false);
	}

	
	
	
	/**
	 * Function called when reading the .sbgn file to convert the libSBGN coordinates to 
	 * Cytoscape coordinates.
	 * 
	 * Converts the libSBGN X coordinate.
	 * 
	 * @param xSbgn
	 * @param width
	 * @return
	 */
	public static int convert_X_coord_SBGN_to_Cytoscape(int xSbgn, int width){
		int xCy = xSbgn + ( width / 2 );
		return xCy;
	}
	
	
	/**
	 * Function called when reading the .sbgn file to convert the libSBGN coordinates to 
	 * Cytoscape coordinates.
	 * 
	 * Converts the libSBGN Y coordinate.
	 * 
	 * @param ySbgn
	 * @param height
	 * @return
	 */
	public static int convert_Y_coord_SBGN_to_Cytoscape(int ySbgn, int height){
		int yCy = ySbgn + ( height / 2 );
		return yCy;
	}
	
	
	public static float convert_X_coord_Cytoscape_to_SBGN(int xCy, int width){
		int xSbgn = xCy - ( width / 2 );
		return xSbgn;
	}
	
	public static float convert_Y_coord_Cytoscape_to_SBGN(int yCy, int height){
		int ySbgn = yCy - ( height / 2 );
		return ySbgn;
	}

	
	public Boolean getDrawCustomNodesShapes() {
		return visualStyle.getDrawCustomNodesShapes();
	}


	public void setDrawCustomNodesShapes(Boolean drawCustomNodesShapes) {
		this.visualStyle.setDrawCustomNodesShapes( drawCustomNodesShapes );
	}


	public Boolean getDrawCustomEdgesShapes() {
		return visualStyle.getDrawCustomEdgesShapes();
	}

	public void setDrawCustomEdgesShapes(Boolean drawCustomEdgesShapes) {
		this.visualStyle.setDrawCustomEdgesShapes( drawCustomEdgesShapes );
	}
	
	
}
