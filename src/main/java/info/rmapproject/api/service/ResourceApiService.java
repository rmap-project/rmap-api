package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.FilterObjType;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.responsemgr.ResourceResponseManager;
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
 * API service for rdfs:Resources in RMap
 * @author khanson
 */

@Path("/resources")
public class ResourceApiService {

	protected static ResourceResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new ResourceResponseManager();
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


/*
 * ------------------------------
 * 
 * 	 GET INFO ABOUT API SERVICE
 *  
 *-------------------------------
 */	
	/**
	 * GET /resource
     * Returns link to Resource API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
	@GET
    @Produces("application/json;charset=UTF-8;")
    public Response apiGetServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getResourceServiceOptions();
	    return response;
    }
        

	/**
	 * HEAD /resource
     * Returns Resource API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetResourceApiDetails()	throws RMapApiException {
    	Response response = responseManager.getResourceServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /resource
     * Returns Resource API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Produces("application/json;charset=UTF-8;")
    public Response apiGetResourceApiDetailedOptions()	throws RMapApiException {
    	Response response = responseManager.getResourceServiceOptions();
	    return response;

    }   

/*
 * ------------------------------
 * 
 *	  GET RELATED OBJECTS LIST
 *  
 *-------------------------------
 */
	/**
	 * GET /resource/{resourceUri}
	 * Returns list of URIs related to the rdfs:Resource URI as PLAIN TEXT or JSON
	 * @param resourceUri
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceAll(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri, @QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.ALL, outputType, status);
	    return response;	
    }

	

	/**
	 * GET /resource/{resourceUri}/stmts
	 * Returns list of all RMap:Statement URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/stmts")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceStmts(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri, @QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.STATEMENTS, outputType, status);
	    return response;	
    }

	/**
	 * GET /resource/{resourceUri}/events
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/events")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceEventsAsJson(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.EVENTS, outputType, null);
	    return response;	
    }
	

	/**
	 * GET /resource/{resourceUri}/agents
	 * Returns list of all RMap:Agent URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceAgentsAsJson(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri, @QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.AGENTS, outputType, status);
	    return response;	
    }
    
	/**
	 * GET /resource/{resourceUri}/discos
	 * Returns list of all RMap:DiSCO URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceDiscos(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri, @QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.DISCOS, outputType, status);
	    return response;	
    }
    
	

	/**
	 * GET /resources/{resourceUri}/triples
	 * Returns list of all rdf:triples related to the rdfs:Resource URI as RDF serialization
	 * @param resourceUri
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/triples")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;"
				})
    public Response apiGetRMapResourceTriples(@Context HttpHeaders headers, @PathParam("resourceUri") String resourceUri, @QueryParam("status") String status) throws RMapApiException {
    	RdfType outputType = HttpTypeMediator.getRdfResponseType(headers);
		Response response = responseManager.getRMapResourceTriples(resourceUri, outputType, status);
	    return response;	
    }
    
    
   
}