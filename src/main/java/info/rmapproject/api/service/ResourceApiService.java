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
	 * Returns list of all URIs related to the rdfs:Resource URI as JSON
	 * @param resourceUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{resourceUri}")
    @Produces("application/json;charset=UTF-8;")
    public Response apiGetRMapResourceAllAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceAllAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceStmtsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceStmtsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceEventsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceEventsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceAgentsAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceAgentsAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceDiscosAsJson(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
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
    public Response apiGetRMapResourceDiscosAsText(@PathParam("resourceUri") String resourceUri) throws RMapApiException {
		Response response = responseManager.getRMapResourceRelatedObjs(resourceUri, FilterObjType.DISCOS, BasicOutputType.PLAIN_TEXT, null);
	    return response;	
    }
   
}