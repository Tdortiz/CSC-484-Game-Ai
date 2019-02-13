package tree_decisionTree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import enums.SteeringState;

/**
 * Based off pseudocode from PG 617 in text book
 * @author Thomas
 *
 */
public class DecisionTreeLearner {

	/** The filename */
	private String fileName;
	public static final int numValues = 8;
	/** Represents each action that can be taken */
	private enum Action {
		PURSUE, ARRIVE_TARGET, SIT, DANCE, WANDER, PATHFIND_TARGET,
	}
	private enum Attribute {
		ENEMY_DIST, TV_DIST, CAN_ACT,
	}
	private enum AttributeValue {
		LOW, MEDIUM, HIGH, TRUE, FALSE, 
	}
	public DecisionNode root;

	
	/**
	 * Represents each row of data
	 */
	public class Example {
		
		public int[] attrValues;
		public Action action;
		
		public Example(Action action, int enemyDistance, int tvDistance, int canAct){
			attrValues = new int[Attribute.values().length];
			this.action = action;
			attrValues[Attribute.ENEMY_DIST.ordinal()] = enemyDistance;
			attrValues[Attribute.TV_DIST.ordinal()] = tvDistance;
			attrValues[Attribute.CAN_ACT.ordinal()] = canAct;
		}
		
		public int getValue(int attribute){
			return attrValues[attribute];
		}
		
		
		public String toString(){
			return "" + action;
		}
		
	}

	/**
	 * Temporary decision node for the learner
	 */
	public class DecisionNode {
		/** Label (the name of the attribute value) */
		public String name;
		/** Dist_Enemy, Dist_TV, can_Act*/
		public Attribute testValue;
		/** The action to take if a leaf */
		public String action;
		public ArrayList<DecisionNode> daughters;
		
		public DecisionNode(){
			this.daughters = new ArrayList<DecisionNode>();
		}
		
		public void populateDaughters(){
			for( int i = 1; i <= 3; i++)
				this.daughters.add(new DecisionNode());
		}
		
		public DecisionNode(String name){
			this.name = name;
			this.daughters = new ArrayList<DecisionNode>();
		}
		
		// name - (action) - testValue
		public String toString(){
			String s = name + " - ";
			
			if( action != null )
				s += action;
			else 
				s += testValue;
			
			return s;
		}
		
		/**
	     * Print the tree in a readable format
	     * 
	     * @param root
	     */
	    public void print(DecisionNode root) {
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
	    public void printHelper(DecisionNode node, int level) {
	    	String name = node.name;
	    	//System.out.println("name = |" + name + "|");
	    	
	    	if( node != null && name != null && !name.equals("null") ){
	    	
	            String s = level + " |";
	            for (int i = 1; i <= level; i++) {
	                s += "\t";
	            }
	            s += node.toString().trim();
	
	            System.out.println(s);
	            for( DecisionNode n : node.daughters ){
	                printHelper(n, level + 1);
	            }
	    	}
	    }

	}
	
	
	/**
	 * Read the log into the field variables
	 * 
	 * @param file
	 * @param monster
	 * @param player
	 */
	public DecisionTreeLearner(String file) {
		this.fileName = file;
		ArrayList<Example> examples = readLog();
		
		ArrayList<Attribute> attributesArray = new ArrayList<Attribute>();
		attributesArray.add(Attribute.ENEMY_DIST);
		attributesArray.add(Attribute.TV_DIST);
		attributesArray.add(Attribute.CAN_ACT);
		
		this.root = makeTree(examples, attributesArray, new DecisionNode("ROOT"));
	}

