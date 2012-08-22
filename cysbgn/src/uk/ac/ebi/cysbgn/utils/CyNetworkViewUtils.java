package uk.ac.ebi.cysbgn.utils;

import giny.view.NodeView;

import java.util.Iterator;

import uk.ac.ebi.cysbgn.enums.SBGNAttributes;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;

public class CyNetworkViewUtils {

	@SuppressWarnings({ "unchecked" })
	public static void refreshNodesAttributes(CyNetworkView cyNetworkView){
		
		Iterator<NodeView> nodesIterator = cyNetworkView.getNodeViewsIterator();
		while( nodesIterator.hasNext() ){
			NodeView nodeView = nodesIterator.next();

			// Refresh nodes positions
			Cytoscape.getNodeAttributes().setAttribute(nodeView.getNode().getIdentifier(), SBGNAttributes.NODE_POS_X.getName(), ((int)nodeView.getXPosition()) );
			Cytoscape.getNodeAttributes().setAttribute(nodeView.getNode().getIdentifier(), SBGNAttributes.NODE_POS_Y.getName(), ((int)nodeView.getYPosition()) );
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static NodeView getCyNodeView(CyNetworkView cyNetworkView, String nodeId){
		
		Iterator<NodeView> nodesIterator = cyNetworkView.getNodeViewsIterator();
		while( nodesIterator.hasNext() ){
			NodeView nodeView = nodesIterator.next();

			if( nodeView.getNode().getIdentifier().equals(nodeId) )
				return nodeView;
		}
		
		return null;
	}
}
