package heuristics;

import graph.Vertex;

public class ConstantHeuristic implements Heuristic {
	
	private Vertex goal;
	private double constant;

	public ConstantHeuristic(Vertex goal, float constant){
		this.goal = goal;
		this.constant = constant;
	}

	@Override
	public double estimate(Vertex node) {
		return this.constant;
	}

}
