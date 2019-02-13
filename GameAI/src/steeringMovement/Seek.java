package steeringMovement;

import character.Kinematic;
import processing.core.PVector;

// Steering Seek - 58
public class Seek implements Behavior {
    
    // Static data for character and target
    protected Kinematic character;
    protected Kinematic target;
    // max speed character can travel
    protected float maxAcceleration;
    
    /**
     * Constructor for steering seek
     * @param character
     * @param target
     * @param maxAcceleration
     */
    public Seek(Kinematic character, Kinematic target, float maxAcceleration){
        this.character = character;
        this.target = target;
        this.maxAcceleration = maxAcceleration;
    }
    
    /**
     * 
     * @return SteeringOutput: acceleration=<?,?>, angularAcceleration=0
     */
    public SteeringOutput getSteering(){
        // Create structure for output
        SteeringOutput steering = new SteeringOutput();
        
        // get direction to the target
        steering.acceleration = PVector.sub(target.getPosition(), character.getPosition());
        
        // give full acceleration along the direction
        steering.acceleration.normalize().mult(maxAcceleration);
        
        // Output the steering
        steering.angularAcc = 0;
        return steering;
    }
    
    public void setTarget(Kinematic target){
        this.target = target;
    }
    
    public Kinematic getTarget(){
        return this.target;
    }
    
}
