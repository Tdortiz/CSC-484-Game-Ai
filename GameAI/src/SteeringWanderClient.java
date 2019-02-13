import java.util.LinkedList;
import java.util.Queue;

import character.Kinematic;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.SteeringOutput;
import steeringMovement.Wander;

public class SteeringWanderClient extends PApplet {
    
    private int widthOffset = 50;
    private int heightOffset = 550;
    int frameCounter = 0;
    private Kinematic character;
    private Queue<float[]> breadcrumbs;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"SteeringWanderClient"});
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        character = new Kinematic(new PVector(widthOffset, heightOffset), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 5, 2.5f);
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
            character.update(output);
            checkBoundary();
            display();
        popMatrix();
    }
    
    public SteeringOutput updateSteering(){
        SteeringOutput output = new SteeringOutput();
        SteeringOutput wanderOutput = new Wander(character, 25, 100, PI/8, 0, character.maxAcceleration).getSteering();
        
        output = wanderOutput;
        return output;
    }
    
    public void display(){
        fill(0);
        ellipse(0, 0, 50, 50);
        triangle(15, 20, 50, 0, 15, -20);
    }
    
    public void checkBoundary(){
        float x = character.getPosition().x;
        float y = character.getPosition().y;
        
        if(x > width || x < 0 || y > height || y < 0){
            character.velocity.mult(-1);
            character.orientation += PApplet.PI;
        }
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
