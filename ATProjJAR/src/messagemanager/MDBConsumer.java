package messagemanager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.Data;
import model.ACLMessage;
import model.AID;
import model.Agent;
import ws.WSEndPoint;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue")
})

public class MDBConsumer implements MessageListener {
	
	@EJB WSEndPoint ws;

	@Override
	public void onMessage(Message msg) {
		ObjectMessage omsg = (ObjectMessage) msg;
		System.out.println("stigao u consumer");
		
		try {
			ACLMessage acl = (ACLMessage) omsg.getObject();
			
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String h = ip.toString().split("/")[1].split("\n")[0];
			
			ArrayList<Agent> rec = new ArrayList<Agent>();
			
			for(AID id : acl.getReceivers()) {
				for(Agent agnt : Data.getAgents()) {
					if(agnt.getId().getName().equals(id.getName())) {
						rec.add(agnt);
					}
				}
			}
			
			for(int i = 0;i<rec.size();i++) {
				Agent agnt = rec.get(i);
				if(agnt.getId().getHost().getAddress().equals(h)) {
					ACLMessage aclm = new ACLMessage(acl, i);
					String s = aclm.toString();
					ws.echoTextMessage(s);
					agnt.handleMessage(acl);
				} else {
					ACLMessage aclm = new ACLMessage(acl, i);
					ResteasyClient rc = new ResteasyClientBuilder().build();			
					String path = "http://" + agnt.getId().getHost().getAddress() + ":8080/ChatWAR/rest/messages";
					System.out.println(path);
					ResteasyWebTarget rwt = rc.target(path);
					Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(aclm, MediaType.APPLICATION_JSON));
				}
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
