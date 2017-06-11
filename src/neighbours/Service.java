package neighbours;

import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.GridPoint;
import net.sf.jasperreports.engine.util.JRStyledText.Run;
import repast.simphony.engine.environment.RunEnvironment;

public abstract class Service extends Building {

	protected int opening; //  hours[0,24]
	protected int closure; //  hours[0,24]
	
	protected boolean opened = false;
	protected int cost;
	
	// in hour
	protected int timePerService;
	
	
	public void provideService(Human human)
	{
		if (human == null)
			return;
		
		if (human.getMoney() >= cost)
		   implementService(human);

		
		human.serviceDone(this);
		human.goHomeFrom(this);
		this.decreaseUsed();
	}
	
	@Watch(watcheeClassName = "neighbours.Human",
			watcheeFieldNames = "moving",
			query = "linked_from",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void triggerService()
	{
		GridPoint pt = MainContext.instance().getGrid().getLocation(this);
		for (Agent a : MainContext.instance().getGrid().getObjectsAt(pt.getX(), pt.getY()))
		{
			if (a instanceof Human)
			{
				Human h = (Human)a;
				RepastEdge<Agent> edge = MainContext.instance().getNetworkBuilding().getEdge(h, this);
				if (edge != null)
				{
					int tick = MainContext.instance().getSchedule().getCurrentTick();
					int tickPerHour = MainContext.instance().getSchedule().aHour;
					ScheduleParameters  params = ScheduleParameters.createOneTime(tick + timePerService * tickPerHour);
					ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();
					
					scheduleSim.schedule(params, this, "provideService", h);
					h.setAtService(true);
				}	
			}
		}
	}
	
	protected abstract void implementService(Human human);
	
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
	public void opened() {
		if (!opened)
		  this.setOpened(true);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watcher.getClosure()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void close() {
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
