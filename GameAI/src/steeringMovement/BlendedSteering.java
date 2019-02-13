package steeringMovement;

import java.util.ArrayList;

public class BlendedSteering implements Behavior {
	
	public class BehaviorAndWeight{
		private Behavior behavior;
		private float weight;
		
		public BehaviorAndWeight(Behavior behavior, float weight){
			this.behavior = behavior;
			this.weight = weight;
		}
	}
	
	// hold a list of behaviorAndWeight instances.
	private ArrayList<BehaviorAndWeight> behaviors;
	// hold maximum acceleration and rotation
	private float maxAcceleration;
	private float maxRotation;
	
	public BlendedSteering(ArrayList<BehaviorAndWeight> bevs, float maxAcceleration, float maxRotation){
	    this.behaviors = bevs;
	    this.maxAcceleration = maxAcceleration;
	    this.maxRotation = maxRotation;
	}

	@Override
	public SteeringOutput getSteering() {
		// create a new steering structure
		SteeringOutput steering = new SteeringOutput();
		
		// accumulate all acceleration
		for( BehaviorAndWeight behavior : behaviors ){
			SteeringOutput behaviorSteering = behavior.behavior.getSteering();
			behaviorSteering.weightSteering(behavior.weight);
			steering = steering.add( behaviorSteering );
		}
		
		// crop the result and return
		if( steering.acceleration.mag() > maxAcceleration ){
			steering.acceleration.normalize().mult(maxAcceleration);
		}
		steering.angularAcc = Math.max( steering.angularAcc, maxRotation );
		return steering;
	}

}
