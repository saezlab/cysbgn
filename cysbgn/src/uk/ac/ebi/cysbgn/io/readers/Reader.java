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
package uk.ac.ebi.cysbgn.io.readers;

import uk.ac.ebi.cysbgn.mapunits.Diagram;

/**
 * Represents a reader, all readers class should implement this interface. 
 * 
 * @author emanuel
 *
 */
public interface Reader {

	/**
	 * Reads the diagram in the file path given.
	 * 
	 * @param diagramFilePath
	 * @return
	 */
	public Diagram read(String diagramFilePath);
	
}
