import java.util.LinkedList;
import java.util.Queue;

import org.jgrapht.graph.DefaultWeightedEdge;

import character.Kinematic;
import graph.Graph;
import graph.Vertex;
import heuristics.ConstantHeuristic;
import heuristics.EuclideanHeuristic;
import heuristics.RandomHeuristic;
import path.Path;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.Arrive;
import steeringMovement.FollowPath;
import steeringMovement.LookWhereYoureGoing;
import steeringMovement.SteeringOutput;

public class PathFollowingClient extends PApplet {
    
	private static final int backgroundColor = 200;
    private static final int floorColor = 150;
    private static final int wallColor = 100;
    private static final int dividerWallColor = 0;
    private static final int miscColor = 175;
    private int frameCounter = 0;
    private Graph g;
    private Kinematic character;
    private Kinematic target;
    private Queue<float[]> breadcrumbs;
    private float radiusOfShape = 5.0f;
    FollowPath followPath;
    private Vertex start;
    private Vertex goal;
    private Path path;
    
    public static void main(String[] args) {
        PApplet.main(new String[] {"PathFollowingClient"});
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
    	character = new Kinematic(new PVector(500, 75), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 1.25f, .25f);
    	target = new Kinematic(new PVector(500, 75), 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 0, 0); 
    	breadcrumbs = new LinkedList<float[]>();
    	drawBackGround();
    	
    	// create a new graph
    	g = new Graph();
    	g.createGraph("graphs/small_graph2.txt");
    	
    	// initial path work
        path = new Path();
        start = g.getClosestPointOnGraph(character.position);
        goal  = g.getClosestPointOnGraph(target.position);
    }
    	
	/**
	 * Draw the background which includes 
	 * graph and world representation
	 */
	public void drawBackGround(){
    	background(backgroundColor);
    	rectMode(CORNER);
    	
    	strokeWeight(4);
    	
    	// Draw overall apartment 
    	fill(floorColor);
    	rect(25 , 50, 750, 500);
    	
    	// Draw all the solid walls
    	fill(wallColor);
    	rect(225, 50, 50, 150);
    	rect(525, 50, 50, 150);
    	
    	// Draw the divider walls
    	stroke(dividerWallColor);
    	// right bathroom walls
    	line(575, 200, 675, 200);
    	line(725, 200, 775, 200);
    	line(575, 200, 575, 50);
    	// left bathroom walls
    	line(25, 200, 75, 200);
    	line(125, 200, 225, 200);
    	line(225, 200, 225, 50);
    	
    	// right rooms walls
    	line(525, 350, 600, 350);
    	line(650, 350, 650, 550);
    	line(715, 350, 775, 350);
    	line(525, 350, 525, 550);
    	
    	// left rooms walls
    	line(25, 350, 100, 350);
    	line(150, 350, 150, 550);
    	line(200, 350, 275, 350);
    	line(275, 350, 275, 550);
    	
    	// Draw all the misc objects
    	fill(miscColor);
    	noStroke();
    	// kitchen counter
    	rect(275, 50, 50, 150);
    	rect(325, 50, 150, 50);
    	// couch
    	rect(475, 350, 50, 200);
    	rect(400, 500, 75, 50);
    	// tv
    	rect(275, 400, 50, 75);
    	// table
    	ellipseMode(CENTER);
    	ellipse(width/2, height/2-25, 100, 50);
    	
    	strokeWeight(1);
    	
    	// Draw Graph
    	fill(0);
    	
    	
    	
    	noStroke();
    	fill(0);
    	// Draw Debug Mouse Position Text
    	text(mouseX + " " + mouseY, width/2-20, height/2-25);
    	
    	noFill();
        stroke(100);
        fill(color(255, 0, 255));
    	ellipse(target.getPosition().x, target.getPosition().y, 10*2, 10*2); // draw ROS
    	noStroke();
    }
	
