package neighbours;

import repast.simphony.context.Context;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Human extends Agent{
	
	private int age;
	private int[] birth;
	private int money;
	private int food;
	private int health;
	
	private House home;
	private Office office;
	
	private Schedule schedule;
	
	public Human(Grid<Agent> grid, int age, int[] birth, int money, int food, int health, House home, Office office)
	{
		super(grid);
		this.age = age;
		this.birth = birth;
		this.money = money;
		this.food = food;
		this.health = health;
		this.home = home;
		this.office = office;
	}

	@Override
	public void compute() {
		birthday();
	}

	@Override
	public void implement() {
		Context<Object> context = ContextUtils.getContext(this);
		death(context);
		GridPoint pos = grid.getLocation(this);
	}
	
	@Watch(watcheeClassName = "neigbours.Schedule",
			watcheeFieldNames = "currMonth",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() {
		money += office.getSalary();
	}
	
	private void death(Context<Object> context) {
		if (health == 0)
			context.remove(this);
	}
	
	private void birthday() {
		if (schedule.getCurrDay() == birth[0] && schedule.getCurrMonth() == birth[1] && schedule.getCurrYear() == birth[2])
			age += 1;
	}
	
}
