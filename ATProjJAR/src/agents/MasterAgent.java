package agents;

import javax.ejb.EJB;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.Performative;
import ws.WSEndPoint;

@Stateful
public class MasterAgent extends Agent {
	
	@EJB
	WSEndPoint ws;

	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.INFORM)) {
			System.out.println(message.getContent());
			//ws.echoTextMessage(message.getContent());
		}
	}
}
