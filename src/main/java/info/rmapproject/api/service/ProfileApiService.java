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
 * API service for RMap Profile 
 *
 */

@Path("/profile")
public class ProfileApiService {

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
    	Response response = responseManager.getProfileServiceOptions();
	    return response;
    }
        
    /**
     * 
     * @return HTTP Response
     * Returns link to Profile API information, and lists HTTP options
     * 
     */
    @HEAD
    @Path("/")
    public Response getProfileApiDetails()	{
    	Response response = responseManager.getProfileServiceHead();
	    return response;
    }
    
    /**
     * 
     * @return HTTP Response
     * Returns Profile API information/link, and lists HTTP options
     * 
     */
    
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getProfileApiDetailedOptions()	{
    	Response response = responseManager.getProfileServiceOptions();
	    return response;

    }    
    
  
    /**
     * 
     * @param profileId
     * @return HTTP Response 
     * Read Profile with output in various formats
     * 
     */
    
    @GET
    @Path("/{profileid}")
    @Produces("application/xml;charset=UTF-8;")
    public Response getRMapProfileAsHTML(@PathParam("profileid") String profileId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfXMLStmt = responseManager.getRMapProfile(profileId, "RDFXML");
	    return rdfXMLStmt;
    }
    
    @GET
    @Path("/{profileid}")
    @Produces("application/ld+json;charset=UTF-8;")
    public Response getRMapProfileAsRDFJSON(@PathParam("profileid") String profileId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfJsonStmt = responseManager.getRMapProfile(profileId, "JSONLD");
    	return rdfJsonStmt;
    }

    @GET
    @Path("/{profileid}")
    @Produces("application/n-quads;charset=UTF-8;")
    public Response getRMapProfileAsRDFNQUADS(@PathParam("profileid") String profileId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfNquadsStmt = responseManager.getRMapProfile(profileId, "RDFNQUADS");
    	return rdfNquadsStmt;
    }    
    
    @GET
    @Path("/{profileid}")
    @Produces("application/rdf+xml;charset=UTF-8;")
    public Response getRMapProfileAsRDFXML(@PathParam("profileid") String profileId) {
    	//TODO: need to add magic here to use uri.getPath and determine how many forward-slashes are in the URI used.
    	Response rdfXmlStmt = responseManager.getRMapProfile(profileId, "RDFXML");
    	return rdfXmlStmt;
    }
    	
    
    /**
     * 
     * @param profileRdf
     * @return HTTP Response 
     * 
     * Post new Profile with input in various formats
     * 
     */   
    
    @POST
    @Path("/")
    @Consumes("application/xml;charset=UTF-8;")
    public Response createRMapProfileFromXML(InputStream profileRdf) {
    	Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFXML");
		return createResponse;
    }	
    
	@POST
	@Path("/")
	@Consumes("application/ld+json;charset=UTF-8;")
	public Response createRMapProfileFromJsonLD(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "JSONLD");
		return createResponse;
	}
    
	@POST
	@Path("/")
	@Consumes("application/n-quads;charset=UTF-8;")
	public Response createRMapProfileFromNquads(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFNQUADS");
		return createResponse;
	}

	@POST
	@Path("/")
	@Consumes("application/rdf+xml;charset=UTF-8;")
	public Response createRMapProfileFromRdfXml(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFXML");
		return createResponse;
	}
	
    /**
     * 
     * @param profileRdf
     * @return HTTP Response 
     * Post new version to existing Profile with input in various formats
     * 
     */
    @POST
    @Path("/{profileid}")
    @Consumes("application/xml;charset=UTF-8;")
    public Response updateRMapProfileFromXML(@PathParam("profileid") String origProfileId, InputStream profileRdf) {
    	Response updateResponse = responseManager.updateRMapProfile(origProfileId, profileRdf, "RDFXML");
		return updateResponse;
    }	
    
	@POST
	@Path("/{profileid}")
	@Consumes("application/ld+json;charset=UTF-8;")
	public Response updateRMapProfileFromJsonLD(@PathParam("profileid") String origProfileId, InputStream profileRdf) {
		Response updateResponse = responseManager.updateRMapProfile(origProfileId, profileRdf, "JSONLD");
		return updateResponse;
	}
    
	@POST
	@Path("/{profileid}")
	@Consumes("application/n-quads;charset=UTF-8;")
	public Response updateRMapProfileFromNquads(@PathParam("profileid") String origProfileId, InputStream profileRdf) {
		Response updateResponse = responseManager.updateRMapProfile(origProfileId, profileRdf, "RDFNQUADS");
		return updateResponse;
	}
    
	@POST
	@Path("/{profileid}")
	@Consumes("application/rdf+xml;charset=UTF-8;")
	public Response updateRMapProfileFromRdfXml(@PathParam("profileid") String origProfileId, InputStream profileRdf) {
		Response updateResponse = responseManager.updateRMapProfile(origProfileId, profileRdf, "RDFXML");
		return updateResponse;
	}
  	
}