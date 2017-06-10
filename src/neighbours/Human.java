package neighbours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;


import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.GridPoint;
import utils.Car;
import utils.Dijkstraa;
import utils.Trajectory;
import utils.TransportType;
import utils.Walk;

public class Human extends Agent{
	
	private int age;
	private int[] birth;
	private int money;
	private boolean paid = false;
	private boolean hungry;
	private boolean moving;
	private boolean isCurrWorking = false;
	private int health;
	private int boredom = 0;
	private int lastBored = 1;
	private Queue<Trajectory> traj_queue;
	private Trajectory currentTraj;
	private CarAgent car;
	

	private ArrayList<IconAgent> animatedIcons = new ArrayList<>();
	
	private House home;
	private RepastEdge<Agent> edge_home;
	private RepastEdge<Agent> edge_office;
	private Office office;
	
	public Office getOffice() {
		return office;
	}

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
		}
	}

	public Office getOffice() {
		return office;
	}

	public void setLastPaid(boolean paid) {
		this.paid = paid;
	}

	public Human() {
		
	}
		

	public Human(int age, int[] birth, int money, int health)
	{
		this.setAge(age);
		this.setBirth(birth);
		this.setMoney(money);
		this.health = health;
		this.setHome(home);
		this.office = office;
		this.setHungry(false);
		this.setCurrWorking(false);
		moving = false;
		traj_queue = new LinkedList<>();
		
		MainContext.instance().getNetworkBuilding().addEdge(this, office);
		MainContext.instance().getNetworkBuilding().addEdge(this, home);
	}

	@Override
	public void compute() {
		birthday();
		move();
		cleanIcons();
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watcher.isCurrWorking() "
			 + "&& $watcher.getLastBored() + $watchee.aHour * 6 <= $watchee.getCurrentTick()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void increaseBoredom()
	{
		boredom++;
		lastBored = MainContext.instance().getSchedule().getCurrentTick();
		animatedIcons.add(new BoredomIconAgent(MainContext.instance().getGrid().getLocation(this)));
	}
	
	public int getLastBored() {
		return lastBored;
	}

	public int getBoredom() {
		return boredom;
	}

	private void cleanIcons()
	{
		// Cleaning animated icon
		if (!animatedIcons.isEmpty())
		{
		  ArrayList<IconAgent> toRmv = new ArrayList<>();
		  
		  for (IconAgent icon : animatedIcons)
		  {
			 if (!MainContext.instance().getContext().contains(icon))
				 toRmv.add(icon);
		  }
		  animatedIcons.removeAll(toRmv);
		}
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
	
	
	public Shop findShop() {
		ArrayList<BuildingZone<Shop>> shop_zones = MainContext.instance().getShopZones();
		for (BuildingZone<Shop> shop_zone : shop_zones) {
			for (Shop shop : shop_zone.getBuildings())
			{
				if (shop.isOpened())
					return shop;
			}
		}
		return null;
	}
	
	public boolean getLastPaid()
	{
		return paid;
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currDay",
			triggerCondition = "$watchee.getCurrDay() == 1 "
							 + "&& $watchee.getCurrHour() == 1 "
					         + "&& $watcher.getLastPaid() == false",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() 
	{
		
		if (office != null && !getLastPaid())
		    setMoney(getMoney() + office.getSalary());
		
		setLastPaid(true);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currDay",
			triggerCondition = "$watchee.getCurrDay() == 2 ",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void resetLastPaid()
	{
		setLastPaid(false);
	}

	@Watch(watcheeClassName = "neighbours.Office",
			watcheeFieldNames = "opened",
			query = "linked_to",
			triggerCondition = "$watchee.isOpened() == true",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void goToWork() {
		if (office != null && home != null)
		{
			isCurrWorking = true;
			moveBuilding2Building(home, office);
		}
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watchee.getCurrHour() == $watchee.getHome().getTimeToEat()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void goHome()
	{
		if (office != null && home != null)
		{
			isCurrWorking = false;
			moveBuilding2Building(office, home);
		}
	}

	public void triggerHungger() {
		setHungry(true);
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
	
	@Watch(watcheeClassName = "neighbours.House",
			watcheeFieldNames = "food",
			query = "linked_to",
			triggerCondition = "$watchee.getFood() <= $watchee.getTesholdFood())",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void scheduleShopping() {
		if (!isCurrWorking)
		{
			Shop shop = findShop();
			if(shop != null)
				moveBuilding2Building(home, shop);
		}
	}
	
	@Watch(watcheeClassName = "neighbours.Shop",
			watcheeFieldNames = "opened",
			query = "within 0",
			triggerCondition = "$watchee.getOpened() == true)",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void shopping() {
		System.out.println("Buy food");
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
			moveBuilding2Building(office, home);
		}
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
			watcheeFieldNames = "currMonth, currDay",
			triggerCondition = "$watchee.getCurrDay() == $watcher.getBirth()[0] "
							 + "&& $watchee.getCurrHour() == 1 "
							 + "&& $watchee.getCurrMonth() == $watche.getBirth[1]",
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

	public boolean isCurrWorking() {
		return isCurrWorking;
	}

	public void setCurrWorking(boolean isCurrWorking) {
		this.isCurrWorking = isCurrWorking;
	}
}
