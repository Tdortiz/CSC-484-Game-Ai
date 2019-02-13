package collision;

import processing.core.PVector;

public class Collision {
	
	public PVector position;
	public PVector normal; 
	
	public Collision(PVector position, PVector normal){
		this.position = position;
		this.normal = normal;
	}
	
}
