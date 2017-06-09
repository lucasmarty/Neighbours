package utils;

import java.util.ArrayList;

import neighbours.Agent;
import neighbours.House;
import neighbours.Office;
import neighbours.Road;
import neighbours.Shop;

public final class Walk extends TransportType {

	
	
	public Walk() {
		super(20);
	}

	@Override
	public int[][] generateWeightMap() {
		ArrayList<Class<? extends Agent>> agentUsed = new ArrayList<>();
		agentUsed.add(Road.class);
		agentUsed.add(Office.class);
		agentUsed.add(House.class);
		agentUsed.add(Shop.class);
		
		return Dijkstraa.buildGridWeight(agentUsed , stepCost, false);
	}

}
