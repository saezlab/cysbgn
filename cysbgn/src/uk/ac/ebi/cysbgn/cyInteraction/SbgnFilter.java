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
package uk.ac.ebi.cysbgn.cyInteraction;

import uk.ac.ebi.cysbgn.CySBGN;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;

/**
 * File Filter for selecting *.sbgn files
 * 
 * @author emanuel
 *
 */
public class SbgnFilter extends CyFileFilter{

	protected CySBGN plugin;

	public SbgnFilter(CySBGN plugin) {
		super("sbgn", "SBGN file", ImportHandler.GRAPH_NATURE);
		this.plugin = plugin;
	}

	public GraphReader getReader(String fileName) {
		return new ImportAction(fileName, plugin);
	}

}
