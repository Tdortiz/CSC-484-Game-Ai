package tree_behaviorTree;

import java.util.HashMap;

import character.Kinematic;
import enums.SteeringState;
import processing.core.PVector;

public abstract class Question extends Task {

	private final String FRIENDLIES = "friendlies";
	private final String ENEMIES = "enemies";
	private final String INANIMATE_OBJECTS = "inanimate_objects";
	
	public Question(Kinematic character, String data){
		super(character, data);
	}
	
	public boolean decideToRun(HashMap<String, HashMap<String, Kinematic>> world){
		Kinematic player = world.get(FRIENDLIES).get("player");
		Kinematic tv = world.get(INANIMATE_OBJECTS).get("tv");
		
		boolean nearEnemy = false;
		boolean nearTV = false;
		
		nearEnemy = character.position.dist(player.position) <= character.MED_RADIUS_TO_ENEMY;
		nearTV = character.position.dist(tv.position) <= character.MED_RADIUS_TO_TV;
		
		switch(data){
			case "Not near TV or Player?":
				return (!nearEnemy && !nearTV);
		
			case "Not Acting?":
				if( character.steering == SteeringState.DANCE || character.steering == SteeringState.SIT){
					return false;
				}
				return true;
				
			case "Can act again?":
				return character.canDanceOrSitAgain;
			
			case "Kill Enemy":
				return true;
			
			case "Near Enemy?":
				return nearEnemy;
				
			case "Very close to enemy?":
				nearEnemy =  character.position.dist(player.position) <= character.SM_RADIUS_TO_ENEMY;
				return nearEnemy;
				
			case "Near TV?":
				return nearTV;
				
			case "Finish Pathfinding to TV?":
				nearTV = character.position.dist(tv.position) <= character.SM_RADIUS_TO_TV;
				return nearTV;
			
			case "ROOT":
				return true;
		}
		return false;
	}
	
	public boolean checkRadialDistance(float x, float x2, float y, float y2, int radius){
		return (Math.abs(x-x2) <= radius) && (Math.abs(y-y2) <= radius);
	}
	
	private boolean checkRadialDistance(PVector position, PVector position2, int radius) {
		return position.dist(position2) <= radius;
	}

}
