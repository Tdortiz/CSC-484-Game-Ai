package tree_behaviorTree;

import java.util.HashMap;

import character.Kinematic;
import enums.SteeringState;

public class PerformAction extends Task {

	public PerformAction(Kinematic character, String data){
		super(character, data);
	}
	
	@Override
	public boolean run(HashMap<String, HashMap<String, Kinematic>> world) {
		Kinematic player = world.get(FRIENDLIES).get("player");
		
		switch(data){
			case "Pursue Enemy":
				character.target = new Kinematic((int)player.position.x, (int)player.position.y);
				character.steering = SteeringState.PURSUE;
				return true;
				
			case "Arrive at enemy":
				character.target = new Kinematic((int)player.position.x, (int)player.position.y);
				character.steering = SteeringState.ARRIVE_TARGET;
				return true;
			
			case "Sit":
				if(character.canDanceOrSitAgain)
					character.steering = SteeringState.SIT;
				else return false;
				return true;
			
			case "Dance":
				if(character.canDanceOrSitAgain)
					character.steering = SteeringState.DANCE;
				else return false;
				return true;
			
			case "Wander":
				if( character.steering != SteeringState.PURSUE)
					character.steering = SteeringState.WANDER;
				else return false;
				return true;
			
			case "Pathfind to TV":
				Kinematic tv = world.get(INANIMATE_OBJECTS).get("tv");
				character.target = new Kinematic((int)tv.position.x, (int)tv.position.y);
				character.steering = SteeringState.PATHFIND_TARGET;
				return true;
			
			default: 
				if( character.steering != SteeringState.PURSUE)
					character.steering = SteeringState.WANDER;
				break;
		}
		return false;
	}
	
	
}
