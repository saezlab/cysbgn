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

import org.sbgn.ConvertMilestone1to2;
import org.sbgn.SbgnUtil;
import org.sbgn.SbgnVersionFinder;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Sbgn;


public class ReadSBGNFile {


	public static void main(String[] args){

		try{
			String sbgnFilePath = "/Users/emanuel/SBGNFiles/BIOMD0000000422.sbgn";

			File file = new File(sbgnFilePath);

			File targetFile = file;

			
			try{
				// Check file version
				int version = SbgnVersionFinder.getVersion(targetFile);
				if (version == 1){
					targetFile = File.createTempFile(file.getName(), ".sbgn");
					System.out.println ("Converted to " + targetFile);
					ConvertMilestone1to2.convert (file, targetFile);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			SbgnUtil sbgnUtil = new SbgnUtil();
			Sbgn map = sbgnUtil.readFromFile(targetFile);

			for(Glyph glyph : map.getMap().getGlyph()){
				System.out.println("ID: " + glyph.getId());
				System.out.println("Class: " + glyph.getClazz());
				System.out.println("");
			}
			
			System.out.println("Number of Glyphs: " + map.getMap().getGlyph().size());
			System.out.println("Number of Arcs: " + map.getMap().getArc().size());

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
