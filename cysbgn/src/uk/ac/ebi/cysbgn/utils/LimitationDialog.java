package uk.ac.ebi.cysbgn.utils;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import uk.ac.ebi.cysbgn.CySBGN;
import uk.ac.ebi.cysbgn.enums.Icons;
import cytoscape.Cytoscape;

@SuppressWarnings("serial")
public class LimitationDialog extends JDialog{

	private JPanel sidePanel;
	
	private JPanel centralPanel;
	private JLabel title;
	private JTextPane limitationsTextPane;
	private JPanel limitationsPanel;
	private JCheckBox dontShowAgain;
	private JButton okButton;
	
	
	public LimitationDialog(){
		super();
		initDialog();
	}
	
	
	private void initDialog(){
		initCenterPanel();
		initSidePanel();
		
		JPanel dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.add(sidePanel, BorderLayout.WEST);
		dialogPanel.add(centralPanel, BorderLayout.CENTER);		
		
		this.setTitle("Rendering Limitations");
		this.setModal(true);
		this.setSize(600, 400);
		this.getContentPane().add(dialogPanel);
		this.setLocationRelativeTo(Cytoscape.getDesktop());
		this.pack();
		this.toFront();
		this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		this.setVisible(true);
		
	}
	
	private void initSidePanel(){
		try {
			sidePanel = new JPanel(new BorderLayout());
			sidePanel.setBorder(new EmptyBorder(20, 10, 20, 10));
			
			InputStream input = getClass().getResourceAsStream( Icons.WARNING_LOGO.getPath() );
			BufferedImage bimage = ImageIO.read(input);
			
			ImageIcon icon = new ImageIcon(bimage);
			JLabel iconLabel = new JLabel(icon);
			
			sidePanel.add(iconLabel, BorderLayout.NORTH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initCenterPanel(){
		centralPanel = new JPanel(new BorderLayout());
		centralPanel.setBorder(new EmptyBorder(10, 10, 5, 20));
		
		// Top
		title = new JLabel(createLimitationsTitle());
		title.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		// Center
		JPanel centerPanel = new JPanel(new BorderLayout());
		limitationsPanel = new JPanel();
		limitationsPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		limitationsPanel.setLayout(new BorderLayout());
		
		limitationsTextPane = new JTextPane();
		limitationsTextPane.addHyperlinkListener(new MyHyperlinkListener());
		limitationsTextPane.setContentType("text/html");
		limitationsTextPane.setEditable(false);
		limitationsTextPane.setText(createLimitationList());
		limitationsPanel.add(limitationsTextPane, BorderLayout.CENTER);
				
		dontShowAgain = new JCheckBox("Dont't show me this again.");
		dontShowAgain.setSelected(false);
		dontShowAgain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( dontShowAgain.isSelected() )
					CySBGN.SHOW_LIMITATIONS_PANEL = false;
				else
					CySBGN.SHOW_LIMITATIONS_PANEL = true;
			}
		});
		
		centerPanel.add(limitationsPanel, BorderLayout.CENTER);
		centerPanel.add(dontShowAgain, BorderLayout.SOUTH);
		
		// Bottom
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new EmptyBorder(10,10,10,10));
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LimitationDialog.this.dispose();
			}
		});
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		// Adding
		centralPanel.add(title, BorderLayout.NORTH);
		centralPanel.add(centerPanel, BorderLayout.CENTER);
		centralPanel.add(bottomPanel, BorderLayout.SOUTH);
		
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
