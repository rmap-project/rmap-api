package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.AgentResponseManager;
import info.rmapproject.api.utils.ListType;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * 
 * API service for RMap Agents
 * @author khanson
 *
 */

@Path("/agent")
public class AgentApiService {

	protected static AgentResponseManager responseManager = null;
	static{
		try {
			responseManager = new AgentResponseManager();
		}
		catch (Exception e){
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);
		}
	}
	
/*
 * ------------------------------
 * 
 * 	 GET INFO ABOUT API SERVICE
 *  
 *-------------------------------
 */
	
	/**
	 * GET /agent
     * Returns link to Agent API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getAgentServiceOptions();
	    return response;
    }
    
    
	/**
	 * HEAD /agent
     * Returns Agent API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/")
    public Response getAgentApiDetails() throws RMapApiException {
    	Response response = responseManager.getAgentServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /agent
     * Returns Agent API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Path("/")
    public Response getAgentApiDetailedOptions() throws RMapApiException {
    	Response response = responseManager.getAgentServiceHead();
	    return response;
    }
    
    

/*
 * ------------------------------
 * 
 *  	  GET AGENT RDF
 *  
 *-------------------------------
 */
    
	/**
	 * GET /agent/{agentUri}
	 * Returns requested RMap:Agent as RDF/XML
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{agentUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","vnd.rmap-project.agent+rdf+xml;charset=UTF-8;"})
    public Response getRMapAgentAsRdfXml(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgent(agentUri, "RDFXML");
	    return rdfAgent;
    }
    
	/**
	 * GET /agent/{agentUri}
	 * Returns requested RMap:Agent as RDF/XML
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{agentUri}")
    @Produces({"application/ld+json;charset=UTF-8;","vnd.rmap-project.agent+ld+json;charset=UTF-8;"})
    public Response getRMapAgentAsRDFJSON(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgent(agentUri, "JSONLD");
	    return rdfAgent;
    }
    
	/**
	 * GET /agent/{agentUri}
	 * Returns requested RMap:Agent as RDF/XML
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{agentUri}")
    @Produces({"application/n-quads;charset=UTF-8;","vnd.rmap-project.agent+n-quads;charset=UTF-8;"})
    public Response getRMapAgentAsRDFNQUADS(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgent(agentUri, "RDFNQUADS");
	    return rdfAgent;
    }    
    
	/**
	 * GET /agent/{agentUri}
	 * Returns requested RMap:Agent as RDF/XML
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{agentUri}")
    @Produces({"text/turtle;charset=UTF-8;","vnd.rmap-project.agent+turtle;charset=UTF-8;"})
    public Response getRMapAgentAsTurtle(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgent(agentUri, "TURTLE");
	    return rdfAgent;
    }
    
/*
 * ------------------------------
 * 
 *	  GET RELATED PROFILE LIST
 *  
 *-------------------------------
 */
    
	/**
	 * GET /agent/{agentUri}/profiles
	 * Returns list of RMap:Profile URIs as JSON
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    
    @GET
    @Path("/{agentUri}/profiles")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapAgentProfilesAsJSon(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgentRelatedProfiles(agentUri, ListType.JSON);
	    return rdfAgent;
    }
    

	/**
	 * GET /agent/{agentUri}/profiles
	 * Returns list of RMap:Profile URIs as plain text
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{agentUri}/profiles")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapAgentAsText(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response rdfAgent = responseManager.getRMapAgentRelatedProfiles(agentUri, ListType.PLAIN_TEXT);
	    return rdfAgent;
    }
    
}