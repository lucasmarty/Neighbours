package utils;

public abstract class TransportType {

	protected int stepCost;
	
	public TransportType(int step)
	{
		stepCost = step;
	}
	
	public int getStep()
	{
		return stepCost;
	}
	
	public abstract int[][] generateWeightMap();
}
