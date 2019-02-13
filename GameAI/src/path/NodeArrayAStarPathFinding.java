package path;

import java.util.ArrayList;
import java.util.Collections;
import org.jgrapht.graph.DefaultWeightedEdge;
import graph.Graph;
import graph.NodeRecord;
import graph.Vertex;
import graph.NodeRecord.Category;
import heuristics.Heuristic;

public class NodeArrayAStarPathFinding implements FindShortestPath {

	private Graph g;
	private Vertex start;
	private Vertex end;
	private Heuristic heuristic;
	private ArrayList<NodeRecord> records;
	
	public NodeArrayAStarPathFinding(Graph g, Vertex start, Vertex end, Heuristic heuristic){
		this.g = g;
		this.start = start;
		this.end = end;
		this.heuristic = heuristic;
		
		// Create array of records
		int numVertices = g.graph.vertexSet().size();
		records = new ArrayList<NodeRecord>(numVertices);
		
		// initialize all NodeRecords to null
		for( int i = 0; i < numVertices; i++)
            records.add(null);
	}
	
	public Path getShortestPath() {
		// Initialize the record for the start node
		NodeRecord startRecord = new NodeRecord(start, null, 0.0f, (float) heuristic.estimate(start), Category.OPEN);

		// Initialize the open list
		PathFindingList open = new PathFindingList();
		open.addRecordByEstimatedTotalCost(startRecord);
		records.set(Integer.parseInt(startRecord.node.id), startRecord);
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
				// Get the cost and other information for end node
			    Vertex endNode = g.graph.getEdgeTarget(connection);
			    int endNodeRecordIndex = Integer.parseInt(endNode.id);
                NodeRecord endNodeRecord = records.get(endNodeRecordIndex); // this is potentially null but we handle it
				double newEndNodeCost = current.costSoFar + g.graph.getEdgeWeight(connection);
				double endNodeHeuristic = 0;
				
				// if node is closed we may have to skip, or remove it from closed list	
				if( endNodeRecord != null && endNodeRecord.category == Category.CLOSED ){ 
					// Find the record corresponding to the endNode
					endNodeRecord = records.get(endNodeRecordIndex);

					// If we didn't find a shorter route, skip
					if (endNodeRecord.costSoFar <= newEndNodeCost) continue;

					// Otherwise remove it from closed list
					records.get(endNodeRecordIndex).category = Category.OPEN;

					// Use node's old cost values to calculate its heuristic to save computation
					endNodeHeuristic = endNodeRecord.estimatedTotalCost - endNodeRecord.costSoFar;

				// Skip if node is open and we've not found a better route
				} else if( endNodeRecord != null && endNodeRecord.category == Category.OPEN ){ 
					// Here we find the record in the open list corresponding to the endNode
					endNodeRecord = records.get(endNodeRecordIndex);

					// If our route isn't better, skip
					if (endNodeRecord.costSoFar <= newEndNodeCost) continue;

					// Use the node's old cost values to calculate its heuristic to save computation
					endNodeHeuristic = endNodeRecord.estimatedTotalCost - endNodeRecord.costSoFar;

				// Otherwise we know we've got an unvisited node, so make a new record
				} else { // if endNodeRecord.category == Category.UNVISITED
				    endNodeRecord = new NodeRecord();
					endNodeRecord.node = endNode;
					records.set(endNodeRecordIndex, endNodeRecord);

					// Need to calculate heuristic since this is new
					endNodeHeuristic = heuristic.estimate(endNode);
				}
				

				// We're here if we need to update the node
				// update the cost, estimate, and connection
				endNodeRecord.costSoFar = newEndNodeCost;
				endNodeRecord.edge = connection;
				endNodeRecord.estimatedTotalCost = newEndNodeCost + endNodeHeuristic;

				// Add it to the open list
				if ( endNodeRecord.category != Category.OPEN ) {
					open.addRecordByEstimatedTotalCost(endNodeRecord);
					endNodeRecord.category = Category.OPEN;
					records.set(endNodeRecordIndex, endNodeRecord);
				}

			}
			
			// We’ve finished looking at the connections for
			// the current node, so add it to the closed list
			// and remove it from the open list
			open.removeRecord(current);
			current.category = Category.CLOSED;
			records.set(Integer.parseInt(current.node.id), current);
		}
		
		// We’re here if we’ve either found the goal, or
		// if we’ve no more nodes to search, find which.
		if (!current.node.equals(end)) {
			// Ran out of nodes without finding the goal, no solution
			return null;
		} else {
		    // Compile the list of connections in the path
			ArrayList<DefaultWeightedEdge> path = new ArrayList<>();

			while (!current.node.equals(start)) {
				path.add(current.edge);
				// Set current (NodeRecord) to is source.
				current = records.get( Integer.parseInt(g.graph.getEdgeSource(current.edge).id) );
			}

			// Reverse the path, and return it
			Collections.reverse(path);
			return new Path(g, path);
		}

	}

}
