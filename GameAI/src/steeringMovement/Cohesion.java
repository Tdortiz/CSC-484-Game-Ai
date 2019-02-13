package steeringMovement;

import java.util.ArrayList;

import character.Kinematic;
import processing.core.PVector;

public class Cohesion extends Seek implements Behavior {

	private ArrayList<Kinematic> flock;
	private float neighborDist;

	/**
	 * 
	 * @param character
	 * @param flock
	 * @param neighborDist
	 */
	public Cohesion(Kinematic character, ArrayList<Kinematic> flock, float neighborDist){
	    super(character, new Kinematic(), character.maxAcceleration);
		this.flock = flock;
		this.neighborDist = neighborDist;
	}
	
	@Override
	public SteeringOutput getSteering() {
		return getSteering(this.flock);
	}
	
	public SteeringOutput getSteering(ArrayList<Kinematic> flock){
        PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all positions
        float count = 0;
        
        for (Kinematic other : flock) {
        	float d = PVector.dist(character.position, other.position);
        	if( (d > 0) && (d < neighborDist) ) {
        		sum.add(other.position); // Add position
        		count++;
        	}
        }
        
        if (count > 0) {
        	sum.div(count);
        	target.setPosition(sum);
        	return super.getSteering();  // Steer towards the position
        }
		
		return new SteeringOutput();
	}

}
