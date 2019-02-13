package heuristics;

import graph.Vertex;
import processing.core.PVector;

public class EuclideanHeuristic implements Heuristic {

	private Vertex goal;
	
	public EuclideanHeuristic(Vertex goal){
		this.goal = goal;
	}
	
	
	/**
	 * Returns the euclidean distance between two vertices
	 */
	@Override
	public double estimate(Vertex node) {
		return new PVector(node.x, node.y).dist(new PVector(goal.x, goal.y));
	}

}
