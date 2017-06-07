package neighbours;

import java.io.IOException;

import neighbours.Agent;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class ContextCreator implements ContextBuilder<Agent>{

	@Override
	public Context<Agent> build(Context<Agent> context) {
		context.setId("Neighbours");
		
		MainContext.instance().setContext(context);
		
		try {
			FileParser.readFile("test.in");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int width = MainContext.instance().getWidth();
		int height = MainContext.instance().getHeight();
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Agent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Agent>(new WrapAroundBorders(),
				new SimpleGridAdder<Agent>(), true, width, height));	//true -> plusieurs agents sur une case
		
		MainContext.instance().setGrid(grid);
		MainContext.instance().generate_building_zones();
		return context; 	
	}

}
