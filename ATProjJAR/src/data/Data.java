package data;

import java.util.ArrayList;

import javax.ejb.Singleton;

import agents.Collector;
import agents.MasterAgent;
import agents.Predictor;
import model.ACLMessage;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Singleton
public class Data {
	
	private static ArrayList<Agent> agents = new ArrayList<>();
	private static ArrayList<AgentCenter> agentCenters = new ArrayList<>();
	private static ArrayList<AgentType> agentClasses = new ArrayList<>();
	private static ArrayList<ACLMessage> messages = new ArrayList<>();
	private static String myAddress = "";
	
	static {		
		AgentType at1 = new AgentType("collector", "collector");
		AgentType at2 = new AgentType("predictor", "predictor");
		AgentType at3 = new AgentType("master", "master");
		
		agentClasses.add(at1);
		agentClasses.add(at2);
		agentClasses.add(at3);
		
		Collector agent1 = new Collector();
		AID id1 = new AID("agent1", new AgentCenter("temp", "temp"), at1);
		agent1.setId(id1);
		
		Predictor agent2 = new Predictor();
		AID id2 = new AID("agent2", new AgentCenter("temp", "temp"), at2);
		agent2.setId(id2);
		
		MasterAgent agent3 = new MasterAgent();
		AID id3 = new AID("agent3", new AgentCenter("temp", "temp"), at3);
		agent3.setId(id3);
		
		agents.add(agent1);
		agents.add(agent2);
		agents.add(agent3);
		
		myAddress = "192.168.0.12";
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

	public static ArrayList<AgentType> getAgentClasses() {
		return agentClasses;
	}

	public static void setAgentClasses(ArrayList<AgentType> agentClasses) {
		Data.agentClasses = agentClasses;
	}

	public static String getMyAddress() {
		return myAddress;
	}

	public static void setMyAddress(String myAddress) {
		Data.myAddress = myAddress;
	}
}
