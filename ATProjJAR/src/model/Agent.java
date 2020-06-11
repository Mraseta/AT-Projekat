package model;

public class Agent implements AgentInterface {
	
	private AID id;

	public Agent(AID id) {
		super();
		this.id = id;
	}

	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		System.out.println("handleMessage Agent");
	}

}