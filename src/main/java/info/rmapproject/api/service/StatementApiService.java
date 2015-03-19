package info.rmapproject.api.service;

import info.rmapproject.api.responsemgr.StatementResponseManager;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author khanson
 * API service for RMap Stmts
 */

@Path("/stmt")
public class StatementApiService {

	protected static StatementResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new StatementResponseManager();
		}
		catch (Exception e){}
	}
	
	@Context
	UriInfo uriInfo;
	//    	String path = uri.getPath();
		
	
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getStatementServiceOptions();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns link to Stmt API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getStmtApiDetails()	{
    	Response response = responseManager.getStatementServiceHead();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns Stmt API information/link, and lists HTTP options
     * 
     */
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getStmtApiDetailedOptions()	{
    	Response response = responseManager.getStatementServiceOptions();
	    return response;

    }
        
    
    /**
     * 
     * @param stmtId
     * @return HTTP Response 
     * Read Stmt with output in various formats
     * 
     */

    @GET
    @Path("/{stmtid}")
    @Produces("application/xml;charset=UTF-8;")
    public Response getRMapStmtAsHTML(@PathParam("stmtid") String stmtId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfXMLStmt = responseManager.getRMapStatement(stmtId, "RDFXML");
	    return rdfXMLStmt;
    }
    
    @GET
    @Path("/{stmtid}")
    @Produces("application/ld+json;charset=UTF-8;")
    public Response getRMapStmtAsRDFJSON(@PathParam("stmtid") String stmtId){
    	Response rdfJsonStmt = responseManager.getRMapStatement(stmtId, "JSONLD");
    	return rdfJsonStmt;
    }

    @GET
    @Path("/{stmtid}")
    @Produces("application/n-quads;charset=UTF-8;")
    public Response getRMapStmtAsRDFNQUADS(@PathParam("stmtid") String stmtId) {
    	Response rdfNquadsStmt = responseManager.getRMapStatement(stmtId, "RDFNQUADS");
    	return rdfNquadsStmt;
    }    
    
    @GET
    @Path("/{stmtid}")
    @Produces("application/rdf+xml;charset=UTF-8;")
    public Response getRMapStmtAsRDFXML(@PathParam("stmtid") String stmtId) {
    	Response rdfXmlStmt = responseManager.getRMapStatement(stmtId, "RDFXML");
    	return rdfXmlStmt;
    }
       
}