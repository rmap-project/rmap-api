package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.StatementResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API service for RMap Stmts
 * @author khanson
 */

@Path("/stmts")
public class StatementApiService {
	
	protected static StatementResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new StatementResponseManager();
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
	 * if ever need full path...
	 * @Context
	 * UriInfo uriInfo;
	 * String path = uri.getPath();
	 */
	
	
/*
 * ------------------------------
 * 
 * 	 GET INFO ABOUT API SERVICE
 *  
 *-------------------------------
 */	
	/**
	 * GET /stmt
     * Returns link to Statement API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Produces("application/json")
    public Response apiGetServiceInfo() throws RMapApiException {
   		//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getStatementServiceOptions();
   		return response;
    }
    
	/**
	 * HEAD /stmt
     * Returns Stmt API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetStmtApiDetails() throws RMapApiException	{
    	Response response = responseManager.getStatementServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /stmt
     * Returns Statement API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Produces("application/json")
    public Response apiGetStmtApiDetailedOptions() throws RMapApiException	{
    	Response response = responseManager.getStatementServiceOptions();
	    return response;
    }
        
    
/*
 *-------------------------------
 *
 *		GET STATEMENT HEADER
 * 
 *-------------------------------
 */
	/**
	 * HEAD /stmts/{stmtUri}
     * Returns status information for specific RMap:Statement as a HTTP response header. 
     * Includes event list and URI
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */
//    @HEAD
//    @Path("/{stmtUri}")
//    public Response apiGetRMapStmtHeader(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
//    	Response eventList = responseManager.getRMapStatementHeader(stmtUri);
//    	return eventList;
//    }
    
/*
 * ------------------------------
 * 
 *  	  GET STATEMENT ID
 *  
 *-------------------------------
 */
	/**
	 * GET /stmts/{subject}/{predicate}/{object}
	 * Returns RMap:Statement URI for subject, predicate, object
     * @param subject
     * @param predicate
     * @param object
	 * @return Response
	 * @throws RMapApiException
	 */  
//    @GET
//    @Path("/{subject}/{predicate}/{object}")
//    @Produces("text/plain")
//    public Response apiGetRMapstmtUri(@PathParam("subject") String subject, 
//    									@PathParam("predicate") String predicate,
//    									@PathParam("object") String object) throws RMapApiException {
//    	Response stmtUriResponse = responseManager.getRMapStatementID(subject, predicate, object);
//    	return stmtUriResponse;
//    }
    
/*
 * ------------------------------
 * 
 *	  GET RELATED EVENT LIST
 *  
 *-------------------------------
 */
    
	/**
	 * GET /stmts/{stmtUri}/events
	 * Returns list of RMap:Event URIs related to the RMap:Statement URI as JSON
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */    
//    @GET
//    @Path("/{stmtUri}/events")
//    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
//    public Response apiGetRMapStmtEvents(@Context HttpHeaders headers, @PathParam("stmtUri") String stmtUri) throws RMapApiException {
//    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
//    	Response eventList = responseManager.getRMapStatementRelatedEvents(stmtUri, outputType);
//    	return eventList;
//    }
       
}