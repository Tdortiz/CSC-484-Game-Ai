package graph;

import java.util.Comparator;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Represents a node record for the open list/closed list/record array
 * 
 * @author Thomas Ortiz (tdortiz)
 */
public class NodeRecord {
	
    /**
     * Represents if the node is open, closed, or unvisited
     */
	public enum Category {
		OPEN, CLOSED, UNVISITED;
	}
	
	/** Node */
	public Vertex node;
	/** Edge to get to the node */
	public DefaultWeightedEdge edge;
	/** CSF */
	public double costSoFar;
	/** Estimated Total Cost = csf + H(node) */
	public double estimatedTotalCost;
	/** Category of open, closed, or unvisited */
	public Category category;
	 
	public NodeRecord(){}
	 
	/**
	 * Creates a new NodeRecord 
	 * 
	 * @param node
	 * @param edge
	 * @param costSoFar
	 * @param estimatedTotalCost
	 */
	public NodeRecord(Vertex node, DefaultWeightedEdge edge, float costSoFar, float estimatedTotalCost){
		this.node = node;
		this.edge = edge;
		this.costSoFar = costSoFar;
		this.estimatedTotalCost = estimatedTotalCost;
		this.category = Category.UNVISITED;
	}
	
	/**
	 * Creates a new NodeRecord for use in a NodeRecord array
	 * 
	 * @param node
	 * @param edge
	 * @param costSoFar
	 * @param estimatedTotalCost
	 * @param category
	 */
	public NodeRecord(Vertex node, DefaultWeightedEdge edge, float costSoFar, float estimatedTotalCost, Category category){
        this.node = node;
        this.edge = edge;
        this.costSoFar = costSoFar;
        this.estimatedTotalCost = estimatedTotalCost;
        this.category = category;
    }

	/**
	 * Comparators t ocompare a NodeRecord
	 */
	public static class Comparators {
		public static final Comparator<NodeRecord> costSoFarComparator 			= (NodeRecord o1, NodeRecord o2) -> Double.compare(o1.costSoFar, o2.costSoFar);
		public static final Comparator<NodeRecord> estimatedTotalCostComparator 	= (NodeRecord o1, NodeRecord o2) -> Double.compare(o1.estimatedTotalCost, o2.estimatedTotalCost);
		public static final Comparator<NodeRecord> id 								= (NodeRecord o1, NodeRecord o2) -> o1.node.compareTo(o2.node);
	}
	
	public String toString(){
		return node.id + " " + edge;
	}
    	 
 }