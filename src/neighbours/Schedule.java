package neighbours;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.space.grid.Grid;


public class Schedule extends Agent{
	
	public static final int aHour = 60;
	public static final int aDay = aHour * 24;
	public static final int aMonth = aDay * 30;
	public static final int aYear = aMonth * 12;
	
	private int currYear;  // start at +2000 A.D
	private int currMonth; // 1->January to 12->December / Start to 1
	private int currDay;   // 1->Monday to 7->Sunday / Start to 1
	private int currHour; // 0 to 24 Start to 0
	
	public Schedule(Grid<Agent> grid, int year, int month, int day, int hour) {
		super(grid);
		this.currYear = year;
		this.currMonth = month;
		this.currDay = day;
		this.currHour = hour;
	}
	
	@Override
	public void compute() {
		double ticks = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		computeSchedule(ticks);
	}
	
	@Override
	public void implement() {
		
	}
	
	public void computeSchedule(double ticks) {
		this.currHour = (ticks%aHour < 24) ? (int)ticks % aHour : 0;
		this.currDay = (ticks%aDay < 7) ? (int)ticks % aDay : 1;
		this.currMonth = (ticks%aMonth < 12) ? (int)ticks % aMonth : 1;
		if (currYear%aYear == 0)
			this.currYear += 1;
	}
	
	public int getCurrDay() {
		return this.currDay;
	}
	
	public int getCurrMonth() {
		return this.currMonth;
	}
	
	public int getCurrHour() {
		return this.currHour;
	}
	
	public int getCurrYear() {
		return this.currYear;
	}
	
}
