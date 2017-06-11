package neighbours;

import java.util.ArrayList;
import java.util.HashSet;

import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import utils.Dijkstraa;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;

// Singleton class which holds context, grid, and zoneBuilding
public class MainContext {
	
	private static MainContext instance = null;
	
	private Context<Agent> context;
	private Grid<Agent> grid;
	private int width;
	private int height;
	private ArrayList<Human> humans = null;
	private ArrayList<BuildingZone<House>> house_zones = null;
	private ArrayList<BuildingZone<Office>> office_zones = null;
	private ArrayList<BuildingZone<Shop>> shop_zones = null;
	private ArrayList<BuildingZone<MovieTheater>> movie_zones = null;
	public ArrayList<BuildingZone<MovieTheater>> getMovie_zones() {
		return movie_zones;
	}

	private ArrayList<Integer> nb_human_per_zones = null;
	private boolean debug = true;
	private Schedule schedule;
	
	
	private MainContext()
	{
	}
	
	public Network<Agent> getNetworkBuilding()
	{
		return (Network<Agent>)context.getProjection("building network");
	}
	
	public void generate_building_zones()
	{
		if (house_zones != null)
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
		}
		if (office_zones != null)
		{
		for (BuildingZone<Office> office : office_zones)
		{
			try {
				office.generates_buildings();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Office o : office.getBuildings())
			{
				o.setClosure(RandomHelper.nextIntFromTo(15, 20));
				o.setOpening(RandomHelper.nextIntFromTo(o.getClosure() - 10, o.getClosure() - 7));
			}
		}
		}
		if (shop_zones != null)
		{
		for (BuildingZone<Shop> shop : shop_zones)
		{
			try {
				shop.generates_buildings();
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (Shop s : shop.getBuildings())
			{
				s.setFoodPerService(RandomHelper.nextIntFromTo(5, 15));
				s.setCost(RandomHelper.nextIntFromTo(150, 500));
				s.setTimePerService(1);
				s.setClosure(RandomHelper.nextIntFromTo(17, 23));
				s.setOpening(RandomHelper.nextIntFromTo(s.getClosure() - 10, s.getClosure() - 7));
			}
		}
		}
		if (movie_zones != null)
		{
			for (BuildingZone<MovieTheater> movie : movie_zones)
			{
				try {
					movie.generates_buildings();
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (MovieTheater m : movie.getBuildings())
				{
					m.setEntertain(RandomHelper.nextIntFromTo(3, 7));
					m.setCost(RandomHelper.nextIntFromTo(150, 500));
					m.setTimePerService(1);
					m.setClosure(RandomHelper.nextIntFromTo(17, 23));
					m.setOpening(RandomHelper.nextIntFromTo(m.getClosure() - 10, m.getClosure() - 7));
				}
			}
		}
		
		generateRoad();
		spawnHumanPerZone();
	}
	
	private void spawnHumanPerZone()
	{
		int[] birth = {12, 10, 2001};

		for (int idx = 0; idx < house_zones.size(); ++idx)
		{
		   int humansPool = nb_human_per_zones.get(idx);
		   for (House house : house_zones.get(idx).getBuildings())
		   {
			   while (humansPool > 0 && !house.isFull())
			   {
				   Human human = new Human(25, birth, 200, 200);
				   context.add(human);
					
				   GridPoint hPos = grid.getLocation(house);
				   grid.moveTo(human, hPos.getX(), hPos.getY());
				   human.setHome(house);
				   chooseOfficeFor(human);
				   --humansPool;
			   }
		   }
		}
	}
	
	private void chooseOfficeFor(Human human)
	{
		for (int idx = 0; idx < office_zones.size(); ++idx)
		{
			for (Office of : office_zones.get(idx).getBuildings())
			{
				if (!of.isFull())
				{
					human.setOffice(of);
					return;
    			}
			}
		}
	}
	
	private void generateRoad()
	{
		// Now generates roads
		ArrayList<Class<? extends Agent>> agentUsed = new ArrayList<>();
		agentUsed.add(Road.class);
				
		int[][] gridWeightRoad = Dijkstraa.buildGridWeight(agentUsed, 1, false);
		Dijkstraa djk = new Dijkstraa(gridWeightRoad);
		
		ArrayList<BuildingZone<? extends Building>> zones = new ArrayList<>();
		zones.addAll(office_zones);
		zones.addAll(shop_zones);
		zones.addAll(house_zones);
		
		for (int idx = 0; idx < zones.size(); ++idx)
		{
			GridPoint start = zones.get(idx).getRoadsLocation().get(0);
		
			for (int cur = idx + 1; cur < zones.size(); ++cur)
			{
				//From first house zone to shop zone:
				HashSet<GridPoint> destSet = new HashSet<>();
				destSet.addAll(zones.get(cur).getRoadsLocation());

				ArrayList<GridPoint> path = djk.shortestPathTo(destSet, start);
				
				for (GridPoint pt : path)
				{
					Agent r = new Road();
					context.add(r);
					grid.moveTo(r, pt.getX(), pt.getY());
					
				}
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
	
	public void add_house_zone(BuildingZone<House> h_zone, int nbHuman)
	{
		if (debug)
			System.out.println("Added zone house: " + h_zone.toString() + ", " + nbHuman);
		if (house_zones == null )
			house_zones = new ArrayList<>();
		if (nb_human_per_zones == null)
			nb_human_per_zones = new ArrayList<>();
		
		house_zones.add(h_zone);
		nb_human_per_zones.add(nbHuman);
	}
	
	public void add_shop_zone(BuildingZone<Shop> s_zone)
	{
		if (debug)
			System.out.println("Added zone shop: " + s_zone.toString());
		if (shop_zones == null )
			shop_zones = new ArrayList<BuildingZone<Shop>>();
		shop_zones.add(s_zone);
	}
	
	public void add_movie_zone(BuildingZone<MovieTheater> m_zone)
	{
		if (debug)
			System.out.println("Added zone movie: " + m_zone.toString());
		if (movie_zones == null )
			movie_zones = new ArrayList<BuildingZone<MovieTheater>>();
		movie_zones.add(m_zone);
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

	public ArrayList<BuildingZone<House>> getHouseZones() {
		return house_zones;
	}
	
	public ArrayList<BuildingZone<Office>> getOfficeZones() {
		return office_zones;
	}
	
	public ArrayList<BuildingZone<Shop>> getShopZones() {
		return shop_zones;
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

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}	
}
