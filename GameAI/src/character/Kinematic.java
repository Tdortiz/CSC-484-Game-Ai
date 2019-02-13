package character;

import enums.CharacterState;
import enums.ObjectType;
import enums.SteeringState;
import graph.Vertex;
import path.Path;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.SteeringOutput;

public class Kinematic {
	
	public PVector position; 			// The character's position vector [ x, y ]
	public float orientation; 			// The current orientation in degrees 
	public PVector velocity; 			// The velocity vector [ x, y] 
	public float rotation;  			// The amount to rotate by 
	public PVector acceleration; 		// The acceleration vector [ a_x, a_y ] 
	public float angularAcc; 			// The Angular Acceleration 
	public float maxAcceleration = 5;	// The max acceleration can go
	public float maxSpeed = 5;			// the max speed a character can go
	
	/** State enums */
	public CharacterState state;
	public SteeringState steering;
	public ObjectType objectType;
	public int awakeMeter = 100;
	public int hungerMeter = 100;
	public Kinematic target;
	public Path path = new Path();
	public Vertex start = new Vertex();
	public Vertex goal = new Vertex();
	public int timeDancing = 0;
	public int maxTimeDancing = 4;
	public int timeSitting = 0;
	public int maxTimeSitting = 4;
	public long timeStartedActing;
	public boolean canDanceOrSitAgain = true;
	/** Look radius */
	public static final int MED_RADIUS_TO_ENEMY = 80;
	public static final int SM_RADIUS_TO_ENEMY = 45;
	public static final int MED_RADIUS_TO_TV = 100;
	public static final int SM_RADIUS_TO_TV = 25;

	
	public Kinematic(){
	    this.position = new PVector(0,0);
	    this.velocity = new PVector(0,0);
	    this.acceleration = new PVector(0,0);
	    this.objectType = ObjectType.INANIMATE_OBJECT;
	    this.steering = SteeringState.NONE;
	}
	
	public Kinematic(PVector position){
		this.position = position;
	}
	
	public Kinematic(int x, int y){
		this.position = new PVector(x,y);
		this.velocity = new PVector(0,0);
		this.objectType = ObjectType.INANIMATE_OBJECT;
		this.steering = SteeringState.NONE;
	}
	
	/**
	 * Constructor for a kinematic 
	 * @param position of character
	 * @param orientation of character
	 * @param velocity of character
	 * @param rotation of character
	 * @param acceleration of character
	 * @param angularAcc of character
	 * @param maxSpeed
	 * @param maxAcceleration
	 */
	public Kinematic(PVector position, float orientation, PVector velocity, float rotation, PVector acceleration, float angularAcc, float maxSpeed, float maxAcceleration) {
		setPosition(position);
	    setOrientation(orientation);
	    setVelocity(velocity);
	    setRotation(rotation);
	    setAcceleration(acceleration);
	    setAngularAcceleration(angularAcc);
	    this.maxSpeed = maxSpeed;
	    this.maxAcceleration = maxAcceleration;
	    this.objectType = ObjectType.INANIMATE_OBJECT;
	    this.steering = SteeringState.NONE;
	}
	
	/** -------------- BASIC KINEMATIC MOTION UPDATE -------------- */
	/**
	 * Standard update for kinematic motion
	 */
	public void update(){
	    // clip velocity
        if( velocity.mag() > maxSpeed ){
            velocity.normalize().mult(maxSpeed);
        }
        
        // clip acceleration
        if( acceleration.mag() > maxAcceleration ){
            acceleration.normalize().mult(maxAcceleration); 
        }
	    
        // update position & orientation
        this.position.add(this.velocity); 
        this.orientation += this.rotation;
        
        // update velocity and rotation
        this.velocity.add(this.acceleration);
        this.rotation += this.angularAcc;
        
        // move the object
        setPosition(new PVector(position.x, position.y));
    }
	
