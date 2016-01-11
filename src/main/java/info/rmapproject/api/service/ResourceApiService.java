package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.ObjType;
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

import org.springframework.beans.factory.annotation.Autowired;

/**
 * API service for rdfs:Resources in RMap
 * @author khanson
 */

@Path("/resources")
public class ResourceApiService {

	private ResourceResponseManager resourceResponseManager = null;

    @Autowired
    public void setResourceResponseManager(ResourceResponseManager resourceResponseManager) throws RMapApiException {
    	if (resourceResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	} else {
    		this.resourceResponseManager = resourceResponseManager;
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
    	Response response = resourceResponseManager.getResourceServiceOptions();
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
    	Response response = resourceResponseManager.getResourceServiceHead();
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
    	Response response = resourceResponseManager.getResourceServiceOptions();
	    return response;

    }   


	/**
	 * GET /resource/{resourceUri}/events[?agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @param agents (as csv)
	 * @param from (date as string yyyymmdd)
	 * @param until (date as string yyyymmdd)
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/events")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceEvents(@Context HttpHeaders headers, 
										    		@PathParam("resourceUri") String resourceUri, 
										    		@QueryParam("agents") String agents, 
										    		@QueryParam("from") String dateFrom, 
										    		@QueryParam("until") String dateTo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = resourceResponseManager.getRMapResourceRelatedObjs(resourceUri, ObjType.EVENTS, outputType, 
															null, agents, dateFrom, dateTo);
	    return response;	
    }
	

	/**
	 * GET /resource/{resourceUri}/agents[?agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:Agent URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @param status
	 * @param sysagents (as csv)
	 * @param from (date as string yyyymmdd)
	 * @param until (date as string yyyymmdd)
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceAssertingAgents(@Context HttpHeaders headers, 
    												@PathParam("resourceUri") String resourceUri, 
    												@QueryParam("agents") String agents, 
    												@QueryParam("from") String dateFrom, 
    												@QueryParam("until") String dateTo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = resourceResponseManager.getRMapResourceRelatedObjs(resourceUri, ObjType.AGENTS, outputType, 
																null, agents, dateFrom, dateTo);
	    return response;	
    }
    
	/**
	 * GET /resource/{resourceUri}/discos[?status={status}&agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:DiSCO URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param resourceUri
	 * @param status
	 * @param agents (as csv)
	 * @param from (date as string yyyymmdd)
	 * @param until (date as string yyyymmdd)
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceDiscos(@Context HttpHeaders headers, 
								    		@PathParam("resourceUri") String resourceUri, 
								    		@QueryParam("status") String status, 
								    		@QueryParam("agents") String agents,
								    		@QueryParam("from") String dateFrom, 
								    		@QueryParam("until") String dateTo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
		Response response = resourceResponseManager.getRMapResourceRelatedObjs(resourceUri, ObjType.DISCOS, outputType, status, agents, dateFrom, dateTo);
	    return response;	
    }

	/**
	 * GET /resources/{resourceUri}[?status={status}&agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all rdf:triples related to the rdfs:Resource URI as RDF serialization
	 * @param resourceUri
	 * @param status
	 * @param agents (as csv)
	 * @param from (date as string yyyymmdd)
	 * @param until (date as string yyyymmdd)
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;"
				})
    public Response apiGetRMapResourceTriples(@Context HttpHeaders headers, 
									    		@PathParam("resourceUri") String resourceUri, 
									    		@QueryParam("status") String status, 
									    		@QueryParam("agents") String agents,
									    		@QueryParam("from") String dateFrom, 
									    		@QueryParam("until") String dateTo) throws RMapApiException {
    	RdfType outputType = HttpTypeMediator.getRdfResponseType(headers);
		Response response = resourceResponseManager.getRMapResourceTriples(resourceUri, outputType, status, agents, dateFrom, dateTo);
	    return response;	
    }
    
    
   
}