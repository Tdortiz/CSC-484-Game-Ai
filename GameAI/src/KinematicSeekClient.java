import java.util.LinkedList;
import java.util.Queue;

import character.Kinematic;
import kinematicMovement.KinematicSeek;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.SteeringOutput;

public class KinematicSeekClient extends PApplet {
    
    private int widthOffset = 50;
    private int heightOffset = 550;
    int frameCounter = 0;
    private Kinematic character;
    private Kinematic target;
    private Queue<float[]> breadcrumbs;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"KinematicSeekClient"});
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        character = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 5);
        target = new Kinematic(new PVector(750, 550), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 5); 
        breadcrumbs = new LinkedList<float[]>();
        smooth();
    }

    public void draw() {
        background(100);
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
        
        float x = character.getPosition().x;
        float y = character.getPosition().y;
        
        // if we reached our target update it
        if( x == 750 && y == 550 ){
            target.setPosition(new PVector(750, 50));
        } else if( x == 750 && y == 50 ){
            target.setPosition(new PVector(50, 50));
        } else if( x == 50 && y == 50 ){
            target.setPosition(new PVector(50, 750));
        } else if( x == 50 && y == 550){
            target.setPosition(new PVector(750, 550));
        }
        
        KinematicSeek seek = new KinematicSeek(character, target, 5.0f);
        SteeringOutput seekOutput = seek.getSteering();
        
        output.setVelocity(seekOutput.getVelocity());
        output.setRotation(seekOutput.getRotation());
        return output;
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


    public void checkBoundary(){
        float x = character.getPosition().x;
        float y = character.getPosition().y;
        
        if(x > 800)
            character.setPosition(new PVector(0, y));
        if(x < 0)
            character.setPosition(new PVector(width, y));
        if(y > 600)
            character.setPosition(new PVector(x, 0));
        if(y < 0)
            character.setPosition(new PVector(x, height));
    }
 
}