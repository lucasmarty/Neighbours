package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Office extends Building {

	private int opening; //  hours[0,24]
	private int closure; //  hours[0,24]
	
	private boolean opened = false;
	
	private int salary = 0;
	

	@Override
	public void compute() {
		// TODO Auto-generated method stub
	}

	public int getSalary() {
		return salary;
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
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getOpening()"
					 + " && $watchee.getCurrDay() != 6" 
					 + " && $watchee.getCurrDay() != 7",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void open() {
		if (!opened)
			this.setOpened(true);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getClosure()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void close() 
	{
		if (opened)
		   this.setOpened(false);	
		
	}

	public int getOpening() {
		return opening;
	}

	public void setOpening(int opening) {
		this.opening = opening;
	}

	public int getClosure() {
		return closure;
	}

	public void setClosure(int closure) {
		this.closure = closure;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

}
