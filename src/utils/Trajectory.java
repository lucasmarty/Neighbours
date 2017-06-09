package utils;

import java.util.ArrayList;

import repast.simphony.space.grid.GridPoint;

public class Trajectory {
	
	private int step;
	private int curStep;
	private int idx;
	private ArrayList<GridPoint> path;
	
	public Trajectory(ArrayList<GridPoint> path, int step)
	{
		idx = 0;
		curStep = 1;
		this.path = path;
		this.step = step;
	}
	
	public boolean isFinished()
	{
		return idx < 0 || idx >= path.size();
	}
	
	public GridPoint step()
	{
		if (curStep >= step && !isFinished())
		{
			++idx;
			curStep = 1;
		}
		
		++curStep;
		return path.get(idx);
	}
	
	public int getStep() {
		return step;
	}
	
	

}
