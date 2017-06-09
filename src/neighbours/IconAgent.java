package neighbours;

import java.util.ArrayList;

import repast.simphony.space.grid.GridPoint;
import utils.Trajectory;
import utils.Walk;

public abstract class IconAgent extends Agent {

	private int len = 5;
	private Trajectory trajectory;
	
	public IconAgent(GridPoint startPoint, int step)
	{
		int y_max = Math.min(MainContext.instance().getHeight() - 1, startPoint.getY() + len);
		ArrayList<GridPoint> path = new ArrayList<>();
		
		for (int idx = 0; idx < y_max - startPoint.getY(); ++idx)
			path.add(new GridPoint(startPoint.getX(), startPoint.getY() + idx));
		
		trajectory = new Trajectory(path, step, Walk.class);
		MainContext.instance().getContext().add(this);
		MainContext.instance().getGrid().moveTo(this, startPoint.getX(), startPoint.getY());
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub

		if (trajectory != null && !trajectory.isFinished())
		{
			GridPoint pt = trajectory.step();
			MainContext.instance().getGrid().moveTo(this, pt.getX(), pt.getY());
		}
		else
		{
			trajectory = null;
			MainContext.instance().getContext().remove(this);
		}
	}

}
