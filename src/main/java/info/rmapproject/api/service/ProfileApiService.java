package info.rmapproject.api.service;

import info.rmapproject.api.responsemgr.ProfileResponseManager;

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

	protected static ProfileResponseManager responseManager = null;
	
	static{
		try {
			responseManager = new ProfileResponseManager();
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
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","vnd.rmap-project.profile+rdf+xml;charset=UTF-8;"})
    public Response getRMapProfileAsRdfXml(@PathParam("profileid") String profileId) {
    	Response rdfXMLStmt = responseManager.getRMapProfile(profileId, "RDFXML");
	    return rdfXMLStmt;
    }
    
    @GET
    @Path("/{profileid}")
    @Produces({"application/ld+json;charset=UTF-8;","vnd.rmap-project.profile+ld+json;charset=UTF-8;"})
    public Response getRMapProfileAsJsonLD(@PathParam("profileid") String profileId) {
    	Response rdfJsonStmt = responseManager.getRMapProfile(profileId, "JSONLD");
    	return rdfJsonStmt;
    }

    @GET
    @Path("/{profileid}")
    @Produces({"application/n-quads;charset=UTF-8;","vnd.rmap-project.profile+n-quads;charset=UTF-8;"})
    public Response getRMapProfileAsRdfNQuads(@PathParam("profileid") String profileId) {
    	Response rdfNquadsStmt = responseManager.getRMapProfile(profileId, "RDFNQUADS");
    	return rdfNquadsStmt;
    }    
    
    @GET
    @Path("/{profileid}")
    @Produces({"text/turtle;charset=UTF-8;","vnd.rmap-project.profile+turtle;charset=UTF-8;"})
    public Response getRMapProfileAsTurtle(@PathParam("profileid") String profileId) {
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
    @Consumes({"application/rdf+xml;charset=UTF-8;","vnd.rmap-project.profile+rdf+xml;charset=UTF-8;"})
    public Response createRMapProfileFromRdfXml(InputStream profileRdf) {
    	Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFXML");
		return createResponse;
    }	
    
	@POST
	@Path("/")
	@Consumes({"application/ld+json;charset=UTF-8;","vnd.rmap-project.profile+ld+json;charset=UTF-8;"})
	public Response createRMapProfileFromJsonLD(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "JSONLD");
		return createResponse;
	}
    
	@POST
	@Path("/")
	@Consumes({"application/n-quads;charset=UTF-8;","vnd.rmap-project.profile+n-quads;charset=UTF-8;"})
	public Response createRMapProfileFromNquads(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFNQUADS");
		return createResponse;
	}

	@POST
	@Path("/")
	@Consumes({"text/turtle;charset=UTF-8;","vnd.rmap-project.profile+turtle;charset=UTF-8;"})
	public Response createRMapProfileFromTurtle(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "TURTLE");
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