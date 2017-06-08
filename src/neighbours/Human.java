package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Human extends Agent{
	
	private int age;
	private int[] birth;
	private int money;
	private boolean hungry;
	private int health;
	
	private House home;
	private Office office;
	
	public Human() {
		
	}
	
	public Human(int age, int[] birth, int money, int health, House home, Office office)
	{
		this.setAge(age);
		this.setBirth(birth);
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

	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currMonth, currDay, currHour",
			triggerCondition = "$watchee.getCurrDay() == 1 "
							 + "&& $watchee.getCurrHour() == 1 "
							 + "&& $watchee.getCurrMonth() == 1",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() {
		//setMoney(getMoney() + office.getSalary());
	}

	@Watch(watcheeClassName = "neighbours.Office",
			watcheeFieldNames = "opened",
			triggerCondition = "$watchee.isOpened",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void goToWork() {
		// TODO
	}
	
	public void goShopping() {
		// TODO
	}
	
	private void death() {
		MainContext.instance().getContext().remove(this);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currMonth, currDay, currHour",
			triggerCondition = "$watchee.getCurrDay() == $watcher.getBirth()[0] "
							 + "&& $watchee.getCurrHour() == 1 "
							 + "&& $watchee.getCurrMonth() ==$watche.getBirth[]",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	private void birthday() {
		setAge(getAge() + 1);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHealth() {
		return health;
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

	public int[] getBirth() {
		return birth;
	}

	public void setBirth(int[] birth) {
		this.birth = birth;
	}
}
