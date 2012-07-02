package uk.ac.ebi.cysbgn.utils;

import org.sbgn.bindings.Arcgroup;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Map;

public class SbgnDiagramUtils {
	
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
