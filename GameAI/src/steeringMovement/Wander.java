package steeringMovement;

import character.Kinematic;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * AIFG -74
 */
public class Wander extends Face implements Behavior {
	// holds the radius and forward offset of the wander circle
	float wanderOffset;
	float wanderRadius;
	// holds maximum rate at which the wander orientation can change (0, PI]
	float wanderRate;
	// hold the current orientation of the wander target
	float wanderOrientation;
	// holds the max acc of the character
	float maxAcceleration;
	
	/**
	 * 
	 * @param character kinematic data
	 * @param wanderOffset forward offset of the wander circle
	 * @param wanderRadius radius of the wander circle
	 * @param wanderRate maximum rate at which the orientation can change (0, PI]
	 * @param wanderOrientation current orientation of the wander target
	 * @param maxAcceleration max acceleration of the character
	 */
	public Wander(Kinematic character, float wanderOffset, float wanderRadius, float wanderRate, float wanderOrientation, float maxAcceleration) {
		super(character, null, PApplet.PI/64, PApplet.PI/32, PApplet.PI/16, PApplet.PI/8);
		this.wanderOffset = wanderOffset;
        this.wanderRadius = wanderRadius;
        this.wanderRate = wanderRate;
        this.wanderOrientation = wanderOrientation;
        this.maxAcceleration = maxAcceleration;
    }
	
	public SteeringOutput getSteering(){
		// 1. calculate target to delegate to face
		// update wander orientation
		wanderOrientation += ( Kinematic.randomBinomial() * wanderRate );
		
		// calculate combined target orientation
		float targetOrientation = wanderOrientation + character.orientation;
		
		// calculate the center of the wander circle
		PVector targetCenter = PVector.add( character.position, PVector.mult( Kinematic.getDirectionFromOrientation(character.orientation), wanderOffset) );
		
		// calculate the target location
		targetCenter.add( PVector.mult( Kinematic.getDirectionFromOrientation(targetOrientation), wanderRadius) );
		
		// 2. Delegate to face
		Kinematic wanderTarget = new Kinematic();
		wanderTarget.setPosition(targetCenter);
		super.target = wanderTarget;
		SteeringOutput steering = super.getSteering(); 
		
		// 3.  Now set the linear acceleration to be at full acc in direction of orientation
		steering.acceleration = PVector.mult(Kinematic.getDirectionFromOrientation(character.orientation), maxAcceleration);
		
		return steering;
	}
}
