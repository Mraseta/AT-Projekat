package beans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.Data;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Singleton
@Startup
@Path("")
public class AgentCenterBean {
	
	private String master = "";
	private String hostip = "";
	
	@PostConstruct
	private void init() {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("master.txt");
			reader = new BufferedReader(new InputStreamReader(in));
			String fileContent = reader.readLine();
			System.out.println("FC " + fileContent);
			InetAddress ip = InetAddress.getLocalHost();
			this.hostip = ip.toString().split("/")[1].split("\n")[0];
			
			AgentCenter n = new AgentCenter(this.hostip, this.hostip);
			Data.getAgentCenters().add(n);
			
			in.close();
			
			System.out.println(hostip + " aaaa " + fileContent.split("=").length);
			
			if (fileContent.split("=").length == 1) {
				String a = fileContent + this.hostip;
				System.out.println(a);
				System.out.println("\n\n\n\n nema master");
				this.master = this.hostip;
				for(Agent ag : Data.getAgents()) {
					System.out.println(ag);
					if(ag.getId().getHost().getAddress().equals("temp")) {
						ag.getId().setHost(n);
					}
					
					System.out.println(ag);
				}
			} else {
				this.master = fileContent.split("=")[1];
				Data.getAgentCenters().add(new AgentCenter(this.master, this.master));
				ResteasyClient rc = new ResteasyClientBuilder().build();			
				String path = "http://" + this.master + ":8080/ChatWAR/rest/node";
				System.out.println(path);
				ResteasyWebTarget rwt = rc.target(path);
				Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(n, MediaType.APPLICATION_JSON));
				
				ResteasyClient rc2 = new ResteasyClientBuilder().build();			
				String path2 = "http://" + this.master + ":8080/ChatWAR/rest/agents/classes";
				System.out.println(path2);
				ResteasyWebTarget rwt2 = rc2.target(path2);
				Response response2 = rwt2.request(MediaType.APPLICATION_JSON).post(Entity.entity(Data.getAgentClasses(), MediaType.APPLICATION_JSON));
				
				ResteasyClient rc3 = new ResteasyClientBuilder().build();			
				String path3 = "http://" + this.master + ":8080/ChatWAR/rest/nodes";
				System.out.println(path3);
				ResteasyWebTarget rwt3 = rc3.target(path3);
				Response response3 = rwt3.request(MediaType.APPLICATION_JSON).get();
				ArrayList<AgentCenter> ret3 = (ArrayList<AgentCenter>) response3.readEntity(new GenericType<List<AgentCenter>>() {});
				for(AgentCenter ac : ret3) {
					boolean found = false;
					for(AgentCenter a : Data.getAgentCenters()) {
						if(a.getAddress().equals(ac.getAddress())) {
							found = true;
							break;
						}
					}
					
					if(!ac.getAddress().equals(this.hostip) && !found) {
						Data.getAgentCenters().add(ac);
					}
				}
				
				ResteasyClient rc4 = new ResteasyClientBuilder().build();			
				String path4 = "http://" + this.master + ":8080/ChatWAR/rest/agents/classes";
				System.out.println(path4);
				ResteasyWebTarget rwt4 = rc4.target(path4);
				Response response4 = rwt4.request(MediaType.APPLICATION_JSON).get();
				ArrayList<AgentType> ret4 = (ArrayList<AgentType>) response4.readEntity(new GenericType<List<AgentType>>() {});
				for(AgentType at : ret4) {
					boolean found = false;
					for(AgentType a : Data.getAgentClasses()) {
						if(a.getName().equals(at.getName())) {
							found = true;
							break;
						}
					}
					
					if(!found) {
						Data.getAgentClasses().add(at);
					}
				}
				
				int cnt = 0;
				Response response5 = null;
				while(cnt<2) {
					ResteasyClient rc5 = new ResteasyClientBuilder().build();			
					String path5 = "http://" + this.master + ":8080/ChatWAR/rest/agents/running";
					System.out.println(path5);
					ResteasyWebTarget rwt5 = rc5.target(path5);
					response5 = rwt5.request(MediaType.APPLICATION_JSON).get();
					if(response5.getStatus() != 200) {
						cnt++;
						continue;
					} else {
						break;
					}
				}
				
				if(cnt<2) {
					ArrayList<Agent> ret5 = (ArrayList<Agent>) response5.readEntity(new GenericType<List<Agent>>() {});
					for(Agent a : ret5) {
						Data.getAgents().add(a);
					}
				} else {
					ResteasyClient rc6 = new ResteasyClientBuilder().build();			
					String path6 = "http://" + this.master + ":8080/ChatWAR/rest/node/" + n.getAlias();
					System.out.println(path6);
					ResteasyWebTarget rwt6 = rc6.target(path6);
					Response response6 = rwt6.request(MediaType.APPLICATION_JSON).delete();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	private void destroy() {
		String alias = "";
		for(AgentCenter ac : Data.getAgentCenters()) {
			if(ac.getAddress().equals(this.hostip)) {
				alias = ac.getAlias();
			}
		}
		
		for(AgentCenter ac : Data.getAgentCenters()) {
			if(!ac.getAddress().equals(this.hostip)) {
				ResteasyClient rc = new ResteasyClientBuilder().build();			
				String path = "http://" + ac.getAddress() + ":8080/ChatWAR/rest/node/" + alias;
				ResteasyWebTarget rwt = rc.target(path);
				Response response = rwt.request(MediaType.APPLICATION_JSON).delete();
				System.out.println(response);
			}
		}
		
		for(Agent a : Data.getAgents()) {
			if(a.getId().getHost().getAddress().equals(this.hostip)) {
				for(AgentCenter ac : Data.getAgentCenters()) {
					if(!ac.getAddress().equals(this.hostip)) {
						ResteasyClient rc = new ResteasyClientBuilder().build();			
						String path = "http://" + ac.getAddress() + ":8080/ChatWAR/rest/agents/running/" + a.getId().getName();
						ResteasyWebTarget rwt = rc.target(path);
						Response response = rwt.request(MediaType.APPLICATION_JSON).delete();
						System.out.println(response);
					}
				}
			}
		}
	}
	
	@GET
	@Path("/node")
	public Response getNode() {
		System.out.println("pingovan");
		return Response.status(200).build();
	}

	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerNode(AgentCenter ac) {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String h = ip.toString().split("/")[1].split("\n")[0];
		
		if(this.master.equals(h)) {
			/*ResteasyClient rc = new ResteasyClientBuilder().build();
			String path = "http://" + ac.getAddress() + ":8080/ChatWAR/rest/agents/classes";
			System.out.println(path);
			ResteasyWebTarget rwt = rc.target(path);
			Response response = rwt.request(MediaType.APPLICATION_JSON).get();
			ArrayList<AgentType> ret = (ArrayList<AgentType>) response.readEntity(new GenericType<List<AgentType>>() {});
			for(AgentType at : ret) {
				boolean found = false;
				for(AgentType a : Data.getAgentClasses()) {
					if(at.getName().equals(a.getName())) {
						found = true;
						break;
					}
				}
				
				if(!found) {
					Data.getAgentClasses().add(at);
				}
			}*/
			
			for(AgentCenter ac2 : Data.getAgentCenters()) { 
				if(!ac2.getAddress().equals(ac.getAddress())) {
					ResteasyClient rc = new ResteasyClientBuilder().build();			
					String path = "http://" + ac2.getAddress() + ":8080/ChatWAR/rest/node";
					System.out.println(path);
					ResteasyWebTarget rwt = rc.target(path);
					Response response = rwt.request(MediaType.APPLICATION_JSON).post(Entity.entity(ac, MediaType.APPLICATION_JSON));
				}
			}
			
			Data.getAgentCenters().add(ac);
		} else {
			Data.getAgentCenters().add(ac);
		}
		
		
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<AgentCenter> getNodes() {
		return Data.getAgentCenters();
	}
	
	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postNodes(ArrayList<AgentCenter> list) {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String h = ip.toString().split("/")[1].split("\n")[0];
		
		for(AgentCenter ac : list) {
			boolean found = false;
			for(AgentCenter a : Data.getAgentCenters()) {
				if(a.getAddress().equals(ac.getAddress())) {
					found = true;
					break;
				}
			}
			
			if(!ac.getAddress().equals(h) && !found) {
				Data.getAgentCenters().add(ac);
			}
		}
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/node/{alias}")
	public Response deleteNode(@PathParam("alias") String alias) {
		for(int i=0;i<Data.getAgentCenters().size();i++) {
			if(Data.getAgentCenters().get(i).getAlias().equals(alias)) {
				Data.getAgentCenters().remove(i);
			}
		}
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String h = ip.toString().split("/")[1].split("\n")[0];
		
		if(this.master.equals(h)) {
			for(AgentCenter ac : Data.getAgentCenters()) {
				if(!ac.getAddress().equals(h)) {
					ResteasyClient rc = new ResteasyClientBuilder().build();			
					String path = "http://" + ac.getAddress() + ":8080/ChatWAR/rest/node/"+alias;
					System.out.println(path);
					ResteasyWebTarget rwt = rc.target(path);
					Response response = rwt.request(MediaType.APPLICATION_JSON).delete();
				}
			}
		}
		
		return Response.status(200).build();
	}
	
	@Schedules({
		@Schedule(hour="*", minute="*", second="*/30", info="heartbeat")
	})
	public void heartbeat() {
		System.out.println("entered heartbeat " + Data.getAgentCenters().size());
		
		for(AgentCenter h : Data.getAgentCenters()) {
			if(!h.getAddress().equals(this.hostip)) {
				ResteasyClient rc = new ResteasyClientBuilder().build();			
				String path = "http://" + h.getAddress() + ":8080/ChatWAR/rest/node";
				System.out.println(path);
				ResteasyWebTarget rwt = rc.target(path);
				Response response = rwt.request(MediaType.APPLICATION_JSON).get();
				System.out.println(response);
				
				if(response.getStatus() != 200) {
					Response response2 = rwt.request(MediaType.APPLICATION_JSON).get();
					if(response2.getStatus() != 200) {
						Data.getAgentCenters().remove(h);
						for(AgentCenter h2 : Data.getAgentCenters()) {
							if(!h2.getAddress().equals(h.getAddress()) && !h2.getAddress().equals(this.hostip)) {
								ResteasyClient rc2 = new ResteasyClientBuilder().build();			
								String path2 = "http://" + h2.getAddress() + ":8080/ChatWAR/rest/node/" + h.getAlias();
								ResteasyWebTarget rwt2 = rc2.target(path2);
								Response response3 = rwt2.request(MediaType.APPLICATION_JSON).delete();
								System.out.println(response3);
							}
						}
					}
				}
			}
		}
		
		for(AgentCenter h : Data.getAgentCenters()) {
			System.out.println(h);
		}
	}
}
