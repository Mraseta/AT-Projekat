package data;

import java.util.ArrayList;

import javax.ejb.Singleton;

import model.ACLMessage;
import model.Agent;
import model.AgentCenter;

@Singleton
public class Data {
	
	private static ArrayList<Agent> agents = new ArrayList<>();
	private static ArrayList<AgentCenter> agentCenters = new ArrayList<>();
	private static ArrayList<ACLMessage> messages = new ArrayList<>();
	
	static {
		agents.add(new Agent());
		agents.add(new Agent());
		agents.add(new Agent());
	}

	public static ArrayList<Agent> getAgents() {
		return agents;
	}

	public static void setAgents(ArrayList<Agent> agents) {
		Data.agents = agents;
	}

	public static ArrayList<AgentCenter> getAgentCenters() {
		return agentCenters;
	}

	public static void setAgentCenters(ArrayList<AgentCenter> agentCenters) {
		Data.agentCenters = agentCenters;
	}

	public static ArrayList<ACLMessage> getMessages() {
		return messages;
	}

	public static void setMessages(ArrayList<ACLMessage> messages) {
		Data.messages = messages;
	}
	
	
}
