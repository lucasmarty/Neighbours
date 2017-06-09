package neighbours;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public abstract class Building extends Agent{
	
	protected int capacity;
	protected BuildingZone<? extends Building> zone;
	

	public int getCapacity() {
		return capacity;
	}

	public GridPoint getStartingPos()
	{
		GridPoint pos = MainContext.instance().getGrid().getLocation(this);
		return zone.getStartingPointFrom(pos);
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public BuildingZone<? extends Building> getZone() {
		return zone;
	}

	public void setZone(BuildingZone<? extends Building> zone) {
		this.zone = zone;
	}
	
	
	
}
