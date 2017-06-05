package neighbours;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;

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
	
	public void generates_buildings() throws InstantiationException, IllegalAccessException{
		

		int side_len = (int) Math.floor(Math.sqrt(nb_buildings));
		for (int x = 0; x < side_len; ++x)
		{
			for (int y = 0; y < side_len; ++y)
			{
			Agent b = buildingClass.newInstance();
			
			MainContext.instance().getContext().add(b);
			MainContext.instance().getGrid().moveTo(b, x + origin_x, y + origin_y);
			}
		}
		
	}
}
