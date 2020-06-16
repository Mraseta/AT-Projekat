package beans;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.Data;
import model.ACLMessage;
import model.Performative;
import ws.WSEndPoint;

@Stateless
@Path("/messages")
@LocalBean
public class MessageBean {
	
	@EJB WSEndPoint ws;
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
	private Queue queue;

	@POST
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendMessage(ACLMessage aclmessage) {
		Data.getMessages().add(aclmessage);
		System.out.println(aclmessage);
		
		String h = Data.getMyAddress();
		
		if(!aclmessage.getReceivers()[0].getHost().getAddress().equals(h)) {
			ResteasyClient rc = new ResteasyClientBuilder().build();			
			String path = "http://" + aclmessage.getReceivers()[0].getHost().getAddress() + ":8080/ATProjWAR/rest/messages";
			System.out.println(path);
			ResteasyWebTarget rwt = rc.target(path);
			Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(aclmessage, MediaType.APPLICATION_JSON));
		} else {
			try {
				QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
				QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
				
				connection.start();
				ObjectMessage msg = session.createObjectMessage(aclmessage);
				long sent = System.currentTimeMillis();
				msg.setLongProperty("sent",	sent);
				
				MessageProducer producer = session.createProducer(queue);
				
				System.out.println("Slanje poruke: " + msg.getObject());
				
				producer.send(msg);
				
				producer.close();
				session.close();
				connection.close();
				// QueueSender sender = session.createSender(queue);
				// create and publish a message
				// TextMessage message = session.createTextMessage();
				// message.setText(aclmessage.getContent());
				// sender.send(message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		ws.echoTextMessage("refresh messages");
		return Response.status(200).build();
	}
	
	@GET
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Performative> getMessages() {
		ArrayList<Performative> ret = new ArrayList<Performative>();
		for (Performative p : Performative.values()) {
			ret.add(p);
		}
		return ret;
	}
}
