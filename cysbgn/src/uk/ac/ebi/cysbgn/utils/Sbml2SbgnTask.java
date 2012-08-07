package uk.ac.ebi.cysbgn.utils;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.sbgn.ArcClazz;
import org.sbgn.GlyphClazz;
import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandManager;
import cytoscape.layout.Tunable;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

public class Sbml2SbgnTask implements Task{

	private CySBGN plugin;
	private TaskMonitor taskMonitor;
	
	private File sbmlFile;
	private List<Glyph> compartments;

	private static final boolean exportToVanted = false;
	
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
			
			taskMonitor.setStatus("Importing Hepatonet1 model...");
			List<Tunable> importArgs = new ArrayList<Tunable>();
//			importArgs.add(new Tunable("file", "", Tunable.STRING, "/Users/emanuel/integration/hepatonet1/msb201062-s5.xml") );
			importArgs.add(new Tunable("file", "", Tunable.STRING, sbmlFile.getAbsolutePath()) );
			CyCommandManager.execute("network", "import", importArgs);
			
			taskMonitor.setStatus("Creating the sbgn map...");
			Map map = new Map();
			Sbgn sbgnMap = new Sbgn();
			sbgnMap.setMap(map);
			map.setLanguage("process description");
			
			taskMonitor.setStatus("Initializing variables...");
			compartments = new ArrayList<Glyph>();
			CyNetworkView cyNetworkView = Cytoscape.getCurrentNetworkView();
			
			// Add all nodes 
			taskMonitor.setStatus("Creating nodes...");
			createSbgnNodes(cyNetworkView, sbgnMap);
			
			// Add all reaction nodes
			taskMonitor.setStatus("Creating reaction nodes...");
			createSbgnNodesReactions(cyNetworkView, sbgnMap);
			
			// Add all edges
			taskMonitor.setStatus("Creating edges...");
			createSbgnEdges(cyNetworkView, sbgnMap);
			
			// Create the file
			taskMonitor.setStatus("Creating SBGN-ML file...");
//			SbgnUtil.writeToFile(sbgnMap, new File("/Users/emanuel/integration/hepatonet1/msb201062-s5.sbgn"));
			String sbgnFile = FilenameUtils.removeExtension(sbmlFile.getAbsolutePath()) + ".sbgn";
			SbgnUtil.writeToFile(sbgnMap, new File(sbgnFile));
			
