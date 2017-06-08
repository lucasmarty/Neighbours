package neighbours;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class BuildingZone<T extends Building> {
	
	private ArrayList<T> buildings;
	private int nb_buildings = 0;
	private int origin_x = 0;
	private int origin_y = 0;
    private Class<T> buildingClass;
	
	
	public BuildingZone(int or_x, int or_y, int nbBuilding, Class<T> b_class)
	{
		origin_x = or_x;
		origin_y = or_y;
		nb_buildings = nbBuilding;
		buildingClass = b_class;
	}
	
	public ArrayList<GridPoint> getRoadsLocation()
	{
		int side_len = (int) Math.floor(Math.sqrt(nb_buildings));
	
		ArrayList<GridPoint> res = new ArrayList<>();
		
		for (int x = -1; x <= side_len; ++x)
			for (int y = -1; y <= side_len; ++y)
			{
				if (y >= 0 && y < side_len && x >= 0 && x < side_len)
					continue;
				int x_coord = x + origin_x;
				int y_coord = y + origin_y;
				
				if (x_coord < 0 || x_coord >= MainContext.instance().getWidth()
						|| y_coord < 0 || y_coord >= MainContext.instance().getHeight())
					continue;
				
				res.add(new GridPoint(x_coord, y_coord));
			}
		return res;
	}
	
	public void generates_buildings() throws InstantiationException, IllegalAccessException{
		

		int side_len = (int) Math.floor(Math.sqrt(nb_buildings));
		System.out.println("Side building for " + buildingClass.toString() + ", " + ((Integer)side_len).toString());
		for (int x = 0; x < side_len; ++x)
		{
			for (int y = 0; y < side_len; ++y)
			{
			Agent b = buildingClass.newInstance();
			
			MainContext.instance().getContext().add(b);
			MainContext.instance().getGrid().moveTo(b, x + origin_x, y + origin_y);
			}
		}
		// Generate Road on the border
		Integer nbRoad = 0;
		for (int x = -1; x <= side_len; ++x)
			for (int y = -1; y <= side_len; ++y)
			{
				if (y >= 0 && y < side_len && x >= 0 && x < side_len)
					continue;
				
				int x_coord = x + origin_x;
				int y_coord = y + origin_y;
				
				if (x_coord < 0 || x_coord >= MainContext.instance().getWidth()
						|| y_coord < 0 || y_coord >= MainContext.instance().getHeight())
					continue;
				
				boolean isRoad = false;
				for (Object obj : MainContext.instance().getGrid().getObjectsAt(x_coord, y_coord))
				{
					if (Road.class.isInstance(obj))
					{
						isRoad = true;
						break;
					}
				}
				if (isRoad)
					continue;
				
				++nbRoad;
				Agent r = (Agent)new Road();
				MainContext.instance().getContext().add(r);
				MainContext.instance().getGrid().moveTo(r, x_coord, y_coord);
			}
	}
}
