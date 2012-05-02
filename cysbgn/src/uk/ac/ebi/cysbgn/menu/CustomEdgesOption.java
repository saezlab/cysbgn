package uk.ac.ebi.cysbgn.menu;

import java.awt.event.ActionEvent;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

public class CustomEdgesOption extends CytoscapeAction {

	private static final String DRAW_EDGES = "Toggle Custom Edges Shapes (on next import...)";
	private static final String HIDE_EDGES = "\u2713 Toggle Custom Edges Shapes (on next import...)";
	
	private CySBGN plugin;
	
	public CustomEdgesOption(CySBGN plugin){
		this.plugin = plugin;
	}
	
	protected void initialize(){
		putValue(NAME, HIDE_EDGES);
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
		if( plugin.getDrawCustomEdgesShapes() ){
			putValue(NAME, DRAW_EDGES);
		}else{
			putValue(NAME, HIDE_EDGES);
		}
		
		plugin.setDrawCustomEdgesShapes( !plugin.getDrawCustomEdgesShapes() );
		
		plugin.refreshCurrentNetworkVisualStyle(Cytoscape.getCurrentNetworkView());
		
		Cytoscape.getCurrentNetworkView().redrawGraph(true, true);

	}

}
