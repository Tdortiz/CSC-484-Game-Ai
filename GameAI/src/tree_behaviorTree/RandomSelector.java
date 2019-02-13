package tree_behaviorTree;

import java.util.Collections;
import java.util.HashMap;

import character.Kinematic;

public class RandomSelector extends Question {

	public RandomSelector(Kinematic character, String data) {
		super(character, data);
	}

	@Override
	public boolean run(HashMap<String, HashMap<String, Kinematic>> world) {
		if( !decideToRun(world) ){
			return false;
		}
		
		Collections.shuffle(children);
		
		for( Task c : children ){
			if( c.run(world) ){
				return true;
			}
		}
		return false;
		
	}

}
