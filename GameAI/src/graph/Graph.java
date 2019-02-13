package graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import heuristics.Heuristic;
import path.AStarPathFinding;
import path.DijkstraPathFinding;
import path.NodeArrayAStarPathFinding;
import path.NodeArrayDijkstraPathFinding;
import path.Path;
import processing.core.PImage;
import processing.core.PVector;

/**
 * A Graph wrapper class that utilizes a jGraphT graph
 * 
 * @author Thomas Ortiz (tdortiz)
 */
public class Graph {

	/** A graph with a custom vertex class and a default weighted edge */
	public SimpleDirectedWeightedGraph<Vertex, DefaultWeightedEdge> graph;
	/** A map of id-s to vertex */
	public Map<String, Vertex> idToVertex;

	/**
	 * Creates a default SimpleDirectedWeightedGraph<Vertex,
	 * DefaultWeightedEdge> and initalizes the hashmap
	 */
	public Graph() {
		graph = new SimpleDirectedWeightedGraph<Vertex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		idToVertex = new LinkedHashMap<String, Vertex>();
	}

	/**
	 * Add a vertex to the graph and makes a mapping of id to vertex
	 * 
	 * @param id
	 *            of vertex
	 * @param x
	 *            pos
	 * @param y
	 *            pos
	 */
	public void addVertex(String id, int x, int y) {
		Vertex v = new Vertex(id, x, y);
		idToVertex.put(id, v);
		graph.addVertex(v);
	}

	/**
	 * Add an edge between v1 and v2 with a weight
	 * 
	 * @param v1
	 * @param v2
	 * @param weight
	 */
	public void addEdgeWithWeight(Vertex v1, Vertex v2, double weight) {
		if( graph.getEdge(v1, v2) == null){
			DefaultWeightedEdge newEdge = graph.addEdge(v1, v2);
			graph.setEdgeWeight(newEdge, weight);
		}
	}

	/**
	 * Returns an array of edges outgoing from the given node PG 213 on look up
	 * tables
	 * 
	 * @param fromNode
	 * @return
	 */
	public ArrayList<DefaultWeightedEdge> getConnections(Vertex fromNode) {
		ArrayList<DefaultWeightedEdge> outgoingEdgesOfNode = new ArrayList<DefaultWeightedEdge>();
		outgoingEdgesOfNode.addAll(graph.outgoingEdgesOf(fromNode));
		return outgoingEdgesOfNode;
	}

	/**
	 * Dijkstra's Algorithm AIFG - 210
	 * 
	 * @param g
	 *            to operate on
	 * @param start
	 *            vertex of path
	 * @param end
	 *            vertex of path
	 * @return The in order list of edges to traverse to get to goal from start
	 */
	public Path pathfindDijkstra(Graph g, Vertex start, Vertex end) {
		DijkstraPathFinding dijk = new DijkstraPathFinding(g, start, end);
		return dijk.getShortestPath();
	}

	/*
	 * A* Algorithm AIFG - 220 AIFG - 210
	 * 
	 * @param g to operate on
	 * 
	 * @param start vertex of path
	 * 
	 * @param end vertex of path
	 * 
	 * @param heuristic to calculate estimated cost
	 * 
	 * @return The in order list of edges to traverse to get to goal from start
	 */
	public Path pathfindNodeArrayDijkstra(Graph g, Vertex start, Vertex end) {
		NodeArrayDijkstraPathFinding nodeArrayDijk = new NodeArrayDijkstraPathFinding(g, start, end);
		return nodeArrayDijk.getShortestPath();
	}

	/**
	 * A* Algorithm AIFG - 220 AIFG - 210
	 * 
	 * @param g
	 *            to operate on
	 * @param start
	 *            vertex of path
	 * @param end
	 *            vertex of path
	 * @param heuristic
	 *            to calculate estimated cost
	 * @return The in order list of edges to traverse to get to goal from start
	 */
	public Path pathfindAStar(Graph g, Vertex start, Vertex end, Heuristic heuristic) {
		AStarPathFinding aStar = new AStarPathFinding(g, start, end, heuristic);
		return aStar.getShortestPath();
	}

	/*
	 * A* Algorithm AIFG - 220 AIFG - 210
	 * 
	 * @param g to operate on
	 * 
	 * @param start vertex of path
	 * 
	 * @param end vertex of path
	 * 
	 * @param heuristic to calculate estimated cost
	 * 
	 * @return The in order list of edges to traverse to get to goal from start
	 */
	public Path pathfindNodeArrayAStar(Graph g, Vertex start, Vertex end, Heuristic heuristic) {
		NodeArrayAStarPathFinding nodeArrayAStar = new NodeArrayAStarPathFinding(g, start, end, heuristic);
		return nodeArrayAStar.getShortestPath();
	}

	/**
	 * Quantizes the character's position into the graph
	 * 
	 * @param position
	 * @return
	 */
	public Vertex getClosestPointOnGraph(PVector position) {
		Vertex v = null;
		float distanceToTarget = Float.MAX_VALUE;

		for (Vertex currV : graph.vertexSet()) {
			float distanceCheck = (float) Math.hypot(currV.x - position.x, currV.y - position.y);
			if (distanceCheck < distanceToTarget) {
				v = currV;
				distanceToTarget = distanceCheck;
			}
		}
		return v;
	}

