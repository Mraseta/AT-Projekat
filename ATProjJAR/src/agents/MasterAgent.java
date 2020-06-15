package agents;

import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;

@Stateful
public class MasterAgent extends Agent {

	@Override
	public void handleMessage(ACLMessage message) {
		
	}
}
