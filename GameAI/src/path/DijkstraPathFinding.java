package path;

import java.util.ArrayList;
import java.util.Collections;

import org.jgrapht.graph.DefaultWeightedEdge;

import graph.Graph;
import graph.NodeRecord;
import graph.Vertex;

/**
 * Dijkstra's Algorithm AIFG - 210
 */
public class DijkstraPathFinding implements FindShortestPath {
	
	private Graph g;
	private Vertex start;
	private Vertex end;

	public DijkstraPathFinding(Graph g, Vertex start, Vertex end){
		this.g = g;
		this.start = start;
		this.end = end;
	}
	
	public Path getShortestPath(){
		// Initialize the record for the start node
		NodeRecord startRecord = new NodeRecord();
		startRecord.node = start;
		startRecord.edge = null;
		startRecord.costSoFar = 0.0f;

		// Initialize the open and closed lists
		PathFindingList open = new PathFindingList();
		open.addRecordByCostSoFar(startRecord);
		PathFindingList closed = new PathFindingList();
		NodeRecord current = null;
		
		// Iterate through processing each node
		while (!open.isEmpty()) {
			// Find smallest element in the open list
			current = open.getSmallestNodeRecordByCostSoFar();

			// If its the goal node, terminate
			if (current.node.equals(end)) break;

			// Otherwise get its outgoing connections
			ArrayList<DefaultWeightedEdge> connections = g.getConnections(current.node);

			// Loop through each connection
			for (DefaultWeightedEdge connection : connections) {
				// Get the cost estimate for the end node
				Vertex endNode = g.graph.getEdgeTarget(connection);
				double endNodeCost = current.costSoFar + g.graph.getEdgeWeight(connection);
				NodeRecord endNodeRecord = null;
				
				// skip if the node is closed
				if (closed.containsById(endNode)) {
					continue;

				// .. or if it is open and we've found a worse route
				} else if (open.contains(endNode)) {
					// Here we find the record in the open list corresponding to
					// the endNode
					endNodeRecord = open.find(endNode);

					if (endNodeRecord.costSoFar <= endNodeCost) {
						continue;
					}

					// Otherwise we know we've got an unvisited node, so make a record
				} else {
					endNodeRecord = new NodeRecord();
					endNodeRecord.node = endNode;
				}

				// We're here if we need to update the node
				// update the cost and connection
				endNodeRecord.costSoFar = endNodeCost;
				endNodeRecord.edge = connection;

				// and add it to the open list
				if (!open.contains(endNode)) {
					open.addRecordByCostSoFar(endNodeRecord);
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

			// work back along the path, accumulating connections	
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