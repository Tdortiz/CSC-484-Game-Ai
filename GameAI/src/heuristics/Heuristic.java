package heuristics;

import graph.Vertex;

public interface Heuristic {
	
	/**
	 * Generates an estimated cost to reach the goal from a given node
	 * @param node 
	 * @return an estimate of the distance between this node and the goal
	 */
	public double estimate(Vertex node);
	
}