	public ArrayList<Vertex> getNeighborVertices(Vertex v, int maxLevel){
		ArrayList<Vertex> neighbors = (ArrayList<Vertex>) Graphs.neighborListOf(graph, v);
		
		return getNeighborVerticesHelper(neighbors, 1, maxLevel);
	}
	
	public ArrayList<Vertex> getNeighborVerticesHelper(ArrayList<Vertex> vertices, int currLevel, int maxLevel){
		if( currLevel >= maxLevel ){
			Set<Vertex> deDuplicate = new LinkedHashSet<>(vertices);
			vertices.clear();
			vertices.addAll(deDuplicate);
			
			System.out.println(vertices);
			return vertices;
		}
		
		ArrayList<Vertex> neighbors = new ArrayList<Vertex>();
		for( Vertex v : vertices){
			neighbors.addAll( Graphs.neighborListOf(graph, v) );
		}
		
		currLevel +=1;
		return getNeighborVerticesHelper(neighbors, currLevel, maxLevel);
	}
	
	/**
	 * Creates a graph based off a file of vertices and edges File is expected
	 * to be like so:
	 * 
	 * vertex_id x y ... $ VertexA VertexB Weight ...
	 * 
	 * @param filename
	 */
	public void createGraph(String filename) {
		InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(filename);
		
		Scanner in = new Scanner(fileStream);
		Scanner lineScan = null;

		// Scan for Vertices
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (line.equals("$"))
				break;

			lineScan = new Scanner(line);
			String id = lineScan.next();
			int x = lineScan.nextInt();
			int y = lineScan.nextInt();

			this.addVertex(id, x, y);
		}

		// Scan for edges
		Scanner edgeScan = null;
		while (in.hasNextLine()) {
			String line = in.nextLine();
			edgeScan = new Scanner(line);
			String id1 = edgeScan.next();
			String id2 = edgeScan.next();
			double weight = edgeScan.nextDouble();

			Vertex v1 = this.idToVertex.get(id1);
			Vertex v2 = this.idToVertex.get(id2);

			this.addEdgeWithWeight(v1, v2, weight);
		}

		// Close scanners
		in.close();
		lineScan.close();
		edgeScan.close();
	}

	public void createGraphFromImage(PImage image) {
		int counter = 0;
		
		System.out.println("[" + image.width + ", " + image.height + "]");
		
		// get nodes
		System.out.println("Created Vertices");
		for (int x = 0; x < image.width; x++) {
			for (int y = 0; y < image.height; y++) {
				if ((image.get(x, y) & 0xff) != 0) {
					this.addVertex("" + counter, x, y);
					counter++;
				}
			}
		}

		// draw edges
		System.out.println("Drawing Edges");
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        vertices.addAll( graph.vertexSet() );
        
        for(Vertex v1 : vertices){
        	for(Vertex v2 : vertices){
        		if( canEdgeExist((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y, image) ){
        			float weight = (float) Math.hypot(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
					addEdgeWithWeight(v1, v2, weight);
					addEdgeWithWeight(v2, v1, weight);
        		}
        	}
        }
        
		/*for (int x = 0; x < image.width; x++) {
			for (int y = 0; y < image.height; y++) {
				
				if( ((image.width*x) + y) >= vertices.size() ){
					break;
				}
				Vertex v1 = vertices.get( (image.width*x) + y );
				
				int index = (image.width*x) + (y+1);
				if(index < vertices.size() && canEdgeExist(x, y, x, y+1, image) ){
					Vertex v2 = vertices.get(index);
					float weight = (float) Math.hypot(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
					addEdgeWithWeight(v1, v2, weight);
					addEdgeWithWeight(v2, v1, weight);
				}
				
				index = (image.width*x) + (y-1);
				if(index < vertices.size() && canEdgeExist(x, y, x, y-1, image) ){
					Vertex v2 = vertices.get(index);
					float weight = (float) Math.hypot(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
					addEdgeWithWeight(v1, v2, weight);
					addEdgeWithWeight(v2, v1, weight);
				}
				
				index = (image.width*(x+1)) + y;
				if( index < vertices.size() && canEdgeExist(x, y, x+1, y, image) ){
					Vertex v2 = vertices.get(index);	
					float weight = (float) Math.hypot(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
					addEdgeWithWeight(v1, v2, weight);
					addEdgeWithWeight(v2, v1, weight);
				}
				
				index = (image.width*(x-1)) + y;
				if(index < vertices.size() && canEdgeExist(x, y, x-1, y, image) ){
					Vertex v2 = vertices.get(index);
					float weight = (float) Math.hypot(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
					addEdgeWithWeight(v1, v2, weight);
					addEdgeWithWeight(v2, v1, weight);
				}
				
			}
		}*/
		
		for(Vertex v : vertices){
			v.x = v.x * 30;
			v.y = v.y * 30;
		}

		System.out.println("Finished");
	}

	private boolean canEdgeExist(int x, int y, int x2, int y2, PImage image) {
		boolean a = (image.get(x, y) & 0xff) != 0;
		boolean b = (image.get(x2, y2) & 0xff) != 0;
		boolean c = (x != x2 || y != y2);
		
		/*System.out.println();
		System.out.println("a = " + a);
		System.out.println("b = " + b);
		System.out.println("c = " + c);
		System.out.println();*/
		
		if( a && b && c ) {
			return true;
		}
		return false;
	}
	

}
