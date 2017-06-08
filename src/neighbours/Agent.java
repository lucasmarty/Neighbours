package neighbours;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.Grid;

public abstract class Agent {

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public abstract void compute();

}
