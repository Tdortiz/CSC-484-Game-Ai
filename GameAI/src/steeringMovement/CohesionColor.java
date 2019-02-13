package steeringMovement;

import java.util.ArrayList;

import character.Boid;
import character.Kinematic;
import processing.core.PVector;

public class CohesionColor extends Seek implements Behavior {

    private ArrayList<Kinematic> flock;
    private float neighborDist;

    /**
     * 
     * @param character
     * @param flock
     * @param neighborDist
     */
    public CohesionColor(Kinematic character, ArrayList<Kinematic> flock, float neighborDist){
        super(character, new Kinematic(), character.maxAcceleration);
        this.flock = flock;
        this.neighborDist = neighborDist;
    }
    
    @Override
    public SteeringOutput getSteering() {
        return getSteering(this.flock);
    }
    
    public SteeringOutput getSteering(ArrayList<Kinematic> flock){
        PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all positions
        float count = 0;
        
        for (Kinematic other : flock) {
            float d = PVector.dist(character.position, other.position);
            Boid tempChar = (Boid) character;
            Boid tempTar = (Boid) other;
            boolean closeColor = isNeighborColor(tempChar.color, tempTar.color, 25);
            
            if( (d > 0) && (d < neighborDist) && closeColor ) {
                sum.add(other.position); // Add position
                count++;
            }
        }
        
        if (count > 0) {
            sum.div(count);
            target.setPosition(sum);
            return super.getSteering();  // Steer towards the position
        }
        
        return new SteeringOutput();
    }
    
    public boolean isNeighborColor(int[] color1, int[] color2, int tolerance) {
        if(tolerance <= 0 || tolerance >= 50) {
            tolerance = 32;
        }
        
        PVector c1 = new PVector(color1[0], color1[1], color1[2]);
        PVector c2 = new PVector(color2[0], color2[1], color2[2]);
        float d = PVector.dist(c1, c2);
        return d <= tolerance;
    }

}
