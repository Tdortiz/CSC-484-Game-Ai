package steeringMovement;

import processing.core.PVector;

public class SteeringOutput {
    public PVector velocity;
    public float rotation;
    public PVector acceleration;
    public float angularAcc;       
    
    public SteeringOutput(){
        this.velocity = new PVector(0,0);
        this.rotation = 0;
        this.acceleration = new PVector(0,0);
        this.angularAcc = 0;
    }
    
    public SteeringOutput(PVector velocity, float rotation, PVector acceleration, float angularAcc){
        this.velocity = velocity;
        this.rotation = rotation;
        this.acceleration = acceleration;
        this.angularAcc = angularAcc;
    }
    
    public SteeringOutput add(SteeringOutput otherSteering){
    	SteeringOutput steering = new SteeringOutput();
    	steering.acceleration = this.acceleration.add(otherSteering.acceleration);
    	steering.velocity = this.velocity.add(otherSteering.velocity);
    	steering.angularAcc += otherSteering.angularAcc;
    	steering.rotation += otherSteering.rotation;
    	return steering;
    }
    
    public void weightSteering(float weight){
    	acceleration = acceleration.mult(weight);
    	angularAcc = angularAcc * weight;
    }
    
    public PVector getVelocity() { return velocity; }
    public void setVelocity(PVector velocity) { this.velocity = velocity; }
    public float getRotation() { return rotation; }
    public void setRotation(float rotation) { this.rotation = rotation; }
    public PVector getAcceleration() { return acceleration; }
    public void setAcceleration(PVector acceleration) { this.acceleration = acceleration; }
    public float getAngularAcc() { return angularAcc; }
    public void setAngularAcc(float angularAcc) { this.angularAcc = angularAcc; } 
}
