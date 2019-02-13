package tree_decisionTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import character.Kinematic;
import enums.CharacterState;
import enums.SteeringState;

/**
 * Represents a binary tree of decisions.
 * 
 * Inner nodes = Decision.java Leafs nodes = Action.java
 * 
 * @author Thomas
 */
public class MonsterDecisionTree {
	
    /** The root of the tree */
    public MonsterDecisionNode root;
    /** World Representation **/
    public Kinematic character;
    public HashMap<String, HashMap<String, Kinematic>> world;
    private final String FRIENDLIES = "friendlies";
	private final String ENEMIES = "enemies";
	private final String INANIMATE_OBJECTS = "inanimate_objects";
    
    /**
     * Constructor 
     * @param world_objects
     */
	public MonsterDecisionTree(HashMap<String, HashMap<String, Kinematic>> world_objects, Kinematic character) {
		this.world = world_objects;	
		this.character = character;
	}
	
	/**
	 * Recursively goes through the tree and makes a decision
	 */
	public void makeDecision(){	
		MonsterDecisionNode actionNode = root.makeDecision(world, character);
		String action = actionNode.data.trim();

		// case statement here
		switch(character.objectType) {
			case PLAYER:
				decidePlayerAction(action);
				break;
			case ENEMY:
				decideEnemyAction(action);
				break;
			case FRIENDLY:
				decideFriendlyAction(action);
				break;
			case INANIMATE_OBJECT:
				break;
			default:
				break;
		}
	}
	
	public void decideEnemyAction(String action){
		Kinematic tv = world.get(INANIMATE_OBJECTS).get("tv");
		Kinematic green = world.get(FRIENDLIES).get("player");
		
		switch(action){
			case "Pathfind Target":
				character.target = tv;
				character.steering = SteeringState.PATHFIND_TARGET;
				break;
			case "Arrive Target":
				character.target = green;
				character.steering = SteeringState.ARRIVE_TARGET;
				break;
			case "Wander":
				character.steering = SteeringState.WANDER;
				break;
			case "Pursue":
				character.target = green;
				character.steering = SteeringState.PURSUE;
				break;
			case "Dance":
				character.steering = SteeringState.DANCE;
				break;
		}
	}
	
	public void decideFriendlyAction(String action){}
	public void decidePlayerAction(String action){}
    
}
