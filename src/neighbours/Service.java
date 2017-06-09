package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public abstract class Service extends Building {

	protected int opening; //  hours[0,24]
	protected int closure; //  hours[0,24]
	
	protected boolean opened;
	protected int cost;
	
	protected int timePerService;
	
	
	public abstract void provideService(Human human);
	
	public int getTimePerService() {
		return timePerService;
	}

	public void setTimePerService(int timePerService) {
		this.timePerService = timePerService;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}
	
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub

	}

	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getOpening()",
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
