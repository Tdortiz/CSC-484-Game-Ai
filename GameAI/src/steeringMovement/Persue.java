package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

public class Persue extends Seek implements Behavior {

    // The max prediction time
    private float maxPrediction;
    
    public Persue(Kinematic character, Kinematic target, float maxAcceleration, float maxPrediction) {
        super(character, target, maxAcceleration);
        this.maxPrediction = maxPrediction;
    }

    @Override
    public SteeringOutput getSteering() {
        // 1. calculate the target to delegate to seek
        // work out the distance to the target
        PVector direction = PVector.sub(target.getPosition(), character.getPosition() );
        float distance = direction.mag();
        
        // work out our current speed
        float speed = character.getVelocity().mag();
        
        // check if speed is too small to give reasonable prediction
        float prediction = 0;
        if( speed <= distance / maxPrediction ){
            prediction = maxPrediction;
        } else {
            prediction = distance / speed;
        }
        
        // put the target together
        super.target.position.add( PVector.mult(target.velocity, prediction) );
        
        // 2. Delegate to seek
        return super.getSteering();
    }

}
