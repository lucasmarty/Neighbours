package neighbours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import utils.Car;
import utils.Dijkstraa;
import utils.Trajectory;
import utils.TransportType;
import utils.Walk;
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
	private CarAgent car;
	
	private House home;
	private RepastEdge<Agent> edge_home;
	private RepastEdge<Agent> edge_office;
	private Office office;
	
	public void setOffice(Office office) {
		
		if (this.office != null && this.edge_office != null)
		{
			MainContext.instance().getNetworkBuilding().removeEdge(edge_office);
			this.office.decreaseUsed();
		}
		
		this.office = office;
		if (this.office != null)
		{
		  edge_office = MainContext.instance().getNetworkBuilding().addEdge(this, office);
		
		  this.office.increasedUsed();
		  System.out.println("office set!");
		}
	}

	public Office getOffice() {
		return office;
	}

	public Human() {
		
	}
	
	public Human(int age, int[] birth, int money, int health)
	{
		this.setAge(age);
		this.setBirth(birth);
		this.setMoney(money);
		this.health = health;
		this.setHungry(false);
		moving = false;
		traj_queue = new LinkedList<>();
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
			
			if (currentTraj.getTransporType().equals(Car.class))
			{	
			  car = new CarAgent();
			  MainContext.instance().getContext().add(car);
			}
		}
		else if (moving)
		{
			if (currentTraj.isFinished())
			{
				if (car != null)
				{
					MainContext.instance().getContext().remove(car);
					car = null;
				}
				moving = false;
				currentTraj = null;
			}
			else
			{
				GridPoint pt = currentTraj.step();
				if (car != null)
				{
					MainContext.instance().getGrid().moveTo(car, pt.getX(), pt.getY());
				}
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
		if (office != null)
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
			moveBuilding2Building(home, office);
		}
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
			moveBuilding2Building(office, home);
		}
	}
	
	private void moveBuilding2Building(Building start, Building stop)
	{
		GridPoint from = start.getStartingPos();
		GridPoint to = stop.getStartingPos();
		GridPoint toW = MainContext.instance().getGrid().getLocation(stop);
		GridPoint fromW = MainContext.instance().getGrid().getLocation(start);
		
		try {
			planNextPath(fromW, from, Walk.class);
			planNextPath(from, to, Car.class);
			planNextPath(to, toW, Walk.class);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void goShopping() {
		// TODO
	}
	
	private void death() {
		MainContext.instance().getContext().remove(this);
	}
	
	
	
	private void planNextPath(GridPoint from, GridPoint dest, Class<? extends TransportType> transport) 
			throws InstantiationException, IllegalAccessException
	{
		TransportType tr = transport.newInstance();
		int[][] weightMap = tr.generateWeightMap();
		
		
		HashSet<GridPoint> destPt = new HashSet<>();
		destPt.add(dest);
		Dijkstraa djk = new Dijkstraa(weightMap);
		
		
		ArrayList<GridPoint> path = djk.shortestPathTo(destPt, from);
		Trajectory traj = new Trajectory(path, tr.getStep(), transport);
		
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
		if (this.home != null && this.edge_home != null)
		{
			MainContext.instance().getNetworkBuilding().removeEdge(edge_home);
			this.home.decreaseUsed();
		}
		
		this.home = home;
		if (this.home != null)
		{
		  edge_home = MainContext.instance().getNetworkBuilding().addEdge(this, this.home);
		
		  this.home.increasedUsed();
		}
	}

	public int[] getBirth() {
		return birth;
	}

	public void setBirth(int[] birth) {
		this.birth = birth;
	}
}
