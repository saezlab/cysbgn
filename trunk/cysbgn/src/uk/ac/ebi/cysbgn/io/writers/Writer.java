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

import uk.ac.ebi.cysbgn.mapunits.Diagram;

/**
 * Represents a writer, all writers class should implement this interface. 
 * 
 * @author emanuel
 *
 */
public interface Writer {

	/**
	 * Stores the information of the SBGN plug-in diagram in the given file path.
	 * 
	 * @param diagram
	 * @param filePath
	 * @return
	 */
	public boolean save(Diagram diagram, String filePath);
	
}
