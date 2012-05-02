package uk.ac.ebi.cysbgn.menu;

import java.awt.event.ActionEvent;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

public class NodeShapesOption extends CytoscapeAction {

	private static final String DRAW_SHAPES = "Toggle Custom Node Shapes";
	private static final String HIDE_SHAPES = "\u2713 Toggle Custom Node Shapes";
	
	private CySBGN plugin;
	
	public NodeShapesOption(CySBGN plugin){
		this.plugin = plugin;
	}
	
	protected void initialize(){
		putValue(NAME, HIDE_SHAPES);
		super.initialize();
	}
	
	public String getPreferredMenu() {
		return CySBGN.SBGN_MENU;
	}

	public boolean isInMenuBar() {
		return true;
	}

	public boolean isInToolBar() {
		return false;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if( plugin.getDrawCustomNodesShapes() ){
			putValue(NAME, DRAW_SHAPES);
		}else{
			putValue(NAME, HIDE_SHAPES);
		}
		
		plugin.setDrawCustomNodesShapes( !plugin.getDrawCustomNodesShapes() );
		
		for(CyNetworkView cyNetworkView : Cytoscape.getNetworkViewMap().values()){
			plugin.refreshCurrentNetworkVisualStyle( cyNetworkView );
			Cytoscape.getCurrentNetworkView().redrawGraph(true, true);
		}

	}

}
