package uk.ac.ebi.cysbgn.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class CyEdgeAttrUtils {

	
	public static String getAnchorAttribute(List<Point2D> anchorsList){
		StringBuilder anchors = new StringBuilder("[");
		
		for(int i=0; i<anchorsList.size(); i++){
			Point2D point = anchorsList.get(i);
			
			anchors.append("(");
			anchors.append(point.getX());
			anchors.append(",");
			anchors.append(point.getY());
			anchors.append(")");
			
			if( (i+1) < anchorsList.size() )
				anchors.append(";");
		}
		
		anchors.append("]");
		
		return anchors.toString();
	}

	/**
	 * Method converts the anchor edge string attribute into a list of anchors. 
	 * 
	 * ex:
	 * 		[(4.0,5.0);(6.0,5.0)]
	 * 
	 * @param anchors
	 * @return
	 */
	public static List<Point2D> getAnchorAttribute(String anchors){
		List<Point2D> anchorsList = new ArrayList<Point2D>();
		
		if( anchors == null )
			return anchorsList;
		
		if( anchors.equals("[]") )
			return anchorsList;
		
		// Remove square braces
		String list = anchors.substring(1, anchors.toCharArray().length-1);
		
		String[] points = list.split(";");
		for(String point : points){
			String coordinatesString = point.substring(1, point.toCharArray().length-1);
			
			String[] coordinates = coordinatesString.split(",");
			
			float x = Float.valueOf( coordinates[0] );
			float y = Float.valueOf( coordinates[1] );
			
			Point2D.Float anchor = new Point2D.Float(x, y);
			
			anchorsList.add(anchor);
		}
		
		return anchorsList;
	}
}
