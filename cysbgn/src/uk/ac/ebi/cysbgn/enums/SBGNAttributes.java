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
package uk.ac.ebi.cysbgn.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerator class that lists all the attributes types needed for SBGN plug-in.
 * 
 * @author emanuel
 *
 */

public enum SBGNAttributes {

	// General Attributes
	CLASS ("sbgn.Class"),
	CLASS_INVISIBLE ("Invisible"),
	SBGN_ID ("sbgn.SBGN_ID"),
	
	VALIDATION ("sbgn.Validation"),
	VALIDATION_CORRECT ("Correct"),
	VALIDATION_INCORRECT ("Incorrect"),
	VALIDATION_NA ("N/A"),
	
	// Nodes Attributes
	NODE_WIDTH ("sbgn.Width"),
	NODE_HEIGHT ("sbgn.Height"),
	NODE_POS_X ("sbgn.X"),
	NODE_POS_Y ("sbgn.Y"),
	NODE_LABEL ("sbgn.Label"),
	
	NODE_COMPARTMENT ("sbgn.Compartment"),
	NODE_COMPARTMENT_NA ("N/A"),
	
	NODE_ORIENTATION ("sbgn.Orientation"),
	NODE_ORIENTATION_UP ("up"),
	NODE_ORIENTATION_DOWN ("down"),
	NODE_ORIENTATION_LEFT ("left"),
	NODE_ORIENTATION_RIGHT ("right"),
	NODE_ORIENTATION_NA ("N/A"),
	
	NODE_CLONE_MARKER ("sbgn.isClone"),
	
	// Edge Attributes
	EDGE_ANCHORS ("sbgn.Anchors")
	;
	
	private static Map<String, SBGNAttributes> attributeLookupMap = new HashMap<String, SBGNAttributes>();
	
	static
	{
		for( SBGNAttributes att : SBGNAttributes.values() )
			attributeLookupMap.put(att.attributeName, att);
	}
	
	
	private SBGNAttributes(String attributeName){
		this.attributeName = attributeName;
	}
	
	private final String attributeName;
	
	public String getName() { return attributeName; }
	public String toString(){ return attributeName; }
	
	public static SBGNAttributes fromAttribute(String attributeName){
		return attributeLookupMap.get(attributeName);
	}
}
