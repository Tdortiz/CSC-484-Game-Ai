package tree_behaviorTree;

import java.util.HashMap;
import character.Kinematic;
import tree_decisionTree.DecisionTreeNode;

public class BehaviorTree {

	/** The root of the tree */
    public Task root;
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
	public BehaviorTree(HashMap<String, HashMap<String, Kinematic>> world_objects, Kinematic character) {
		this.world = world_objects;	
		this.character = character;
	}
	
	public void run(){
		root.run(world);
	}
	

	/**
     * Print the tree in a readable format including all nulls
     * 
     * @param root
     */
    public void printNulls(Task root) {
    	System.out.println("----------------Behavior-Tree---------------");
        printHelperNulls(root, 0);
        System.out.println("--------------------------------------------");
    }

    
    /**
     * Print all null nodes as well.
     * 
     * @param node
     * @param level
     */
    public void printHelperNulls(Task node, int level) {
        String s = level + " |";
        for (int i = 1; i <= level; i++) {
            s += "    ";
        }
           
        s += node.toString().trim();
        System.out.println(s);
        
        for( Task task : node.children ){
        	printHelperNulls(task, level + 1);
        }
    }
    
}
