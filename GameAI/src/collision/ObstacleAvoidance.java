package collision;

import character.Kinematic;
import processing.core.PVector;
import steeringMovement.Behavior;
import steeringMovement.Seek;
import steeringMovement.SteeringOutput;

public class ObstacleAvoidance extends Seek implements Behavior {
	
	// holds a collision detector
	private CollisionDetector collisionDetector;
	// Holds the minimum distance to a wall (i.e., how far
	// to avoid collision) should be greater than the
	// radius of the character.
	private float avoidDistance;
	// holds the distance to look ahead for a collision (length of collision ray)
	private float lookahead;
	
	public ObstacleAvoidance(CollisionDetector collisionDetector, float avoidDistance, float lookahead, Kinematic character, Kinematic target, float maxAcceleration){
		super(character, target, maxAcceleration);
		this.collisionDetector = collisionDetector;
		this.avoidDistance = avoidDistance;
		this.lookahead = lookahead;
	}
	
	@Override
	public SteeringOutput getSteering() {
		// TODO Auto-generated method stub
		// 1. Calculate the target to delegate to seek
		
		// Calculate the collision ray vector
		PVector rayVector = character.getVelocity();
		rayVector.normalize();
		rayVector.mult(lookahead);

		// Find the collision
		Collision collision = collisionDetector.getCollision(character.getPosition(), rayVector);
		
		// If have no collision, do nothing
		SteeringOutput steering = new SteeringOutput();
		if( collision == null ) return steering;
		
		// Otherwise create a target
		target.position = PVector.add( collision.position, PVector.mult(collision.normal, avoidDistance) );
		
		// 2. Delegate to seek
		return super.getSteering();
	}

}
