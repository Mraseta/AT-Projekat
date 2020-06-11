package data;

import java.util.ArrayList;

import javax.ejb.Singleton;

import model.Agent;

@Singleton
public class Data {
	
	private static ArrayList<Agent> agents = new ArrayList<>();

	public static ArrayList<Agent> getAgents() {
		return agents;
	}

	public static void setAgents(ArrayList<Agent> agents) {
		Data.agents = agents;
	}
	
	
}
