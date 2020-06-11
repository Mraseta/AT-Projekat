package beans;

import java.util.HashSet;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import data.Data;
import model.Agent;
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
}
