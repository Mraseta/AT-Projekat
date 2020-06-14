package beans;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import data.Data;
import model.AID;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Stateless
@Path("/agents")
@LocalBean
public class AgentBean {

	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<AgentType> getAgentTypes() {
		return Data.getAgentClasses();
	}
	
	@POST
	@Path("/classes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postAgentTypes(ArrayList<AgentType> list) {
		for(AgentType at : list) {
			boolean found = false;
			for(AgentType a : Data.getAgentClasses()) {
				if(a.getName().equals(at.getName())) {
					found = true;
				}
			}
			
			if(!found) {
				Data.getAgentClasses().add(at);
			}
		}
		
		String hostip = "";
		String master = "";
		BufferedReader reader = null;
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("master.txt");
			reader = new BufferedReader(new InputStreamReader(in));
			String fileContent = reader.readLine();
			InetAddress ip = InetAddress.getLocalHost();
			hostip = ip.toString().split("/")[1].split("\n")[0];
			master = fileContent.split("=")[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(hostip.equals(master)) {
			for(AgentCenter ac : Data.getAgentCenters()) {
				ResteasyClient rc2 = new ResteasyClientBuilder().build();			
				String path2 = "http://" + ac.getAddress() + ":8080/ChatWAR/rest/agents/classes";
				System.out.println(path2);
				ResteasyWebTarget rwt2 = rc2.target(path2);
				Response response2 = rwt2.request(MediaType.APPLICATION_JSON).post(Entity.entity(Data.getAgentClasses(), MediaType.APPLICATION_JSON));
			}
		}
		
		return Response.status(200).build();
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Agent> getRunningAgents() {
		return Data.getAgents();
	}
	
	@POST
	@Path("/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postRunning(ArrayList<Agent> list) {
		for(Agent a : list) {
			boolean found = false;
			for(Agent aa : Data.getAgents()) {
				if(aa.getId().getName().equals(a.getId().getName())) {
					found = true;
				}
			}
			if(!found) {
				Data.getAgents().add(a);
			}
		}
		
		return Response.status(200).build();
	}
	
	@PUT
	@Path("/running/{type}/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response newAgent(@PathParam("type") String type, @PathParam("name") String name) {
		Agent a = new Agent();
		
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String h = ip.toString().split("/")[1].split("\n")[0];
		
		AgentCenter ac = new AgentCenter();
		
		for(AgentCenter center : Data.getAgentCenters()) {
			if(center.getAddress().equals(h)) {
				ac = center;
				break;
			}
		}
		
		a.setId(new AID(name, ac, new AgentType(type, type)));
		Data.getAgents().add(a);
		
		return Response.status(200).build();
	}
	
	@DELETE
	@Path("/running/{aid}")
	public Response removeAgent(@PathParam("aid") String aid) {
		for(int i=0;i<Data.getAgents().size();i++) {
			if(Data.getAgents().get(i).getId().getName().equals(aid)) {
				Data.getAgents().remove(i);
				return Response.status(200).build();
			}
		}
		
		return Response.status(400).build();
	}
}
