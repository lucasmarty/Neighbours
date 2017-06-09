package neighbours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import utils.Car;
import utils.Dijkstraa;
import utils.Trajectory;
import utils.TransportType;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Human extends Agent{
	
	private int age;
	private int[] birth;
	private int money;
	private boolean hungry;
	private boolean moving;
	private int health;
	private Queue<Trajectory> traj_queue;
	private Trajectory currentTraj;
	
	private House home;
	private Office office;
	
	public Office getOffice() {
		return office;
	}

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
		moving = false;
		traj_queue = new LinkedList<>();
		
		MainContext.instance().getNetworkBuilding().addEdge(this, office);
	}

	@Override
	public void compute() {
		birthday();
		
		move();
	}
	
	private void move()
	{
		if (!moving && !traj_queue.isEmpty())
		{
			moving = true;
			currentTraj = traj_queue.remove();
		}
		else if (moving)
		{
			if (currentTraj.isFinished())
			{
				MainContext.instance().getGrid().moveTo(this, currentTraj.getEnd().getX(),
						currentTraj.getEnd().getY());
				moving = false;
				currentTraj = null;
			}
			else
			{
				GridPoint pt = currentTraj.step();
				//if (currentTraj.getTransporType().isInstance(Car.class))
				MainContext.instance().getGrid().moveTo(this, pt.getX(), pt.getY());
			}
		}
	}

	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currDay, currHour",
			triggerCondition = "$watchee.getCurrDay() == 1 "
							 + "&& $watchee.getCurrHour() == 1 ",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() {
		setMoney(getMoney() + office.getSalary());
	}

	@Watch(watcheeClassName = "neighbours.Office",
			watcheeFieldNames = "opened",
			query = "linked_to",
			triggerCondition = "$watchee.isOpened() == true",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void goToWork() {
		if (office != null && home != null)
		{
		GridPoint from = home.getStartingPos();
		GridPoint to = office.getStartingPos();
		GridPoint end = MainContext.instance().getGrid().getLocation(office);
		
		System.out.println("Starting point from: " + MainContext.instance().getGrid().getLocation(home)
				+ " is " + from);
		
		System.out.println("Starting point from: " + end
				+ " is " + to);
		try {
			planNextPath(from, to, end, Car.class);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	public void goShopping() {
		// TODO
	}
	
	private void death() {
		MainContext.instance().getContext().remove(this);
	}
	
	@Watch(watcheeClassName = "neighbours.Office",
			watcheeFieldNames = "opened",
			query = "linked_to",
			triggerCondition = "$watchee.isOpened() == false",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void goHome()
	{
		
		if (office != null && home != null)
		{
		GridPoint from = office.getStartingPos();
		GridPoint to = home.getStartingPos();
		GridPoint end = MainContext.instance().getGrid().getLocation(home);
		try {
			planNextPath(from, to, end, Car.class);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	private void planNextPath(GridPoint from, GridPoint dest, GridPoint end, Class<? extends TransportType> transport) 
			throws InstantiationException, IllegalAccessException
	{
		TransportType tr = transport.newInstance();
		int[][] weightMap = tr.generateWeightMap();
		
		
		HashSet<GridPoint> destPt = new HashSet<>();
		destPt.add(dest);
		Dijkstraa djk = new Dijkstraa(weightMap);
		
		
		ArrayList<GridPoint> path = djk.shortestPathTo(destPt, from);
		Trajectory traj = new Trajectory(path, end, tr.getStep(), transport);
		
		traj_queue.add(traj);
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
