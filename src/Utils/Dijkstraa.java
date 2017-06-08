package Utils;

import java.util.ArrayList;
import java.util.HashSet;

import repast.simphony.space.grid.GridPoint;
import java.util.PriorityQueue;

import neighbours.Agent;
import neighbours.MainContext;
import neighbours.Road;

public class Dijkstraa {
	
	private int[][] gridWeight_;
	// if is 0 then it is not visited and infinite.
	private int[][] distMap_;
	private GridPoint[][] prev_;
	private int x_len;
	private int y_len;
	private PriorityQueue<GridPoint> minHeap;
	private GridPoint[] directions;
	
	public Dijkstraa(int[][] gridWeight)
	{
		if (gridWeight.length <= 0 || gridWeight[0].length <= 0)
			throw new IllegalArgumentException("wrong grid dimensions");
		gridWeight_ = gridWeight;
		x_len = gridWeight.length;
		y_len = gridWeight[0].length;
		
		
		directions = new GridPoint[4];
		directions[0] = new GridPoint(1, 0);
		directions[1] = new GridPoint(0, 1);
		directions[2] = new GridPoint(-1, 0);
		directions[3] = new GridPoint(0, -1);
		
	}
	
	private boolean checkPt(GridPoint pt)
	{
		if (pt.getX() < 0 || pt.getX() >= x_len)
			return false;
		if (pt.getY() < 0 || pt.getY() >= y_len)
			return false;
		
		if (gridWeight_[pt.getX()][pt.getY()] == 0)
			return false;
		
		if (distMap_[pt.getX()][pt.getY()] != 0)
			return false;
		
		return true;
	}
	
	private void updateDist(GridPoint from, GridPoint to)
	{
		if (distMap_[to.getX()][to.getY()] == 0 
				|| distMap_[to.getX()][to.getY()] > distMap_[from.getX()][from.getY()] + gridWeight_[from.getX()][from.getY()])
		{
			distMap_[to.getX()][to.getY()] = distMap_[from.getX()][from.getY()] + gridWeight_[from.getX()][from.getY()];
			prev_[to.getX()][to.getY()] = from;
		}
	}
	
	private ArrayList<GridPoint> buildPath(PriorityQueue<GridPoint> minHeapDest, GridPoint start)
	{
		ArrayList<GridPoint> result = new ArrayList<>();
		
		GridPoint end = minHeapDest.element();
		result.add(0, end);
		
		GridPoint cur = end;

		while (cur.getX() != start.getX() || cur.getY() != start.getY())
		{
			GridPoint prev = prev_[cur.getX()][cur.getY()];
			result.add(0, prev);
			cur = prev;
		}
		
		return result;
	}
	
	public ArrayList<GridPoint> shortestPathTo(HashSet<GridPoint> destination, GridPoint start)
	{
		distMap_ = new int[gridWeight_.length][gridWeight_[0].length];
		prev_ = new GridPoint[gridWeight_.length][gridWeight_[0].length];
		minHeap = new PriorityQueue<GridPoint>(100, new ComparatorGridPoint(distMap_));
		PriorityQueue<GridPoint> minHeapDest = new PriorityQueue<>(100, new ComparatorGridPoint(distMap_));
		
		minHeap.add(start);
		
		while (!minHeap.isEmpty() && !destination.isEmpty())
		{
			GridPoint cur = minHeap.remove();
			/*Agent r = new Road();
			MainContext.instance().getContext().add(r);
			MainContext.instance().getGrid().moveTo(r,  cur.getX(), cur.getY());*/
			for (GridPoint dir : directions)
			{
				GridPoint neighbour = new GridPoint(dir.getX() + cur.getX(), dir.getY() + cur.getY());
				if ((neighbour.getX() == start.getX() && neighbour.getY() == start.getY())
						|| !checkPt(neighbour))
					continue;
				
				updateDist(cur, neighbour);
				minHeap.add(neighbour);
			}
			
			if (destination.contains(cur))
			{
				destination.remove(cur);
				minHeapDest.add(cur);
			}
		}
		
		return buildPath(minHeapDest, start);
	}

	
	public static int[][] buildGridWeight(ArrayList<Class<? extends Agent>> agentUsed, int weight, boolean exclude_empty)
	{
		int[][] gridWeight = new int[MainContext.instance().getWidth()][MainContext.instance().getHeight()];
		
		for (int x = 0; x < MainContext.instance().getWidth(); ++x)
			for (int y = 0; y < MainContext.instance().getHeight(); ++y)
		    {
				boolean empty = true;
				boolean suitable = false;
				for( Object obj : MainContext.instance().getGrid().getObjectsAt(x, y))
				{
					if (empty)
						empty = false;
					for (Class<? extends Agent> objAuth : agentUsed)
					{
						if (obj.getClass().equals(objAuth))
						{
							suitable = true;
							break;
						}
					}
				}
				suitable = suitable || (empty && !exclude_empty);
				if (suitable)
				   gridWeight[x][y] = weight;

		   }
		
		return gridWeight;
	}
}
