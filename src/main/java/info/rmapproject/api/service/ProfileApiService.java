package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.BasicOutputType;
import info.rmapproject.api.responsemgr.ProfileResponseManager;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * 
 * API service for RMap Profile 
 * @author khanson
 *
 */

@Path("/profile")
public class ProfileApiService {

	protected static ProfileResponseManager responseManager = null;	
	static{
		try {
			responseManager = new ProfileResponseManager();
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
	 * GET /profile
     * Returns link to Profile API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getProfileServiceOptions();
	    return response;
    }
        
	/**
	 * HEAD /profile
     * Returns Profile API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/")
    public Response getProfileApiDetails() throws RMapApiException {
    	Response response = responseManager.getProfileServiceHead();
	    return response;
    }
    
	/**
	 * OPTIONS /profile
     * Returns Profile API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getProfileApiDetailedOptions() throws RMapApiException {
    	Response response = responseManager.getProfileServiceOptions();
	    return response;

    }    
    
  

/*
 * ------------------------------
 * 
 *  	  GET PROFILE RDF
 *  
 *-------------------------------
 */    

	/**
	 * GET /profile/{profileUri}
	 * Returns requested RMap:Profile as RDF/XML
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{profileUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","application/vnd.rmap-project.profile+rdf+xml;charset=UTF-8;"})
    public Response getRMapProfileAsRdfXml(@PathParam("profileUri") String profileUri) {
    	Response rdfXMLStmt = responseManager.getRMapProfile(profileUri, "RDFXML");
	    return rdfXMLStmt;
    }
    
	/**
	 * GET /profile/{profileUri}
	 * Returns requested RMap:Profile as JSON-LD
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */ 
    @GET
    @Path("/{profileUri}")
    @Produces({"application/ld+json;charset=UTF-8;","application/vnd.rmap-project.profile+ld+json;charset=UTF-8;"})
    public Response getRMapProfileAsJsonLD(@PathParam("profileUri") String profileUri) {
    	Response rdfJsonStmt = responseManager.getRMapProfile(profileUri, "JSONLD");
    	return rdfJsonStmt;
    }
    
	/**
	 * GET /profile/{profileUri}
	 * Returns requested RMap:Profile as NQUADS
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */ 
    @GET
    @Path("/{profileUri}")
    @Produces({"application/n-quads;charset=UTF-8;","application/vnd.rmap-project.profile+n-quads;charset=UTF-8;"})
    public Response getRMapProfileAsRdfNQuads(@PathParam("profileUri") String profileUri) {
    	Response rdfNquadsStmt = responseManager.getRMapProfile(profileUri, "RDFNQUADS");
    	return rdfNquadsStmt;
    }  
    
	/**
	 * GET /profile/{profileUri}
	 * Returns requested RMap:Profile as TURTLE
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */ 
    @GET
    @Path("/{profileUri}")
    @Produces({"text/turtle;charset=UTF-8;","application/vnd.rmap-project.profile+turtle;charset=UTF-8;"})
    public Response getRMapProfileAsTurtle(@PathParam("profileUri") String profileUri) {
    	Response rdfXmlStmt = responseManager.getRMapProfile(profileUri, "TURTLE");
    	return rdfXmlStmt;
    }
    	
    
/*
 * ------------------------------
 * 
 *  	 CREATE NEW PROFILES
 *  
 *-------------------------------
 */ 
    
	/**
	 * POST /profile/
	 * Creates new Profile from RDF/XML
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @POST
    @Path("/")
    @Consumes({"application/rdf+xml;charset=UTF-8;","application/vnd.rmap-project.profile+rdf+xml;charset=UTF-8;"})
    public Response createRMapProfileFromRdfXml(InputStream profileRdf) {
    	Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFXML");
		return createResponse;
    }	
    
	/**
	 * POST /profile/
	 * Creates new Profile from JSON-LD
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
	@Consumes({"application/ld+json;charset=UTF-8;","application/vnd.rmap-project.profile+ld+json;charset=UTF-8;"})
	public Response createRMapProfileFromJsonLD(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "JSONLD");
		return createResponse;
	}
    
	/**
	 * POST /profile/
	 * Creates new Profile from NQUADS
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
	@Consumes({"application/n-quads;charset=UTF-8;","application/vnd.rmap-project.profile+n-quads;charset=UTF-8;"})
	public Response createRMapProfileFromNquads(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "RDFNQUADS");
		return createResponse;
	}

	/**
	 * POST /profile/
	 * Creates new Profile from TURTLE
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
	@Consumes({"text/turtle;charset=UTF-8;","application/vnd.rmap-project.profile+turtle;charset=UTF-8;"})
	public Response createRMapProfileFromTurtle(InputStream profileRdf) {
		Response createResponse = responseManager.createRMapProfile(profileRdf, "TURTLE");
		return createResponse;
	}
	

/*
 * ------------------------------
 * 
 *	  GET RELATED EVENT LIST
 *  
 *-------------------------------
 */
    
	/**
	 * GET /profile/{profileUri}/events
	 * Returns list of RMap:Event URIs related to the Profile URI as JSON
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{profileUri}/events")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapProfileEventListAsJSon(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapProfileEvents(profileUri, BasicOutputType.JSON);
	    return eventList;
    }
    

	/**
	 * GET /profile/{profileUri}/events
	 * Returns list of RMap:Event URIs related to the Profile URI as plain text
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{profileUri}/events")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapProfileEventListAsText(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapProfileEvents(profileUri, BasicOutputType.PLAIN_TEXT);
	    return eventList;
    }
	
/*
 * ------------------------------
 * 
 *	  CHANGE PROFILE STATUS
 *  
 *-------------------------------
 */
    
	/**
	 * DELETE /profile/{profileUri}
	 * Sets status of target RMap:Profile to "tombstoned".  It will still be stored in the triplestore
	 * but won't be visible through the API.
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @DELETE
    @Path("/{profileUri}")
    public Response deleteRMapProfile(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response response = responseManager.tombstoneRMapProfile(profileUri);
	    return response;
    }

	/**
	 * PUT /profile/{profileUri}
	 * Sets status of target RMap:Profile to "inactive".  It will still be stored in the triplestore
	 * and will still be visible through the API for certain requests.
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    //TODO:using PUT temporarily to distinguish but we didn't decide on an HTTP verb for this update.
    @PUT
    @Path("/{profileUri}")
    public Response inactivateRMapProfile(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response response = responseManager.inactivateRMapProfile(profileUri);
	    return response;
    }

/*
 * ------------------------------
 * 
 *	  GET RELATED IDENTITIES
 *  
 *-------------------------------
 */
    
	/**
	 * GET /profile/{profileUri}/identities
	 * Returns list of Identity URIs related to the Profile URI as JSON
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{profileUri}/identities")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapProfileIdentitiesListAsJSon(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response response = responseManager.getRMapProfileRelatedIdentities(profileUri, BasicOutputType.JSON);
	    return response;
    }

	/**
	 * GET /profile/{profileUri}/events
	 * Returns list of Identity URIs related to the Profile URI as plain text
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{profileUri}/identities")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapProfileIdentitiesListAsText(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response response = responseManager.getRMapProfileRelatedIdentities(profileUri, BasicOutputType.PLAIN_TEXT);
	    return response;
    }
    

	/**
	 * GET /profile/{profileUri}/preferredid
	 * Returns preferred identity URI for the Profile URI as plain text
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{profileUri}/preferredid")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapProfilePreferredIdentity(@PathParam("profileUri") String profileUri) throws RMapApiException {
    	Response response = responseManager.getRMapProfilePreferredIdentity(profileUri);
	    return response;
    }
}