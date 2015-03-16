package info.rmapproject.api.service;

import info.rmapproject.api.responsemgr.ResponseManager;
import info.rmapproject.api.responsemgr.ResponseManagerFactoryIOC;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * 
 * @author khanson
 * API service for RMap DiSCO 
 *
 */

@Path("/disco")
public class DiSCOApiService {

	protected static ResponseManager responseManager = null;
	
	static{
		try {
			responseManager = ResponseManagerFactoryIOC.getFactory().createService();
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
    	Response response = responseManager.getDiSCOServiceOptions();
	    return response;
    }
        
    /**
     * 
     * @return HTTP Response
     * Returns link to DiSCO API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getDiscoApiDetails()	{
    	Response response = responseManager.getDiSCOServiceHead();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns DiSCO API information/link, and lists HTTP options
     * 
     */
    
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getDiscoApiDetailedOptions()	{
    	Response response = responseManager.getDiSCOServiceOptions();
	    return response;

    }    
    
  
    /**
     * 
     * @param discoId
     * @return HTTP Response 
     * Read DiSCO with output in various formats
     * 
     */
    
    @GET
    @Path("/{discoid}")
    @Produces("application/xml;charset=UTF-8;")
    public Response getRMapDiSCOAsHTML(@PathParam("discoid") String discoId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfXMLStmt = responseManager.getRMapDiSCO(discoId, "RDFXML");
	    return rdfXMLStmt;
    }
    
    @GET
    @Path("/{discoid}")
    @Produces("application/ld+json;charset=UTF-8;")
    public Response getRMapDiSCOAsRDFJSON(@PathParam("discoid") String discoId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfJsonStmt = responseManager.getRMapDiSCO(discoId, "JSONLD");
    	return rdfJsonStmt;
    }

    @GET
    @Path("/{discoid}")
    @Produces("application/n-quads;charset=UTF-8;")
    public Response getRMapDiSCOAsRDFNQUADS(@PathParam("discoid") String discoId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfNquadsStmt = responseManager.getRMapDiSCO(discoId, "RDFNQUADS");
    	return rdfNquadsStmt;
    }    
    
    @GET
    @Path("/{discoid}")
    @Produces("application/rdf+xml;charset=UTF-8;")
    public Response getRMapDiSCOAsRDFXML(@PathParam("discoid") String discoId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfXmlStmt = responseManager.getRMapDiSCO(discoId, "RDFXML");
    	return rdfXmlStmt;
    }
    	
    
    /**
     * 
     * @param discoRdf
     * @return HTTP Response 
     * 
     * Post new DiSCO with input in various formats
     * 
     */   
    
    @POST
    @Path("/")
    @Consumes("application/xml;charset=UTF-8;")
    public Response createRMapDiSCOFromXML(InputStream discoRdf) {
    	Response createResponse = responseManager.createRMapDiSCO(discoRdf, "RDFXML");
		return createResponse;
    }	
    
	@POST
	@Path("/")
	@Consumes("application/ld+json;charset=UTF-8;")
	public Response createRMapDiSCOFromJsonLD(InputStream discoRdf) {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "JSONLD");
		return createResponse;
	}
    
	@POST
	@Path("/")
	@Consumes("application/n-quads;charset=UTF-8;")
	public Response createRMapDiSCOFromNquads(InputStream discoRdf) {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "RDFNQUADS");
		return createResponse;
	}

	@POST
	@Path("/")
	@Consumes("application/rdf+xml;charset=UTF-8;")
	public Response createRMapDiSCOFromRdfXml(InputStream discoRdf) {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "RDFXML");
		return createResponse;
	}
	
    /**
     * 
     * @param discoRdf
     * @return HTTP Response 
     * Post new version to existing DiSCO with input in various formats
     * 
     */
    @POST
    @Path("/{discoid}")
    @Consumes("application/xml;charset=UTF-8;")
    public Response updateRMapDiSCOFromXML(@PathParam("discoid") String origDiscoId, InputStream discoRdf) {
    	Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFXML");
		return updateResponse;
    }	
    
	@POST
	@Path("/{discoid}")
	@Consumes("application/ld+json;charset=UTF-8;")
	public Response updateRMapDiSCOFromJsonLD(@PathParam("discoid") String origDiscoId, InputStream discoRdf) {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "JSONLD");
		return updateResponse;
	}
    
	@POST
	@Path("/{discoid}")
	@Consumes("application/n-quads;charset=UTF-8;")
	public Response updateRMapDiSCOFromNquads(@PathParam("discoid") String origDiscoId, InputStream discoRdf) {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFNQUADS");
		return updateResponse;
	}
    
	@POST
	@Path("/{discoid}")
	@Consumes("application/rdf+xml;charset=UTF-8;")
	public Response updateRMapDiSCOFromRdfXml(@PathParam("discoid") String origDiscoId, InputStream discoRdf) {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFXML");
		return updateResponse;
	}
  	
}