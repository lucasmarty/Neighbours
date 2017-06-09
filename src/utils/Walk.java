package utils;

import java.util.ArrayList;

import neighbours.Agent;
import neighbours.Road;

public final class Walk extends TransportType {

	
	
	public Walk() {
		super(4);
	}

	@Override
	public int[][] generateWeightMap() {
		ArrayList<Class<? extends Agent>> agentUsed = new ArrayList<>();
		agentUsed.add(Road.class);
		
		return Dijkstraa.buildGridWeight(agentUsed , stepCost, false);
	}

}
