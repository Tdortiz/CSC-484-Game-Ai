package heuristics;

import java.util.Random;

import graph.Vertex;

public class RandomHeuristic implements Heuristic {
	
	private Vertex goal;
	private float max;
	private Random r = new Random();
	
	public RandomHeuristic(Vertex goal, float max){
		this.goal = goal;
		this.max = max;
	}
	
	@Override
	public double estimate(Vertex node) {
		return r.nextDouble()*max;
	}

}