	public DecisionNode makeTree(ArrayList<Example> examples, ArrayList<Attribute> attributes, DecisionNode decisionNode){		
		double initialEntropy = entropy(examples);
		
		// if no entropy, can't divide any further
		if( initialEntropy <= 0 ){
			// Give the node its action
			if( examples.size() > 0 ){
				decisionNode.action = examples.get(0).action.name();
				System.out.print("");
			} 
			return decisionNode;
		}
		
		// Find # of examples
		int exampleCount = examples.size();
		
		double bestInformationGain = 0.0;
		Attribute bestSplitAttribute = null;
		ArrayList<ArrayList<Example>> bestSets = new ArrayList<ArrayList<Example>>();
		
		// Go through each attribute
		for( Attribute attribute : attributes ){
			// perform split
			ArrayList<ArrayList<Example>> sets = splitByAttribute(examples, attribute);
			
			// find overall entropy and information gain
			double overallEntropy = entropyOfSets(sets, exampleCount);
			double informationGain = initialEntropy - overallEntropy;
			
			// check fi we've got the best so far
			if( informationGain > bestInformationGain ){
				bestInformationGain = informationGain;
				bestSplitAttribute = attribute;
				bestSets = sets;
			}
		}
		
		// Set the decision Node's test
		decisionNode.testValue = bestSplitAttribute;
		decisionNode.populateDaughters();
		
		// The list of attributes to pass on down the tree should have the one we're using removed
		ArrayList<Attribute> newAttributes = null;
		
		// if greater than 0 copy over all but the 1 attribute we don't need
		if( attributes.size() > 0){
			newAttributes = new ArrayList<Attribute>(attributes.size()-1);
			for(Attribute a : attributes){
				if( a != bestSplitAttribute){
					newAttributes.add(a);
				}
			}
		} else { // assign action to node
			boolean[] possibleActions = new boolean[Action.values().length];
			// record possible actions
			for( Example example : examples ){
				Action action = example.action;
				if(!possibleActions[action.ordinal()]){
					possibleActions[action.ordinal()] = true;
				}
			}
			
			for( int i = 0; i < possibleActions.length; i++){
				if(possibleActions[i]){
					decisionNode.action = Action.values()[i].name();
				}
			}
			return decisionNode;
		}
		
		// Fill daughter nodes
		for( ArrayList<Example> set : bestSets ){
			if(!set.isEmpty()){
				// find value for attribute in this set
				int attributeValue = set.get(0).getValue(bestSplitAttribute.ordinal());
				
				//AttributeValue.values()[attributeValue].name(); /// TODO
				
				// Create daughterNode
				DecisionNode daughter = new DecisionNode(AttributeValue.values()[attributeValue].name());
				
				// Add to tree
				decisionNode.daughters.add(attributeValue, daughter);
				
				// recurse the algorithm
				makeTree(set, newAttributes, daughter);
			}
		}
		
		return decisionNode;
	}
	
	public ArrayList<ArrayList<Example>> splitByAttribute(ArrayList<Example> examples, Attribute attribute){
		// Create set of lists, so we can access each list by attribute value
		ArrayList<ArrayList<Example>> sets = new ArrayList<ArrayList<Example>>();
		
		// initialize dummy data
		for(int i = 0; i < numValues; i++)
			sets.add(i,  new ArrayList<Example>() );
		
		// Loop through each example
		for( Example example : examples ){
			// assign it to right set
			ArrayList<Example> set = sets.get(example.getValue(attribute.ordinal())); /// TODO: Maybe here
			set.add(example);
		}
		
		// return the sets
		return sets;
	}
	
	public double entropy(ArrayList<Example> examples){
		// Get # of examples
		int exampleCount = examples.size();
		
		// check if we have only 1: if so entropy is 0
		if( exampleCount == 0 ) return 0;
		
		// keep tally of how many of each different kind of action we have
		int[] actionTallies = new int[Action.values().length];
		
		// Go through examples
		for( Example example : examples ){
			// inc appropriate tally
			actionTallies[example.action.ordinal()]++;
		}
		
		// we now have the count for each action in the set
		//int actionCount = actionTallies.length;
		int actionCount = 0;
		for(int i = 0; i < actionTallies.length; i++){
			if(actionTallies[i] > 0){
				actionCount++;
			}
		}
		
		// if only one action --> 0 entropy
		if( actionCount <= 1 ) return 0;
		
		// start with zero entropy
		double entropy = 0;
		
		// add in the contribute to entropy of each action
		for( int actionTally : actionTallies ){
			double proportion = (double) actionTally / (double)exampleCount;
			entropy -= (proportion * log2(proportion));
		}
		
		// return total entropy
		return entropy;
	}
	
	public double entropyOfSets(ArrayList<ArrayList<Example>> sets, int exampleCount){
		// start with 0 entropy
		double entropy = 0.0;
		
		// get entropy contribution of each set
		for( ArrayList<Example> set : sets ){
			// calc proprotion of the whole in this set
			double proportion = (double) set.size() / (double) exampleCount;
			
			// calc entropy contribution
			entropy -= (proportion * entropy(set));
		}
		return entropy;
	}
	
	private double log2(double x){
		if(x == 0) return 0;
		
		double result = Math.log10(x) / Math.log(2.0);
		
		if (Double.isNaN(result) || Double.isInfinite(result) || (result == -0.0))
			return 0.0;
		
		return result;
	}

	private ArrayList<Example> readLog() {
		InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(this.fileName);
		Scanner s = new Scanner(fileStream);

		
		ArrayList<Example> examples = new ArrayList<Example>(10000);
		try {
			s.nextLine(); // delete header
			
			while(s.hasNextLine()) {
				Scanner lineScanner = new Scanner(s.nextLine());
				
				String enemyDistance = lineScanner.next();
				String tvDistance = lineScanner.next();
				String canAct = lineScanner.next();
				String actionToken = lineScanner.next();
				
				Action currAction = Action.valueOf(actionToken);
				
				Example ex = new Example(currAction, AttributeValue.valueOf(enemyDistance).ordinal(), AttributeValue.valueOf(tvDistance).ordinal(), AttributeValue.valueOf(canAct).ordinal());
				examples.add(ex);
				
				lineScanner.close();
			}
		} catch (InputMismatchException e) {
			e.printStackTrace();
		} finally {
			s.close();
		}
		
		return examples;
	}

}