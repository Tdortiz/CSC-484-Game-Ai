package path;

import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.graph.DefaultWeightedEdge;

import graph.Graph;
import graph.NodeRecord;
import graph.Vertex;
import heuristics.Heuristic;

/**
 * A* Algorithm AIFG - 220 AIFG - 210
 */
public class AStarPathFinding implements FindShortestPath {
	
	private Graph g;
	private Vertex start;
	private Vertex end;
	private Heuristic heuristic;

	public AStarPathFinding(Graph g, Vertex start, Vertex end, Heuristic heuristic){
		this.g = g;
		this.start = start;
		this.end = end;
		this.heuristic = heuristic;
	}
	
	public Path getShortestPath() {
		// Initialize the record for the start node
		NodeRecord startRecord = new NodeRecord(start, null, 0.0f, (float) heuristic.estimate(start));
		startRecord.estimatedTotalCost = heuristic.estimate(start);

		// Initialize the open and closed lists
		PathFindingList open = new PathFindingList();
		open.addRecordByEstimatedTotalCost(startRecord);
		PathFindingList closed = new PathFindingList();
		NodeRecord current = null;

		// Iterate through processing each node
		while (!open.isEmpty()) {
			// Find smallest element in the open list using estimatedTotalCost
			current = open.getSmallestNodeRecordByEstimatedTotalCost();

			// If its the goal node, terminate
			if (current.node.equals(end)) break;

			// Otherwise get its outgoing connections
			ArrayList<DefaultWeightedEdge> connections = g.getConnections(current.node);

			// Loop through each connection
			for (DefaultWeightedEdge connection : connections) {
				// Get the cost estimate for the end node
				Vertex endNode = g.graph.getEdgeTarget(connection);
				double newEndNodeCost = current.costSoFar + g.graph.getEdgeWeight(connection);

				NodeRecord endNodeRecord = null;
				double endNodeHeuristic = 0;
				
				// if node is closed we may have to skip, or remove it from closed list				
				if (closed.containsById(endNode)) {
					// find the record in the closed list corresponding to the endNode
					endNodeRecord = closed.findById(endNode);

					// if we didn't find a shorter route, skip
					if (endNodeRecord.costSoFar <= newEndNodeCost) continue;

					// otherwise remove it from closed list
					closed.removeRecordById(endNodeRecord);

					// we can use the node's old cost values to calculate its heuristic 
					// without calling the possibly expensive heuristic function
					/// TODO line 63 of book - possible double check this
					//endNodeHeuristic = heuristic.estimate(endNode);
					endNodeHeuristic = endNodeRecord.estimatedTotalCost - endNodeRecord.costSoFar;

				// skip if node is open and we've not found a better route
				} else if (open.contains(endNode)) {
					// Here we find the record in the open list corresponding to the endNode
					endNodeRecord = open.find(endNode);

					// if our route isn't better, skip
					if (endNodeRecord.costSoFar <= newEndNodeCost) continue;

					// we can use the node's old cost values to calculate its
					// heuristic
					// without calling the possibly expensive heuristic function
					/// TODO line 81 of book - possible double check this
					//endNodeHeuristic = heuristic.estimate(endNode);
					endNodeHeuristic = endNodeRecord.estimatedTotalCost - endNodeRecord.costSoFar;

				// Otherwise we know we've got an unvisited node, so make a record
				} else {
					endNodeRecord = new NodeRecord();
					endNodeRecord.node = endNode;

					// Need to calculate heuristic value using the function
					// since we don't have an existing record to use
					endNodeHeuristic = heuristic.estimate(endNode);
				}
				

				// We're here if we need to update the node
				// update the cost, estimate, and connection
				endNodeRecord.costSoFar = newEndNodeCost;
				endNodeRecord.edge = connection;
				endNodeRecord.estimatedTotalCost = newEndNodeCost + endNodeHeuristic;

				// Add it to the open list
				if (!open.contains(endNode)) {
					open.addRecordByEstimatedTotalCost(endNodeRecord);
				}

			}
			
			// We’ve finished looking at the connections for
			// the current node, so add it to the closed list
			// and remove it from the open list
			open.removeRecord(current);
			closed.addRecordById(current);
		}
		
		// We’re here if we’ve either found the goal, or
		// if we’ve no more nodes to search, find which.
		if (!current.node.equals(end)) {
			// Ran out of nodes without finding the goal, no solution
			return null;
		} else {
		    // compile the list of connections in the path
			ArrayList<DefaultWeightedEdge> path = new ArrayList<>();

			while (!current.node.equals(start)) {
				path.add(current.edge);
				// set current (NodeRecord) to is source
				current = closed.findById(g.graph.getEdgeSource(current.edge));
			}

			// Reverse the path, and return it
			Collections.reverse(path);
			return new Path(g, path);
		}

	}
}
