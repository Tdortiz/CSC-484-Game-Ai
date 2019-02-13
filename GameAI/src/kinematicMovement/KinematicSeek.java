package kinematicMovement;

import character.Kinematic;
import processing.core.PVector;
import steeringMovement.Behavior;
import steeringMovement.SteeringOutput;

// Kinematic Seek - 49
public class KinematicSeek implements Behavior {
	
	// Static data for character and target
	private Kinematic character;
	private Kinematic target;
	// max speed character can travel
	private float maxSpeed;
	
	public KinematicSeek(Kinematic character, Kinematic target, float maxSpeed){
		this.character = character;
		this.target = target;
		this.maxSpeed = maxSpeed;
	}
	
	public SteeringOutput getSteering(){
		// Create structure for output
		SteeringOutput steering = new SteeringOutput();
		
		// Get direction to the target 
		steering.setVelocity( PVector.sub(target.getPosition(),  character.getPosition()) );
        
        // velocity is along this direction at full speed
        steering.getVelocity().normalize().mult(maxSpeed);
        
        // face in direction we want to move
        character.setOrientation( Kinematic.getNewOrientation(character.getOrientation(), steering.getVelocity()) );
        
        // Output the steering
        steering.setRotation(0);
        return steering;
	}
	
}
