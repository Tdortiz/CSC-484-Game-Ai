package character;

import java.util.LinkedList;

import enums.CharacterState;
import enums.ObjectType;
import enums.SteeringState;

public class Actor {
	
	public Kinematic kinematic;
	public CharacterState state;
	public SteeringState steering;
	public ObjectType objectType;
	public int awakeMeter = 100;
	public int hungerMeter = 100;
	public Kinematic target;
	public LinkedList<float[]> breadcrumbs;
	
	public Actor(){
		this.kinematic = new Kinematic();
		this.objectType = ObjectType.INANIMATE_OBJECT;
	    this.steering = SteeringState.NONE;
	    this.breadcrumbs = new LinkedList<float[]>();
	}

}
