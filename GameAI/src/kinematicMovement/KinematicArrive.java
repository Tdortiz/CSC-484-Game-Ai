package kinematicMovement;

import character.Kinematic;
import processing.core.PVector;
import steeringMovement.Behavior;
import steeringMovement.SteeringOutput;

// Kinematic Arrive - 52
public class KinematicArrive implements Behavior {
	
	// Static data for character and target
	private Kinematic character;
	private Kinematic target;
	// max speed character can travel
	private float maxSpeed;
	// holds satisfaction radius
	private float ros;
	// time to target constant
	private float timeToTarget = 1/10f;
	
	public KinematicArrive(Kinematic character, Kinematic target, float maxSpeed, float ros){
		this.character = character;
		this.target = target;
		this.maxSpeed = maxSpeed;
		this.ros = ros;
	}
	
	public SteeringOutput getSteering(){
		// Create structure for output
		SteeringOutput steering = new SteeringOutput();
		
		// Get direction to the target
		steering.setVelocity( PVector.sub(target.getPosition(), character.getPosition()) );
		
		// check if within radius
		if( steering.getVelocity().mag() < ros ){
			steering.setRotation(0);
			steering.setVelocity(new PVector(0,0));
			return steering; // return null;
		}
		
		// move target to get there in timeToTarget seconds
		steering.getVelocity().div(timeToTarget);
		
		// clip if its too fast
		if( steering.getVelocity().mag() > maxSpeed ){
			steering.getVelocity().normalize().mult(maxSpeed);
		}
		
		// face in the correct direction
		character.setOrientation( Kinematic.getNewOrientation(character.getOrientation(), steering.getVelocity()) );
		
		// output steering
		steering.setRotation(0);
        return steering;
	}
	
	public float getROS() { return ros; }

}
