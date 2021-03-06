package neighbours;

import repast.simphony.engine.environment.RunEnvironment;

public class Schedule extends Agent{
	
	public static final int aHour = 60; // change this value to accelerate or not the scheduling
	public static final int aDay = aHour * 24;
	public static final int aMonth = aDay * 30;
	public static final int aYear = aMonth * 12;
	
	private int currYear;  // start at +2000 A.D
	private int currMonth; // 1->January to 12->December / Start to 1
	private int currDay;   // 1->Monday to 7->Sunday / Start to 1
	private int currHour; // 1 to 24 Start to 1
	
	public Schedule(int year, int month, int day, int hour) {
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
	
	public void computeSchedule(double ticks) {
		int newHour =(ticks%aHour == 0) ? currHour+1 : (currHour > 24) ? 1 : currHour;
		if (newHour != currHour)
			this.currHour = newHour;
		
		int newDay = (ticks%aDay == 0) ?  currDay+1 : (currDay > 7) ? 1 : currDay;
		if (newDay != currDay)
			this.currDay = newDay;
		
		int newMonth = (ticks%aMonth == 0) ? currMonth+1 : (currMonth > 12 ) ? 1 : currMonth;
		if (newMonth != currMonth)
			this.currMonth = newMonth;
		
		if (ticks%aYear == 0)
			this.currYear++;
	}
	
	public int getCurrentTick()
	{
		return (int)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
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
