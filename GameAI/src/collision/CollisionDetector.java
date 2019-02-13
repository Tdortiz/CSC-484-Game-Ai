package collision;

import processing.core.PVector;

public class CollisionDetector {
	
	/**
	 * 
	 * @param position starting position
	 * @param moveAmount move amount from starting position
	 * @return collision where position = collision point
	 * 		   and normal is the normal of the wall at the point
	 * 		   of collision.
	 * Typically, this call is implemented by casting a ray from position
	 * to position + moveAmount and checking for intersections with walls or other obstacles.
	 */
	public Collision getCollision(PVector position, PVector moveAmount){
		/// TODO implement this
		return null;
	}
	
}
