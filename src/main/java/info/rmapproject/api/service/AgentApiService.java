package info.rmapproject.api.service;

import info.rmapproject.api.responsemgr.AgentResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

@Path("/agent")
public class AgentApiService {

	protected static AgentResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new AgentResponseManager();
		}
		catch (Exception e){}
	}
	
	
	@Context
	UriInfo uriInfo;
	//    	String path = uri.getPath();
		
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getAgentServiceOptions();
	    return response;
    }
        
    /**
     * 
     * @return HTTP Response
     * Returns link to DiSCO API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getAgentApiDetails()	{
    	Response response = responseManager.getAgentServiceHead();
	    return response;
    }
    
    @OPTIONS
    @Path("/")
    public Response getAgentApiDetailedOptions()	{
    	Response response = responseManager.getAgentServiceHead();
	    return response;
    }
    
    @GET
    @Path("/{agentId}")
    @Produces("application/xml;charset=UTF-8;")
    public Response getRMapAgentAsHTML(@PathParam("agentId") String agentId) throws RepositoryException, RDFHandlerException, Exception {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfAgent = responseManager.getRMapAgent(agentId, "RDFXML");
	    return rdfAgent;
    }
    
    @GET
    @Path("/{agentId}")
    @Produces("application/ld+json;charset=UTF-8;")
    public Response getRMapAgentAsRDFJSON(@PathParam("agentId") String agentId) throws RepositoryException, RDFHandlerException, Exception {
    	Response rdfAgent = responseManager.getRMapAgent(agentId, "JSONLD");
	    return rdfAgent;
    }

    @GET
    @Path("/{agentId}")
    @Produces("application/n-quads;charset=UTF-8;")
    public Response getRMapAgentAsRDFNQUADS(@PathParam("agentId") String agentId) throws RepositoryException, RDFHandlerException, Exception {
    	Response rdfAgent = responseManager.getRMapAgent(agentId, "RDFNQUADS");
	    return rdfAgent;
    }    
    
    @GET
    @Path("/{agentId}")
    @Produces("application/rdf+xml;charset=UTF-8;")
    public Response getRMapAgentAsRDFXML(@PathParam("agentId") String agentId) throws RepositoryException, RDFHandlerException, Exception {
    	Response rdfAgent = responseManager.getRMapAgent(agentId, "RDFXML");
	    return rdfAgent;
    }
    
}