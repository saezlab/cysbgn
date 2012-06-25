package uk.ac.ebi.cysbgn.utils;

import org.sbgn.GlyphClazz;

import uk.ac.ebi.cysbgn.enums.SBGNAttributes;

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
}
