package utils;

import java.util.ArrayList;

import neighbours.Agent;
import neighbours.Road;

public final class Car extends TransportType {

	public Car() {
		super(2);
	}

	@Override
	public int[][] generateWeightMap() {
		
		ArrayList<Class<? extends Agent>> agentUsed = new ArrayList<>();
		agentUsed.add(Road.class);
		
		return Dijkstraa.buildGridWeight(agentUsed , stepCost, true);
	}

}
