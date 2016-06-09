package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.responsemgr.AgentResponseManager;
import info.rmapproject.api.utils.HttpTypeMediator;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;


/**
 * 
 * API service for RMap Agents
 * @author khanson
 *
 */

@Scope("request")
@Path("/agents")
public class AgentApiService {
	
	private AgentResponseManager agentResponseManager;

    @Autowired
    public void setAgentResponseManager(AgentResponseManager agentResponseManager) throws RMapApiException {
    	if (agentResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	} 
    	this.agentResponseManager = agentResponseManager;
	}

	/**
	 * HEAD /agent
     * Returns Agent API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetApiDetails() throws RMapApiException {
    	Response response = agentResponseManager.getAgentServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /agent
     * Returns Agent API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    public Response apiGetApiDetailedOptions() throws RMapApiException {
    	Response response = agentResponseManager.getAgentServiceHead();
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
	 * Returns requested RMap:Agent as RDF/XML, JSON-LD, Turtle or NQUADS
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{agentUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;", "application/vnd.rmap-project.agent+rdf+xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.agent+ld+json;charset=UTF-8;",
				"application/n-quads;charset=UTF-8;", "application/vnd.rmap-project.agent+n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.agent+turtle;charset=UTF-8;"
				})
    public Response apiGetRMapAgent(@Context HttpHeaders headers, @PathParam("agentUri") String agentUri) throws RMapApiException {
    	RdfMediaType returnType = HttpTypeMediator.getRdfResponseType(headers);
    	Response response=agentResponseManager.getRMapAgent(agentUri, returnType);
    	return response;
    }
    
/*
 *-------------------------------
 *
 *		GET AGENT HEADER
 * 
 *-------------------------------
 */
	/**
	 * HEAD /agent/{agentUri}
     * Returns status information for specific Agent as a HTTP response header. 
     * Includes event list, versions, and URI
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/{agentUri}")
    public Response apiGetAgentStatus(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response response = agentResponseManager.getRMapAgentHeader(agentUri);
	    return response;
    }
   
   
/*
 * ------------------------------
 * 
 *	  GET RELATED OBJECT LISTS
 *  
 *-------------------------------
 */
    
	/**
	 * GET /agent/{agentUri}/events
	 * Returns list of RMap:Event URIs related to the Agent URI as JSON or PLAINTEXT
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{agentUri}/events")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapAgentEventList(	@Context HttpHeaders headers, 
												@PathParam("agentUri") String agentUri, 
									    		@Context UriInfo uriInfo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response eventList = agentResponseManager.getRMapAgentEvents(agentUri, outputType, queryParams);
    	return eventList;
    }
    
   
	/**
	 * GET /agent/{agentUri}/discos
	 * Returns list of URIs for RMap:DiSCOs that were created by the Agent URI as JSON or PLAINTEXT
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{agentUri}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapAgentDiSCOList (	@Context HttpHeaders headers, 
    											@PathParam("agentUri") String agentUri, 
    											@Context UriInfo uriInfo) throws RMapApiException {
    	
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response discoList = agentResponseManager.getRMapAgentDiSCOs(agentUri, outputType, queryParams);
    	return discoList;
    }
    
    
}