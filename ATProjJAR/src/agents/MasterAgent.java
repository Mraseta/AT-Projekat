package agents;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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
			try {
				Context context = new InitialContext();
				WSEndPoint ws = (WSEndPoint) context.lookup(WSEndPoint.LOOKUP);
				ws.echoTextMessage(message.getContent());
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
}
