package neighbours;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;


public class House extends Building {

	private int food; 
	private static int thesholdFood = 20;
	private int timeToEat = 20; // hours[1->24]
	
	private ArrayList<Human> habitants;

	public House()
	{
		habitants = new ArrayList<Human>();
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Human> getHabitants() {
		return habitants;
	}
	
	
	public boolean addHabitant(Human human) {
		if (habitants.size() >= this.capacity)
			return false;
		habitants.add(human);
		return true;
	}
	
	@Watch(watcheeClassName = "neighbours.Human",
			watcheeFieldNames = "hungry",
			query = "linked_from",
			triggerCondition = "$watchee.getHungry() == true",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void consumeFood() {
		this.setFood(this.getFood() - 1);
	}
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public static int getThesholdFood() {
		return thesholdFood;
	}

	public static void setThesholdFood(int thesholdFood) {
		House.thesholdFood = thesholdFood;
	}

	public int getTimeToEat() {
		return timeToEat;
	}

	public void setTimeToEat(int timeToEat) {
		this.timeToEat = timeToEat;
	}
}
