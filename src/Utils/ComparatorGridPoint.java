package Utils;

import java.util.Comparator;

import repast.simphony.space.grid.GridPoint;

public class ComparatorGridPoint implements Comparator<GridPoint>
{
	
	private int[][] distMap_;

	@Override
	public int compare(GridPoint arg0, GridPoint arg1) {
		
		return distMap_[arg0.getX()][arg0.getY()] - distMap_[arg1.getX()][arg1.getY()];
	}
	
	public ComparatorGridPoint(int[][] distMap)
	{
		distMap_ = distMap;
	}

}
