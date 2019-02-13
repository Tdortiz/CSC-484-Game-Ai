package tree_behaviorTree;

import java.util.HashMap;

import character.Kinematic;

public class Sequence extends Question {

	public Sequence(Kinematic character, String data) {
		super(character, data);
	}

	@Override
	public boolean run(HashMap<String, HashMap<String, Kinematic>> world) {
		if( !decideToRun(world) ){
			return false;
		}
		
		for( Task c : children ){
			if( !c.run(world) ){
				return false;
			}
		}
		return true;
	}

	
	
}
