package neighbours;

import repast.simphony.space.grid.Grid;

public class House extends Building {

	private int food;
	
	public House(Grid<Agent> grid, int cpacity, int food) {
		super(grid, cpacity);
		this.food = food;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}
}
