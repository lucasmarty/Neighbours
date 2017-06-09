package neighbours;

import java.util.ArrayList;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;


public class House extends Building {

	private int food;
	private ArrayList<Human> habitants = new ArrayList<Human>();
	private ArrayList<IconAgent> animatedIcons = new ArrayList<>();
	private boolean hasMoneyIcon = false;

	
	
	@Override
	public void compute() {
		// Cleaning animated icon
		if (!animatedIcons.isEmpty())
		{
		  ArrayList<IconAgent> toRmv = new ArrayList<>();
		  
		  for (IconAgent icon : animatedIcons)
		  {
			 if (!MainContext.instance().getContext().contains(icon))
			 {
				 toRmv.add(icon);
				 if (icon instanceof MoneyIconAgent)
					 hasMoneyIcon = false;
			 }
		  }
		  animatedIcons.removeAll(toRmv);
		}
		
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
			watcheeFieldNames = "money",
			query = "linked_from",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void spawnMoneyIcon()
	{
		if (!hasMoneyIcon)
		{
		animatedIcons.add(new MoneyIconAgent(MainContext.instance().getGrid().getLocation(this)));
		hasMoneyIcon = true;
		}
	}
	
	
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}
}
