import java.util.LinkedList;
import java.util.Queue;

import character.Kinematic;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.Arrive;
import steeringMovement.LookWhereYoureGoing;
import steeringMovement.SteeringOutput;

public class SteeringArriveClient extends PApplet {

    private int widthOffset = 50;
    private int heightOffset = 550;
    int frameCounter = 0;
    private Kinematic character;
    private Kinematic target;
    private Queue<float[]> breadcrumbs;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"SteeringArriveClient"});
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
    	character = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, .15f);
    	target = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 5); 
    	breadcrumbs = new LinkedList<float[]>();
        smooth();
    }
    
    public void printDebug(){
        background(100);
        noFill();
        
        ellipse(target.getPosition().x, target.getPosition().y, 25*2, 25*2); // draw ROS
        ellipse(target.getPosition().x, target.getPosition().y, 150*2, 150*2); // draw ROS
    }

    public void draw() {
        printDebug();
        breadCrumbUpdate(); 

        // Update the shape
        pushMatrix();
            // move origin to temporary pivot point 
            translate(character.getPosition().x, character.getPosition().y);
            // rotate the grid
            rotate(character.getOrientation());
            // Update coordinates and display the compass
            SteeringOutput output = updateSteering();
            character.update(output);
            display();
        popMatrix();
    }
    
    public SteeringOutput updateSteering(){
        SteeringOutput output = new SteeringOutput();
        
        SteeringOutput arriveOutput = new Arrive(character, target, character.maxSpeed, character.maxAcceleration, 25, 150).getSteering();
        SteeringOutput lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, target, PI/32, PI/128, PI/32, PI/16).getSteering();
        
        output.acceleration = arriveOutput.getAcceleration();
        output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
        return output;
    }
    
    public void mousePressed(){
		target.setPosition( new PVector(mouseX, mouseY) );
	}
    
    public void display(){
    	fill(0);
		ellipse(0, 0, 50, 50);
		triangle(15, 20, 50, 0, 15, -20);
    }
    
    /**
     * Creates a new bread crumb if needed and then prints bread crumbs out
     */
    public void breadCrumbUpdate(){
        fill(0);
        if( frameCounter % 15 == 0){
            float[] newBreadCrumb = { this.character.getPosition().x, this.character.getPosition().y, character.getOrientation() };
            if( breadcrumbs.size() == 10 ){
                breadcrumbs.remove();
            }
            breadcrumbs.add(newBreadCrumb);
        }
        frameCounter++;
        
        for( float[] coords : this.breadcrumbs ){
        	pushMatrix();
        		translate(coords[0], coords[1]);
        		rotate(coords[2]);
	            rectMode(CENTER);
	            rect(0, 0, 8, 8);
	            triangle(0, 0+8, 0+8, 0+0, 0, 0-8);
            popMatrix();
        }
    }
 
}
