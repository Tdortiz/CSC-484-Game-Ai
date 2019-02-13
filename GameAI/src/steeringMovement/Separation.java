package steeringMovement;

import java.util.ArrayList;

import character.Kinematic;
import processing.core.PVector;

/**
 * AIFG - Separation - 82
 */
public class Separation implements Behavior {

    // Kinematic data for character
    private Kinematic character;
    // hold list of potential targets
    private ArrayList<Kinematic> targets;
    // hold the threshold to take action 
    private float threshold;
    // hold the constant coefficient of decay of the inverse square law
    private float decayCoefficient;
    // holds the maximum acceleration of the character
    private float maxAcceleration;
    
    /**
     * Separation Constructor
     * @param character
     * @param targets
     * @param threshold
     * @param decayCoefficient
     * @param maxAcceleration
     */
    public Separation(Kinematic character, ArrayList<Kinematic> targets, float threshold, float decayCoefficient, float maxAcceleration) {
        this.character = character;
        this.targets = targets;
        this.threshold = threshold;
        this.decayCoefficient = decayCoefficient;
        this.maxAcceleration = maxAcceleration;
    }

    @Override
    public SteeringOutput getSteering() {
        // The steering variable holds the output
        SteeringOutput steering = new SteeringOutput();
        
        /** old version 
        steering.acceleration.mult(0);
        
        // Loop through each target
        for( Kinematic target : targets){
            
            // Check if the target is close
            float distance = PVector.dist(character.position, target.position);
            if( (distance < threshold) && (distance > 0) ){
            	PVector diff = PVector.sub(character.position, target.position);
            	diff.normalize();
            	diff.div(distance);
                // Add the acceleration
                steering.acceleration.add( diff );
            }
        }
        
        if( steering.acceleration.mag() > 0 ){
        	steering.acceleration.setMag(character.maxSpeed);
        	steering.acceleration.sub(character.velocity);
        	steering.acceleration.limit(maxAcceleration);
        } */
        for( Kinematic target : targets ){
            // check if target is close
            PVector direction = PVector.sub(character.position, target.position);
            float distance = direction.mag();
            if( (distance < threshold) && (distance > 0) ){
                float strength = Math.min(decayCoefficient / (distance * distance), maxAcceleration);
                
                // add acceleration
                direction.normalize();
                steering.acceleration.add( PVector.mult(direction, strength) );
            }
        }
         
        // We’ve gone through all targets, return the result
        return steering;
    }

}
