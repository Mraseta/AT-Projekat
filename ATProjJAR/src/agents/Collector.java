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
import model.FootballMatch;
import model.Performative;

@Stateful
public class Collector extends Agent {
	
	private ArrayList<FootballMatch> matches = new ArrayList<FootballMatch>();
	
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
				
				String h = ip.toString().split("/")[1].split("\n")[0];
				
				System.out.println(h);
				
				ACLMessage req = new ACLMessage();
				req.setPerformative(Performative.COLLECT_REQUEST);
				req.setReplyTo(getId());
				req.setContent(message.getContent());
				
				
				for(Agent a : Data.getAgents()) {
					if(!a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("collector")) {
						req.setReceivers(new AID[] {a.getId()});
						ResteasyClient rc = new ResteasyClientBuilder().build();			
						String path = "http://" + a.getId().getHost().getAddress() + ":8080/ChatWAR/rest/messages";
						System.out.println(path);
						ResteasyWebTarget rwt = rc.target(path);
						Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(req, MediaType.APPLICATION_JSON));
					}
				}
				
				String cont = "";
				for(FootballMatch fm : matches) {
					cont = cont + fm + "\n";
				}
				
				ACLMessage mess = new ACLMessage();
				mess.setContent(cont);
				mess.setPerformative(Performative.PREDICT);
				this.matches = new ArrayList<FootballMatch>();
				
				for(Agent a : Data.getAgents()) {
					if(a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("predictor")) {
						a.handleMessage(mess);
						break;
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
				String path = "http://" + message.getReplyTo().getHost().getAddress() + ":8080/ChatWAR/rest/messages";
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
			mess.setPerformative(Performative.PREDICT);
			this.matches = new ArrayList<FootballMatch>();
			
			InetAddress ip = null;
			try {
				ip = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String h = ip.toString().split("/")[1].split("\n")[0];
			
			for(Agent a : Data.getAgents()) {
				if(a.getId().getHost().getAddress().equals(h) && a.getId().getType().getName().equals("predictor")) {
					a.handleMessage(mess);
					break;
				}
			}
		}
	}

	public ArrayList<FootballMatch> getMatches() {
		return matches;
	}

	public void setMatches(ArrayList<FootballMatch> matches) {
		this.matches = matches;
	}
}
