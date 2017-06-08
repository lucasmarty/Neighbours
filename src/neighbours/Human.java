package neighbours;

import repast.simphony.space.grid.Grid;

public class Human extends Agent{
	
	private int age;
	private int money;
	private int food;
	private int health;
	
	private House home;
	private Office office;
	
	private Schedule schedule;
	
	public Human(Grid<Agent> grid, int age, int money, int food, int health, House home, Office office)
	{
		super(grid);
		this.age = age;
		this.money = money;
		this.food = food;
		this.health = health;
		this.home = home;
		this.office = office;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void implement() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
