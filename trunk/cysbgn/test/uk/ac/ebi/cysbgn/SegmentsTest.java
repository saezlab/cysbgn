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
package uk.ac.ebi.cysbgn;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sbgn.bindings.Arc.End;
import org.sbgn.bindings.Arc.Next;
import org.sbgn.bindings.Arc.Start;
import org.sbgn.bindings.Glyph;
import org.sbgn.bindings.Port;

import uk.ac.ebi.cysbgn.methods.ArcSegmentationAlgorithm;
import uk.ac.ebi.cysbgn.methods.SegmentationPoint;

public class SegmentsTest {

	private Start start;
	private End end;
	
	private Next next1;
	private Next next2;
	private Next next3;
	private Next next4;
	
	private Port port1;
	private Port port2;
	
	private List<Next> nextPoints;
	private List<Port> portsPoints;
	
	
	@Before
	public void setUp() throws Exception {
		
		start = createStartPoint(10, 10);
		end = createEndPoint(20, 5);
		
		nextPoints = new ArrayList<Next>();
		next1 = createNextPoint(15, 10); nextPoints.add(next1);
		next2 = createNextPoint(15, 15); nextPoints.add(next2);
		next3 = createNextPoint(25, 15); nextPoints.add(next3);
		next4 = createNextPoint(25, 5); nextPoints.add(next4);
		
		portsPoints = new ArrayList<Port>();
		port1 = createPortPoint(20, 15); portsPoints.add(port1);
		port2 = createPortPoint(22, 5); portsPoints.add(port2);
		
	}
	
	
	@Test
	public void testSortedList(){
		
		ArcSegmentationAlgorithm creator  = new ArcSegmentationAlgorithm();
		List<SegmentationPoint> sortedList = creator.generateSortedPointsList(portsPoints, nextPoints, new ArrayList<Glyph>(), start, end);
		
		if( !compareCoordinates(sortedList.get(0), start.getX(), start.getY()) ){
			Assert.fail("Start point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(1), next1.getX(), next1.getY()) ){
			Assert.fail("Next1 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(2), next2.getX(), next2.getY()) ){
			Assert.fail("Next2 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(3), port1.getX(), port1.getY()) ){
			Assert.fail("Port1 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(4), next3.getX(), next3.getY()) ){
			Assert.fail("Next3 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(5), next4.getX(), next4.getY()) ){
			Assert.fail("Next4 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(6), port2.getX(), port2.getY()) ){
			Assert.fail("Port2 point not the same"); return;
		}
		
		if( !compareCoordinates(sortedList.get(7), end.getX(), end.getY()) ){
			Assert.fail("End point not the same"); return;
		}
		
		Assert.assertTrue("Segment points correclty sorted.", true);
		
		System.out.println(sortedList.toString());
	}
	
	public boolean compareCoordinates(SegmentationPoint point1D, float x, float y){
		if( point1D.getX()==x && point1D.getY()==y)
			return true;
		return false;
	}
	
	private Port createPortPoint(int x, int y){
		Port port = new Port();
		port.setX(x);
		port.setY(y);
		return port;
	}
	
	private End createEndPoint(int x, int y){
		End end = new End();
		end.setX(x);
		end.setY(y);
		return end;
	}
	
	private Start createStartPoint(int x, int y){
		Start start = new Start();
		start.setX(x);
		start.setY(y);
		return start;
	}
	
	private Next createNextPoint(int x, int y){
		Next next = new Next();
		next.setX(x);
		next.setY(y);
		return next;
	}
}
