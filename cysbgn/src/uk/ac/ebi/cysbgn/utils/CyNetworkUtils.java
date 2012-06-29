package uk.ac.ebi.cysbgn.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.sbgn.GlyphClazz;

import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

public class CyNetworkUtils {

	public static CyNode getNode(CyNetwork network, String nodeID){
		for(Object node: network.nodesList())
			if( ((CyNode)node).getIdentifier().equals(nodeID) )
				return (CyNode) node;

		return null;
	}

	public static String createUniqueNodeID(String proposedCyNodeID){
		String uniqueCyNodeID = proposedCyNodeID;
		
		if( !nodeIdExists(proposedCyNodeID) ) return proposedCyNodeID;
		
		while( nodeIdExists(uniqueCyNodeID) ){
			uniqueCyNodeID = proposedCyNodeID + Math.random();
		}
		
		return uniqueCyNodeID;
	}
	
	public static boolean nodeIdExists(String proposedNodeID){
		for(CyNetwork cyNetwork : Cytoscape.getNetworkSet()){
			CyNode cyNode = CyNetworkUtils.getNode(cyNetwork, proposedNodeID);
			
			if( (cyNode != null) && (cyNetwork.containsNode(cyNode)) )
				return true;
		}
		return false;
	}
	
	public static GlyphClazz getCyNodeClass(CyNode cyNode){
		String cyNodeClass = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.CLASS.getName());
		
		if( cyNodeClass != null)
			return GlyphClazz.fromClazz(cyNodeClass);
		else
			return null;
	}
	
	public static List<CyNode> getCyNodesBySbgnId(CyNetwork cyNetwork, String sbgnID){
		
		List<CyNode> cyNodes = new ArrayList<CyNode>();
	
		Iterator<CyNode> nodesIterator = cyNetwork.nodesIterator();
		while( nodesIterator.hasNext() ){
			CyNode cyNode = nodesIterator.next();
			String nodeSbgnID = Cytoscape.getNodeAttributes().getStringAttribute(cyNode.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			if( sbgnID.equals(nodeSbgnID) )
				cyNodes.add(cyNode);
		}
		
		return cyNodes;
	}
	
	public static List<CyEdge> getCyEdgesBySbgnId(CyNetwork cyNetwork, String sbgnID){
		
		List<CyEdge> cyNodes = new ArrayList<CyEdge>();
	
		Iterator<CyEdge> edgesIterator = cyNetwork.edgesIterator();
		while( edgesIterator.hasNext() ){
			CyEdge cyEdge = edgesIterator.next();
			String cyEdgeSbgnID = Cytoscape.getEdgeAttributes().getStringAttribute(cyEdge.getIdentifier(), SBGNAttributes.SBGN_ID.getName());
			
			if( sbgnID.equals(cyEdgeSbgnID) )
				cyNodes.add(cyEdge);
		}
		
		return cyNodes;
	}
}
