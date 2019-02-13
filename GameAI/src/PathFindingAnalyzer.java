import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import graph.Graph;
import graph.Vertex;
import heuristics.ConstantHeuristic;
import heuristics.EuclideanHeuristic;
import heuristics.RandomHeuristic;

public class PathFindingAnalyzer {
	
	public static final double nsToSec = 1000000000.0;
	public static final double nsToMs  = 1000000.0;
	public static DecimalFormat df = new DecimalFormat("#,##0.00");
	
	public static void main(String[] args){
		runAnalysis("graphs/small_graph2.txt", 500);
		//runAnalysis("graphs/big_graph_05000.txt", 10);
		//runAnalysis("graphs/big_graph_10000.txt", 10);
		//runAnalysis("graphs/big_graph_15000.txt", 10);
		//runAnalysis("graphs/big_graph_20000.txt", 10);
		//runAnalysis("graphs/big_graph_25000.txt", 10);
		//runAnalysis("graphs/big_graph_50000.txt", 5);
		//runAnalysis("graphs/big_graph_75000.txt", 5);
	    //runAnalysis("graphs/big_graph_100000.txt", 1);
		//runAnalysis("graphs/graph_50000_250000.txt", 100);
	}
	
	public static void runAnalysis(String graphName, int timesToRunTests){
		Graph g = new Graph();
		System.out.println("Creating " + graphName.substring(graphName.indexOf('/')+1, graphName.indexOf('.')));
		g.createGraph(graphName);
		System.out.println("Finished Creating Graph\n");
		
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    	vertices.addAll(g.graph.vertexSet());
    	
    	double dijk = 0.00;
    	double NodeArrayDijk = 0.0;
    	double NodeArrayAStarEucl = 0.00;
    	double NodeArrayAStarConstant = 0.00;
    	double NodeArrayAStarRandom = 0.00;
    	double AStarEucl = 0.00;
    	double AStarConstant = 0.00;
    	double AStarRandom = 0.00;
    	
    	for(int i = 1; i <= timesToRunTests; i++){
    		Vertex start = getRandomVertex(vertices, null);
	    	Vertex goal = getRandomVertex(vertices, start);
	    	System.out.println(start + " --> "  + goal);
	    	
	    	// Run the tests and aggregate the results
	    	dijk += (long) getDijkRunTime(g, start, goal);
	    	System.out.println("Finished Dijk - " + i);
	    	
	    	AStarEucl += (long) getAStarEuclRunTime(g, start, goal);
	    	System.out.println("Finished A*-Eucl - " + i);
	    	
	    	AStarConstant += (long) getAStarConstantRunTime(g, start, goal, 50f);
	    	System.out.println("Finished A*-Constant - " + i);
	    	
	    	AStarRandom += (long) getAStarRandomRunTime(g, start, goal, 50f);
	    	System.out.println("Finished A*-Random - " + i);
	    	
	    	NodeArrayDijk += (long) getNodeArrayDijkRunTime(g, start, goal);
	    	System.out.println("Finished Node-Array-Dijk - " + i);
	    	
	    	NodeArrayAStarEucl += (long) getNodeArrayAStarEuclRunTime(g, start, goal);
	    	System.out.println("Finished Node-A*-Eucl - " + i);
	    	
	    	NodeArrayAStarConstant += (long) getNodeArrayAStarConstantRunTime(g, start, goal, 50f);
            System.out.println("Finished Node-A*-Constant - " + i);
            
            NodeArrayAStarRandom += (long) getNodeArrayAStarRandomRunTime(g, start, goal, 50f);
            System.out.println("Finished Node-A*-Random - " + i);
	    	
	    	System.out.println();
    	}
    	System.out.println();
    	
    	// average the results
    	dijk /= timesToRunTests;
    	NodeArrayDijk /= timesToRunTests;
    	NodeArrayAStarEucl /= timesToRunTests;
    	NodeArrayAStarConstant /= timesToRunTests;
        NodeArrayAStarRandom /= timesToRunTests;
    	AStarEucl /= timesToRunTests;
    	AStarConstant /= timesToRunTests;
    	AStarRandom /= timesToRunTests;
    	
    	// Assemble String Array of results
    	Object[][] table = new String[9][];
    	table[0] = new String[] { "Behavior"			, "Time (sec)"												, "Time (ms)"};
    	table[1] = new String[] { "Dijk"				, df.format(dijk/nsToSec) 					+ " seconds"	, df.format(dijk/nsToMs) 					+ " ms"	};
    	table[2] = new String[] { "A*-Eucl"				, df.format(AStarEucl/nsToSec) 				+ " seconds"	, df.format(AStarEucl/nsToMs)				+ " ms"	};
    	table[3] = new String[] { "A*-Constant"			, df.format(AStarConstant/nsToSec) 			+ " seconds"	, df.format(AStarConstant/nsToMs) 			+ " ms"	};
    	table[4] = new String[] { "A*-Random"			, df.format(AStarRandom/nsToSec)  			+ " seconds"	, df.format(AStarRandom/nsToMs)				+ " ms"	};
    	table[5] = new String[] { "Array-Dijk"          , df.format(NodeArrayDijk/nsToSec)          + " seconds"    , df.format(NodeArrayDijk/nsToMs)           + " ms" };
    	table[6] = new String[] { "Array-A*-Eucl"		, df.format(NodeArrayAStarEucl/nsToSec) 	+ " seconds"	, df.format(NodeArrayAStarEucl/nsToMs) 		+ " ms" };
    	table[7] = new String[] { "Array-A*-Constant"	, df.format(NodeArrayAStarConstant/nsToSec)	+ " seconds"	, df.format(NodeArrayAStarConstant/nsToMs)	+ " ms" };
    	table[8] = new String[] { "Array-A*-Random"		, df.format(NodeArrayAStarRandom/nsToSec) 	+ " seconds"	, df.format(NodeArrayAStarRandom/nsToMs) 	+ " ms" };

    	// Print table
    	System.out.format("%-15s%-15s%-15s\n", "", graphName, "");  
    	System.out.format("%-20s%-20s%-20s\n", table[0][0], table[0][1], table[0][2]);  
    	for(int i = 1; i < table.length; i++){
    		System.out.format("%-20s%-20s%-20s\n", table[i][0], table[i][1], table[i][2]);  
    	}
    	
    	// Print output to file
    	String outputFileName = "output/" + graphName.substring(graphName.indexOf('/')+1, graphName.indexOf('.')) + "_output.txt";
    	File outputFile = new File(outputFileName);
    	
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			outputFile.createNewFile();
			fw = new FileWriter(outputFile.getAbsoluteFile(), false);
			bw = new BufferedWriter(fw);
			
			bw.write(  String.format("%-15s%-15s%-15s\n", "", graphName, "") );
			bw.write( String.format("%-20s%-20s%-20s\n", table[0][0], table[0][1], table[0][2]) );  
	    	for(int i = 1; i < table.length; i++){
	    		bw.write( String.format("%-20s%-20s%-20s\n", table[i][0], table[i][1], table[i][2]) );  
	    	}
	    	
	    	bw.close();
	    	fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static long getNodeArrayDijkRunTime(Graph g, Vertex start, Vertex goal) {
	    long totalTime = 0;
        long startTime = System.nanoTime();
        
        g.pathfindNodeArrayDijkstra(g, start, goal);
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        totalTime += duration;
        
        return totalTime;
    }

    public static long getDijkRunTime(Graph g, Vertex start, Vertex goal){
		long totalTime = 0;
		long startTime = System.nanoTime();
    	
    	g.pathfindDijkstra(g, start, goal);
    	long endTime = System.nanoTime();

    	long duration = (endTime - startTime);
    	totalTime += duration;
		
    	return totalTime;
	}
	
	public static long getNodeArrayAStarEuclRunTime(Graph g, Vertex start, Vertex goal){
		long totalTime = 0;
		long startTime = System.nanoTime();
		
    	g.pathfindNodeArrayAStar(g, start, goal, new EuclideanHeuristic(goal));
    	long endTime = System.nanoTime();

    	long duration = (endTime - startTime);
    	totalTime += duration;
		
    	return totalTime;
	}
	
	public static long getNodeArrayAStarConstantRunTime(Graph g, Vertex start, Vertex goal, float constant){
        long totalTime = 0;
        long startTime = System.nanoTime();

        g.pathfindNodeArrayAStar(g, start, goal, new ConstantHeuristic(goal, constant));
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        totalTime += duration;
        
        return totalTime;
    }
    
    public static long getNodeArrayAStarRandomRunTime(Graph g, Vertex start, Vertex goal, float maxRandom){
        long totalTime = 0;
        long startTime = System.nanoTime();
        
        g.pathfindNodeArrayAStar(g, start, goal, new RandomHeuristic(goal, maxRandom));
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        totalTime += duration;
        
        return totalTime;
    }
	
	public static long getAStarEuclRunTime(Graph g, Vertex start, Vertex goal){
		long totalTime = 0;
		long startTime = System.nanoTime();
		
    	g.pathfindAStar(g, start, goal, new EuclideanHeuristic(goal));
    	long endTime = System.nanoTime();

    	long duration = (endTime - startTime);
    	totalTime += duration;
		
    	return totalTime;
	}
	
	public static long getAStarConstantRunTime(Graph g, Vertex start, Vertex goal, float constant){
		long totalTime = 0;
		long startTime = System.nanoTime();

    	g.pathfindAStar(g, start, goal, new ConstantHeuristic(goal, constant));
    	long endTime = System.nanoTime();

    	long duration = (endTime - startTime);
    	totalTime += duration;
		
    	return totalTime;
	}
	
	public static long getAStarRandomRunTime(Graph g, Vertex start, Vertex goal, float maxRandom){
		long totalTime = 0;
		long startTime = System.nanoTime();
		
    	g.pathfindAStar(g, start, goal, new RandomHeuristic(goal, maxRandom));
    	long endTime = System.nanoTime();

    	long duration = (endTime - startTime);
    	totalTime += duration;
		
    	return totalTime;
	}
	
	
	
    /**
     * Gets a random vertex inside the graph
     * 
     * @param vertices list
     * @param v don't return this vertex (prevents picking the same vertex)
     * @return a new vertex
     */
	public static Vertex getRandomVertex(ArrayList<Vertex> vertices, Vertex v) {
		Random r = new Random();
		int index = r.nextInt(vertices.size());
		
		Vertex retVertex = vertices.get(index);
		
		// If we picked the same vertex get a different one
		while( v != null && v.id.equals(retVertex.id) ){
			index = r.nextInt(vertices.size());
			retVertex = vertices.get(index);
		}
		
		return retVertex;
	}
	
}