	public void drawGraph(){
		// Draw Edges
    	stroke(dividerWallColor);
    	for( DefaultWeightedEdge e : g.graph.edgeSet() ){
    		Vertex v1 = g.graph.getEdgeSource(e);
    		Vertex v2 = g.graph.getEdgeTarget(e);
    		
    		// draw a line
    		line(v1.x, v1.y, v2.x, v2.y);
    		
    		// Draw an arrow signifying direction of edge
    		pushMatrix();
    			fill(color(255, 0, 0)); 
    			translate( (v1.x + v2.x)/2, (v1.y+v2.y)/2);
    			rotate(atan2(v2.y-v1.y, v2.x-v1.x));
    			//triangle(0, 0, -5, 2.5f, -5, -2.5f);
    		popMatrix(); 
    	
    	}
    	
    	// Draw vertices
    	for( Vertex v : g.graph.vertexSet() ){
    		fill(0);
    		ellipse(v.x, v.y, 5, 5);
    		fill(color(255, 204, 0)); 
    		text(v.id, v.x+2, v.y-2);
    	}
    	
    	noFill();
    	noStroke();
	}
    
    public void draw() {
    	drawBackGround();
    	drawGraph();
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
        
        // Get Acceleration for that path
        SteeringOutput followPathOutput = new SteeringOutput();
        SteeringOutput lookWhereYoureGoingOutput = new SteeringOutput();
        
        // Check if the path finding returned anything
        if( !path.getPath().isEmpty() ){
            followPathOutput = followPath.getSteering(); //= new FollowPath(character, target, character.maxAcceleration, path, 1).getSteering();
            lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, target, PI/32, PI/128, PI/32, PI/16).getSteering();
        } else { // run one final arrive to move toward the last point on the path
        	Kinematic goalKinematic = new Kinematic();
        	goalKinematic.position = new PVector(goal.x, goal.y);
        	
        	SteeringOutput arrive = new Arrive(character, goalKinematic, character.maxSpeed, character.maxAcceleration, 5, 25).getSteering();
        	lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, target, PI/32, PI/128, PI/32, PI/16).getSteering();
        	
        	output.acceleration = arrive.getAcceleration();
        	output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
        	return output;
        }
        
        output.acceleration = followPathOutput.getAcceleration();
        output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
        return output;
    }
    
    /**
     * On mouse click move toward that point
     */
    public void mousePressed(){
		target.setPosition( new PVector(mouseX, mouseY) );
		
		// Find path by quantizing the start and goal
		start = g.getClosestPointOnGraph(character.position);
        goal  = g.getClosestPointOnGraph(target.position);
        
        System.out.println("Character is going from (" + start.id + ") to (" + goal.id + ")");
		
        /// TODO Pick Your Path Finding Algorithm Here
        //path = g.pathfindAStar(g, start, goal, new EuclideanHeuristic(goal) );
        //path = g.pathfindAStar(g, start, goal, new ConstantHeuristic(goal, 1.5f) );
        //path = g.pathfindAStar(g, start, goal, new RandomHeuristic(goal, 3f) );
        //path = g.pathfindDijkstra(g, start, goal);
        path = g.pathfindNodeArrayAStar(g, start, goal, new EuclideanHeuristic(goal) );
        
        
        System.out.println("The Path is : " + path + "\n");
        
        followPath = new FollowPath(character, target, character.maxAcceleration, path, 1);
	}
    
    /**
     * Draw the shape
     */
    public void display(){
    	fill(color(255, 0, 0));
    	ellipse(0, 0, radiusOfShape*2, radiusOfShape*2);
        triangle(3, 4, 10, 0, 3, -4);
    }
    
    /**
     * Creates a new bread crumb if needed and then prints bread crumbs out
     */
    public void breadCrumbUpdate(){
    	fill(color(255, 0, 255));
        if( frameCounter % 30 == 0){
            float[] newBreadCrumb = { this.character.getPosition().x, this.character.getPosition().y, character.getOrientation() };
            if( breadcrumbs.size() == 15 ){
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
	            //rect(0, 0, 4, 4);
	            triangle(0, 0+5, 0+10, 0+0, 0, 0-5);
            popMatrix();
        }
    }
 
}
