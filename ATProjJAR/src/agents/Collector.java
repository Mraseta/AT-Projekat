package agents;

import javax.ejb.Stateful;

import data.Data;
import model.ACLMessage;
import model.AID;
import model.Agent;

@Stateful
public class Collector extends Agent {
	
	@Override
	public void handleMessage(ACLMessage message) {
		
	}
}
