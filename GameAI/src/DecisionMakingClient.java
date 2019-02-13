import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import org.jgrapht.graph.DefaultWeightedEdge;

import character.Kinematic;
import enums.ObjectType;
import enums.SteeringState;
import graph.Graph;
import graph.Vertex;
import heuristics.EuclideanHeuristic;
import processing.core.PApplet;
import processing.core.PVector;
import steeringMovement.Arrive;
import steeringMovement.Face;
import steeringMovement.FollowPath;
import steeringMovement.LookWhereYoureGoing;
import steeringMovement.SteeringOutput;
import tree_behaviorTree.BehaviorTree;
import tree_behaviorTree.PerformAction;
import tree_behaviorTree.Question;
import tree_behaviorTree.RandomSelector;
import tree_behaviorTree.Selector;
import tree_behaviorTree.Sequence;
import tree_behaviorTree.Task;
import tree_decisionTree.DecisionTree;
import tree_decisionTree.DecisionTreeLearner;
import tree_decisionTree.MonsterAction;
import tree_decisionTree.MonsterDecisionNode;
import tree_decisionTree.MonsterDecisionTree;

public class DecisionMakingClient extends PApplet {

	private static final int backgroundColor = 200;
	private static final int floorColor = 150;
	private static final int wallColor = 100;
	private static final int dividerWallColor = 0;
	private static final int miscColor = 175;
	private Graph g;
	
	/** world objects */
	private HashMap<String, HashMap<String, Kinematic>> world_objects;
	private final String FRIENDLIES = "friendlies";
	private final String ENEMIES = "enemies";
	private final String INANIMATE_OBJECTS = "inanimate_objects";
	
	private Kinematic tv;
	private Kinematic character;
	private Kinematic enemy;
	private PVector playerStart = new PVector(500, 75);
	private PVector enemyStart = new PVector(75, 100);
	private DecisionTree character_dt;
	private BehaviorTree enemy_bt;
	private MonsterDecisionTree enemy_learned_dt;
	private DecisionTreeLearner dt_learner;
	private Queue<float[]> characterBreadcrumbs;
	private Queue<float[]> enemyBreadcrumbs;
	private float radiusOfShape = 5.0f;
	private Random r = new Random();
	
	// True/False values for showing/doing stuff
	private static final boolean DEBUG = false;
	private boolean showGraph = false;
	private boolean useEnemyDT = false;
	private boolean useEnemyBT = true;

