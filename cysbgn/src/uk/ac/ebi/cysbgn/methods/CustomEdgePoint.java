package uk.ac.ebi.cysbgn.methods;

import org.sbgn.ArcClazz;
import org.sbgn.bindings.Port;

public class CustomEdgePoint{
	
	private ArcClazz arcClazz;
	private Port port;
	
	
	public CustomEdgePoint(ArcClazz arcClass){
		this(arcClass, new Port());
	}
	
	public CustomEdgePoint(ArcClazz arcClass, Port port){
		this.arcClazz = arcClass;
		this.port = port;
	}

	public ArcClazz getArcClazz() {
		return arcClazz;
	}

	public void setArcClazz(ArcClazz arcClazz) {
		this.arcClazz = arcClazz;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}
	
	public void setID(String portID){
		port.setId(portID);
	}
	
	public String getID(){
		return port.getId();
	}
	
	public void setY(float y){
		port.setY(y);
	}
	
	public float getY(){
		return port.getY();
	}
	
	public void setX(float x){
		port.setX(x);
	}
	
	public float getX(){
		return port.getX();
	}
}
