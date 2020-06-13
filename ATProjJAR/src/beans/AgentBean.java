package beans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
	public HashSet<AgentType> getAgentTypes() {
		HashSet<AgentType> ret = new HashSet<AgentType>();
		for(Agent a : Data.getAgents()) {
			ret.add(a.getId().getType());
		}
		
		return ret;
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Agent> getRunningAgents() {
		return Data.getAgents();
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
