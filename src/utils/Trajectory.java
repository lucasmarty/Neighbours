package utils;

import java.util.ArrayList;

import repast.simphony.space.grid.GridPoint;

public class Trajectory {
	
	private int step;
	private int curStep;
	private int idx;
	private ArrayList<GridPoint> path;
	private GridPoint end;
	private Class<? extends TransportType> transporType;
	
	public Trajectory(ArrayList<GridPoint> path, GridPoint end, int step,
			Class<? extends TransportType> transporType)
	{
		idx = 0;
		curStep = 1;
		this.path = path;
		this.step = step;
		this.end = end;
		this.transporType = transporType;
	}
	
	public boolean isFinished()
	{
		return idx < 0 || idx >= path.size();
	}
	
	public GridPoint step()
	{
		
		GridPoint res =  path.get(idx);
		if (curStep >= step && !isFinished())
		{
			++idx;
			curStep = 1;
		}
		++curStep;
		return res;
	}
	
	
	public GridPoint getEnd() {
		return end;
	}

	public int getStep() {
		return step;
	}

	public Class<? extends TransportType> getTransporType() {
		return transporType;
	}
}
