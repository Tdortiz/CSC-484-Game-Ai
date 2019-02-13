package path;

import java.util.ArrayList;
import java.util.Collections;
import org.jgrapht.graph.DefaultWeightedEdge;
import graph.Graph;
import graph.NodeRecord;
import graph.Vertex;
import graph.NodeRecord.Category;

/**
 * Dijkstra's Algorithm AIFG - 210 (Node array adaption)
 */
public class NodeArrayDijkstraPathFinding implements FindShortestPath {
    
    private Graph g;
    private Vertex start;
    private Vertex end;
    private ArrayList<NodeRecord> records;

    public NodeArrayDijkstraPathFinding(Graph g, Vertex start, Vertex end){
        this.g = g;
        this.start = start;
        this.end = end;
        
        // Create array of records
        int numVertices = g.graph.vertexSet().size();
        records = new ArrayList<NodeRecord>(numVertices);
        
        // initialize all NodeRecords to null
        for( int i = 0; i < numVertices; i++)
            records.add(null);
    }
    
    public Path getShortestPath(){
        // Initialize the record for the start node
        NodeRecord startRecord = new NodeRecord(start, null, 0.0f, 0.0f, Category.OPEN);

        // Initialize the open and closed lists
        PathFindingList open = new PathFindingList();
        open.addRecordByCostSoFar(startRecord);
        records.set(Integer.parseInt(startRecord.node.id), startRecord);
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
                int endNodeRecordIndex = Integer.parseInt(endNode.id);
                NodeRecord endNodeRecord = records.get(endNodeRecordIndex); // this is potentially null but we handle it
                double newEndNodeCost = current.costSoFar + g.graph.getEdgeWeight(connection);
                
                // skip if the node is closed
                if (endNodeRecord != null && endNodeRecord.category == Category.CLOSED) {
                    continue;

                // .. or if it is open and we've found a worse route
                } else if (endNodeRecord != null && endNodeRecord.category == Category.OPEN) {
                    // Here we find the record in the open list corresponding to
                    // the endNode
                    endNodeRecord = records.get(endNodeRecordIndex);

                    if (endNodeRecord.costSoFar <= newEndNodeCost) continue;

                // .. Otherwise we know we've got an unvisited node, so make a record
                } else { // if endNodeRecord.category == Category.UNVISITED
                    endNodeRecord = new NodeRecord();
                    endNodeRecord.node = endNode;
                    records.set(endNodeRecordIndex, endNodeRecord);
                }

                // We're here if we need to update the node
                // update the cost and connection
                endNodeRecord.costSoFar = newEndNodeCost;
                endNodeRecord.edge = connection;

                // and add it to the open list
                if (endNodeRecord.category != Category.OPEN) {
                    open.addRecordByCostSoFar(endNodeRecord);
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
            // compile the list of connections in the path
            ArrayList<DefaultWeightedEdge> path = new ArrayList<>();

            // work back along the path, accumulating connections   
            while (!current.node.equals(start)) {
                path.add(current.edge);
                // set current (NodeRecord) to is source
                current = records.get( Integer.parseInt(g.graph.getEdgeSource(current.edge).id) );
            }

            // Reverse the path, and return it
            Collections.reverse(path);
            
            return new Path(g, path);
        }

    }
}