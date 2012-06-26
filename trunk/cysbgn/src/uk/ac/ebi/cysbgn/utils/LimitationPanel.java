package uk.ac.ebi.cysbgn.utils;

import java.awt.BorderLayout;
import java.net.URI;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class LimitationPanel extends JPanel{

	
	public LimitationPanel(){
		super();
		initPanel();
	}
	
	private void initPanel(){
		JLabel title = new JLabel(createLimitationsTitle());
		title.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		JPanel limitationsPanel = new JPanel();
		limitationsPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		limitationsPanel.setLayout(new BorderLayout());
		
		JTextPane limitationsTextPane = new JTextPane();
		limitationsTextPane.addHyperlinkListener(new MyHyperlinkListener());
		limitationsTextPane.setContentType("text/html");
		limitationsTextPane.setEditable(false);
		limitationsTextPane.setText(createLimitationList());
		limitationsPanel.add(limitationsTextPane, BorderLayout.CENTER);
				
		setLayout(new BorderLayout());
		add(title, BorderLayout.NORTH);
		add(limitationsPanel, BorderLayout.CENTER);
		
	}
	
	private String createLimitationsTitle(){
		String briefExplanation = 
				"<html> Be aware that due to some Cytoscape v2.8.x limitations SBGN diagrams may <b>NOT</b> be fully <br>" +
				"supported. Hopefully, in Cytoscape 3 all of these limitations will be fixed:<br> </html>";
		
		return briefExplanation;
	}
	
	private String createLimitationList(){
		StringBuilder limitations = new StringBuilder("<html> <ul>");
		
		limitations.append(
				"<li><b>No Z Ordering: </b>The Z coordinate of the nodes is not calculated, therefore Cytoscape <br>" +
				" is not able to distinguish nodes order when they overlap. For further details <a href=https://sourceforge.net/projects/cysbgn/>click here</a>. </li>");
		
		limitations.append(
				"<li><b>Limited Edges Shapes: </b>Cytoscape offers a limited amount of edges shapes and it is <br>" +
				"not possible to extend them. Thus, some edges are not represented exactly as the original<br>" +
				"SBGN diagram. For further details <a href=https://sourceforge.net/projects/cysbgn/>click here</a>. </li>");
		
		limitations.append(
				"<li><b>No Compartments: </b>Compartments are not supported by Cytoscape therefore compartment<br>" +
				"nodes in SBGN visual style are drawn transparent. Though, if they are selected they may<br>" +
				"cover inner nodes due to the node order limitation of Cytoscape. For further details <a href=https://sourceforge.net/projects/cysbgn/>click here</a>. </li>");
		
		limitations.append(
				"<li><b>Analysis Methods: </b>In order to render the SBGN diagrams as close as possible to<br>" +
				"the original aspect some auxiliary nodes and edges are drawn. Since these introduce some<br>" +
				"problems when running analysis methods we offer an option under CySBGN menu in the Plugins<br>" +
				"menu to simplify the network. For further details <a href=https://sourceforge.net/projects/cysbgn/>click here</a>. <br></li>");
		
		limitations.append("</ul> </html>");
		
		return limitations.toString();
	}
	
	
	class MyHyperlinkListener implements HyperlinkListener {
		
	    public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
	    	HyperlinkEvent.EventType type = hyperlinkEvent.getEventType();
	    	final URL url = hyperlinkEvent.getURL();
	    	
	    	
	        if (type == HyperlinkEvent.EventType.ACTIVATED) {
	        	try{
			    	if (java.awt.Desktop.isDesktopSupported()) {
						java.awt.Desktop.getDesktop().browse( new URI(url.toString()) );
						
					
					}
		    	}catch(Exception e){
		    		e.printStackTrace();
		    	}
	        }
	    }
	}
}
