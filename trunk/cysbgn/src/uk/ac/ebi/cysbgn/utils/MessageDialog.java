package uk.ac.ebi.cysbgn.utils;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import cytoscape.Cytoscape;

@SuppressWarnings("serial")
public class MessageDialog extends JDialog{
	
private JPanel sidePanel;
	
	private static final String NO_DETAILS = "empty card";
	private static final String DETAILS = "exception card";

	private Boolean isDetailedDialog = false;
	
	private String dialogBoxTitle;
	
	private String shortTitle;
	private String detailedMessage;
	private String iconPath;

	private JButton detailsButton;
	
	private JPanel cardsPanel;
	private JPanel centralPanel;
	private JLabel titleLabel;
	private JTextPane limitationsTextPane;
	private JPanel emptyCard;
	private JPanel exceptionCard;
	private JButton okButton;
	
	
	public MessageDialog(String dialogBoxTitle, String shortTitle, String detailedMessage, String iconFilePath){
		super();

		this.dialogBoxTitle = dialogBoxTitle;
		this.shortTitle = shortTitle;
		this.detailedMessage = detailedMessage;
		this.iconPath = iconFilePath;
		
		initDialog();
	}
	
	private void initDialog(){
		initCenterPanel();
		initSidePanel();
		
		JPanel dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.add(sidePanel, BorderLayout.WEST);
		dialogPanel.add(centralPanel, BorderLayout.CENTER);		
		
		this.setTitle(dialogBoxTitle);
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
			
			InputStream input = getClass().getResourceAsStream( iconPath );
			BufferedImage bimage = ImageIO.read(input);
			
			ImageIcon icon = new ImageIcon(bimage);
			JLabel iconLabel = new JLabel(icon);
			
			sidePanel.add(iconLabel, BorderLayout.NORTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initCenterPanel(){
		centralPanel = new JPanel(new BorderLayout());
		centralPanel.setBorder(new EmptyBorder(10, 10, 5, 20));
		
		// Top
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		titleLabel = new JLabel("");
		titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		titleLabel.setText(shortTitle);
		
		detailsButton = new JButton("Details");
		if( detailedMessage == null)
			detailsButton.setEnabled(false);
		
		detailsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if( isDetailedDialog ){
					limitationsTextPane.setText("");
					isDetailedDialog = false;
					CardLayout layout = ((CardLayout)cardsPanel.getLayout());
					layout.show(cardsPanel, NO_DETAILS);
				}else{
					limitationsTextPane.setText( detailedMessage );
					isDetailedDialog = true;
					CardLayout layout = ((CardLayout)cardsPanel.getLayout());
					layout.show(cardsPanel, DETAILS);
				}
				
				MessageDialog.this.pack();
			}
		});
		
		topPanel.add(titleLabel, BorderLayout.CENTER);
		topPanel.add(detailsButton, BorderLayout.EAST);
		
		// Center
		cardsPanel = new JPanel(new CardLayout());
		cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		emptyCard = new JPanel();
		
		exceptionCard = new JPanel(new BorderLayout());
		exceptionCard.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		exceptionCard.setLayout(new BorderLayout());
		
		limitationsTextPane = new JTextPane();
//		limitationsTextPane.addHyperlinkListener(new MyHyperlinkListener());
//		limitationsTextPane.setContentType("text/html");
		limitationsTextPane.setEditable(false);
		limitationsTextPane.setText("");
		exceptionCard.add(limitationsTextPane, BorderLayout.CENTER);
				
		cardsPanel.add(emptyCard, NO_DETAILS);
		cardsPanel.add(exceptionCard, DETAILS);
		
		// Bottom
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(new EmptyBorder(10,10,10,10));
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageDialog.this.dispose();
			}
		});
		bottomPanel.add(okButton, BorderLayout.EAST);
		
		// Adding
		centralPanel.add(topPanel, BorderLayout.NORTH);
		centralPanel.add(cardsPanel, BorderLayout.CENTER);
		centralPanel.add(bottomPanel, BorderLayout.SOUTH);
		
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
