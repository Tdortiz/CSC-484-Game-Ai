package collision;

import java.util.ArrayList;

import character.Kinematic;
import processing.core.PVector;
import steeringMovement.Behavior;
import steeringMovement.SteeringOutput;

// AIFG - 87
public class CollisionAvoidance implements Behavior {
    
    private Kinematic character; // holds kinematic data 
    private float maxAcceleration; // holds maximum acceleration
    private ArrayList<Kinematic> targets; // list of targets
    private float radius; // Holds the collision radius of a character (we assume all characters have the same radius here)
    
    /**
     * 
     * @param character
     * @param maxAcceleration
     * @param targets
     * @param radius
     */
    public CollisionAvoidance(Kinematic character, float maxAcceleration, ArrayList<Kinematic> targets, float radius) {
        this.character = character;
        this.maxAcceleration = maxAcceleration;
        this.targets = targets;
        this.radius = radius;
    }

    @Override
    public SteeringOutput getSteering() {
        // 1. Find the target that’s closest to collision
        // Store the first collision time
        float shortestTime = Integer.MAX_VALUE; // infinity;
        
        // Store the target that collides then, and other data
        // that we will need and can avoid recalculating
        Kinematic firstTarget = null;
        float firstMinSeparation = 0;
        float firstDistance = 0;
        PVector firstRelativePos = new PVector(0,0);
        PVector firstRelativeVel = new PVector(0,0);
        
        // loop through targets
        for( Kinematic target : targets ){
            // calculate the time to collision
            PVector relativePos = PVector.sub(target.getPosition(), character.getPosition());
            PVector relativeVel = PVector.sub(target.getVelocity(), character.getVelocity());
            float relativeSpeed = relativeVel.mag();
            float timeToCollision = PVector.dot(relativePos, relativeVel) / (relativeSpeed * relativeSpeed);
            
            // check if its going to be a collision at all
            float distance = relativePos.mag();
            float minSeparation = distance - relativeSpeed * shortestTime;
            if( minSeparation > 2 * radius ) continue;
            
            
            if( timeToCollision > 0 && timeToCollision < shortestTime ){
                // store the time, target, and other data
                shortestTime = timeToCollision;
                firstTarget = target;
                firstMinSeparation = minSeparation;
                firstDistance = distance;
                firstRelativePos = relativePos;
                firstRelativeVel = relativeVel;
            }
        }
        
        // 2. Calculate the steering
        SteeringOutput steering = new SteeringOutput();
        // if we have no target, then exit
        if( firstTarget == null ) return steering;
        
        // If we’re going to hit exactly, or if we’re already
        // colliding, then do the steering based on current position.
        PVector relativePos = new PVector(0,0);
        if( firstMinSeparation <= 0 || firstDistance < 2 * radius){
            relativePos = PVector.sub(firstTarget.getPosition(), character.getPosition());
        } else {
            relativePos = firstRelativePos.add(PVector.mult(firstRelativeVel, shortestTime));
        }
        
        // avoid the target
        relativePos.normalize();
        steering.acceleration = relativePos.mult(maxAcceleration);
        
        // return the steering
        return steering;
    }

}
