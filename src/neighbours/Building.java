package neighbours;

import repast.simphony.space.grid.Grid;

public abstract class Building extends Agent{
	private int capacity;
	
	public Building(Grid<Agent> grid, int cpacity) {
		super(grid);
		this.capacity = capacity;
	}
	
}
