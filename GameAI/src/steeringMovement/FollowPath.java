package steeringMovement;

import character.Kinematic;
import path.Path;
import processing.core.PVector;

public class FollowPath extends Arrive implements Behavior {
	
	// The path to follow
	private Path path;
	// Holds the distance along the path to generate the target. 
	// Can be negative if the character is to move along the reverse direction.
	private float pathOffset;
	// hold the current position on the path (where in the path we finished last iteration)
	private int currentIndex;
	// holds the time in the future in the future to predict the character's position
	private float predictTime = 0.1f;
	// other data derived from superclass
	
	/**
	 * Delegates behaviors to seek 
	 * @param character
	 * @param target
	 * @param maxAcceleration
	 * @param path to follow
	 * @param pathOffset Holds the distance along the path to generate the target. Negative if the character is to move along the reverse direction.
	 */
	public FollowPath(Kinematic character, Kinematic target, float maxAcceleration, Path path, float pathOffset){
		super(character, target, character.maxSpeed, character.maxAcceleration, 5, 25);
		//super(character, target, character.maxAcceleration);
		this.path = path;
		this.pathOffset = pathOffset;
	}
	
	@Override
	public SteeringOutput getSteering() {
	    // 1. Calculate the target to delegate to face
		
		// Find the predicted future location
		PVector futurePos = PVector.add(character.position, PVector.mult(character.velocity, predictTime));
		
		// find the position on the path we'll be in the future
		currentIndex = path.getIndex(currentIndex, futurePos);
		
		// Offset it, don't use the field variable to avoid moving through the path too quick
		int currentIndexWithOffset = currentIndex + 1;
		
		// Get and set the target position for seek to seek
		Kinematic newTarget = new Kinematic();
		newTarget.position = path.getPosition(currentIndexWithOffset);
		target = newTarget;
		
		//target.position = path.getPosition(currentIndex);
	
		// 2. Delegate to seek
		return super.getSteering();
	}

}
