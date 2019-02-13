package graphTest;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.junit.Test;
import graph.Graph;
import graph.Vertex;
import heuristics.ConstantHeuristic;
import heuristics.EuclideanHeuristic;
import heuristics.RandomHeuristic;
import path.Path;

public class graphTest {
	
	private Graph g;
	Path returnedPath;
	Path expectedPath;
	/** Book file based on graph from 219 */
	String SevVertSevEdge = "graphs/Graph_7V_7E_Book.txt";
	String smallGraph = "graphs/small_graph.txt";
	
	/**
	 * Creates a graph based off a file of vertices and edges
	 * @param filename
	 */
	public void createGraph(String filename){
		g = new Graph();
		Scanner in = null;
		Scanner lineScan = null;
		
		try {
			in = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Scan for Vertices
		while( in.hasNextLine() ){
			String line = in.nextLine();
			if(line.equals("$")) break;
			
			lineScan = new Scanner(line);
			String id = lineScan.next();
			int x = lineScan.nextInt();
			int y = lineScan.nextInt();
			
			g.addVertex(id, x, y);
		}
		
		// Scan for edges
		Scanner edgeScan = null;
		while( in.hasNextLine() ){
			String line = in.nextLine();
			edgeScan = new Scanner(line);
			String id1 = edgeScan.next();
			String id2 = edgeScan.next();
			double weight = edgeScan.nextDouble();
			
			Vertex v1 = g.idToVertex.get(id1);
			Vertex v2 = g.idToVertex.get(id2);
			g.addEdgeWithWeight(v1, v2, weight);
		}
		
	}

	public void testGraphCreation(){
		createGraph("graphs/Graph_6V_7E.txt");
		assertEquals(6, g.graph.vertexSet().size());
		assertEquals(7, g.graph.edgeSet().size());
		
		createGraph("graphs/Graph_7V_7E.txt");
		assertEquals(7, g.graph.vertexSet().size());
		assertEquals(7, g.graph.edgeSet().size());
	}
	
	
	@Test
	public void testDijkstra(){
		createGraph(SevVertSevEdge);
		
		returnedPath = g.pathfindDijkstra(g, g.idToVertex.get("a"), g.idToVertex.get("g"));
		expectedPath = new Path();
		System.out.println("Dijkstra\t" + returnedPath);
		assertTrue(true);
	}
	
	 @Test
	public void testAStarRandom(){
		createGraph(SevVertSevEdge);
		
		returnedPath = g.pathfindAStar(g, g.idToVertex.get("a"), g.idToVertex.get("g"), new RandomHeuristic(g.idToVertex.get("g"), 1));
		expectedPath = new Path();
		System.out.println("RandomHeuristic\t" + returnedPath);
		
		assertTrue(true);
	}
	
	@Test
	public void testAStarZero(){
		createGraph(SevVertSevEdge);
		
		returnedPath = g.pathfindAStar(g, g.idToVertex.get("a"), g.idToVertex.get("g"), new ConstantHeuristic(g.idToVertex.get("g"), 0));
		expectedPath = new Path();
		System.out.println("ZeroHeuristic\t" + returnedPath);

		assertTrue(true);
	}
	
	@Test
	public void testAStarEuclidean(){
		createGraph(SevVertSevEdge);
	
		returnedPath = g.pathfindAStar(g, g.idToVertex.get("a"), g.idToVertex.get("g"), new EuclideanHeuristic(g.idToVertex.get("g")));
		expectedPath = new Path();
		System.out.println("Eucl-Heuristic\t" + returnedPath);
		
		assertTrue(true);
	}


	
	
	
	/** Testing for my small graph */
	@Test
	public void testDijkstraLakeView(){
		createGraph(smallGraph);
		Vertex start = g.idToVertex.get("a");
		Vertex goal = g.idToVertex.get("cc");
		
		returnedPath = g.pathfindDijkstra(g, start, goal);
		System.out.println("Aprt Dijkstra\t" + returnedPath);
		
		assertTrue(true);
	}
	
	@Test
	public void testAStarRandomLakeView(){
		createGraph(smallGraph);
		Vertex start = g.idToVertex.get("a");
		Vertex goal = g.idToVertex.get("cc");
		
		returnedPath = g.pathfindAStar(g, start, goal, new RandomHeuristic(goal, 5.0f));
		System.out.println("Aprt RandomHeuristic\t" + returnedPath);
		
		assertTrue(true);
	}
	
	@Test
	public void testAStarConstantLakeView(){
		createGraph(smallGraph);
		Vertex start = g.idToVertex.get("a");
		Vertex goal = g.idToVertex.get("cc");
		
		returnedPath = g.pathfindAStar(g, start, goal, new ConstantHeuristic(goal, .50f) );
		System.out.println("Aprt ConstHeuristic\t" + returnedPath);

		assertTrue(true);
	}
	
	@Test
	public void testAStarEuclideanLakeView(){
		createGraph(smallGraph);
		Vertex start = g.idToVertex.get("a");
		Vertex goal = g.idToVertex.get("cc");
	
		returnedPath = g.pathfindAStar(g, start, goal, new EuclideanHeuristic(goal) );
		System.out.println("Aprt EuDistHeuristic\t" + returnedPath);
		
		assertTrue(true);
	} 
	
	@Test
	public void testNodeAStarEuclideanLakeView(){
		createGraph("graphs/Graph_7V_7E_Book_Numeric.txt");
		Vertex start = g.idToVertex.get("0");
		Vertex goal = g.idToVertex.get("6");
	
		returnedPath = g.pathfindNodeArrayAStar(g, start, goal, new EuclideanHeuristic(goal) );
		System.out.println("Aprt Node EuDistHeuristic\t" + returnedPath);
		
		assertTrue(true);
	} 
}
