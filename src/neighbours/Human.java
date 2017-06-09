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
		moving = false;
		traj_queue = new LinkedList<>();
		schedule = MainContext.instance().getSchedule();
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
				moving = false;
				currentTraj = null;
			}
			else
			{
				GridPoint pt = currentTraj.step();
				MainContext.instance().getGrid().moveTo(this, pt.getX(), pt.getY());
			}
		}
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void implement() {
		GridPoint pos = MainContext.instance().getGrid().getLocation(this);
	}
	
	@Watch(watcheeClassName = "neighbours.Schedule",
			watcheeFieldNames = "currDay, currHour",
			triggerCondition = "$watchee.getCurrDay() == 1 "
							 + "&& $watchee.getCurrHour() == 1 ",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void getPaid() {
		setMoney(getMoney() + office.getSalary());
	}

	private void death() {
		MainContext.instance().getContext().remove(this);
	}
	
	@Watch(watcheeClassName = "neighbours.Office",
			watcheeFieldNames = "opened",
			triggerCondition = "$watchee.isOpened() == false"
					+ " && $watchee.getId() == $watcher.getOffice().getId()",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE
			)
	public void closeOffice()
	{
		
		System.out.println("schedule path to home");
		/*GridPoint from = office.getStartingPos();
		GridPoint to = home.getStartingPos();
		try {
			planNextPath(from, to, Car.class);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
		Trajectory traj = new Trajectory(path, tr.getStep());
		
		traj_queue.add(traj);
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
	
}
