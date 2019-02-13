package tree_decisionTree;

import java.util.HashMap;

import character.Kinematic;
import enums.CharacterState;

/**
 * Represents a node in a decision tree
 * 
 * Inner nodes = Decision.java
 * Leafs nodes = Action.java
 * 
 * @author Thomas
 *
 */
public class DecisionTreeNode {
	
    public String data;
    public boolean decision = false;
    public boolean action = false;
    public DecisionTreeNode trueBranch;
    public DecisionTreeNode falseBranch;
    
	public DecisionTreeNode(String data){
		this.data = data;
	}
	
    /**
     * Recursively walks through the tree
     * @param character 
     */
    public DecisionTreeNode makeDecision(HashMap<String, HashMap<String, Kinematic>> world, Kinematic character){
    	if( trueBranch == null && falseBranch == null){
    		return this;
    	}
    		
    	
    	boolean isHungry = character.hungerMeter <= 0;
    	boolean needSleep = character.awakeMeter <= 0;
    	
    	switch (data){
    		case "Hungry?":
    			if(isHungry){
    				return trueBranch.makeDecision(world, character);
    			} else {
    				return falseBranch.makeDecision(world, character);
    			}
    		case "Need sleep?":
    			if(needSleep){
    				return trueBranch.makeDecision(world, character);
    			} else {
    				return falseBranch.makeDecision(world, character);
    			}
    	}
    	
    	return null;
    	
    }
    
    public String toString(){
        return this.data;
    }
}
