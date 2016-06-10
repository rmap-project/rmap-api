package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.responsemgr.EventResponseManager;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.core.model.RMapObjectType;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

/**
 * API service for RMap Events
 * @author khanson
 */

@Path("/events")
public class EventApiService {


    @Autowired
    private WebApplicationContext context;
	//private EventResponseManager eventResponseManager = null;	

    /**
     * Get new event response manager bean - must use WebApplicationContext to avoid thread issues.
     * @return
     * @throws RMapApiException
     */
    private EventResponseManager getEventResponseManager() throws RMapApiException {
    	EventResponseManager eventResponseManager = (EventResponseManager)context.getBean("eventResponseManager");
    	if (eventResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	} 
    	return eventResponseManager;
	}
	
	
/*
 * ------------------------------
 * 
 * 	 GET INFO ABOUT API SERVICE
 *  
 *-------------------------------
 */	
	/**
	 * GET /event
     * Returns link to Event API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */	
    @GET
    @Produces("application/json;charset=UTF-8;")
    public Response apiGetServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = getEventResponseManager().getEventServiceOptions();
	    return response;
    }
    

	/**
	 * HEAD /event
     * Returns Event API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetEventApiDetails() throws RMapApiException {
    	Response response = getEventResponseManager().getEventServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /event
     * Returns Event API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Produces("application/json;charset=UTF-8;")
    public Response apiGetEventApiDetailedOptions() throws RMapApiException {
    	Response response = getEventResponseManager().getEventServiceOptions();
	    return response;

    }
        
/*
 * ------------------------------
 * 
 *  	  GET EVENT RDF
 *  
 *-------------------------------
 */
	/**
	 * GET /event/{eventUri}
	 * Returns requested RMap:Event as RDF/XML, JSON-LD, NQUADS, TURTLE
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{eventUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;", "application/vnd.rmap-project.event+rdf+xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.event+ld+json;charset=UTF-8;",
				"application/n-quads;charset=UTF-8;", "application/vnd.rmap-project.event+n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.event+turtle;charset=UTF-8;"
				})
    public Response apiGetRMapEvent(@Context HttpHeaders headers, @PathParam("eventUri") String eventUri) throws RMapApiException {
    	RdfMediaType returnType = HttpTypeMediator.getRdfResponseType(headers);
    	Response response=getEventResponseManager().getRMapEvent(eventUri, returnType);
    	return response;
    }

    
    
/*
 *-------------------------------
 *
 *	GET OBJECTS RELATED TO EVENT 
 * 
 *-------------------------------
 */
    
    
	/**
	 * GET /event/{eventUri}/discos
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as TEXT or JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapEventDiSCOs(@Context HttpHeaders headers, @PathParam("eventUri") String eventUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response relatedDiscos = getEventResponseManager().getRMapEventRelatedObjs(eventUri, RMapObjectType.EVENT, outputType);
	    return relatedDiscos;
    }

	/**
	 * GET /event/{eventUri}/agents
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as TEXT or JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapEventAgents(@Context HttpHeaders headers, @PathParam("eventUri") String eventUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response relatedAgents = getEventResponseManager().getRMapEventRelatedObjs(eventUri, RMapObjectType.AGENT, outputType);
	    return relatedAgents;
    }

	/**
	 * GET /event/{eventUri}/resources
	 * Returns list of rdfs:Resource URIs related to the RMap:Event URI as TEXT or JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/rmapobjs")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapEventResources(@Context HttpHeaders headers, @PathParam("eventUri") String eventUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response relatedResources = getEventResponseManager().getRMapEventRelatedObjs(eventUri, null, outputType);
	    return relatedResources;
    }


}