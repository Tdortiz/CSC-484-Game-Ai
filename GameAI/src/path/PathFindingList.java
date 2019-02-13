package path;

import java.util.ArrayList;
import java.util.Collections;
import graph.NodeRecord;
import graph.Vertex;

public class PathFindingList {
	
	public ArrayList<NodeRecord> list;

	public PathFindingList(){ 
		list = new ArrayList<NodeRecord>();
	}
	
	/**
	 * Check if a record is in the list by matching to a vertex
	 * O(n)
	 * @param record
	 * @return true if the vertex is in the list
	 */
	public boolean contains(Vertex record){
		for( NodeRecord nr : list ){
			if( nr.node.id.equals(record.id) )
				return true;
		}
		return false;
	}
	
	/**
	 * Check if a record is in the list by matching to a vertex
	 * O(log(n))
	 * @param record
	 * @return true if the vertex is in the list
	 */
	public boolean containsById(Vertex record){
		NodeRecord rec = new NodeRecord(record, null, 0.0f, 0.0f);
		int pos = Collections.binarySearch(list, rec, NodeRecord.Comparators.id);

		if (pos >= 0) {
	        return true;
	    }
		return false;
	}
	
	/**
	 * Add a record at the end of list
	 * O(1)
	 * @param record
	 */
	public void addRecord(NodeRecord record){
		list.add(record);
	}
	
	/**
	 * Add a record into the list based off its cost
	 * Ascending (0 -> Max)
	 * O(log(n))
	 * @param record
	 */
	public void addRecordByCostSoFar(NodeRecord record){
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.costSoFarComparator);
		if (pos < 0) {
	        list.add(-pos-1, record);
	    } else { 
	    	list.add(pos, record);
	    }
	}
	
	/**
	 * Add a record into the list based off its estimated total cost
	 * Ascending (0 -> Max)
	 * O(log(n))
	 * @param record
	 */
	public void addRecordByEstimatedTotalCost(NodeRecord record){
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.estimatedTotalCostComparator);
		if (pos < 0) {
	        list.add(-pos-1, record);
	    } else { 
	    	list.add(pos, record);
	    }
	}
	
	/**
	 * Add a record into the list based off its id
	 * Ascending ( a -> z )
	 * O(log(n))
	 * @param record
	 */
	public void addRecordById(NodeRecord record){
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.id);
		if (pos < 0) {
	        list.add(-pos-1, record);
	    } else { 
	    	list.add(pos, record);
	    }
	}
	
	/**
	 * Removes a record
	 * O(n)
	 * @param record
	 */
	public void removeRecord(NodeRecord record){
		list.remove(record);
	}
	
	public void removeRecordByCostSoFar(NodeRecord record) {
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.costSoFarComparator);
		if (pos >= 0) {
	        list.remove(pos);
	    }
	}
	
	/**
	 * Removes a record from the list based off its estimated total cost
	 * Ascending (0 -> Max)
	 * O(log(n))
	 * @param record
	 */
	public void removeRecordByEstimatedTotalCost(NodeRecord record){
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.estimatedTotalCostComparator);
		if (pos >= 0) {
	        list.remove(pos);
	    }
	}
	
	/**
	 * Removes a record with binary search
	 * O(log(n))
	 * @param record
	 */
	public void removeRecordById(NodeRecord record){
		int pos = Collections.binarySearch(list, record, NodeRecord.Comparators.id);
		if (pos >= 0) {
	        list.remove(pos);
	    }
	}
	
	/**
	 * Gets the smallest node by its csf 
	 * This happens to be at 0 since we use sort on insertion
	 * @return record with smallest CSF
	 */
	public NodeRecord getSmallestNodeRecordByCostSoFar(){
	   	return list.get(0);
    }
	
	/**
	 * Gets the smallest node by its estimated total cost 
	 * This happens to be at 0 since we use sort on insertion
	 * @return record with smallest estimated total cost
	 */
	public NodeRecord getSmallestNodeRecordByEstimatedTotalCost(){
		return list.get(0);
	}
	
	/**
	 * Finds the record by matching the vertex
	 * O(n)
	 * @param record
	 * @return the node record corresponding to the vertex
	 */
	public NodeRecord find(Vertex record){
		for(NodeRecord nr : list){
			if( nr.node.id.equals(record.id) )
				return nr;
		}
		return null;
	}
	
	/**
	 * Finds the record by matching the vertex
	 * O(log(n))
	 * @param record
	 * @return the node record corresponding to the vertex
	 */
	public NodeRecord findById(Vertex record){
		NodeRecord rec = new NodeRecord(record, null, 0.0f, 0.0f);
		int pos = Collections.binarySearch(list, rec, NodeRecord.Comparators.id);
		if (pos >= 0) {
	        return list.get(pos);
	    }
		return null;
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public String toString(){
		String retString = "";
		for( NodeRecord record : list ){
			retString += record.node + "-" + record.costSoFar + " ";
		}
		return retString;
	}

}
