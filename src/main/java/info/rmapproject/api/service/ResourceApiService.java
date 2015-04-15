package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.BasicOutputType;
import info.rmapproject.api.lists.FilterObjType;
import info.rmapproject.api.responsemgr.ResourceResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API service for rdfs:Resources in RMap
 * @author khanson
 */

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
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
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
    @Path("/")
    public Response getResourceApiDetails()	throws RMapApiException {
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
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getResourceApiDetailedOptions()	throws RMapApiException {
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
	 * Returns list of all URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceAllAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.ALL, BasicOutputType.JSON, null);
	    return response;	
    }

	/**
	 * GET /resource/{resourceUri}
	 * Returns list of URIs related to the rdfs:Resource URI as PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceAllAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.ALL, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
	

	/**
	 * GET /resource/{resourceUri}/stmts
	 * Returns list of all RMap:Statement URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/stmts")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceStmtsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.STATEMENTS, BasicOutputType.JSON, null);
	    return response;	
    }

	/**
	 * GET /resource/{resourceUri}/stmts
	 * Returns list of all RMap:Statement URIs related to the rdfs:Resource URI as PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/stmts")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceStmtsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.STATEMENTS, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
	

	/**
	 * GET /resource/{resourceUri}/events
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/events")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceEventsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.EVENTS, BasicOutputType.JSON, null);
	    return response;	
    }

	/**
	 * GET /resource/{resourceUri}/events
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/events")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceEventsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.EVENTS, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
	

	/**
	 * GET /resource/{resourceUri}/agents
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/agents")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceAgentsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.AGENTS, BasicOutputType.JSON, null);
	    return response;	
    }
    

	/**
	 * GET /resource/{resourceUri}/agents
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/agents")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceAgentsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.AGENTS, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
	/**
	 * GET /resource/{resourceUri}/agents
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/discos")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapResourceDiscosAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.DISCOS, BasicOutputType.JSON, null);
	    return response;	
    }
    

	/**
	 * GET /resource/{resourceUri}/agents
	 * Returns list of all RMap:Event URIs related to the rdfs:Resource URI as PLAIN TEXT
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}/discos")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapResourceDiscosAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.DISCOS, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
   
}