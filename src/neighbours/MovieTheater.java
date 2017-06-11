package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class MovieTheater extends Service {

	
	private int entertain;
	
	public int getEntertain() {
		return entertain;
	}

	public void setEntertain(int entertain) {
		this.entertain = entertain;
	}

	@Override
	public void compute() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void implementService(Human human) 
	{

			human.setMoney(human.getMoney() - cost);
			int cur_bordedom = human.getBoredom();
			human.setBoredom(Math.max(cur_bordedom - entertain, 0));
	}

	
	

}
