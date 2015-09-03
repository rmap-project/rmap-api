package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.responsemgr.StatementResponseManager;
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
 * ------------------------------
 * 
 *  	  GET STMT DISCOS
 *  
 *-------------------------------
 */
	/**
	 * GET /stmts/{subject}/{predicate}/{object}/discos
	 * Returns list of RMap:DiSCO URIs that contain the statement matching the subject, predicate, object provided
     * @param subject
     * @param predicate
     * @param object
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{subject}/{predicate}/{object}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapDiSCOsContainingStmt(@Context HttpHeaders headers, 
    										@PathParam("subject") String subject, 
    										@PathParam("predicate") String predicate, 
    										@PathParam("object") String object, 
    										@QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response response = responseManager.getStatementRelatedDiSCOs(subject, predicate, object, status, outputType);
	    return response;	
    }

/*
 * ------------------------------
 * 
 *  	  GET STMT AGENTS
 *  
 *-------------------------------
 */
	/**
	 * GET /stmts/{subject}/{predicate}/{object}/agents
	 * Returns list of RMap:DiSCO URIs that contain the statement matching the subject, predicate, object provided
     * @param subject
     * @param predicate
     * @param object
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{subject}/{predicate}/{object}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapAgentsContainingStmt(@Context HttpHeaders headers, 
    										@PathParam("subject") String subject, 
    										@PathParam("predicate") String predicate, 
    										@PathParam("object") String object, 
    										@QueryParam("status") String status) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response response = responseManager.getStatementRelatedAgents(subject, predicate, object, status, outputType);
	    return response;	
    }
    
    /*
     * ------------------------------
     * 
     *  	  GET STMT ASSERTING AGTS
     *  
     *-------------------------------
     */
    	/**
    	 * GET /stmts/{subject}/{predicate}/{object}/sysagents
    	 * Returns list of RMap:DiSCO URIs that contain the statement matching the subject, predicate, object provided
         * @param subject
         * @param predicate
         * @param object
    	 * @return Response
    	 * @throws RMapApiException
    	 */  
        @GET
        @Path("/{subject}/{predicate}/{object}/sysagents")
        @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
        public Response apiGetStmtAssertingAgents(@Context HttpHeaders headers, 
        										@PathParam("subject") String subject, 
        										@PathParam("predicate") String predicate, 
        										@PathParam("object") String object, 
        										@QueryParam("status") String status) throws RMapApiException {
        	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
        	Response response = responseManager.getStatementAssertingAgents(subject, predicate, object, status, outputType);
    	    return response;	
        }
       
}