/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 ******************************************************************************/
package uk.ac.ebi.cysbgn.utils;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.sbfc.converter.models.GeneralModel;
import org.sbfc.converter.models.SBMLModel;
import org.sbfc.converter.sbml2sbgnml.SbmlToSbgnML;
import org.sbgn.GlyphClazz;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import uk.ac.ebi.cysbgn.io.SBGNMLReader;
import uk.ac.ebi.cysbgn.visualization.SBGNVisualStyle;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandManager;
import cytoscape.layout.Tunable;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

public class Sbml2SbgnTask implements Task{

	private CySBGN plugin;
	private TaskMonitor taskMonitor;
	private File sbmlFile;
	
	private CyNetworkView sbmlCyNetworkView;
	
	private CyNetworkView sbgnCyNetworkView;
	private CyNetwork sbgnCyNetwork;

	
	public Sbml2SbgnTask(CySBGN plugin, File sbmlFile){
		this.plugin = plugin;
		this.sbmlFile = sbmlFile;
	}

	@Override
	public String getTitle() {
		return "Convert SBML into SBGN";
	}

	@Override
	public void halt() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		taskMonitor.setPercentCompleted(-1);
		taskMonitor.setStatus("Converting sbml network...");

		try{
			
			// Import SBML diagram
			taskMonitor.setStatus("Importing SBML model...");
			List<Tunable> importArgs = new ArrayList<Tunable>();
			importArgs.add(new Tunable("file", "", Tunable.STRING, sbmlFile.getAbsolutePath()) );
			CyCommandManager.execute("network", "import", importArgs);
			sbmlCyNetworkView = Cytoscape.getCurrentNetworkView();
			
			// Convert the selected SBML file into a SBGN model
			taskMonitor.setStatus("Converting SBML to SBGN using SBFC...");

			SbmlToSbgnML converter = new SbmlToSbgnML();
			
			GeneralModel sbmlModel = new SBMLModel();
			sbmlModel.setModelFromFile(sbmlFile.getAbsolutePath());
			
			GeneralModel sbgnModel = converter.convert( sbmlModel );
			String sbgnFile = FilenameUtils.removeExtension(sbmlFile.getAbsolutePath())+".sbgn";
			sbgnModel.modelToFile(sbgnFile);
			
			// Import the created SBGN-ML file
			taskMonitor.setStatus("Importing generated SBGN diagram...");
			
			SBGNMLReader newReader = new SBGNMLReader(false);
			sbgnCyNetwork = Cytoscape.createNetwork(sbmlCyNetworkView.getIdentifier() + " SBGN", false);
			sbgnCyNetwork = newReader.read(sbgnFile, sbgnCyNetwork);
			
			plugin.addNetwork(sbmlCyNetworkView.getNetwork(), newReader.getMap(), sbgnFile);
//			sbgnCyNetworkView = Cytoscape.getNetworkView(sbgnCyNetwork.getIdentifier());
			
			// Load the SBML map coordinates and adjust sizes
			setPropretiesAccordingToSBML(newReader.getMap());
			clearEdgesAnchors(sbmlCyNetworkView);
			Cytoscape.destroyNetwork(sbgnCyNetwork);
//			setNodesCoordinates(sbgnCyNetworkView);
			
//			DrawCustomNodes costumNodeShapes = new DrawCustomNodes(plugin);
//			costumNodeShapes.drawCustomNodes(sbgnCyNetwork, Cytoscape.getNetworkView(sbgnCyNetwork.getIdentifier()));
//			
			SBGNVisualStyle visualStyle = new SBGNVisualStyle(plugin);
			visualStyle.applyVisualStyle();
			sbmlCyNetworkView.fitContent();
			
//			sbmlCyNetworkView.redrawGraph(true, true);
			
			taskMonitor.setStatus("Convertion finished! SBGN diagram and file created.");
		}catch (Exception e) {
			taskMonitor.setException(e, "ERROR");
			e.printStackTrace();
			return;
		}

		taskMonitor.setPercentCompleted(100);
	}

	public void setNodesCoordinates(CyNetworkView cyNetworkView){
		Iterator<NodeView> nodesIterator = cyNetworkView.getNodeViewsIterator();
		while( nodesIterator.hasNext() ){
			NodeView nodeView = nodesIterator.next();
			
			double nodeX = Cytoscape.getNodeAttributes().getIntegerAttribute(nodeView.getNode().getIdentifier(), SBGNAttributes.NODE_POS_X.getName());
			double nodeY = Cytoscape.getNodeAttributes().getIntegerAttribute(nodeView.getNode().getIdentifier(), SBGNAttributes.NODE_POS_Y.getName());
			
			nodeView.setXPosition( nodeX );
			nodeView.setYPosition( nodeY );
		}
	}
	
	private void clearEdgesAnchors(CyNetworkView cyNetworkView){
		Iterator<EdgeView> edgesIterator = cyNetworkView.getEdgeViewsIterator();
		while( edgesIterator.hasNext() ){
			EdgeView edgeView = edgesIterator.next();
			
			edgeView.clearBends();
		}		
	}
	
	public void setPropretiesAccordingToSBML(Sbgn map){
		
		for(Glyph glyph : map.getMap().getGlyph()){
			NodeView sbmlNode = CyNetworkViewUtils.getCyNodeView(sbmlCyNetworkView, glyph.getId());
			
			if(sbmlNode!=null){
				Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_POS_X.getName(), ((int)sbmlNode.getXPosition()) );
				Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_POS_Y.getName(), ((int)sbmlNode.getYPosition()) );
				
				GlyphClazz sbmlNodeClazz = CyNetworkUtils.getCyNodeClass((CyNode) sbmlNode.getNode());
				switch(sbmlNodeClazz){
					case PROCESS :
					case OMITTED_PROCESS:
					case UNCERTAIN_PROCESS:
					case ASSOCIATION:
					case DISSOCIATION:
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(20) );
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(20) );
						break;
					
					case AND:
					case OR:
					case NOT:
					case DELAY:
					case OUTCOME:
					case EXISTENCE:
					case LOCATION:
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(40) );
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(40) );
						break;
						
					case UNIT_OF_INFORMATION:
					case STATE_VARIABLE:
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(70) );
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(30) );
						break;
						
					case SOURCE_AND_SINK:
					case SIMPLE_CHEMICAL:
					case SIMPLE_CHEMICAL_MULTIMER:
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(60) );
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(60) );
						break;
					
					default:
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_WIDTH.getName(), new Double(120) );
						Cytoscape.getNodeAttributes().setAttribute(sbmlNode.getNode().getIdentifier(), SBGNAttributes.NODE_HEIGHT.getName(), new Double(60) );
						break;
				}
			}
		}
	}

	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		this.taskMonitor = monitor;
	}

}
