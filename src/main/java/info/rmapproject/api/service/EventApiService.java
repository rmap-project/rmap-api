package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.EventResponseManager;
import info.rmapproject.api.utils.BasicReturnType;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API service for RMap Events
 * @author khanson
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
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getEventServiceOptions();
	    return response;
    }
    

	/**
	 * HEAD /event
     * Returns Event API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/")
    public Response getEventApiDetails() throws RMapApiException {
    	Response response = responseManager.getEventServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /event
     * Returns Event API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getEventApiDetailedOptions() throws RMapApiException {
    	Response response = responseManager.getEventServiceOptions();
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
	 * Returns requested RMap:Event as RDF/XML
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{eventUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","application/vnd.rmap-project.event+rdf+xml;charset=UTF-8;"})
    public Response getRMapEventAsRdfXml(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response rdfXMLStmt = responseManager.getRMapEvent(eventUri, "RDFXML");
	    return rdfXMLStmt;
    }
    
	/**
	 * GET /event/{eventUri}
	 * Returns requested RMap:Event as JSON-LD
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{eventUri}")
    @Produces({"application/ld+json;charset=UTF-8;","application/vnd.rmap-project.event+ld+json;charset=UTF-8;"})
    public Response getRMapEventAsJsonLD(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response rdfJsonEvent = responseManager.getRMapEvent(eventUri, "JSONLD");
    	return rdfJsonEvent;
    }

	/**
	 * GET /event/{eventUri}
	 * Returns requested RMap:Event as NQUADS
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{eventUri}")
    @Produces({"application/n-quads;charset=UTF-8;","application/vnd.rmap-project.event+n-quads;charset=UTF-8;"})
    public Response getRMapEventAsRdfNQuads(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response rdfNquadsEvent = responseManager.getRMapEvent(eventUri, "RDFNQUADS");
    	return rdfNquadsEvent;
    }    
    
	/**
	 * GET /event/{eventUri}
	 * Returns requested RMap:Event as TURTLE
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{eventUri}")
    @Produces({"text/turtle;charset=UTF-8;","application/vnd.rmap-project.event+turtle;charset=UTF-8;"})
    public Response getRMapEventAsTurtle(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response rdfXmlEvent = responseManager.getRMapEvent(eventUri, "TURTLE");
    	return rdfXmlEvent;
    }
    
    
/*
 *-------------------------------
 *
 *	GET OBJECTS RELATED TO EVENT 
 * 
 *-------------------------------
 */
    
	/**
	 * GET /event/{eventUri}/stmts
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as TEXT
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/stmts")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventStmtsAsTXT(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedStmts = responseManager.getRMapEventRelatedObjs(eventUri, "STATEMENTS",BasicReturnType.PLAIN_TEXT);
	    return relatedStmts;
    }  

	/**
	 * GET /event/{eventUri}/stmts
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/stmts")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventStmtsAsJSON(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedStmts = responseManager.getRMapEventRelatedObjs(eventUri, "STATEMENTS",BasicReturnType.JSON);
	    return relatedStmts;
    }

	/**
	 * GET /event/{eventUri}/discos
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as TEXT
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/discos")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventDiSCOsAsTXT(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedDiscos = responseManager.getRMapEventRelatedObjs(eventUri, "DISCOS",BasicReturnType.PLAIN_TEXT);
	    return relatedDiscos;
    }

	/**
	 * GET /event/{eventUri}/discos
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/discos")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventDiSCOsAsJSON(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedDiscos = responseManager.getRMapEventRelatedObjs(eventUri, "DISCOS",BasicReturnType.JSON);
	    return relatedDiscos;
    }

	/**
	 * GET /event/{eventUri}/agents
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as TEXT
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/agents")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventAgentsAsTXT(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedAgents = responseManager.getRMapEventRelatedObjs(eventUri, "AGENTS",BasicReturnType.PLAIN_TEXT);
	    return relatedAgents;
    }

	/**
	 * GET /event/{eventUri}/agents
	 * Returns list of RMap:Statement URIs related to the RMap:Event URI as JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/agents")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventAgentsAsJSON(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedAgents = responseManager.getRMapEventRelatedObjs(eventUri, "AGENTS",BasicReturnType.JSON);
	    return relatedAgents;
    }

	/**
	 * GET /event/{eventUri}/resources
	 * Returns list of rdfs:Resource URIs related to the RMap:Event URI as TEXT
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/resources")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapEventResourcesAsTXT(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedResources = responseManager.getRMapEventRelatedObjs(eventUri, "RESOURCES",BasicReturnType.PLAIN_TEXT);
	    return relatedResources;
    }

	/**
	 * GET /event/{eventUri}/resources
	 * Returns list of rdfs:Resource URIs related to the RMap:Event URI as JSON
	 * @param eventUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{eventUri}/resources")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapEventResourcesAsJSON(@PathParam("eventUri") String eventUri) throws RMapApiException {
    	Response relatedResources = responseManager.getRMapEventRelatedObjs(eventUri, "RESOURCES",BasicReturnType.JSON);
	    return relatedResources;
    }
   
}