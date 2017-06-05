package neighbours;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import repast.simphony.context.Context;

// Singleton class which holds context, grid, and zoneBuilding
public class MainContext {
	
	private static MainContext instance = null;
	
	private Context<Agent> context;
	private Grid<Agent> grid;
	private ArrayList<BuildingZone<House>> house_zones = null;
	private ArrayList<BuildingZone<Office>> office_zones = null;
	
	private MainContext()
	{
	}
	
	public void add_office_zone(BuildingZone<Office> of_zone)
	{
		if (office_zones == null )
			office_zones = new ArrayList<BuildingZone<Office>>();
		office_zones.add(of_zone);
	}
	
	public void add_house_zone(BuildingZone<House> h_zone)
	{
		if (house_zones == null )
			house_zones = new ArrayList<BuildingZone<House>>();
		house_zones.add(h_zone);
	}
	
	public static MainContext instance()
	{
		if (instance == null)
			instance = new MainContext();
		return instance;
	}

	public Context<Agent> getContext() {
		return context;
	}

	public void setContext(Context<Agent> context) {
		this.context = context;
	}

	public Grid<Agent> getGrid() {
		return grid;
	}

	public void setGrid(Grid<Agent> grid) {
		this.grid = grid;
	}
}
