package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.StatementResponseManager;
import info.rmapproject.api.utils.ListType;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * API service for RMap Stmts
 * @author khanson
 */

@Path("/stmt")
public class StatementApiService {
	
	protected static StatementResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new StatementResponseManager();
		}
		catch (Exception e){
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);
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
    @Path("/")
    @Produces("application/json")
    public Response getServiceInfo() throws RMapApiException {
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
    @Path("/")
    public Response getStmtApiDetails() throws RMapApiException	{
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
    @Path("/")
    @Produces("application/json")
    public Response getStmtApiDetailedOptions() throws RMapApiException	{
    	Response response = responseManager.getStatementServiceOptions();
	    return response;
    }
        

/*
 * ------------------------------
 * 
 *  	  GET STATEMENT RDF
 *  
 *-------------------------------
 */
	/**
	 * GET /stmt/{stmtUri}
	 * Returns requested RMap:Statement as RDF/XML
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{stmtUri}")
    @Produces("application/rdf+xml,application/xml,application/vnd.rmap-project.statement+rdf+xml")
    public Response getRMapStmtAsRdfXml(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response rdfXMLStmt = responseManager.getRMapStatement(stmtUri, "RDFXML");
	    return rdfXMLStmt;
    }

	/**
	 * GET /stmt/{stmtUri}
	 * Returns requested RMap:Statement as JSON-LD
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{stmtUri}")
    @Produces("application/ld+json,application/vnd.rmap-project.statement+ld+json")
    public Response getRMapStmtAsJsonLD(@PathParam("stmtUri") String stmtUri)  throws RMapApiException {
    	Response rdfJsonStmt = responseManager.getRMapStatement(stmtUri, "JSONLD");
    	return rdfJsonStmt;
    }

	/**
	 * GET /stmt/{stmtUri}
	 * Returns requested RMap:Statement as NQUADS
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{stmtUri}")
    @Produces("application/n-quads,application/vnd.rmap-project.statement+n-quads")
    public Response getRMapStmtAsRdfNQuads(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response rdfNquadsStmt = responseManager.getRMapStatement(stmtUri, "RDFNQUADS");
    	return rdfNquadsStmt;
    }    

	/**
	 * GET /stmt/{stmtUri}
	 * Returns requested RMap:Statement as TURTLE
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{stmtUri}")
    @Produces("text/turtle,application/vnd.rmap-project.statement+turtle")
    public Response getRMapStmtAsTurtle(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response rdfXmlStmt = responseManager.getRMapStatement(stmtUri, "TURTLE");
    	return rdfXmlStmt;
    }
    
    
/*
 *-------------------------------
 *
 *		GET STATEMENT HEADER
 * 
 *-------------------------------
 */
	/**
	 * HEAD /stmt/{stmtUri}
     * Returns status information for specific RMap:Statement as a HTTP response header. 
     * Includes event list and URI
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/{stmtUri}")
    public Response getRMapStmtHeader(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapStatementHeader(stmtUri);
    	return eventList;
    }
    
/*
 * ------------------------------
 * 
 *  	  GET STATEMENT ID
 *  
 *-------------------------------
 */
	/**
	 * GET /stmt/{subject}/{predicate}/{object}
	 * Returns RMap:Statement URI for subject, predicate, object
     * @param subject
     * @param predicate
     * @param object
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{subject}/{predicate}/{object}")
    @Produces("text/plain")
    public Response getRMapstmtUriAsTEXT(@PathParam("subject") String subject, 
    									@PathParam("predicate") String predicate,
    									@PathParam("object") String object) throws RMapApiException {
    	Response stmtUriResponse = responseManager.getRMapStatementID(subject, predicate, object);
    	return stmtUriResponse;
    }
    
/*
 * ------------------------------
 * 
 *	  GET RELATED EVENT LIST
 *  
 *-------------------------------
 */
    
	/**
	 * GET /stmt/{stmtUri}/events
	 * Returns list of RMap:Event URIs related to the RMap:Statement URI as JSON
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{stmtUri}/events")
	@Produces("application/json")
    public Response getRMapStmtEventsAsJSON(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapStatementRelatedEvents(stmtUri, ListType.JSON);
    	return eventList;
    }

	/**
	 * GET /stmt/{stmtUri}/events
	 * Returns list of RMap:Event URIs related to the RMap:Statement URI as plain text
	 * @param stmtUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{stmtUri}/events")
    @Produces("text/plain")
    public Response getRMapStmtEventsAsText(@PathParam("stmtUri") String stmtUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapStatementRelatedEvents(stmtUri, ListType.PLAIN_TEXT);
    	return eventList;
    }

       
}