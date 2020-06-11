package beans;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ejb.LocalBean;

@Stateless
@Path("/user")
@LocalBean
public class UserBean {
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public String test() {
		return "ok";
	}
}
