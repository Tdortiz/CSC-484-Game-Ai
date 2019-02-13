import java.util.LinkedList;
import java.util.Queue;

import character.Kinematic;
import kinematicMovement.KinematicArrive;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.SteeringOutput;

public class KinematicArriveClient extends PApplet {
    
    private int widthOffset = 50;
    private int heightOffset = 550;
    int frameCounter = 0;
    private Kinematic character;
    private Kinematic target;
    private Queue<float[]> breadcrumbs;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"KinematicArriveClient"});
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        character = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 5);
        target = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 5); 
        breadcrumbs = new LinkedList<float[]>();
        smooth();
    }
    
    public void printDebug(){
        background(100);
        noFill();
        ellipse(target.getPosition().x, target.getPosition().y, 50*2, 50*2); // draw ROS
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
            character.updateKinematic(output);
            display();
        popMatrix();
    }
    
    public SteeringOutput updateSteering(){
    	SteeringOutput output = new SteeringOutput();
        
        KinematicArrive arrive = new KinematicArrive(character, target, 5, 50);
        SteeringOutput arriveOutput = arrive.getSteering();
        
        output.setVelocity(arriveOutput.getVelocity());
        output.setRotation(arriveOutput.getRotation());
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
    
    // Creates a new bread crumb if needed and then prints bread crumbs out
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
