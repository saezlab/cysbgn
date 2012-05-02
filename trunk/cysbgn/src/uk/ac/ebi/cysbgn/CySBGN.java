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

import uk.ac.ebi.cysbgn.cyInteraction.SBGNConverter;
import uk.ac.ebi.cysbgn.cyInteraction.SbgnFilter;
import uk.ac.ebi.cysbgn.io.MessagesHandler;
import uk.ac.ebi.cysbgn.io.readers.Reader;
import uk.ac.ebi.cysbgn.io.readers.SBGNReader;
import uk.ac.ebi.cysbgn.io.writers.SBGNWriter;
import uk.ac.ebi.cysbgn.io.writers.Writer;
import uk.ac.ebi.cysbgn.mapunits.Diagram;
import uk.ac.ebi.cysbgn.menu.CustomEdgesOption;
import uk.ac.ebi.cysbgn.menu.NodeShapesOption;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;


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
	
	private SBGNVisualStyle visualStyle;
	private SBGNConverter converter; 
	
	private Boolean drawCustomNodesShapes = true;
	private Boolean drawCustomEdgesShapes = true;
	
	
	public CySBGN(){
		System.out.println("Loading CySBGN...");
		
		Cytoscape.getImportHandler().addFilter(new SbgnFilter(this));

		visualStyle = new SBGNVisualStyle(this);
		converter = new SBGNConverter();
		
		NodeShapesOption nodeShapesMenuAction = new NodeShapesOption(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction)nodeShapesMenuAction);
		
		CustomEdgesOption edgesShapesMenuAction = new CustomEdgesOption(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction)edgesShapesMenuAction);
		
//		testMethod();
	}
	
	
	private void testMethod(){
		File sbgnDiagram = new File("/Users/emanuel/SBGNFiles/AF/activity-nodes.sbgn");
		LoadNetworkTask.loadFile(sbgnDiagram, true);
	}
	
	public void displayDiagram(Diagram diagram, CyNetworkView cyNetworkView){
		converter.displayDiagram(diagram, cyNetworkView);
    	
		visualStyle.applyVisualStyle();
    	
    	cyNetworkView.redrawGraph(true, false);
	}

	public void refreshCurrentNetworkVisualStyle(CyNetworkView cyNetworkView){
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        CalculatorCatalog catalog = manager.getCalculatorCatalog();
        catalog.removeVisualStyle(SBGNVisualStyle.NAME);
		
		visualStyle.applyVisualStyle(cyNetworkView.getNetwork());
    	
    	cyNetworkView.redrawGraph(true, false);
	}
	
	public Diagram readSBGNDiagram(String filePath){
		Reader newReader = new SBGNReader(this);
		
		Diagram newDiagram = newReader.read(filePath);
		
		return newDiagram;
	}
	

	public void writeSBGNDiagram(Diagram diagram, String filePath){
		Writer sbgnWriter = new SBGNWriter();
		
		if( !sbgnWriter.save(diagram, filePath) )
			MessagesHandler.showErrorMessageDialog(diagram.getName()+" couldn't be saved in "+filePath, "Diagram not saved", new Exception());
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
	public static int convertXCoordinate(int xSbgn, int width){
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
	public static int convertYCoordinate(int ySbgn, int height){
		int yCy = ySbgn + ( height / 2 );
		return yCy;
	}


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
