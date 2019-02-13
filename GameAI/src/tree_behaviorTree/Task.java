package tree_behaviorTree;

import java.util.ArrayList;
import java.util.HashMap;

import character.Kinematic;

public abstract class Task {

	public String data;
	public Kinematic character;
	public ArrayList<Task> children;
	
	public final String FRIENDLIES = "friendlies";
	public final String ENEMIES = "enemies";
	public final String INANIMATE_OBJECTS = "inanimate_objects";
	
	public Task(){
		children = new ArrayList<Task>(3);
	}
	
	public Task(Kinematic character, String data){
		this.children = new ArrayList<Task>(3);
		this.character = character;
		this.data = data;
	}
	
	/**
	 * Terminates with either success (True) or failure (False)
	 * @return
	 */
	public abstract boolean run(HashMap<String, HashMap<String, Kinematic>> world);

	
	public void addChild(Task child){
		children.add(child);
	}
	
	public String toString(){
		return this.data;
	}
	
	
}
