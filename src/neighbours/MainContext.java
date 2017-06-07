package neighbours;

import java.util.ArrayList;

import repast.simphony.space.grid.Grid;
import repast.simphony.context.Context;

// Singleton class which holds context, grid, and zoneBuilding
public class MainContext {
	
	private static MainContext instance = null;
	
	private Context<Agent> context;
	private Grid<Agent> grid;
	private int width;
	private int height;
	private ArrayList<BuildingZone<House>> house_zones = null;
	private ArrayList<BuildingZone<Office>> office_zones = null;
	private ArrayList<BuildingZone<Trade>> trade_zones = null;
	private boolean debug = true;
	
	private MainContext()
	{
	}
	
	public void generate_building_zones()
	{
		for (BuildingZone<House> houses : house_zones)
		{
			try {
				houses.generates_buildings();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (BuildingZone<Office> office : office_zones)
		{
			try {
				office.generates_buildings();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void add_office_zone(BuildingZone<Office> of_zone)
	{
		if (debug)
			System.out.println("Added zone office: " + of_zone.toString());
		if (office_zones == null )
			office_zones = new ArrayList<BuildingZone<Office>>();
		office_zones.add(of_zone);
	}
	
	public void add_house_zone(BuildingZone<House> h_zone)
	{
		if (debug)
			System.out.println("Added zone house: " + h_zone.toString());
		if (house_zones == null )
			house_zones = new ArrayList<BuildingZone<House>>();
		house_zones.add(h_zone);
	}
	
	public void add_trade_zone(BuildingZone<Trade> t_zone)
	{
		if (debug)
			System.out.println("Added zone trade: " + t_zone.toString());
		if (trade_zones == null )
			trade_zones = new ArrayList<BuildingZone<Trade>>();
		trade_zones.add(t_zone);
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

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
}
