/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 *     Martijn van Iersel - co-supervisor
 *     Julio Saez-Rodriguez - supervisor
 ******************************************************************************/
package uk.ac.ebi.cysbgn.io.writers;

import java.io.File;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.sbgn.SbgnUtil;
import org.sbgn.bindings.Arc;
import org.sbgn.bindings.Bbox;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Label;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Sbgn;

import uk.ac.ebi.cysbgn.mapunits.Diagram;
import uk.ac.ebi.cysbgn.mapunits.MapArc;
import uk.ac.ebi.cysbgn.mapunits.MapNode;

/**
 * AbstarctWriter class implements all methods shared among the SBGN writers.  
 * 
 * @author emanuel
 *
 */
public class SBGNWriter implements Writer{

	
	public boolean save(Diagram diagram, String filePath){
		File sbgnFile = new File(filePath);
		
		Sbgn sbgnDiagram = new Sbgn();
		Map map = new Map();
		sbgnDiagram.setMap(map);
		
		map.setLanguage(diagram.getDiagramType().getName());
		
		for(MapNode node : ((HashMap<String, MapNode>)diagram.getMapNodes()).values()){
			Glyph glyph = new Glyph();
			glyph.setId(node.getId());
			glyph.setClazz(node.getType().getClazz());
			
			map.getGlyph().add(glyph);
			
			Bbox bbox1 = new Bbox();
			
			bbox1.setX( new Float(node.getWidth()) );
			
			bbox1.setX( new Float(node.getHeight()) );
			
			bbox1.setX( new Float(node.getX()) );
			
			bbox1.setY( new Float(node.getY()) );
			
			glyph.setBbox(bbox1);
			
			if(node.getLabel() != null){
				Label glyphLabel = new Label();
				glyphLabel.setText(node.getLabel());
				glyph.setLabel(glyphLabel);
			}
		
		}	
		
		for(MapArc mapArc : diagram.getMapArcs().values()){
			Arc arc = new Arc();
			arc.setId(mapArc.getId());
			arc.setClazz(mapArc.getType().getClazz());
			
			Glyph glyphSource = new Glyph();
			glyphSource.setId(mapArc.getSourceNode().getId());
			Glyph glyphTarget = new Glyph();
			glyphTarget.setId(mapArc.getTargetNode().getId());
			
			arc.setSource(glyphSource);
			arc.setTarget(glyphTarget);
			
			map.getArc().add(arc);
		}
		
		try {
			SbgnUtil.writeToFile(sbgnDiagram, sbgnFile);
		} catch (JAXBException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
