package uk.ac.ebi.cysbgn.utils;

import cytoscape.CyNetwork;
import cytoscape.CyNode;

public class CyNetworkUtils {

	public static CyNode getNode(CyNetwork network, String nodeID){
		for(Object node: network.nodesList())
			if( ((CyNode)node).getIdentifier().equals(nodeID) )
				return (CyNode) node;
		
		return null;
	}
}
