package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.EventResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author khanson
 * API service for RMap Events
 */

@Path("/event")
public class EventApiService {

	protected static EventResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new EventResponseManager();
		}
		catch (Exception e){
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);
		}
	}
	
	@Context
	UriInfo uriInfo;
	//    	String path = uri.getPath();
		
	
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getEventServiceOptions();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns link to Event API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getEventApiDetails()	{
    	Response response = responseManager.getEventServiceHead();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns Event API information/link, and lists HTTP options
     * 
     */
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getEventApiDetailedOptions()	{
    	Response response = responseManager.getEventServiceOptions();
	    return response;

    }
        
    
    /**
     * 
     * @param eventId
     * @return HTTP Response 
     * Read Event with output in various formats
     * 
     */

    @GET
    @Path("/{eventid}")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","vnd.rmap-project.event+rdf+xml;charset=UTF-8;"})
    public Response getRMapEventAsRdfXml(@PathParam("eventid") String eventId) {
    	Response rdfXMLStmt = responseManager.getRMapEvent(eventId, "RDFXML");
	    return rdfXMLStmt;
    }
    
    @GET
    @Path("/{eventid}")
    @Produces({"application/ld+json;charset=UTF-8;","vnd.rmap-project.event+ld+json;charset=UTF-8;"})
    public Response getRMapEventAsJsonLD(@PathParam("eventid") String eventId){
    	Response rdfJsonEvent = responseManager.getRMapEvent(eventId, "JSONLD");
    	return rdfJsonEvent;
    }

    @GET
    @Path("/{eventid}")
    @Produces({"application/n-quads;charset=UTF-8;","vnd.rmap-project.event+n-quads;charset=UTF-8;"})
    public Response getRMapEventAsRdfNQuads(@PathParam("eventid") String eventId) {
    	Response rdfNquadsEvent = responseManager.getRMapEvent(eventId, "RDFNQUADS");
    	return rdfNquadsEvent;
    }    
    
    @GET
    @Path("/{eventid}")
    @Produces({"text/turtle;charset=UTF-8;","vnd.rmap-project.event+turtle;charset=UTF-8;"})
    public Response getRMapEventAsTurtle(@PathParam("eventid") String eventId) {
    	Response rdfXmlEvent = responseManager.getRMapEvent(eventId, "TURTLE");
    	return rdfXmlEvent;
    }
    
	/**
	 * 
	 * @param eventId
	 * @return HTTP Response 
	 * Gets a list of URIs associated with Event
	 * 
	 */
    //TODO:should probably use an enum for object types and maybe output types?
    
    @GET
    @Path("/{eventid}/stmts")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventStmtsAsTXT(@PathParam("eventid") String eventId) {
    	Response relatedStmts = responseManager.getRMapEventRelatedObjs(eventId, "STATEMENTS","TEXT");
	    return relatedStmts;
    }  
    
    @GET
    @Path("/{eventid}/stmts")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventStmtsAsJSON(@PathParam("eventid") String eventId) {
    	Response relatedStmts = responseManager.getRMapEventRelatedObjs(eventId, "STATEMENTS","JSON");
	    return relatedStmts;
    }

    @GET
    @Path("/{eventid}/discos")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventDiSCOsAsTXT(@PathParam("eventid") String eventId) {
    	Response relatedDiscos = responseManager.getRMapEventRelatedObjs(eventId, "DISCOS","TEXT");
	    return relatedDiscos;
    }

    @GET
    @Path("/{eventid}/discos")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventDiSCOsAsJSON(@PathParam("eventid") String eventId) {
    	Response relatedDiscos = responseManager.getRMapEventRelatedObjs(eventId, "DISCOS","JSON");
	    return relatedDiscos;
    }

    @GET
    @Path("/{eventid}/agents")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventAgentsAsTXT(@PathParam("eventid") String eventId) {
    	Response relatedAgents = responseManager.getRMapEventRelatedObjs(eventId, "AGENTS","TEXT");
	    return relatedAgents;
    }
    
    @GET
    @Path("/{eventid}/agents")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventAgentsAsJSON(@PathParam("eventid") String eventId) {
    	Response relatedAgents = responseManager.getRMapEventRelatedObjs(eventId, "AGENTS","JSON");
	    return relatedAgents;
    }
    
    @GET
    @Path("/{eventid}/resources")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventResourcesAsTXT(@PathParam("eventid") String eventId) {
    	Response relatedResources = responseManager.getRMapEventRelatedObjs(eventId, "RESOURCES","TEXT");
	    return relatedResources;
    }

    @GET
    @Path("/{eventid}/resources")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventResourcesAsJSON(@PathParam("eventid") String eventId) {
    	Response relatedResources = responseManager.getRMapEventRelatedObjs(eventId, "RESOURCES","JSON");
	    return relatedResources;
    }
   
}