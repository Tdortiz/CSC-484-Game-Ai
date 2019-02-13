package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

public class VelocityMatch implements Behavior {
    
    private Kinematic character;
    private Kinematic target;
    private float maxAcceleration;
    private float timeToTarget = 1/10f;

    public VelocityMatch(){
        character = new Kinematic();
        target = new Kinematic();
        maxAcceleration = 5;
    }
    
    public VelocityMatch(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering() {
        return getSteering(target);
    }
    
    public SteeringOutput getSteering(Kinematic target){
        // create structure to hold data
        SteeringOutput steering = new SteeringOutput();
        
        // acceleration tries to get to the target velocity
        steering.acceleration = PVector.sub(target.getVelocity(), character.getVelocity()).div(timeToTarget);
        
        // check if the acceleration is too fast
        if( steering.acceleration.mag() > maxAcceleration ){
            steering.acceleration = steering.acceleration.normalize().mult(maxAcceleration);
        }
        
        // output the steering
        steering.angularAcc = 0;
        return steering;
    }

}
