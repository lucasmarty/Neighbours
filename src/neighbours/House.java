package neighbours;

import java.util.ArrayList;


public class House extends Building {

	private int food;
	private ArrayList<Human> habitants;

	public House()
	{
		habitants = new ArrayList<Human>();
	}
	
	@Override
	public void compute() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<Human> getHabitants() {
		return habitants;
	}
	
	
	public boolean addHabitant(Human human) {
		if (habitants.size() >= this.capacity)
			return false;
		habitants.add(human);
		return true;
	}
	
	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}
}
