/*******************************************************************************
 * Copyright (c) 2012 Emanuel Goncalves.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Emanuel Goncalves - initial API and implementation
 ******************************************************************************/
package uk.ac.ebi.cysbgn.utils;

import org.sbgn.bindings.Arcgroup;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Map;
import org.sbgn.bindings.Sbgn;

public class SbgnDiagramUtils {
	
	public static Glyph getGlyph(String glyphID, Sbgn map){
		return getGlyph(glyphID, map.getMap());
	}
	
	public static Glyph getGlyph(String glyphID, Map diagram){
		// Check Glyph
		for(Glyph glyph : diagram.getGlyph()){
			if( glyph.getId().equals(glyphID) )
				return glyph;
			
			// Check inner Glyphs
			for(Glyph innerGlyph : glyph.getGlyph())
				if( innerGlyph.getId().equals(glyphID) )
					return innerGlyph;
		}
		
		// Check ArcGroup glyphs
		for(Arcgroup arcGroup : diagram.getArcgroup()){
			for(Glyph glyph : arcGroup.getGlyph()){
				if( glyph.getId().equals(glyphID) )
					return glyph;
				
				// Check ArcGroup inner Glyphs
				for(Glyph innerGlyph : glyph.getGlyph())
					if( innerGlyph.getId().equals(glyphID) )
						return innerGlyph;
			}
		}
		
		return null;
	} 

}
