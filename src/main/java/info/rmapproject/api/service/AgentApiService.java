package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.responsemgr.AgentResponseManager;
import info.rmapproject.api.utils.HttpTypeMediator;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * 
 * API service for RMap Agents
 * @author khanson
 *
 */

@Path("/agents")
public class AgentApiService {
	
	protected static AgentResponseManager responseManager = null;
	static{
		try {
			responseManager = new AgentResponseManager();
		}
		catch (Exception e){
			try {
				throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);
			} catch (RMapApiException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	//map auth to sysagent

	//@Context 
	//private SecurityContext userInfo;


	/**
	 * GET /agent or GET /agent?representedAgent={uri}[&creator={creatorUri}]
     * If no additional filters applied, returns link to Agent API information, and lists HTTP options
     * If there are filters on the agent, applies filter.
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetServiceInfo(@Context HttpHeaders headers, 
    								@QueryParam("representedAgent") String uri, 
    								@QueryParam("creator") String creator) throws RMapApiException {
    	Response response = null;
    	if (uri!=null)	{
        	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
       		response = responseManager.getRMapAgentRepresentations(uri, creator, outputType);    
    	}
    	else	{
        	//TODO: for now returns same as options, but might want html response to describe API?
        	response = responseManager.getAgentServiceOptions();		
    	}    	
    	return response;
    }

	/**
	 * HEAD /agent
     * Returns Agent API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetApiDetails() throws RMapApiException {
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
    public Response apiGetApiDetailedOptions() throws RMapApiException {
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
    	RdfType returnType = HttpTypeMediator.getRdfResponseType(headers);
    	Response response=responseManager.getRMapAgent(agentUri, returnType);
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
    	Response response = responseManager.getRMapAgentHeader(agentUri);
	    return response;
    }
   
    
/*

 * ------------------------------
 * 
 *  	 CREATE/DELETE AGENT
 *  
 *-------------------------------
  
    //
     * 
     *  No longer writing Agents through the API! All agents are now System Agents
     *
    
	*//**
	 * POST /agent/
	 * Creates new Agent from RDF/XML, JSON-LD or TURTLE
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 *//*
    
     
    @Path("/")
    @Consumes({"application/rdf+xml;charset=UTF-8;", "application/vnd.rmap-project.agent+rdf+xml;charset=UTF-8;",
		"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.agent+ld+json;charset=UTF-8;",
		"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.agent+turtle;charset=UTF-8;"
		})
    public Response apiCreateRMapAgent(@Context HttpHeaders headers, InputStream agentRdf) throws RMapApiException {
    	RdfType requestFormat = HttpTypeMediator.getRdfTypeOfRequest(headers);
    	Response createResponse = responseManager.createRMapAgent(agentRdf, requestFormat, getSysAgentId());
		return createResponse;
    }	
    
    
	*//**
	 * DELETE /agent/{agentUri}
	 * Sets status of target rmap:Agent to "tombstoned".  It will still be stored in the triplestore
	 * but won't be visible through the API.
	 * @param agentUri
	 * @return Response
	 * @throws RMapApiException
	 *//*    
    @DELETE
    @Path("/{agentUri}")
    public Response apiDeleteRMapAgent(@PathParam("agentUri") String agentUri) throws RMapApiException {
    	Response response = responseManager.tombstoneRMapAgent(agentUri,getSysAgentId());
	    return response;
    }
    */
    
    

/*
 * ------------------------------
 * 
 *	  GET RELATED EVENT LIST
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
    public Response apiGetRMapAgentEventList(@Context HttpHeaders headers, @PathParam("agentUri") String agentUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response eventList = responseManager.getRMapAgentEvents(agentUri, outputType);
    	return eventList;
    }
    
}