	public static void main(String[] args) {
		if( DEBUG ){
			try {
				System.setOut(new PrintStream(new FileOutputStream("log/output-tv-enemy-dist.txt")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("ENEMY-DIST\tTV-DIST\tCAN-ACT\tR-ACTION");
		}
		PApplet.main(new String[] { "DecisionMakingClient" });
	}

	
	public void settings() {
		size(800, 700);
	}

	public void setup() {
		// create a new graph
		g = new Graph();
		g.createGraph("small_graph.txt");

		// Create base hashmaps
		world_objects = new HashMap<String, HashMap<String, Kinematic>>();
		HashMap<String, Kinematic> friendlies = new HashMap<String, Kinematic>();
		HashMap<String, Kinematic> enemies = new HashMap<String, Kinematic>();
		HashMap<String, Kinematic> inanimate_objects = new HashMap<String, Kinematic>();
		
		// Add base hasmaps to world_objects
		world_objects.put(FRIENDLIES, friendlies);
		world_objects.put(ENEMIES, enemies );
		world_objects.put(INANIMATE_OBJECTS, inanimate_objects);
		
		// Create "character" objects
		character = new Kinematic(playerStart, 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 1.25f, .25f);
		character.objectType = ObjectType.PLAYER;
		characterBreadcrumbs = new LinkedList<float[]>();
		character.target = new Kinematic(playerStart, 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 0, 0);
		character.start = g.getClosestPointOnGraph(character.position);
		character.goal = g.getClosestPointOnGraph(character.target.position);
		
		enemy = new Kinematic(enemyStart, 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 1.5f, .50f);
		enemy.target = new Kinematic(enemyStart, 0, new PVector(0, 0), 0, new PVector(0, 0), 0, 1.5f, .25f);
		enemyBreadcrumbs = new LinkedList<float[]>();
		enemy.objectType = ObjectType.ENEMY;
		enemy.start = g.getClosestPointOnGraph(enemy.position);
		enemy.goal = g.getClosestPointOnGraph(enemy.target.position);
		
		// Add characters to their lists
		world_objects.get(FRIENDLIES).put("player", character);
		world_objects.get(ENEMIES).put("enemy", enemy);
		
		// Create environment objects (chairs, tables, ect)
		Kinematic chair1 = new Kinematic(400, 240);
		Kinematic chair2 = new Kinematic(400, 310);
		Kinematic stove = new Kinematic(365,110);
		Kinematic refrigerator = new Kinematic(450,110);
		Kinematic table = new Kinematic(width/2, height/2);
		Kinematic bed1 = new Kinematic(105,525);
		Kinematic bed2 = new Kinematic(230,525);
		Kinematic bed3 = new Kinematic(605,525);
		Kinematic bed4 = new Kinematic(725,525);
		Kinematic toiletLeft = new Kinematic(60,100);
		Kinematic toiletRight = new Kinematic(745,100);
		Kinematic sink = new Kinematic(340,150);
		Kinematic couch = new Kinematic(475,430);
		tv = new Kinematic(350, 437);
		
		world_objects.get(INANIMATE_OBJECTS).put("chair1", chair1);
		world_objects.get(INANIMATE_OBJECTS).put("chair2", chair2);
		world_objects.get(INANIMATE_OBJECTS).put("couch", couch);
		world_objects.get(INANIMATE_OBJECTS).put("tv", tv);
		world_objects.get(INANIMATE_OBJECTS).put("stove", stove);
		world_objects.get(INANIMATE_OBJECTS).put("refrigerator", refrigerator);
		world_objects.get(INANIMATE_OBJECTS).put("sink", sink);
		world_objects.get(INANIMATE_OBJECTS).put("toiletLeft", toiletLeft);
		world_objects.get(INANIMATE_OBJECTS).put("toiletRight", toiletRight);
		world_objects.get(INANIMATE_OBJECTS).put("table", table);
		world_objects.get(INANIMATE_OBJECTS).put("bed1", bed1);
		world_objects.get(INANIMATE_OBJECTS).put("bed2", bed2);
		world_objects.get(INANIMATE_OBJECTS).put("bed3", bed3);
		world_objects.get(INANIMATE_OBJECTS).put("bed4", bed4);

		createTrees();
	}
	
	/**
	 * Create trees for red and green
	 */
	public void createTrees(){
		// create the character's decision tree
		character_dt = new DecisionTree(world_objects, character);
		character_dt.fileToTree("mainCharacterDecisionTree.txt");
		
		// Create enemy's behavior tree
		enemy_bt = new BehaviorTree(world_objects, enemy);
		
		// Create behavior nodes
		Task a0 = new Selector(enemy, "Not Acting?");
			Question b0 = new Selector(enemy, "Near Enemy?");
				Task c0 = new Sequence(enemy, "Kill Enemy");
					Task d0 = new PerformAction(enemy, "Pursue Enemy");
					Question d3 = new Sequence(enemy, "Very close to enemy?");
						Task e2 = new PerformAction(enemy, "Arrive at enemy");
			Question b1 = new Selector(enemy, "Near TV?");
				Question c1 = new Sequence(enemy, "Can act again?");
					Task d1 = new PerformAction(enemy, "Pathfind to TV");
					Question d2 = new RandomSelector(enemy, "Finish Pathfinding to TV?");
						Task e0 = new PerformAction(enemy, "Sit");
						Task e1 = new PerformAction(enemy, "Dance");
			Question b2 = new Selector(enemy, "Not near TV or Player?");
				Task c4 = new PerformAction(enemy, "Wander");
		
		// Add behavior nodes
		enemy_bt.root = a0;
		a0.addChild(b0);
		a0.addChild(b1);
		a0.addChild(b2);
		
		b0.addChild(c0);
		b1.addChild(c1);
		b2.addChild(c4);
		
		c0.addChild(d0);
		c0.addChild(d3);
		c1.addChild(d1);
		c1.addChild(d2);
		
		d2.addChild(e0);
		d2.addChild(e1);
		d3.addChild(e2);		
		
		if( !DEBUG ){
			character_dt.print(character_dt.root);
			enemy_bt.printNulls(enemy_bt.root);	
			//dt_learner = new DecisionTreeLearner("output-tv-enemy-dist.txt"); // TODO: don't need this to run every time but I take what it outputed and created it in createLearnedTree()
			this.enemy_learned_dt = createLearnedTree();
		}	
	}

	public MonsterDecisionTree createLearnedTree(){
		MonsterDecisionTree dt = new MonsterDecisionTree(this.world_objects, this.enemy);
		// "Can Act?" , "TV Act?", "Enemy Dist?"
		MonsterDecisionNode a0 = new MonsterDecisionNode("Can Act?"); a0.populateChildren();
			MonsterDecisionNode b0 = new MonsterDecisionNode("TV Dist?"); b0.populateChildren();
				MonsterAction c0 = new MonsterAction("Pathfind Target");  
				MonsterDecisionNode c1 = new MonsterDecisionNode("Enemy Dist?"); c1.populateChildren();
					MonsterAction d0 = new MonsterAction("Arrive Target"); 
					MonsterAction d1 = new MonsterAction("Pathfind Target"); 
					MonsterAction d2 = new MonsterAction("Pathfind Target"); 
				MonsterDecisionNode c2 = new MonsterDecisionNode("Enemy Dist?"); c2.populateChildren();
					MonsterAction d3 = new MonsterAction("Arrive Target"); 
					MonsterAction d4 = new MonsterAction("Wander"); 
					MonsterAction d5 = new MonsterAction("Wander"); 
			MonsterDecisionNode b1 = new MonsterDecisionNode("Enemy Dist?"); b1.populateChildren();
				MonsterDecisionNode c3 = new MonsterDecisionNode("TV Dist?"); c3.populateChildren();
					MonsterAction d6 = new MonsterAction("Dance"); 
					MonsterAction d7 = new MonsterAction("Arrive Target"); 
					MonsterAction d8 = new MonsterAction("Arrive Target"); 
				MonsterDecisionNode c4 = new MonsterDecisionNode("TV Dist?"); c4.populateChildren();
					MonsterAction d9 = new MonsterAction("Wander"); 
					MonsterAction d10 = new MonsterAction("Pursue");
					MonsterAction d11 = new MonsterAction("Wander");
				MonsterDecisionNode c5 = new MonsterDecisionNode("TV Dist?"); c5.populateChildren();
					MonsterAction d12 = new MonsterAction("Wander"); 
					MonsterAction d13 = new MonsterAction("Wander");
					MonsterAction d14 = new MonsterAction("Wander");
		
		
		dt.root = a0;
		a0.add(MonsterDecisionNode.values.TRUE.ordinal(), b0);
			b0.add(MonsterDecisionNode.values.tLOW.ordinal(), c0);
			b0.add(MonsterDecisionNode.values.tMEDIUM.ordinal(), c1);
				c1.add(MonsterDecisionNode.values.eLOW.ordinal(), d0);
				c1.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), d1);
				c1.add(MonsterDecisionNode.values.eHIGH.ordinal(), d2);
			b0.add(MonsterDecisionNode.values.tHIGH.ordinal(), c2);
				c2.add(MonsterDecisionNode.values.eLOW.ordinal(), d3);
				c2.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), d4);
				c2.add(MonsterDecisionNode.values.eHIGH.ordinal(), d5);
		a0.add(MonsterDecisionNode.values.FALSE.ordinal(), b1);
			b1.add(MonsterDecisionNode.values.eLOW.ordinal(), c3);
				c3.add(MonsterDecisionNode.values.eLOW.ordinal(), d6);
				c3.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), d7);
				c3.add(MonsterDecisionNode.values.eHIGH.ordinal(), d8);
			b1.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), c4);
				c4.add(MonsterDecisionNode.values.eLOW.ordinal(), d9);
				c4.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), d10);
				c4.add(MonsterDecisionNode.values.eHIGH.ordinal(), d11);
			b1.add(MonsterDecisionNode.values.eHIGH.ordinal(), c5);
				c5.add(MonsterDecisionNode.values.eLOW.ordinal(), d12);
				c5.add(MonsterDecisionNode.values.eMEDIUM.ordinal(), d13);
				c5.add(MonsterDecisionNode.values.eHIGH.ordinal(), d14);

		a0.print(a0);
		return dt;
	}
	
	/**
	 * Draw the background which includes graph and world representation
	 */
	public void drawBackGround() {
		background(backgroundColor);
		rectMode(CORNER);

		strokeWeight(4);

		// Draw overall apartment
		fill(floorColor);
		rect(25, 50, 750, 500);

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
		ellipse(width/2, height/2 - 25 - 50, 100, 50);
		// beds
		rect(50, 505, 75, 40);
		rect(175, 505, 75, 40);
		rect(550, 505, 75, 40);
		rect(675, 505, 75, 40);
		rect(50, 505, 75, 40);
		rect(175, 505, 75, 40);
		rect(550, 505, 75, 40);
		rect(675, 505, 75, 40);
		
		// chairs
		rectMode(CENTER);
		PVector chair = world_objects.get(INANIMATE_OBJECTS).get("chair1").position;
		rect(chair.x, chair.y, 20, 15);
		chair = world_objects.get(INANIMATE_OBJECTS).get("chair2").position;
		rect(chair.x, chair.y, 20, 15);
		
		strokeWeight(1);
		
		// Draw Graph
		fill(0);

		noStroke();
		fill(0);
		// Draw Debug Mouse Position Text
		text(mouseX + " " + mouseY, width / 2 - 20, height / 2 - 25 - 50);

		fill(miscColor);
		stroke(10);
		// refrigerator
		rectMode(CORNER);
		rect(425,50, 50, 50);
		// Sink sides
		rect(280, 115, 30, 30);
		rect(280, 150, 30, 30);
		// Stove
		rect(340, 50, 50, 50);
		rectMode(CENTER);
		// bath right
		rect(755, 100, 25, 25);
		// bath left
		rect(50, 100, 25, 25);
		
		// text
		fill(0);
		textSize(10);
		text("Sink", 285, 130);
		text("Sink", 285, 170);
		text("Stove", 355, 80);
		text("Fridge", 435, 75);
		text("TV", 290, 440);
		text("Couch", 465, 525);
		text("Couch", 485, 440);
		text("bed1", 75, 530);
		text("bed2", 200, 530);
		text("bed3", 575, 530);
		text("bed4", 700, 530);
		textSize(8);
		text("toilet", 745, 100);
		text("toilet", 40, 100);
		
		// Green Information
		textSize(20);
		noFill();
		
		rectMode(CORNER);
		stroke(0,255,0);
		fill(0,255,0);
		rect(10, 5, 85, 30);
		fill(0);
		text("Green", 25, 28);
		text("Hunger: " + character.hungerMeter, 125, 25);
		text("Awakeness: " + character.awakeMeter, 300, 25);
		String bev = "" + character.steering;
		if( character.steering == SteeringState.PATHFIND_TARGET && character.awakeMeter <= 0){
			bev = "Goto Bed";
		} else if(character.steering == SteeringState.PATHFIND_TARGET && character.hungerMeter <= 0) {
			bev = "Goto Fridge";
		}
		text("Behavior: " + bev, 500, 25);
		
		// Red Information
		stroke(255,0,0);
		fill(255,0,0);
		rect(10, 555, 85, 30);
		fill(0);
		text("Red", 33, 578);
		text("Can Act: " + enemy.canDanceOrSitAgain, 125, 580);
		bev = "" + enemy.steering;
		if( enemy.steering == SteeringState.PURSUE){
			bev += " 'green'";
		}
		text("Behavior: " + bev, 500, 580);
		
		text("Left Click = Reset World", 250, 625);
		text("Right Click = Set Red Goal", 250, 650);	
		textSize(12);
		text("(Right Click only works if not currently pursuing Green)", 225, 680);	
		
		noFill();
		stroke(100);
		
		
		fill(0,255,0,100);
		ellipse(character.goal.x, character.goal.y, 10 * 2, 10 * 2); // draw ROS
	

		fill(255,0,0,100);
		ellipse(enemy.goal.x, enemy.goal.y, 10 * 2, 10 * 2); // draw ROS

		
		fill(255,0,0, 10);
		Kinematic tv = world_objects.get(INANIMATE_OBJECTS).get("tv");
		ellipse(tv.position.x, tv.position.y, 100*2, 100*2);
		noStroke();
		
		// Buttons
		noFill();
		stroke(100);
		rectMode(CORNER);
		// Toggle Graph button
		rect(25, 625, 75, 50);
		// Toggle Dt vs BT button
		rect(625, 625, 100, 50);
		
		fill(0);
		textSize(10);
		text("Toggle Graph", 28, 655);
		
		String header = "Using Behavior Tree";
		String label = "Use Decision Tree";
		if(this.useEnemyDT){
			header = "Using Decision Tree";
			label = "Use Behavior Tree";
		}
		
		text(header, 625, 615);
		text(label, 630, 655);
		
		line(0, 598, width, 598);
		noStroke();
	}

	public void drawGraph() {
		// Draw Edges
		stroke(dividerWallColor);
		for (DefaultWeightedEdge e : g.graph.edgeSet()) {
			Vertex v1 = g.graph.getEdgeSource(e);
			Vertex v2 = g.graph.getEdgeTarget(e);

			// draw a line
			line(v1.x, v1.y, v2.x, v2.y);

			// Draw an arrow signifying direction of edge
			pushMatrix();
			fill(color(255, 0, 0));
			translate((v1.x + v2.x) / 2, (v1.y + v2.y) / 2);
			rotate(atan2(v2.y - v1.y, v2.x - v1.x));
			// triangle(0, 0, -5, 2.5f, -5, -2.5f);
			popMatrix();

		}

		// Draw vertices
		for (Vertex v : g.graph.vertexSet()) {
			fill(0);
			ellipse(v.x, v.y, 5, 5);
			fill(color(255, 204, 0));
			text(v.id, v.x + 2, v.y - 2);
		}

		noFill();
		noStroke();
	}

	public void draw() {
		drawBackGround();
		
		if( this.showGraph )
			drawGraph();
		
		if( checkRadialDistance(character.position, enemy.position, 25) ) resetWorld();
		
		SteeringOutput output = null;
		
		// Green Character
		breadCrumbUpdate(character, enemyBreadcrumbs, new int[]{0,255,0});
		updateMeters(character);
		// Draw 'green'
		pushMatrix();
			// move origin to temporary pivot point
			translate(character.getPosition().x, character.getPosition().y);
			// rotate the grid
			rotate(character.getOrientation());
			// figure out what to do next
			character_dt.makeDecision();
			// Update coordinates and display the compass
			output = updateSteering(character);
			character.update(output);
			displayCharacter(new int[]{0,255,0});
		popMatrix();
		
		// Red Character
		breadCrumbUpdate(enemy, characterBreadcrumbs, new int[]{255,0,0});
		updateMeters(enemy);
		// Draw 'red'
		pushMatrix();
			// move origin to temporary pivot point
			translate(enemy.getPosition().x, enemy.getPosition().y);
			// rotate the grid
			rotate(enemy.getOrientation());
			// Update the enemy with B-Tree
			if(this.useEnemyBT) enemy_bt.run();
			if(this.useEnemyDT) enemy_learned_dt.makeDecision();
			// Update character kinematic
			output = updateSteering(enemy);
			enemy.update(output);
			displayCharacter(new int[]{255,0,0});
		popMatrix();
	}

	/**
	 * Update the steering for a character
	 * @param character
	 * @return
	 */
	public SteeringOutput updateSteering(Kinematic character) {	
		SteeringOutput output = new SteeringOutput();
		FollowPath followPath = null;
		SteeringOutput followPathOutput = null;
		SteeringOutput arrive = null;
		SteeringOutput lookWhereYoureGoingOutput = null;
		
		// Switch statement to alter steering
		switch(character.steering){
			case WANDER:
				// Only update wander goal if we reached the last one
				if( checkRadialDistance( character.position, new PVector(character.goal.x, character.goal.y), 35) ){
					// Find random point on graph to wander to
					character.start = g.getClosestPointOnGraph(character.position);
					character.target.position = new PVector(r.nextInt(width)-1, r.nextInt(height)-1);
					character.goal  = g.getClosestPointOnGraph(character.target.position);
			        // Find path from start to goal
			        character.path = g.pathfindNodeArrayAStar(g, character.start, character.goal, new EuclideanHeuristic(character.goal) );
				}
				
		        // Get acceleration
				followPath = new FollowPath(character, character.target, character.maxAcceleration, character.path, 1);
				followPathOutput = followPath.getSteering(); 
				lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, character.target, PI/32, PI/128, PI/32, PI/16).getSteering();
				
				output.acceleration = followPathOutput.getAcceleration();
				output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
				if(DEBUG)
					printToFile();
				
				break;
			
			case SIT:
				if( character.timeSitting == 0 ){
					character.timeSitting += 1;
					character.velocity = new PVector();
					character.acceleration = new PVector();
					character.rotation = 0;
					character.angularAcc = 0;
					character.canDanceOrSitAgain = false;
					character.timeStartedActing = millis();
				} else {
					if( frameCount % 60 == 0 )
						character.timeSitting++;
					
					Kinematic target = new Kinematic(300, 435); // where to look at when sitting
					
					Face face = new Face(character, target, PI/32, PI/128, PI/16, PI/32);
					SteeringOutput faceOutput = face.getSteering();
					output.acceleration = faceOutput.acceleration;
					output.angularAcc = faceOutput.angularAcc;
					
					if( character.timeSitting >= character.maxTimeSitting  ){
						character.timeSitting = 0;
						character.steering = SteeringState.WANDER;
					}
				}
				
				if(DEBUG)
					printToFile();
				break;
				
			case DANCE:
				if( character.timeDancing == 0 ){
					character.timeDancing += 1;
					character.velocity = new PVector();
					character.acceleration = new PVector();
					character.angularAcc = 0;
					character.rotation = 0;
					character.canDanceOrSitAgain = false;
					character.timeStartedActing = millis();
				} else {
					if( frameCount % 60 == 0 )
						character.timeDancing++;
					
					character.orientation += 0.10f;
					if( character.timeDancing >= character.maxTimeDancing ){
						character.timeDancing = 0;
						character.steering = SteeringState.WANDER;
					}
				}
				
				if(DEBUG)
					printToFile();
				break;
				
			case PATHFIND_TARGET:
				Kinematic refrigerator = world_objects.get(INANIMATE_OBJECTS).get("refrigerator");
				Kinematic bed = world_objects.get(INANIMATE_OBJECTS).get("bed4");
				
				// Check if we need to update food/awake meters
				boolean atTarget = checkRadialDistance(character.position, new PVector(character.goal.x, character.goal.y), 50);
				if( atTarget ){
					PVector char_pos = character.position;
					boolean atRefrigerator = checkRadialDistance(char_pos.x, refrigerator.position.x, char_pos.y, refrigerator.position.y, 50);
					boolean atBed 		   = checkRadialDistance(char_pos.x, bed.position.x, char_pos.y, bed.position.y, 50);

					if( atRefrigerator ){
						character.hungerMeter = 100;
					} else if( atBed ){
						character.awakeMeter = 100;
					}
					
				}
				
				// Arrive to target if we're close enough
				if( checkRadialDistance(character.position, new PVector(character.goal.x, character.goal.y), 50) ){
					Kinematic goalKinematic = new Kinematic();
					character.goal  = g.getClosestPointOnGraph(character.target.position);
		        	goalKinematic.position = new PVector(character.goal.x, character.goal.y);
		        	
		        	arrive = new Arrive(character, goalKinematic, character.maxSpeed, character.maxAcceleration, 20, 20).getSteering();
		        	lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, character.target, PI/32, PI/128, PI/32, PI/16).getSteering();
		        	
		        	output.acceleration = arrive.getAcceleration();
		        	output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
				
	        	// Not close enough, pathfind to target. 
				} else {
					// Find point to pathfind to
					character.start = g.getClosestPointOnGraph(character.position);
					character.goal  = g.getClosestPointOnGraph(character.target.position);
			        // Find path from start to goal
					character.path = g.pathfindNodeArrayAStar(g, character.start, character.goal, new EuclideanHeuristic(character.goal) );
					
			        // Get acceleration
					followPath = new FollowPath(character, character.target, character.maxAcceleration, character.path, 1);
					followPathOutput = followPath.getSteering(); 
					lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, character.target, PI/32, PI/128, PI/32, PI/16).getSteering();
					
					output.acceleration = followPathOutput.getAcceleration();
					output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
				}
				if(DEBUG)
					printToFile();
				break;
				
			case ARRIVE_TARGET:
				arrive = new Arrive(character, character.target, character.maxSpeed, character.maxAcceleration, 25, 25).getSteering();
	        	lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, character.target, PI/32, PI/128, PI/32, PI/16).getSteering();
	        	character.goal  = g.getClosestPointOnGraph(character.target.position); // this isn't needed for arrive but used to update the goal ellipse
	        	
	        	output.acceleration = arrive.getAcceleration();
	        	output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();

	        	if(DEBUG)
					printToFile();
	        	
	        	break;
				
			case PURSUE:
				// Find point to pathfind to
				character.start = g.getClosestPointOnGraph(character.position);
				character.goal  = g.getClosestPointOnGraph(this.character.position);
		        // Find path from start to goal
				character.path = g.pathfindNodeArrayAStar(g, character.start, character.goal, new EuclideanHeuristic(character.goal) );
			
		        // Get acceleration
				followPath = new FollowPath(character, character.target, character.maxAcceleration, character.path, 1);
				followPathOutput = followPath.getSteering(); 
				lookWhereYoureGoingOutput = new LookWhereYoureGoing(character, character.target, PI / 32, PI / 128, PI / 32, PI / 16).getSteering();
				
				output.acceleration = followPathOutput.getAcceleration();
				output.angularAcc = lookWhereYoureGoingOutput.getAngularAcc();
				
				if(DEBUG)
					printToFile();
				
				break;

			default:
				break;
				
		}
		
		return output;
	}

	/**
	 * Update the meters for a character
	 * @param character
	 */
	private void updateMeters(Kinematic character) {		
		// Reset act meter after 15 seconds ONLY if 15 sec have passed since last act
		if( millis()-character.timeStartedActing > (1000*15) ){
			if( !character.canDanceOrSitAgain && character.steering != SteeringState.DANCE && character.steering != SteeringState.SIT){
				character.canDanceOrSitAgain = true;
			}
		}
	
		// Update hunger/awake meter every 2 seconds
		if( frameCount % (60*2) == 0){
			if( character.hungerMeter > 0) character.hungerMeter -= 15;
			if( character.hungerMeter < 0 ) character.hungerMeter = 0;
			
			if( character.awakeMeter > 0) character.awakeMeter -= 20;
			if( character.awakeMeter < 0) character.awakeMeter = 0;
		}
	}

	/**
	 * Resets the world to their initial positions/stats
	 */
	private void resetWorld() {
		Kinematic player = world_objects.get(FRIENDLIES).get("player");
		Kinematic enemy = world_objects.get(ENEMIES).get("enemy");
		
		// reset player
		player.hungerMeter = 100;
		player.awakeMeter = 100;
		player.position = playerStart;
		player.target = new Kinematic(playerStart);
		player.acceleration = new PVector();
		player.velocity = new PVector();
		player.start = g.getClosestPointOnGraph(player.position);
		player.goal = g.getClosestPointOnGraph(player.target.position);
		player.path = g.pathfindNodeArrayAStar(g, player.start, player.goal, new EuclideanHeuristic(player.goal) );
		this.characterBreadcrumbs.clear();
		player.steering = SteeringState.NONE;
		
		// reset enemy
		enemy.position = enemyStart;
		enemy.target = new Kinematic(enemyStart);
		enemy.acceleration = new PVector();
		enemy.velocity = new PVector();
		enemy.start = g.getClosestPointOnGraph(enemy.position);
		enemy.goal = g.getClosestPointOnGraph(enemy.target.position);
		enemy.canDanceOrSitAgain = true;
		enemy.path = g.pathfindNodeArrayAStar(g, enemy.start, enemy.goal, new EuclideanHeuristic(enemy.goal) );
		this.enemyBreadcrumbs.clear();
		enemy.steering = SteeringState.NONE;
	}

	/**
	 * Draw the shape
	 */
	public void displayCharacter(int[] color) {
		fill(color[0], color[1], color[2]);
		ellipse(0, 0, radiusOfShape * 2, radiusOfShape * 2);
		triangle(3, 4, 10, 0, 3, -4);
	}

	/**
	 * Creates a new bread crumb if needed and then prints bread crumbs out
	 */
	private void breadCrumbUpdate(Kinematic character, Queue<float[]> breadcrumbs, int[] color) {
		fill(color[0], color[1], color[2], 200);
		if (frameCount % 30 == 0) {
			float[] newBreadCrumb = { character.getPosition().x, character.getPosition().y,
					character.getOrientation() };
			if (breadcrumbs.size() == 15) {
				breadcrumbs.remove();
			}
			breadcrumbs.add(newBreadCrumb);
		}

		for (float[] coords : breadcrumbs) {
			pushMatrix();
				translate(coords[0], coords[1]);
				rotate(coords[2]);
				triangle(0, 0 + 3, 0 + 6, 0 + 0, 0, 0 - 3);
			popMatrix();
		}
	}

	public boolean checkRadialDistance(float x, float x2, float y, float y2, int radius){
		return (Math.abs(x-x2) <= radius) && (Math.abs(y-y2) <= radius);
	}
	
	private boolean checkRadialDistance(PVector position, PVector position2, int radius) {
		return position.dist(position2) <= radius;
	}
	
	/**
	 * Left click = reset world
	 * Right click = force enemy to go to click
	 */
	public void mousePressed(){
		if( mouseX >= 25 && mouseX <= (25+75) && mouseY >= 625 && mouseY <= 625+50){
			this.showGraph = !this.showGraph;
			return;
		}
		
		// rect(625, 625, 100, 50);
		if( mouseX >= 625 && mouseX <= (625+100) && mouseY >= 625 && mouseY <= (625+50)){
			this.useEnemyBT = !this.useEnemyBT;
			this.useEnemyDT = !this.useEnemyDT;
			return;
		}
		
		if(mouseButton == LEFT){
			resetWorld();
		} else if(mouseButton == RIGHT){
			enemy.target = new Kinematic(mouseX, mouseY);
			enemy.goal = g.getClosestPointOnGraph(enemy.target.position);
			enemy.path = g.pathfindNodeArrayAStar(g, enemy.start, enemy.goal, new EuclideanHeuristic(enemy.goal) );
		}
	}
	
	public void printToFile(){
		int distEnemy = (int) PVector.dist(this.character.position, this.enemy.position);
		int distTv = (int) PVector.dist(this.enemy.position, this.tv.position);
		boolean canAct = enemy.canDanceOrSitAgain;
		SteeringState redAction = this.enemy.steering;
		
		if( redAction == SteeringState.NONE ) return;
		
		
		String easyEnemyDist = "";
		if( distEnemy < Kinematic.SM_RADIUS_TO_ENEMY ){
			easyEnemyDist = "LOW";
		} else if( distEnemy < Kinematic.MED_RADIUS_TO_ENEMY){
			easyEnemyDist = "MEDIUM";
		} else {
			easyEnemyDist = "HIGH";
		}
		
		String easyTVDist = "";
		if( distTv < Kinematic.SM_RADIUS_TO_TV ){
			easyTVDist = "LOW";
		} else if( distTv < Kinematic.MED_RADIUS_TO_TV){
			easyTVDist = "MEDIUM";
		} else {
			easyTVDist = "HIGH";
		}
		
		String canActStr = "" + canAct;
		canActStr = canActStr.toUpperCase();
		
		//String s = easyDist + "\t" + colliding + "\t" + canAct + "\t" + redAction;
		String s = easyEnemyDist + "\t" + easyTVDist + "\t" + canActStr + "\t" + redAction;
		System.out.println(s);
	}
}