			// Import SBGN-ML diagram
			taskMonitor.setStatus("Import SBGN diagram...");
			importArgs = new ArrayList<Tunable>();
			importArgs.add(new Tunable("file", "", Tunable.STRING, sbgnFile) );
			CyCommandManager.execute("network", "import", importArgs);
			
			
		}catch (Exception e) {
			taskMonitor.setException(e, "ERROR");
			return;
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("Convertion finished!");
	}


	public void createSbgnNodes(CyNetworkView cyNetworkView, Sbgn map){

		Iterator<NodeView> nodesIterator = cyNetworkView.getNodeViewsIterator();
		while( nodesIterator.hasNext() ){
			NodeView nodeView = nodesIterator.next();

			String sbmlType = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml type");

			if( sbmlType.equals("species") ){
				String sbmlName = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml name");
				String sbmlID = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml id");
				String sbmlCompartmentRef = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml compartment");

				double x = nodeView.getXPosition();
				double y = nodeView.getYPosition();
				double width = 60;
				double height = 60;

				Glyph node = new Glyph();
				node.setId(sbmlID);
				node.setClazz(GlyphClazz.SIMPLE_CHEMICAL.getClazz());

				if( sbmlCompartmentRef != null ){
					node.setCompartmentRef( getCompartment(sbmlCompartmentRef, map) );
				}

				if( sbmlName != null ){
					Label label = new Label();
					label.setText(sbmlName);
					node.setLabel(label);
				}

				Bbox bbox = new Bbox();
				bbox.setW((float) width);
				bbox.setH((float) height);
				bbox.setX((float) x);
				bbox.setY((float) y);
				node.setBbox(bbox);

				map.getMap().getGlyph().add(node);
			}


		}

	}
	
	public void createSbgnNodesReactions(CyNetworkView cyNetworkView, Sbgn map){

		Iterator<NodeView> nodesIterator = cyNetworkView.getNodeViewsIterator();
		while( nodesIterator.hasNext() ){
			NodeView nodeView = nodesIterator.next();

			String sbmlType = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml type");

			if( sbmlType.equals("reaction") ){
				boolean sbmlReversible = Cytoscape.getNodeAttributes().getBooleanAttribute(nodeView.getNode().getIdentifier(), "sbml reversible");
				String sbmlID = Cytoscape.getNodeAttributes().getStringAttribute(nodeView.getNode().getIdentifier(), "sbml id");
				
				double x = nodeView.getXPosition();
				double y = nodeView.getYPosition();
				double width = 24;
				double height = 24;
				
				Glyph node = new Glyph();
				node.setId(sbmlID);
				node.setClazz(GlyphClazz.PROCESS.getClazz());
				
				node.setOrientation("horizontal");
				
				Bbox bbox = new Bbox();
				bbox.setW((float) width);
				bbox.setH((float) height);
				bbox.setX((float) x);
				bbox.setY((float) y);
				node.setBbox(bbox);
				
				map.getMap().getGlyph().add(node);
				
//				if( sbmlReversible ){
//					Glyph reversibleNode = new Glyph();
//					reversibleNode.setId(sbmlID + "_R");
//					reversibleNode.setClazz(GlyphClazz.PROCESS.getClazz());
//					
//					reversibleNode.setOrientation("horizontal");
//					
//					Bbox rbbox = new Bbox();
//					rbbox.setW((float) width);
//					rbbox.setH((float) height);
//					rbbox.setX((float) (x+30));
//					rbbox.setY((float) y);
//					reversibleNode.setBbox(rbbox);
//					
//					map.getMap().getGlyph().add(reversibleNode);
//				}
			}

		}
	}

	public void createSbgnEdges(CyNetworkView cyNetworkView, Sbgn map){
		Iterator<EdgeView> edgesIterator = cyNetworkView.getEdgeViewsIterator();
		while( edgesIterator.hasNext() ){
			EdgeView edgeView = edgesIterator.next();

			// Fetch sbml attributes
//			String sbmlStoichiometry = Cytoscape.getEdgeAttributes().getStringAttribute(edgeView.getEdge().getIdentifier(), "sbml stoichiometry");
//			String sbmlMetaId = Cytoscape.getEdgeAttributes().getStringAttribute(edgeView.getEdge().getIdentifier(), "sbml metaId");
//			String sbmlSbo = Cytoscape.getEdgeAttributes().getStringAttribute(edgeView.getEdge().getIdentifier(), "sbml sbo");
			String sbmlInteraction = Cytoscape.getEdgeAttributes().getStringAttribute(edgeView.getEdge().getIdentifier(), "interaction");

			// Create sbgn arc with the sbml ID
			Arc edge = new Arc();
			
			String id = edgeView.getEdge().getIdentifier();
			if( exportToVanted ){
				id = id.replaceAll(" ", "_");
				id = id.replace("(", "");
				id = id.replace(")", "");
			}
			edge.setId( id );
			
			
			// Set source node
			CyNode sourceNode = (CyNode) edgeView.getEdge().getSource();
			String sourceNodeSbmlID = Cytoscape.getNodeAttributes().getStringAttribute(sourceNode.getIdentifier(), "sbml id");
			edge.setSource( SbgnDiagramUtils.getGlyph(sourceNodeSbmlID, map) );
			
			// Set target node
			CyNode targetNode = (CyNode) edgeView.getEdge().getTarget();
			String targetNodeSbmlID = Cytoscape.getNodeAttributes().getStringAttribute(targetNode.getIdentifier(), "sbml id");
			edge.setTarget( SbgnDiagramUtils.getGlyph(targetNodeSbmlID, map) );
			
			// Check if the reaction is reversible
			boolean isReversible = false;
			if( sbmlInteraction.equals("reaction-reactant") ){
				edge.setClazz(ArcClazz.CONSUMPTION.getClazz());
				
				Object targetRev = Cytoscape.getNodeAttributes().getAttribute(targetNode.getIdentifier(), "sbml reversible");
				if( targetRev!=null && Cytoscape.getNodeAttributes().getBooleanAttribute(targetNode.getIdentifier(), "sbml reversible") )
					isReversible = true;
			}else{
				edge.setClazz(ArcClazz.PRODUCTION.getClazz());
				
				Object sourceRev = Cytoscape.getNodeAttributes().getAttribute(sourceNode.getIdentifier(), "sbml reversible");
				if( sourceRev!=null && Cytoscape.getNodeAttributes().getBooleanAttribute(sourceNode.getIdentifier(), "sbml reversible") )
					isReversible = true;
			}
			
			// Set start point
			Start start = new Start();
			start.setX( ((Glyph)edge.getSource()).getBbox().getX() );
			start.setY( ((Glyph)edge.getSource()).getBbox().getY() );
			edge.setStart(start);
			
			// Set end point
			End end = new End();
			end.setX( ((Glyph)edge.getTarget()).getBbox().getX() );
			end.setY( ((Glyph)edge.getTarget()).getBbox().getY() );
			edge.setEnd(end);
			
			map.getMap().getArc().add(edge);
			
			// Create the reversible reaction
//			if( isReversible ){
//				Arc reversibleEdge = new Arc();
//				reversibleEdge.setId( edgeView.getEdge().getIdentifier() + "_R" );
//				
//				reversibleEdge.setSource( SbgnDiagramUtils.getGlyph(targetNodeSbmlID, map) );
//				reversibleEdge.setTarget( SbgnDiagramUtils.getGlyph(sourceNodeSbmlID, map) );
//				
//				if( sbmlInteraction.equals("reaction-reactant") )
//					reversibleEdge.setClazz(ArcClazz.CONSUMPTION.getClazz());
//				else
//					reversibleEdge.setClazz(ArcClazz.PRODUCTION.getClazz());
//				
//				Start revStart = new Start();
//				revStart.setX( ((Glyph)edge.getTarget()).getBbox().getX() );
//				revStart.setY( ((Glyph)edge.getTarget()).getBbox().getY() );
//				reversibleEdge.setStart(revStart);
//				
//				End revEnd = new End();
//				revEnd.setX( ((Glyph)edge.getSource()).getBbox().getX() );
//				revEnd.setY( ((Glyph)edge.getSource()).getBbox().getY() );
//				reversibleEdge.setEnd(revEnd);
//				
//				map.getMap().getArc().add(reversibleEdge);
//			}

		}

	}

	
	// Auxiliary methods
	public Glyph getCompartment(String compartmentRef, Sbgn map){
		
		// Return the compartment if it already exists
		for(Glyph compartment : compartments)
			if(compartment.getId().equals( compartmentRef.trim() ))
				return compartment;

		// If the compartment does not exist create one and return it
		Glyph newCompartment = new Glyph();
		newCompartment.setId(compartmentRef);
		newCompartment.setClazz(GlyphClazz.COMPARTMENT.getClazz());

		Bbox bbox = new Bbox();
		bbox.setH(20);
		bbox.setW(20);
		bbox.setX(0);
		bbox.setY(0);
		newCompartment.setBbox(bbox);
		
		map.getMap().getGlyph().add(newCompartment);
		compartments.add(newCompartment);

		return newCompartment;
	}

	@Override
	public void setTaskMonitor(TaskMonitor monitor) throws IllegalThreadStateException {
		this.taskMonitor = monitor;
	}

}
