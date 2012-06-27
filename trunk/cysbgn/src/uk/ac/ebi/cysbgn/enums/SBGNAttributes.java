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
	CLASS ("Class"),
	CLASS_INVISIBLE ("Invisible"),
	SBGN_ID ("SBGN_ID"),
	
	VALIDATION ("Validation"),
	VALIDATION_CORRECT ("Correct"),
	VALIDATION_INCORRECT ("Incorrect"),
	VALIDATION_NA ("N/A"),
	
	// Nodes Attributes
	NODE_WIDTH ("Width"),
	NODE_HEIGHT ("Height"),
	NODE_POS_X ("X"),
	NODE_POS_Y ("Y"),
	NODE_LABEL ("Label"),
	
	NODE_COMPARTMENT ("Compartment"),
	NODE_COMPARTMENT_NA ("N/A"),
	
	NODE_ORIENTATION_UP ("up"),
	NODE_ORIENTATION_DOWN ("down"),
	NODE_ORIENTATION_LEFT ("left"),
	NODE_ORIENTATION_RIGHT ("right"),
	NODE_ORIENTATION ("Orientation"),
	NODE_ORIENTATION_NA ("N/A"),
	
	NODE_CLONE_MARKER ("isClone"),
	
	// Edge Attributes
	EDGE_ANCHORS ("Anchors")
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
