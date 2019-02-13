package character;

import java.util.ArrayList;

import processing.core.PApplet;

/**
 * Represents a flock of boids
 */
public class Flock {
	/** An ArrayList for all the boids */
	public ArrayList<Boid> boids;
	public PApplet app;
	private float radiusOfShape = 5.0f;

	/**
	 * Creates a new flock with 10 boids
	 */
	public Flock() {
		boids = new ArrayList<Boid>(); 
	}
	
	public Flock(PApplet app){
	    boids = new ArrayList<Boid>();
	    this.app = app;
	}
	
	/**
	 * Add a boid to the flock
	 * @param b boid to add
	 */
	public void addBoid(Boid b) {
		boids.add(b);
	}

	/**
	 * Update the steering for the flock
	 * @param wanderLeaders
	 */
	public void updateSteering(ArrayList<Kinematic> wanderLeaders) {
	    ArrayList<Kinematic> kins = new ArrayList<Kinematic>();
        for( Boid b : boids ) kins.add(b);
	    
		for (Boid  b : boids) {
			b.updateSteering(kins, wanderLeaders);
			checkBoundary(b);
			display(b);
		}
	}
	
	public void checkBoundary(Boid b){
	    float x = b.getPosition().x;
        float y = b.getPosition().y;
        
        // Check horizontal
        if (x < radiusOfShape){
            b.velocity.x *= -1;
            b.position.x += radiusOfShape;
        } else if (x > app.width-radiusOfShape){
            b.velocity.x *= -1;
            b.position.x -= radiusOfShape;
        }
        
        // Check vertical
        if (y < radiusOfShape){
            b.velocity.y *= -1;
            b.position.y += radiusOfShape;
        } else if (y > app.height-radiusOfShape){
            b.velocity.y *= -1;
            b.position.y -= radiusOfShape;
        }
	}
	
	/**
     * Draw each boid (arrow shape) in the flock
     */
    public void display(Boid b){          
        app.pushMatrix();
            app.translate(b.position.x, b.position.y);
            app.rotate(b.velocity.heading());
            app.fill(b.color[0], b.color[1], b.color[2]);
            app.stroke(0, 0);
            app.ellipse(0, 0, radiusOfShape*2, radiusOfShape*2);
            app.triangle(3, 4, 10, 0, 3, -4);
        app.popMatrix();
    }

}