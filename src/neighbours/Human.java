package neighbours;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ISchedulableAction;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
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
	private boolean hungry;
	private boolean moving;
	private int initHealth;
	private boolean isCurrWorking = false;
	private boolean isAtService = false;
	public boolean isAtService() {
		return isAtService;
	}

	public void setAtService(boolean isAtService) {
		this.isAtService = isAtService;
	}

	private int health;
	private int boredom = 0;
	private int lastBored = 1;
	private Queue<Trajectory> traj_queue;
	private Trajectory currentTraj;
	private CarAgent car;
	private ISchedulableAction scheduleShopping = null;
	private ISchedulableAction scheduleMovie = null;
	private boolean scheduledMovie = false;
	

	private ArrayList<IconAgent> animatedIcons = new ArrayList<>();
	
	private House home;
	private Office office;
	

	public boolean setOffice(Office office) {
		
		if (this.office != null)
		{
			RepastEdge<Agent> edge = MainContext.instance().getNetworkBuilding().getEdge(this, this.office);
			if (edge != null)
			   MainContext.instance().getNetworkBuilding().removeEdge(edge);
			this.office.decreaseUsed();
		}
		
		this.office = office;
		if (this.office != null && !this.office.isFull())
		{
		  MainContext.instance().getNetworkBuilding().addEdge(this, office);
		
		  this.office.increasedUsed();
		  return true;
		}
		
		return false;
	}
	
	public boolean setHome(House home) {
		if (this.home != null)
		{
			RepastEdge<Agent> edge = MainContext.instance().getNetworkBuilding().getEdge(this, this.home);
			if (edge != null)
			   MainContext.instance().getNetworkBuilding().removeEdge(edge);
			this.home.decreaseUsed();
		}
		
		this.home = home;
		if (this.home != null && !this.home.isFull())
		{
		  MainContext.instance().getNetworkBuilding().addEdge(this, home);
		
		  this.home.increasedUsed();
		  return true;
		}
		
		return false;
	}
	

	public void setBoredom(int boredom) {
		if (boredom > this.boredom)
		   animatedIcons.add(new BoredomIconAgent(MainContext.instance().getGrid().getLocation(this)));
		else if (boredom < this.boredom)
		{
			GridPoint pt = MainContext.instance().getGrid().getLocation(this);
			GridPoint side = new GridPoint(pt.getX() - 1, pt.getY());
			animatedIcons.add(new HappyIconAgent(side));
		}
		this.boredom = boredom;
	}

	public Office getOffice() {
		return office;
	}
		

	public Human(int age, int[] birth, int money, int health)
	{
		this.setAge(age);
		this.setBirth(birth);
		this.money = money;
		this.health = health;
		initHealth = health;
		hungry = false;
		this.setCurrWorking(false);
		moving = false;
		traj_queue = new LinkedList<>();
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
		setBoredom(boredom + 1);
		lastBored = MainContext.instance().getSchedule().getCurrentTick();
	}
	
	public void setMoney(int money) 
	{
		
		if (money > this.money)
			animatedIcons.add(new MoneyPlusIconAgent(MainContext.instance().getGrid().getLocation(this)));
		else if (money < this.money)
			animatedIcons.add(new MoneyLessIconAgent(MainContext.instance().getGrid().getLocation(this)));
		this.money = money;
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
			 {
				 toRmv.add(icon);
				 if (icon instanceof DeathIconAgent)
					 death();
			 }
					 
		  }
		  animatedIcons.removeAll(toRmv);
		}
	}
	
	private void move()
	{
		if (!moving && !traj_queue.isEmpty() && !isAtService)
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
			watcheeFieldNames = "currDay",
			triggerCondition = "$watchee.getCurrDay() == 1 ",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() 
	{
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
			isCurrWorking = true;
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
			isCurrWorking = false;
			moveBuilding2Building(office, home);
		}
	}
	
	public void goHomeFrom(Building b)
	{
		if (b != null && home != null)
		{
			moveBuilding2Building(b, home);
		}
	}
	


	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currHour",
			triggerCondition = "$watcher.getHome() != null "
			+ "&& $watchee.getCurrHour() == $watcher.getHome().getTimeToEat()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void triggerHungger()
	{
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
			triggerCondition = "$watchee.getFood() <= $watchee.getThresholdFood()"
			                + " && $watchee.isShoppingScheduled() == false",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void scheduleShopping() 
	{
		int tick = MainContext.instance().getSchedule().getCurrentTick();
		int tickPerHour = MainContext.instance().getSchedule().aHour;
		
		ScheduleParameters  params = ScheduleParameters.createRepeating(tick, tickPerHour);
		ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();
		
		Boolean notEnoughMoney = Boolean.FALSE;
		boolean tryS = tryShopping(notEnoughMoney, false);
		if (!tryS && !notEnoughMoney)
		{
		   scheduleShopping = scheduleSim.schedule(params, this, "tryShopping", notEnoughMoney, true);
		   home.setShoppingScheduled(true);
		}
		else if (tryS)
			home.setShoppingScheduled(true);
	}
	
	@Watch(watcheeClassName = "neighbours.Human",
			watcheeFieldNames = "boredom",
			triggerCondition = "$watchee.getBoredom() > 10"
			                + " && $watchee.equals($watcher)",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void scheduleMovie() 
	{
		if (!scheduledMovie)
		{
		int tick = MainContext.instance().getSchedule().getCurrentTick();
		int tickPerHour = MainContext.instance().getSchedule().aHour;
		
		ScheduleParameters  params = ScheduleParameters.createRepeating(tick, tickPerHour);
		ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();

		scheduleMovie = scheduleSim.schedule(params, this, "tryMovie");
		scheduledMovie = true;
		}
	}
	
	public void removeSchedule(ISchedulableAction action)
	{
		ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();
		scheduleSim.removeAction(action);
	}
	
	public boolean tryShopping(Boolean notEnoughMoney, Boolean scheduled)
	{
		if (!isCurrWorking && MainContext.instance().getShopZones() != null)
		{
			Service shop = null;
			for (BuildingZone<? extends Service> zone : MainContext.instance().getShopZones())
			{
			    shop = findService(zone, notEnoughMoney);
			    if (shop != null)
			    	break;
			}
			if(shop != null)
			{
				if (scheduled)
				{
				int tick = MainContext.instance().getSchedule().getCurrentTick();
				
				ScheduleParameters  params = ScheduleParameters.createOneTime(tick + 2);
				ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();
				scheduleSim.schedule(params, this, "removeSchedule", scheduleShopping);
				}
				
				goToService(shop);
				return true;
			}
		}
		return false;
	}
	
	public void tryMovie()
	{
		if (!isCurrWorking && MainContext.instance().getMovie_zones() != null)
		{
			Service movie = null;
			for (BuildingZone<? extends Service> zone : MainContext.instance().getMovie_zones())
			{
				Boolean notEnoughMoney = new Boolean(false);
			    movie = findService(zone, notEnoughMoney);
			    if (movie != null)
			    	break;
			}
			if(movie != null)
			{
				
				int tick = MainContext.instance().getSchedule().getCurrentTick();
				
				ScheduleParameters  params = ScheduleParameters.createOneTime(tick + 2);
				ISchedule scheduleSim = RunEnvironment.getInstance().getCurrentSchedule();
				scheduleSim.schedule(params, this, "removeSchedule", scheduleMovie);
				
				goToService(movie);
			}
		}
	}
	
	public Service findService(BuildingZone<? extends Service> zone, Boolean moneyNotEnough) {
		
		moneyNotEnough = Boolean.FALSE;
			for (Service s : zone.getBuildings())
			{
				int timePlusService = MainContext.instance().getSchedule().getCurrHour() + s.getTimePerService();
				
				if (s.isOpened() && !s.isFull() 
						&& timePlusService < s.getClosure())
				{
					if ( money >= s.getCost())
					   return s;
					else
						moneyNotEnough = Boolean.TRUE; 
				}
			}
		
		return null;
	}
	
	private void goToService(Service s)
	{
		if (s != null && home != null)
		{
		  MainContext.instance().getNetworkBuilding().addEdge(this, s);
		  moveBuilding2Building(home, s);
		  s.increasedUsed();
		}
	}
	
	public void serviceDone(Service s)
	{
		RepastEdge<Agent> edge = MainContext.instance().getNetworkBuilding().getEdge(this, s);
		if (edge != null)
		   MainContext.instance().getNetworkBuilding().removeEdge(edge);
		if (s instanceof Shop)
			this.home.setShoppingScheduled(false);
		else if (s instanceof MovieTheater)
			scheduledMovie = false;
		
		isAtService = false;
	}
	
	private void death() {
		for (RepastEdge<Agent> edge : MainContext.instance().getNetworkBuilding().getEdges(this))
		   MainContext.instance().getNetworkBuilding().removeEdge(edge);
		
		MainContext.instance().getContext().remove(this);
		this.home = null;
		this.office = null;
		
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
							 + "&& $watchee.getCurrMonth() == $watcher.getBirth()[1]",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void birthday() {
		setAge(getAge() + 1);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currDay",
			triggerCondition = "$watcher.isHungry() == true",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void starve() {
		health -= 10;
		if (health <= 0)
			animatedIcons.add(new DeathIconAgent(MainContext.instance().getGrid().getLocation(this)));
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



	public boolean isHungry() {
		return hungry;
	}

	public void setHungry(boolean hungry) {
		this.hungry = hungry;
		if (hungry == false && health < initHealth)
			health += 10;
	}

	public House getHome() {
		return home;
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
