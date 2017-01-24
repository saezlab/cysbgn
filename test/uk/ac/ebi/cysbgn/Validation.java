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
package uk.ac.ebi.cysbgn;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.sbgn.schematron.Issue;
import org.sbgn.schematron.SchematronValidator;
import org.xml.sax.SAXException;

public class Validation {
	
	public static void main(String[] args){
		try {
			String sbgnfile = "/Users/emanuel/SBGNFiles/error-test-files/AF/af10101-fail.sbgn";
		
			File file = new File(sbgnfile);

			List<Issue> issues = SchematronValidator.validate(file);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

}
