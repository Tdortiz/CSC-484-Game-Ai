package character;

import java.util.ArrayList;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.Arrive;
import steeringMovement.Behavior;
import steeringMovement.BlendedSteering;
import steeringMovement.BlendedSteering.BehaviorAndWeight;
import steeringMovement.Cohesion;
import steeringMovement.CohesionColor;
import steeringMovement.Separation;
import steeringMovement.SteeringOutput;
import steeringMovement.VelocityMatch;
import steeringMovement.Wander;

public class Boid extends Kinematic {
    
    public float separationWeight       = 0.60f;
    public float cohesionWeight         = 0.20f;
    public float cohesionColorWeight    = 0.05f;
    public float velocityMatchWeight    = 0.15f;
    ArrayList<Kinematic> wanderLeaders;
    public int[] color;
	
    /**
     * Creates a new boid at pos x and pos y
     * @param x position
     * @param y position
     */
	public Boid(float x, float y) {
	    maxSpeed = 2f;
        maxAcceleration = 0.15f;
        position = new PVector(x, y);
		velocity = PVector.random2D().mult(maxSpeed);
		Random rand = new Random();
		color = new int[]{ rand.nextInt(255), rand.nextInt(255), rand.nextInt(255) };
	}
	
	public void updateSteering(ArrayList<Kinematic> boids, ArrayList<Kinematic> wanderLeaders) {
		this.wanderLeaders = wanderLeaders; 
		SteeringOutput output = getSteeringOutput(boids);
		update(output);
	}
	
	/**
	 * Update the steering by blending separation, cohesion, and velocityMatching
	 * @param boids kinematic information
	 * @return SteeringOutput to direct the current boid
	 */
	public SteeringOutput getSteeringOutput(ArrayList<Kinematic> boids){
	    ArrayList<BehaviorAndWeight> bevs = new ArrayList<BehaviorAndWeight>();
	    BlendedSteering blend = new BlendedSteering(bevs, maxAcceleration, 0);
	    
	    // Separation
	    Behavior behavior = new Separation(this, boids, 25, 250, maxAcceleration);
	    BehaviorAndWeight bev = blend.new BehaviorAndWeight(behavior, separationWeight);  
	    bevs.add(bev);
	    
	    // Cohesion
	    behavior = new Cohesion(this, boids, 50);
	    bev = blend.new BehaviorAndWeight(behavior, cohesionWeight); 
	    bevs.add(bev);
	    
	    // Velocity Match
	    behavior = getVelocityMatchFlock(boids, 50);
	    
	    // If we have some wander leaders follow them instead of velocity matching
	    if( !wanderLeaders.isEmpty() )
	        behavior = followTheClosestWanderer(boids);
	    
	    bev = blend.new BehaviorAndWeight(behavior, velocityMatchWeight);
	    bevs.add(bev); 
	    
	    // Color Cohesion
	    behavior = new CohesionColor(this, boids, 200);
	    bev = blend.new BehaviorAndWeight(behavior, cohesionColorWeight);
	    bevs.add(bev);
	    
	    return blend.getSteering();
	}
	
	/**
	 * Returns a velocityMatch based on the flock
	 * @param boids kinematic structures
	 * @return a velocityMatch algorithm
	 */
	public Behavior getVelocityMatchFlock(ArrayList<Kinematic> boids, float neighborDist){
        PVector sum = new PVector(0,0);
        int count = 0;
          
        for( Kinematic other : boids){
            float d = PVector.dist(position, other.position);
            if ( (d > 0) && (d < neighborDist) ) {
                sum.add(other.velocity);
                count++;
            }
        }
          
        if( count > 0 ){
            sum.div(count);
            Kinematic target = new Kinematic();
            target.setVelocity(sum);
            VelocityMatch veloictyMatch = new VelocityMatch(this, target, maxAcceleration);
            return veloictyMatch;
        } else { // wander if there is no one nearby
            return new Wander(this, 100, 25, PApplet.PI/4, 0, maxAcceleration);
        }
    }
	
	/**
	 * Returns a velocityMatch based on the flock
	 * @param boids kinematic structures
	 * @return a velocityMatch algorithm
	 */
	public Arrive followTheClosestWanderer(ArrayList<Kinematic> boids){
		Kinematic target = null;

        float closestTargetDistance = Integer.MAX_VALUE;
        for( Kinematic wander : wanderLeaders ){
        	float d = PVector.dist(position, wander.position);
            if ( d > 0 && d < closestTargetDistance ) {
                closestTargetDistance = d;
                target = wander;
            }
        }
        
        return new Arrive(this, target, maxSpeed, maxAcceleration, 25, 150);
    }
	
}