package neighbours;

import repast.simphony.engine.schedule.ScheduledMethod;

public abstract class Agent {

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public abstract void compute();

}
