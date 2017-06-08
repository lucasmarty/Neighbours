package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.grid.Grid;

public class Office extends Building {

	private int opening; //  hours[0,24]
	private int closure; //  hours[0,24]
	
	private boolean opened;
	
	private int salary;
	
	public Office(Grid<Agent> grid, int capacity, int opening, int closure) {
		super(grid, capacity);
		this.setOpening(opening);
		this.setClosure(closure);
		this.setOpened(false);
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	public int getSalary() {
		return salary;
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getOpening() "
					 + "&& $watchee.getCurrDay() != 6" 
					 + "&& $watchee.getCurrDay() != 7",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	private void opened() {
		this.setOpened(true);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getClosure()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	private void close() {
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
