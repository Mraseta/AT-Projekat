package agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.ejb.Stateful;
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
import model.AgentCenter;
import model.FootballMatch;
import model.Performative;

@Stateful
public class Collector extends Agent {
	
	@SuppressWarnings("resource")
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.COLLECT)) {
			System.out.println("Usao u collect");
			//ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();
			
			String home = message.getContent().split("-")[0];
			String away = message.getContent().split("-")[1];
			System.out.println(home + "    " + away);
			
			BufferedReader reader = null;
			
			try {
				InputStream in = getClass().getClassLoader().getResourceAsStream("utakmice.txt");
				reader = new BufferedReader(new InputStreamReader(in));
				String line;
				
				while((line = reader.readLine()) != null) {
					//System.out.println(line);
					String h = line.split("-")[0];
					String a = line.split("-")[1];
					int hg = Integer.parseInt(line.split("-")[2]);
					int ag = Integer.parseInt(line.split("-")[3]);
					
					if(home.equals(h) && away.equals(a)) {
						matches.add(new FootballMatch(h,a,hg,ag));
					}
					
					if(home.equals(a) && away.equals(h)) {
						matches.add(new FootballMatch(h,a,hg,ag));
					}
				}
				
				in.close();
				
				InetAddress ip = null;
				try {
					ip = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String h = Data.getMyAddress();
				
				System.out.println(h);
				
				ACLMessage req = new ACLMessage();
				req.setPerformative(Performative.COLLECT_REQUEST);
				req.setReplyTo(getId());
				req.setContent(message.getContent());
				
				
				for(Agent a : Data.getAgents()) {
					System.out.println(a);
					if(!a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("collector")) {
						System.out.println("usao da salje poruku agentu na drugom serveru");
						req.setReceivers(new AID[] {a.getId()});
						ResteasyClient rc = new ResteasyClientBuilder().build();			
						String path = "http://" + a.getId().getHost().getAddress() + ":8080/ATProjWAR/rest/messages";
						System.out.println(path);
						ResteasyWebTarget rwt = rc.target(path);
						Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(req, MediaType.APPLICATION_JSON));
					}
				}
				
				if(Data.getAgentCenters().size() == 1) {
					String cont = "";
					for(FootballMatch fm : matches) {
						cont = cont + fm + "\n";
					}
					
					ACLMessage mess = new ACLMessage();
					mess.setContent(cont);
					mess.setPerformative(Performative.PREDICT);
					mess.setReplyTo(new AID("temp",null,null));
					this.matches = new ArrayList<FootballMatch>();
					
					for(Agent a : Data.getAgents()) {
						if(a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("predictor")) {
							a.handleMessage(mess);
							break;
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(message.getPerformative().equals(Performative.COLLECT_REQUEST)) {
			System.out.println("Usao u collect request");
			//ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();
			
			String home = message.getContent().split("-")[0];
			String away = message.getContent().split("-")[1];
			
			BufferedReader reader = null;
			
			try {
				InputStream in = getClass().getClassLoader().getResourceAsStream("utakmice.txt");
				reader = new BufferedReader(new InputStreamReader(in));
				String line;
				String ret = "";
				
				while((line = reader.readLine()) != null) {
					if(home.equals(line.split("-")[0]) && away.equals(line.split("-")[1])) {
						ret = ret + line + "\n";
					}
					
					if(home.equals(line.split("-")[1]) && away.equals(line.split("-")[0])) {
						ret = ret + line + "\n";
					}
				}
				
				
				ACLMessage res = new ACLMessage();
				res.setContent(ret.substring(0, ret.length()-1));
				res.setPerformative(Performative.COLLECT_RESPONSE);
				res.setReceivers(new AID[] {message.getReplyTo()});
				
				ResteasyClient rc = new ResteasyClientBuilder().build();			
				String path = "http://" + message.getReplyTo().getHost().getAddress() + ":8080/ATProjWAR/rest/messages";
				System.out.println(path);
				ResteasyWebTarget rwt = rc.target(path);
				Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(res, MediaType.APPLICATION_JSON));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(message.getPerformative().equals(Performative.COLLECT_RESPONSE)) {
			System.out.println("Usao u collect response");
			//ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();
			
			String[] res = message.getContent().split("\n");
			
			for(String s : res) {
				String h = s.split("-")[0];
				String a = s.split("-")[1];
				int hg = Integer.parseInt(s.split("-")[2]);
				int ag = Integer.parseInt(s.split("-")[3]);
				
				matches.add(new FootballMatch(h,a,hg,ag));
			}
			
			String cont = "";
			for(FootballMatch fm : matches) {
				cont = cont + fm + "\n";
			}
			
			ACLMessage mess = new ACLMessage();
			mess.setContent(cont);
			mess.setReplyTo(new AID("temp",null,null));
			mess.setPerformative(Performative.PREDICT);
			this.matches = new ArrayList<FootballMatch>();
			
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String h = Data.getMyAddress();
			boolean found = false;
			
			for(Agent a : Data.getAgents()) {
				if(a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("predictor")) {
					a.handleMessage(mess);
					found = true;
					break;
				}
			}
			
			if(!found) {
				for(Agent a : Data.getAgents()) {
					if(a.getId().getType().getName().equals("predictor")) {
						mess.setReceivers(new AID[] {a.getId()});
						mess.setReplyTo(getId());
						ResteasyClient rc = new ResteasyClientBuilder().build();			
						String path = "http://" + a.getId().getHost().getAddress() + ":8080/ATProjWAR/rest/messages";
						System.out.println(path);
						ResteasyWebTarget rwt = rc.target(path);
						Response response7 = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(mess, MediaType.APPLICATION_JSON));
						break;
					}
				}
			}
		}
		
		
	}
}
