package neighbours;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class Human extends Agent{
	
	private int age;
	private int[] birth;
	private int money;
	private boolean hungry;
	private int health;
	
	private House home;
	private Office office;
	
	private Schedule schedule;
	
	public Human(int age, int[] birth, int money, int health, House home, Office office)
	{
		this.setAge(age);
		this.birth = birth;
		this.setMoney(money);
		this.health = health;
		this.setHome(home);
		this.office = office;
		this.setHungry(false);
	}

	@Override
	public void compute() {
		birthday();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void implement() {
		Context<Object> context = ContextUtils.getContext(this);
		death(context);
		GridPoint pos = MainContext.instance().getGrid().getLocation(this);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currMonth",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() {
		setMoney(getMoney() + office.getSalary());
	}
	
	private void death(Context<Object> context) {
		if (health == 0)
			context.remove(this);
	}
	
	private void birthday() {
		if (schedule.getCurrDay() == birth[0] && schedule.getCurrMonth() == birth[1] && schedule.getCurrYear() == birth[2])
			setAge(getAge() + 1);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public boolean isHungry() {
		return hungry;
	}

	public void setHungry(boolean hungry) {
		this.hungry = hungry;
	}

	public House getHome() {
		return home;
	}

	public void setHome(House home) {
		this.home = home;
	}
	
}
