package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Agent implements AgentInterface, Serializable {
	
	private AID id;
	protected ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();

	public Agent(AID id) {
		super();
		this.id = id;
	}

	public Agent() {
		// TODO Auto-generated constructor stub
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	public ArrayList<FootballMatch> getMatches() {
		return matches;
	}

	public void setMatches(ArrayList<FootballMatch> matches) {
		this.matches = matches;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		System.out.println("handleMessage Agent");
	}

	@Override
	public String toString() {
		return "Agent [id=" + id + "]";
	}
}
