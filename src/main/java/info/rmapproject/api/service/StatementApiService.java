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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * API service for RMap Stmts
 * @author khanson
 */

@Path("/stmts")
public class StatementApiService {
	
	private StatementResponseManager statementResponseManager = null;

    @Autowired
    public void setStatementResponseManager(StatementResponseManager statementResponseManager) throws RMapApiException {
    	if (statementResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	}
    	this.statementResponseManager = statementResponseManager;
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
    	Response response = statementResponseManager.getStatementServiceOptions();
   		return response;
    }
    
	/**
	 * HEAD /stmts
     * Returns Stmt API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response apiGetStmtApiDetails() throws RMapApiException	{
    	Response response = statementResponseManager.getStatementServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /stmts
     * Returns Statement API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Produces("application/json")
    public Response apiGetStmtApiDetailedOptions() throws RMapApiException	{
    	Response response = statementResponseManager.getStatementServiceOptions();
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
	 * GET /stmts/{subject}/{predicate}/{object}/discos[?status={status}&agents={agentsCsv}&from={dateFrom}&until={dateTo}]
	 * Returns list of URIs for RMap:DiSCOs  that contain the statement matching the subject, predicate, object provided
     * @param subject
     * @param predicate
     * @param object
     * @param status
     * @param agents
     * @param from
     * @param until
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{subject}/{predicate}/{object}/discos")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapDiSCOsContainingStmt( @Context HttpHeaders headers, 
		    										@PathParam("subject") String subject, 
		    										@PathParam("predicate") String predicate, 
		    										@PathParam("object") String object, 
	    											@Context UriInfo uriInfo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response response = statementResponseManager.getStatementRelatedDiSCOs(subject, predicate, object, outputType, queryParams);
	    return response;	
    }

/*
 * -------------------------------------
 * 
 *  	  GET STMT ASSERTING AGENTS
 *  
 *--------------------------------------
 */
    /**
     * GET /stmts/{subject}/{predicate}/{object}/agents[?status={status}&from={dateFrom}&until={dateTo}]
	 * Returns list of URIs for RMap:Agents that asserted the statement matching the subject, predicate, object provided
     * @param headers
     * @param subject
     * @param predicate
     * @param object
     * @param uriInfo
     * @return
     * @throws RMapApiException
     */
    @GET
    @Path("/{subject}/{predicate}/{object}/agents")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetStmtAssertingAgents(@Context HttpHeaders headers, 
    										@PathParam("subject") String subject, 
    										@PathParam("predicate") String predicate, 
    										@PathParam("object") String object, 
											@Context UriInfo uriInfo) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    	Response response = statementResponseManager.getStatementAssertingAgents(subject, predicate, object, outputType, queryParams);
	    return response;	
    }
       
}