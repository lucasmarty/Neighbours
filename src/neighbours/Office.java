package neighbours;

import repast.simphony.space.grid.Grid;

public class Office extends Building {

	private int opening; //  hours[0,24]
	private int closure; //  hours[0,24]
	
	private boolean opened;
	
	private int salary;
	
	public Office(Grid<Agent> grid, int capacity, int opening, int closure) {
		super(grid, capacity);
		this.opening = opening;
		this.closure = closure;
		this.opened = false;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
		
	}

	public int getSalary() {
		return salary;
	}

}
