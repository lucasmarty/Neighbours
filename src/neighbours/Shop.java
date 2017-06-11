package neighbours;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;
import repast.simphony.space.grid.Grid;

public class Shop extends Service {


	private int foodPerService;
	
	@Override
	public void compute() {
		
	}

	@Override
	protected void implementService(Human human) {

		   human.setMoney(human.getMoney() - cost);
		   if (human.getHome() != null)
			   human.getHome().setFood(human.getHome().getFood() + foodPerService);
		   System.out.println("Human bought food !");
	}

	
	public int getFoodPerService() {
		return foodPerService;
	}

	public void setFoodPerService(int foodPerService) {
		this.foodPerService = foodPerService;
	}
	
	
}
