package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.responsemgr.ResourceResponseManager;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

/**
 * API service for rdfs:Resources in RMap
 * @author khanson
 */

@Path("/resources")
public class ResourceApiService {

    @Autowired
    private WebApplicationContext context;
	//private ResourceResponseManager resourceResponseManager = null;

    /**
     * Get new resource response manager bean - must use WebApplicationContext to avoid thread issues.
     * @return
     * @throws RMapApiException
     */
    private ResourceResponseManager getResourceResponseManager() throws RMapApiException {
    	ResourceResponseManager resourceResponseManager = (ResourceResponseManager)context.getBean("resourceResponseManager");
    	if (resourceResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	} 
    	return resourceResponseManager;
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
    	Response response = getResourceResponseManager().getResourceServiceOptions();
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
    	Response response = getResourceResponseManager().getResourceServiceHead();
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
    	Response response = getResourceResponseManager().getResourceServiceOptions();
	    return response;

    }   

	/**
	 * 
	 * GET /resource/{resourceUri}/events[?agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param headers
	 * @param resourceUri
	 * @param uriInfo
	 * @return
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{resourceUri}/events")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceEvents(@Context HttpHeaders headers, 
										    		@PathParam("resourceUri") String resourceUri, 
	    											@Context UriInfo uriInfo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response response = getResourceResponseManager().getRMapResourceRelatedObjs(resourceUri, RMapObjectType.EVENT, outputType, queryParams);
	    return response;	
    }
		
	/**
	 * GET /resource/{resourceUri}/agents[?agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:Agent URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param headers
	 * @param resourceUri
	 * @param uriInfo
	 * @return
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{resourceUri}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceAssertingAgents(@Context HttpHeaders headers, 
    												@PathParam("resourceUri") String resourceUri, 
	    											@Context UriInfo uriInfo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response response = getResourceResponseManager().getRMapResourceRelatedObjs(resourceUri, RMapObjectType.AGENT, outputType, queryParams);
	    return response;	
    }
    
	/**
	 * GET /resource/{resourceUri}/discos[?status={status}&agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all RMap:DiSCO URIs related to the rdfs:Resource URI as JSON or PLAIN TEXT
	 * @param headers
	 * @param resourceUri
	 * @param uriInfo
	 * @return
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{resourceUri}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapResourceDiscos(@Context HttpHeaders headers, 
								    		@PathParam("resourceUri") String resourceUri, 
											@Context UriInfo uriInfo) throws RMapApiException {
    	
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Response response = getResourceResponseManager().getRMapResourceRelatedObjs(resourceUri, RMapObjectType.DISCO, outputType, queryParams);
	    return response;	
    }

	/**
	 * GET /resources/{resourceUri}[?status={status}&agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of all rdf:triples related to the rdfs:Resource URI as RDF serialization
	 * @param headers
	 * @param resourceUri
	 * @param uriInfo
	 * @return
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
												@Context UriInfo uriInfo) throws RMapApiException {

    	RdfMediaType outputType = HttpTypeMediator.getRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Response response = getResourceResponseManager().getRMapResourceTriples(resourceUri, outputType, queryParams);
	    return response;	
    }
    
    
   
}