package neighbours;

import repast.simphony.space.grid.Grid;

public abstract class Building extends Agent{
	protected int capacity;

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	
}
