package tree_decisionTree;

import java.util.ArrayList;
import java.util.HashMap;

import character.Kinematic;
import tree_decisionTree.DecisionTreeLearner.DecisionNode;

public class MonsterDecisionNode {
	public String data;
    public ArrayList<MonsterDecisionNode> children;
    private final String FRIENDLIES = "friendlies";
	private final String ENEMIES = "enemies";
	private final String INANIMATE_OBJECTS = "inanimate_objects";
    public static enum values {
    	TRUE, FALSE, eLOW, eMEDIUM, eHIGH, tLOW, tMEDIUM, tHIGH, 
    }
    
	public MonsterDecisionNode(String data){
		this.data = data;
		this.children = new ArrayList<MonsterDecisionNode>(values.values().length);
	}
	
	public void populateChildren(){
		for(int i = 0; i < values.values().length; i++)
			this.children.add(i, new MonsterDecisionNode(""));
	}
	
    /**
     * Recursively walks through the tree
     * @param character 
     */
    public MonsterDecisionNode makeDecision(HashMap<String, HashMap<String, Kinematic>> world, Kinematic character){
    	// If its an action do this
    	if( this.children.isEmpty() ){
    		return this;
    	}
    		
    	switch (data){
    		case "Can Act?":
    			if(character.canDanceOrSitAgain){
    				return children.get(values.TRUE.ordinal()).makeDecision(world, character);
    			} else {
    				return children.get(values.FALSE.ordinal()).makeDecision(world, character);
    			}
    		case "TV Dist?":
    			Kinematic tv = world.get(INANIMATE_OBJECTS).get("tv");

    			if( character.position.dist(tv.position) < Kinematic.SM_RADIUS_TO_TV ){
    				return children.get(values.tLOW.ordinal()).makeDecision(world, character);
    			} else if(character.position.dist(tv.position) < Kinematic.MED_RADIUS_TO_TV ){
    				return children.get(values.tMEDIUM.ordinal()).makeDecision(world, character);
    			} else {
    				return children.get(values.tHIGH.ordinal()).makeDecision(world, character);
    			}
    		case "Enemy Dist?" :
    			Kinematic green = world.get(FRIENDLIES).get("player");
    			
    			if( character.position.dist(green.position) < Kinematic.SM_RADIUS_TO_ENEMY ){
    				return children.get(values.eLOW.ordinal()).makeDecision(world, character);
    			} else if(character.position.dist(green.position) < Kinematic.MED_RADIUS_TO_ENEMY ){
    				return children.get(values.eMEDIUM.ordinal()).makeDecision(world, character);
    			} else {
    				return children.get(values.eHIGH.ordinal()).makeDecision(world, character);
    			}
    	}
    	return null;
    }
    
    public String toString(){
        return this.data;
    }
    
    public void add(int i, MonsterDecisionNode node){
		children.set(i, node);
    }
    
    /**
     * Print the tree in a readable format
     * 
     * @param root
     */
    public void print(MonsterDecisionNode root) {
    	System.out.println("---------------- Learned Decision-Tree ---------------");
        printHelper(root, 0);
        System.out.println("------------------------------------------------------");
    }

    
    /**
     * Recursive print helper
     * 
     * @param node
     * @param level
     */
    public void printHelper(MonsterDecisionNode node, int level) {
    	String name = node.data;
    	
    	if( node != null && name != null && !name.equals("null") && !name.isEmpty() ){
    	
            String s = level + " |";
            for (int i = 1; i <= level; i++) {
                s += "\t";
            }
            s += node.toString().trim();

            System.out.println(s);
            for( MonsterDecisionNode n : node.children ){
                printHelper(n, level + 1);
            }
    	}
    }
    
}
