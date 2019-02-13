package graph;

/**
 * Represents a vertex inside of a graph 
 * 
 * @author Thomas Ortiz (tdortz)
 */
public class Vertex implements Comparable<Vertex>  {
	
    /** Id of vertex */
    public String id;
    /** x pos */
	public float x;
	/** y pos */
	public float y;
	
	public Vertex(){}
	
	/**
	 * Creates a new vertex 
	 * 
	 * @param id of vertex 
	 * @param x pos
	 * @param y pos
	 */
	public Vertex(String id, float x, float y){
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return id;
	}
	
	public boolean equals(Object other){
		if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Vertex)) return false;
	    
	    Vertex otherVertex = (Vertex)other;
	    if( otherVertex.id.equals(this.id) ) return true;
	    
		return false;
	}

	@Override
	public int compareTo(Vertex otherV) {
		return this.id.compareTo(otherV.id);
	}
}
