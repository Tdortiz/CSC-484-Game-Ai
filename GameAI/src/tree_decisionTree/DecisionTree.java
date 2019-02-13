package tree_decisionTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
public class DecisionTree {
	
    /** The root of the tree */
    public DecisionTreeNode root;
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
	public DecisionTree(HashMap<String, HashMap<String, Kinematic>> world_objects, Kinematic character) {
		this.world = world_objects;	
		this.character = character;
	}
	
	/**
	 * Recursively goes through the tree and makes a decision
	 */
	public void makeDecision(){	
		DecisionTreeNode actionNode = root.makeDecision(world, character);
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
	
	public void decidePlayerAction(String action){
		switch(action){
			case "Get food;":
				character.steering = SteeringState.PATHFIND_TARGET;
				Kinematic refrigerator = world.get(INANIMATE_OBJECTS).get("refrigerator");
				character.target = new Kinematic((int)refrigerator.position.x, (int)refrigerator.position.y);
				break;
				
			case "Go to bed;":
				character.state = CharacterState.MOVING;
				character.steering = SteeringState.PATHFIND_TARGET;
				Kinematic bed = world.get(INANIMATE_OBJECTS).get("bed4");
				character.target = new Kinematic((int)bed.position.x, (int)bed.position.y);
				break;
				
			case "Wander;":
				character.state = CharacterState.MOVING;
				character.steering = SteeringState.WANDER;
				break;
				
			default:
				character.state = CharacterState.STANDING;
				character.steering = SteeringState.NONE;
		}
	}
	
	public void decideEnemyAction(String action){
		Kinematic tv = world.get(INANIMATE_OBJECTS).get("tv");
		Kinematic green = world.get(INANIMATE_OBJECTS).get("player");
		
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
	
	public void decideFriendlyAction(String action){
		
	}
	
	
	
	
	/**
	 * From a file, create a tree.
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void fileToTree(String fileName) {
		InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
		Scanner fileScanner = new Scanner(fileStream);
		
		try {
			this.root = fileToTreeHelper(fileScanner);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * Recursive function to create tree from a file.
	 * 
	 * @param fileScanner
	 * @return DecisionTreeNode
	 * @throws IOException
	 */
	public static DecisionTreeNode fileToTreeHelper(Scanner fileScanner) throws IOException {
		// Check if theres a next line, if not return the null node
		if (!fileScanner.hasNextLine())
			return null;

		String line = fileScanner.nextLine().replaceAll("\t", "");

		// Check if the next line represents a null choice
		if (line.equals("null"))
			return null;

		DecisionTreeNode node = null;
		// If node is a Decision, it can have up to 2 subnodes with the answers
		// / nested questions.
		if (line.contains("?")) {
			node = new DecisionTreeNode(line);
			node.decision = true;

			// If there is an option that has only 1 answer, then this should
			// have an if that
			// checks if there is a ";" and then create the node or set it as
			// null.
			node.trueBranch  = fileToTreeHelper(fileScanner);
			node.falseBranch = fileToTreeHelper(fileScanner);

			// If the node is an Action, just create it
		} else if (!line.matches("[\t]+")) {
			node = new Action(line);
		}

		return node;
	}
    
    
	/**
     * Prints in order
     * 
     * print trueBranch tree, print current, print falseBranch tree
     * 
     * @param node
     *            to start printing from
     */
    public void printInOrder(DecisionTreeNode node) {
        if (node != null) {
            printInOrder(node.trueBranch);
            System.out.println(node);
            printInOrder(node.falseBranch);
        }
    }

    
    /**
     * Prints pre order
     * 
     * print print current, trueBranch tree, print falseBranch tree
     * 
     * @param node
     *            to start printing from
     */
    public void printPreOrder(DecisionTreeNode node) {
        if (node != null) {
            System.out.println(node);
            printPreOrder(node.trueBranch);
            printPreOrder(node.falseBranch);
        }
    }

    
    /**
     * Prints in order
     * 
     * print trueBranch tree, print falseBranch tree, print current
     * 
     * @param node
     *            to start printing from
     */
    public void printPostOrder(DecisionTreeNode node) {
        if (node != null) {
            printPostOrder(node.trueBranch);
            printPostOrder(node.falseBranch);
            System.out.println(node);
        }
    }

    
    /**
     * Print the tree in a readable format
     * 
     * @param root
     */
    public void print(DecisionTreeNode root) {
    	System.out.println("----------------Decision-Tree---------------");
        printHelper(root, 0);
        System.out.println("--------------------------------------------");
    }

    
    /**
     * Recursive print helper
     * 
     * @param node
     * @param level
     */
    public void printHelper(DecisionTreeNode node, int level) {
        if (node != null) {
            String s = level + " |";
            for (int i = 1; i <= level; i++) {
                s += "\t";
            }
            s += node.toString().trim();

            System.out.println(s);
            printHelper(node.trueBranch, level + 1);
            printHelper(node.falseBranch, level + 1);
        }
    }
    

    /**
     * Print the tree in a readable format including all nulls
     * 
     * @param root
     */
    public void printNulls(DecisionTreeNode root) {
        System.out.println("----------------Decision-Tree---------------");
        printHelperNulls(root, 0);
        System.out.println("--------------------------------------------");
    }

    
    /**
     * Print all null nodes as well.
     * 
     * @param node
     * @param level
     */
    public void printHelperNulls(DecisionTreeNode node, int level) {
        String s = level + " |";
        for (int i = 1; i <= level; i++) {
            s += "    ";
        }

        if (node == null) {
            s += "null";
            System.out.println(s);
        } else {
            s += node.toString().trim();
            System.out.println(s);
            if( node.trueBranch != null && node.trueBranch != null){
                printHelperNulls(node.trueBranch, level + 1);
                printHelperNulls(node.falseBranch, level + 1);
            }
        }

    }
    
}