	/**
	 * Updates the kineamtic structure with a kinematic behavior standard update
	 * @param steeringOutput
	 */
	public void updateKinematic(SteeringOutput steeringOutput){
		setVelocity(steeringOutput.getVelocity());
		setRotation(steeringOutput.getRotation());
		update(); 
	}
	
	/**
	 * Updates the kinematic structure with a steering behavior
	 * @param behavior to update with
	 */
	public void update(SteeringOutput steeringOutput){
		setAcceleration(steeringOutput.acceleration);
        setAngularAcceleration(steeringOutput.angularAcc);
        update(); 
	}
	
	/** -------------- ORIENTATION  -------------- */
	/**
	 * Returns an orientation from a direction
	 * @param currentOrientation of the kinematic structure
	 * @param velocity to calculate orientation from
	 * @return new orientation
	 */
	public static float getNewOrientation( float currentOrientation, PVector velocity ){
		if( velocity.mag() != 0 ){
	    	return getOrientationFromDirection(velocity);
	    } else {
	        return currentOrientation;
	    }
	}
    
	/**
	 * Returns an orientation from a direction
	 * @param direction the kinematic is going
	 * @return an orientation from a direction
	 */
    public static float getOrientationFromDirection(PVector direction){
        float newOrientation = PApplet.atan2(direction.y, direction.x);
        return newOrientation;
    }
    
    /**
     * Calculates the direction based of the orientation
     * @param orientation of the kinematic
     * @return direction in form of a PVector
     */
    public static PVector getDirectionFromOrientation(float orientation){
        float xComp = PApplet.cos(orientation);
        float yComp = PApplet.sin(orientation);
        return new PVector(xComp, yComp).normalize();
    }
    
    /**
     * Maps a rotation value by shortest rotation to travel
     * @param changeOrientation orientation to change by
     * @return value between -pi and pi
     */
    public static float mapToRange(float changeOrientation){
    	float r = changeOrientation % (PApplet.PI*2);
    	if( Math.abs(r) <= PApplet.PI ){
    		return r;
    	} else {
    		if( r > PApplet.PI ){
    			return r-(PApplet.PI*2);
    		} else {
    			return r+(PApplet.PI*2);
    		}
    	}
    }
    
    /**
     * Retrieves a random binomial between -1 and 1
     * @return float value between -1 and 1
     */
    public static float randomBinomial(){
    	return (float) (Math.random() - Math.random());
    }
    
    /** -------------- GETTERS/SETTERS -------------- */
	public PVector getPosition(){ return this.position; }
	public void setPosition(PVector position){ this.position = position; }
	public float getOrientation(){ return this.orientation; }
	public void setOrientation(float orientation){ this.orientation = orientation; }
	public PVector getVelocity(){ return this.velocity; }
	public void setVelocity(PVector velocity){ this.velocity = velocity; }
	public float getRotation(){ return this.rotation; }
	public void setRotation(float rotation){ this.rotation = rotation; }
	public PVector getAcceleration(){ return this.acceleration; }
	public void setAcceleration(PVector acceleration){ this.acceleration = acceleration; }
	public float getAngularAcceleration(){ return this.angularAcc; }
	public void setAngularAcceleration(float angularAcceleration){ this.angularAcc = angularAcceleration; }
    public void setMaxAcceleration(float maxAcceleration) { this.maxAcceleration = maxAcceleration; }
    public float getMaxSpeed() { return this.maxSpeed; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }
    public float getMaxAcceleration() { return this.maxAcceleration; } 
    
    public String toString(){
        String ret = "";
        ret += "Position = \t" + position + "\n"; 
        ret += "Velocity = \t" + velocity + "\n"; 
        ret += "Accelera = \t" + acceleration + "\n"; 
        ret += "Orientat = \t" + orientation + "\n"; 
        ret += "Rotation = \t" + rotation + "\n"; 
        ret += "AngularA = \t" + angularAcc + "\n"; 
        
        return ret;
    }
    
    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        Kinematic other = (Kinematic) o;
        // field comparison
        return ( this.position.x == other.position.x && this.position.y == other.position.y);
    }

}