package neighbours;

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
		
		int width = 10;
		int height = 10;
		
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Agent> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Agent>(new WrapAroundBorders(),
				new SimpleGridAdder<Agent>(), true, width, height));	//true -> plusieurs agents sur une case
		for (int x = 0; x < 3; x++)
		{
			for (int y = 0; y < 3; y++) {
				Agent a = new House();
				context.add(a);
				grid.moveTo(a, x, y);
			}
		}
		return context; 	
	}

}
