package neighbours;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;


public class House extends Building {

	private int food;
	private ArrayList<Human> habitants;

	public House()
	{
		habitants = new ArrayList<Human>();
	}

	public void decreaseUsed()
	{
		if (used > 0)
		 --used;
	}
	
	public void increasedUsed()
	{
		if (used < capacity)
			++used;
	}
	
	public boolean isFull()
	{
		return used == capacity;
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
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}
}
