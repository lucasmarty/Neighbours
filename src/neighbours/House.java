package neighbours;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;


public class House extends Building {

	private int food = 1;
	private boolean shoppingScheduled = false;
	private ArrayList<Human> habitants = new ArrayList<Human>();

	
	private static int thresholdFood = 10;
	private int timeToEat = 20; // hours[1->24]
	

	
	@Override
	public void compute() {
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
			triggerCondition = "$watchee.isHungry() == true",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void consumeFood() {
		if (food > 0)
		{
			for (Agent h : MainContext.instance().getNetworkBuilding().getPredecessors(this))
			{
				if (h instanceof Human)
				{
				   Human human = (Human)h;
				   human.setHungry(false);
				   
				}
			}
			System.out.println("eating !");
			--food;
		}
	}
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}


	public static int getThresholdFood() {
		return thresholdFood;
	}

	public static void setThresholdFood(int thresholdFood) {
		House.thresholdFood = thresholdFood;
	}

	public int getTimeToEat() {
		return timeToEat;
	}

	public void setTimeToEat(int timeToEat) {
		this.timeToEat = timeToEat;
	}

	public boolean isShoppingScheduled() {
		return shoppingScheduled;
	}

	public void setShoppingScheduled(boolean shoppingScheduled) {
		this.shoppingScheduled = shoppingScheduled;
	}
}
