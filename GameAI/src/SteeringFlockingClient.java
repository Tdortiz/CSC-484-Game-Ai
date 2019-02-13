import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.SteeringOutput;
import steeringMovement.Wander;

import java.util.ArrayList;
import java.util.Random;

import character.Boid;
import character.Flock;
import character.Kinematic;

/**
 * Flocking - 98
 * https://processing.org/examples/flocking.html
 */
@SuppressWarnings("unused")
public class SteeringFlockingClient extends PApplet {
    
    private Flock flock;
    private final int flockSize = 250;
    private float radiusOfShape = 5.0f;
    private ArrayList<Kinematic> wanderLeaders;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"SteeringFlockingClient"});
    }

    public void settings() {
        size(1000, 800);
    }

    public void setup() {
        this.flock = new Flock(this);
        wanderLeaders = new ArrayList<Kinematic>();
        Random r = new Random();
        
        for(int i = 1; i <= 2; i++ ){
            //wanderLeaders.add(new Kinematic(new PVector(r.nextInt(width), r.nextInt(height)), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 2, 0.75f));
        }
            
        for(int i = 1; i <= flockSize/2; i++){
        	flock.addBoid( new Boid(r.nextInt(width),r.nextInt(height) ));
        	flock.addBoid( new Boid(width/2, height/2) );
        }
    }
    
    public void draw() {
    	background(50);
    	for( Kinematic wander : wanderLeaders ) updateWander(wander);
        flock.updateSteering(wanderLeaders);     // update the flock 
    }
    
    /**
     * Update the wander leader
     * @param wander
     */
    public void updateWander(Kinematic wander){
        SteeringOutput wanderOutput = new Wander(wander, 25, 100, PI/8, 0, wander.maxAcceleration).getSteering();
        wander.update(wanderOutput);
        checkWanderBoundary(wander);
        displayWander();
    }
    
    /**
     * On mouse click add a new boid to the flock
     */
    public void mousePressed() {
    	flock.addBoid(new Boid(mouseX,mouseY));
    }
    
    public void checkWanderBoundary(Kinematic character){
        float x = character.getPosition().x;
        float y = character.getPosition().y;
        
        if(x > width || x < 0 || y > height || y < 0){
            character.velocity.mult(-1);
            character.orientation += PApplet.PI;
        }
    }
    
    /**
     * Draw each wanderer
     */
    public void displayWander(){
    	for( Kinematic wander : wanderLeaders ){
            pushMatrix();
                translate(wander.position.x, wander.position.y);
                rotate(wander.getOrientation());
                fill(0, 0, 0);
                stroke(0, 0);
                ellipse(0, 0, radiusOfShape*2, radiusOfShape*2);
                triangle(3, 4, 10, 0, 3, -4);
            popMatrix();
        }
    }
 
}
