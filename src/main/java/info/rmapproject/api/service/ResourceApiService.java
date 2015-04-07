package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.ResourceResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/resource")
public class ResourceApiService {

	protected static ResourceResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new ResourceResponseManager();
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
    	Response response = responseManager.getResourceServiceOptions();
	    return response;
    }
        
    /**
     * 
     * @return HTTP Response
     * Returns link to Resource API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getResourceApiDetails()	{
    	Response response = responseManager.getResourceServiceHead();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns Resource API information/link, and lists HTTP options
     * 
     */
    
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getResourceApiDetailedOptions()	{
    	Response response = responseManager.getResourceServiceOptions();
	    return response;

    }   
	
	/**
	 * 
	 * @param resourceUri
	 * @return
	 * Gets all related objects
	 */
	
    @GET
    @Path("/{resourceuri}")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceAllAsJson(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "ALL", "JSON", null);
	    return response;	
    }

    @GET
    @Path("/{resourceuri}")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceAllAsText(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "ALL", "TEXT", null);
	    return response;	
    }
	
	/**
	 * 
	 * @param resourceUri
	 * @return
	 * Gets related statements
	 */
    @GET
    @Path("/{resourceuri}")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceStmtsAsJson(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "STATEMENTS", "JSON", null);
	    return response;	
    }

    @GET
    @Path("/{resourceuri}")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceStmtsAsText(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "STATEMENTS", "TEXT", null);
	    return response;	
    }
	
	/**
	 * 
	 * @param resourceUri
	 * @return
	 * Gets related events
	 */
    @GET
    @Path("/{resourceuri}")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceEventsAsJson(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "EVENTS", "JSON", null);
	    return response;	
    }

    @GET
    @Path("/{resourceuri}")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceEventsAsText(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "EVENTS", "TEXT", null);
	    return response;	
    }
	
	/**
	 * 
	 * @param resourceUri
	 * @return
	 * Gets related events
	 */
    @GET
    @Path("/{resourceuri}")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceAgentsAsJson(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "AGENTS", "JSON", null);
	    return response;	
    }
    @GET
    @Path("/{resourceuri}")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceAgentsAsText(@PathParam("resourceuri") String resourceUri) {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, "AGENTS", "TEXT", null);
	    return response;	
    }
   
}