package path;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;

import graph.Graph;
import graph.Vertex;
import processing.core.PVector;

/**
 * A path for a character to follow
 */
public class Path {

    private Graph g;
    private ArrayList<DefaultWeightedEdge> path;
    
    public Path() {
        path = new ArrayList<DefaultWeightedEdge>();
    }
    
    public Path(Graph g, ArrayList<DefaultWeightedEdge> path){
        this.g = g;
    	this.path = path;
    }
    
    /**
     * Finds the closest point on the path
     * @param position (future position)
     * @param lastParam (current character position)
     */
    public int getIndex(int currentIndexOnPath, PVector futurePosition){
        float distanceToTarget = Float.MAX_VALUE;
        int targetIndex = path.size()-1;
        
        for(int i = currentIndexOnPath; i < path.size(); i++){
            DefaultWeightedEdge currEdge = path.get(i);
            
            Vertex source = null;
            try { 
                source = g.graph.getEdgeSource(currEdge);
            } catch (NullPointerException e){
                continue;
            }
            
            PVector sourceVector = new PVector(source.x, source.y);
            
        	float distanceCheck = sourceVector.dist(futurePosition);
        	if( distanceCheck < distanceToTarget ){
        		targetIndex = i;
        		distanceToTarget = distanceCheck;
        	}
        	
        }
        
        return targetIndex;
    }
    
    /**
     * Returns the present PVector position on the path
     * @param index on the path
     * @return position PVector
     */
    public PVector getPosition(int index){
    	if( path.isEmpty() )
    		return new PVector(0,0);
    	
    	PVector position = null;
    	
    	// Special Case for last node on path
    	if( index >= path.size() ){
    		index = path.size()-1;
    		if( index < 0 ) index = 0;
    		DefaultWeightedEdge returnEdge = path.get(index);
        	Vertex source = g.graph.getEdgeTarget(returnEdge);
        	return new PVector(source.x, source.y);
    	}
    		
    	DefaultWeightedEdge returnEdge = path.get(index);
    	Vertex source = g.graph.getEdgeSource(returnEdge);
    	position = new PVector(source.x, source.y);
    	
        return position;
    }
    
    public void addPathNode(DefaultWeightedEdge edge){ path.add(edge); }
    public ArrayList<DefaultWeightedEdge> getPath(){ return this.path; }
    
    public String toString(){
        String retString = "";
        for(DefaultWeightedEdge e : path){
            retString += e + " ";
        }
        return retString;
    }
   
}
