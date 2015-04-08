package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.responsemgr.DiscoResponseManager;
import info.rmapproject.api.utils.ListType;

import java.io.InputStream;
import java.util.List;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * API service for RMap DiSCO 
 * @author khanson
 *
 */

@Path("/disco")
public class DiSCOApiService {

	protected static DiscoResponseManager responseManager = null;
	static{
		try {
			responseManager = new DiscoResponseManager();
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
	 * GET /disco
     * Returns link to DiSCO API information, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/")
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = responseManager.getDiSCOServiceOptions();
	    return response;
    }
    
    
	/**
	 * HEAD /disco
     * Returns DiSCO API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/")
    public Response getDiSCOApiDetails() throws RMapApiException {
    	Response response = responseManager.getDiSCOServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /disco
     * Returns DiSCO API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    @Path("/")
    public Response getDiSCOApiDetailedOptions() throws RMapApiException {
    	Response response = responseManager.getDiSCOServiceHead();
	    return response;
    }
    
    

/*
 * ------------------------------
 * 
 *  	  GET DISCO RDF
 *  
 *-------------------------------
 */
    

	/**
	 * GET /disco/{discoUri}
	 * Returns requested RMap:DiSCO as RDF/XML, NQUADS, TURTLE or JSON-LD
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */  
    @GET
    @Path("/{discoUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;", "vnd.rmap-project.disco+rdf+xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "vnd.rmap-project.disco+ld+json;charset=UTF-8;",
				"application/n-quads;charset=UTF-8;", "vnd.rmap-project.disco+n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;", "vnd.rmap-project.disco+turtle;charset=UTF-8;"
				})
    public Response test(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response=null;
    	
    	List<MediaType> acceptTypes=headers.getAcceptableMediaTypes();
    	
    	//List <String> acceptList = headers.getRequestHeader(HttpHeaders.ACCEPT);
    
    	if (acceptTypes.contains("application/ld+json")
    			|| acceptTypes.contains("vnd.rmap-project.disco+ld+json")) {
    		response = responseManager.getRMapDiSCO(discoUri, "JSONLD");    		
    	}
    	else if (acceptTypes.contains("application/n-quads")
    			|| acceptTypes.contains("vnd.rmap-project.disco+n-quads")) {
    		response = responseManager.getRMapDiSCO(discoUri, "RDFNQUADS");    		
    	}
    	else if (acceptTypes.contains("text/turtle")
    			|| acceptTypes.contains("vnd.rmap-project.disco+turtle")) {
    		response = responseManager.getRMapDiSCO(discoUri, "TURTLE");    		
    	}
    	else if (acceptTypes.contains("application/rdf+xml")
    			|| acceptTypes.contains(MediaType.APPLICATION_XML)
    			|| acceptTypes.contains("vnd.rmap-project.disco+rdf+xml"))	{
    		response = responseManager.getRMapDiSCO(discoUri, "RDFXML");
    	}
    	else { //use RDF/XML as default
    		response = responseManager.getRMapDiSCO(discoUri, "RDFXML");
    	}
    	
    	return response;
    }
    
    
	/**
	 * GET /disco/{discoUri}
	 * Returns requested RMap:DiSCO as RDF/XML
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    /*
    @GET
    @Path("/{discoUri}")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","vnd.rmap-project.disco+rdf+xml;charset=UTF-8;"})
    public Response getRMapDiSCOAsRdfXml(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getRMapDiSCO(discoUri, "RDFXML");
	    return rdfDiSCO;
    }
    */
    
	/**
	 * GET /disco/{discoUri}
	 * Returns requested RMap:DiSCO as JSON-LD
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */ 
    /*
    @GET
    @Path("/{discoUri}")
    @Produces({"application/ld+json;charset=UTF-8;","vnd.rmap-project.disco+ld+json;charset=UTF-8;"})
    public Response getRMapDiSCOAsRDFJSON(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getRMapDiSCO(discoUri, "JSONLD");
	    return rdfDiSCO;
    }
    */
    
	/**
	 * GET /disco/{discoUri}
	 * Returns requested RMap:DiSCO as NQUADS
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    /*
    @GET
    @Path("/{discoUri}")
    @Produces({"application/n-quads;charset=UTF-8;","vnd.rmap-project.disco+n-quads;charset=UTF-8;"})
    public Response getRMapDiSCOAsRDFNQUADS(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getRMapDiSCO(discoUri, "RDFNQUADS");
	    return rdfDiSCO;
    } 
    */   
    
	/**
	 * GET /disco/{discoUri}
	 * Returns requested RMap:DiSCO as TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    /*
    @GET
    @Path("/{discoUri}")
    @Produces({"text/turtle;charset=UTF-8;","vnd.rmap-project.disco+turtle;charset=UTF-8;"})
    public Response getRMapDiSCOAsTurtle(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getRMapDiSCO(discoUri, "TURTLE");
	    return rdfDiSCO;
    }
    */

/*
 *-------------------------------
 *
 *	 GET LATEST DISCO VERSION
 * 
 *-------------------------------
 */

	/**
	 * GET /disco/{discoUri}/latest
	 * Returns latest version of requested RMap:DiSCO as RDF/XML
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/latest")
    @Produces({"application/rdf+xml;charset=UTF-8;","application/xml;charset=UTF-8;","vnd.rmap-project.disco+rdf+xml;charset=UTF-8;"})
    public Response getLatestRMapDiSCOAsRdfXml(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getLatestRMapDiSCOVersion(discoUri, "RDFXML");
	    return rdfDiSCO;
    }
    
	/**
	 * GET /disco/{discoUri}/latest
	 * Returns latest version of requested RMap:DiSCO as JSON-LD
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/latest")
    @Produces({"application/ld+json;charset=UTF-8;","vnd.rmap-project.disco+ld+json;charset=UTF-8;"})
    public Response getLatestRMapDiSCOAsRDFJSON(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getLatestRMapDiSCOVersion(discoUri, "JSONLD");
	    return rdfDiSCO;
    }
    
	/**
	 * GET /disco/{discoUri}/latest
	 * Returns latest version of requested RMap:DiSCO as NQUADS
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/latest")
    @Produces({"application/n-quads;charset=UTF-8;","vnd.rmap-project.disco+n-quads;charset=UTF-8;"})
    public Response getRMapLatestDiSCOAsRDFNQUADS(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getLatestRMapDiSCOVersion(discoUri, "RDFNQUADS");
	    return rdfDiSCO;
    }    
    
	/**
	 * GET /disco/{discoUri}/latest
	 * Returns latest version of requested RMap:DiSCO as RDF/XML
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/latest")
    @Produces({"text/turtle;charset=UTF-8;","vnd.rmap-project.disco+turtle;charset=UTF-8;"})
    public Response getRMapLatestDiSCOAsTurtle(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response rdfDiSCO = responseManager.getLatestRMapDiSCOVersion(discoUri, "TURTLE");
	    return rdfDiSCO;
    }
    
    
    
/*
 *-------------------------------
 *
 *		GET DISCO HEADER
 * 
 *-------------------------------
 */
	/**
	 * HEAD /disco/{discoUri}
     * Returns status information for specific DiSCO as a HTTP response header. 
     * Includes event list, versions, and URI
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    @Path("/{discoUri}")
    public Response getDiSCOStatus(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = responseManager.getRMapDiSCOHeader(discoUri);
	    return response;
    }

    
/*
 * ------------------------------
 * 
 *  	 CREATE NEW DISCOS
 *  
 *-------------------------------
 */ 
    
	/**
	 * POST /disco/
	 * Creates new DiSCO from RDF/XML
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @POST
    @Path("/")
    @Consumes({"application/rdf+xml;charset=UTF-8;","vnd.rmap-project.disco+rdf+xml;charset=UTF-8;"})
    public Response createRMapDiSCOFromRdfXml(InputStream discoRdf) throws RMapApiException {
    	Response createResponse = responseManager.createRMapDiSCO(discoRdf, "RDFXML");
		return createResponse;
    }	

	/**
	 * POST /disco/
	 * Creates new DiSCO from JSON-LD
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
	@Consumes({"application/ld+json;charset=UTF-8;","vnd.rmap-project.disco+ld+json;charset=UTF-8;"})
	public Response createRMapDiSCOFromJsonLD(InputStream discoRdf) throws RMapApiException {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "JSONLD");
		return createResponse;
	}

	/**
	 * POST /disco/
	 * Creates new DiSCO from NQUADS
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
    @Consumes({"application/n-quads;charset=UTF-8;","vnd.rmap-project.disco+n-quads;charset=UTF-8;"})
	public Response createRMapDiSCOFromNquads(InputStream discoRdf) throws RMapApiException {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "RDFNQUADS");
		return createResponse;
	}

	/**
	 * POST /disco/
	 * Creates new DiSCO from TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/")
    @Consumes({"text/turtle;charset=UTF-8;","vnd.rmap-project.disco+turtle;charset=UTF-8;"})
	public Response createRMapDiSCOFromTurtle(InputStream discoRdf) throws RMapApiException {
		Response createResponse = responseManager.createRMapDiSCO(discoRdf, "TURTLE");
		return createResponse;
	}
	
	
	
	
/*
 * ------------------------------
 * 
 *  	UPDATE DISCO
 *  
 *-------------------------------
 */ 

	/**
	 * POST /disco/{discoid}
	 * Sets original DiSCO as inactive and creates a new DiSCO from RDF/XML
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @POST
    @Path("/{discoid}")
    @Consumes({"application/rdf+xml;charset=UTF-8;","vnd.rmap-project.disco+rdf+xml;charset=UTF-8;"})
    public Response updateRMapDiSCOFromXML(@PathParam("discoid") String origDiscoId, InputStream discoRdf) throws RMapApiException {
    	Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFXML");
		return updateResponse;
    }	

	/**
	 * POST /disco/{discoid}
	 * Sets original DiSCO as inactive and creates a new DiSCO from JSON-LD
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/{discoid}")
	@Consumes({"application/ld+json;charset=UTF-8;","vnd.rmap-project.disco+ld+json;charset=UTF-8;"})
	public Response updateRMapDiSCOFromJsonLD(@PathParam("discoid") String origDiscoId, InputStream discoRdf) throws RMapApiException {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "JSONLD");
		return updateResponse;
	}

	/**
	 * POST /disco/{discoid}
	 * Sets original DiSCO as inactive and creates a new DiSCO from NQUADS
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/{discoid}")
	@Consumes({"application/n-quads;charset=UTF-8;","vnd.rmap-project.disco+n-quads;charset=UTF-8;"})
	public Response updateRMapDiSCOFromNquads(@PathParam("discoid") String origDiscoId, InputStream discoRdf) throws RMapApiException {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFNQUADS");
		return updateResponse;
	}

	/**
	 * POST /disco/{discoid}
	 * Sets original DiSCO as inactive and creates a new DiSCO from TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	@POST
	@Path("/{discoid}")
	@Consumes({"text/turtle;charset=UTF-8;","vnd.rmap-project.disco+turtle;charset=UTF-8;"})
	public Response updateRMapDiSCOFromRdfXml(@PathParam("discoid") String origDiscoId, InputStream discoRdf) throws RMapApiException {
		Response updateResponse = responseManager.updateRMapDiSCO(origDiscoId, discoRdf, "RDFXML");
		return updateResponse;
	}

/*
 * ------------------------------
 * 
 *	  GET RELATED EVENT LIST
 *  
 *-------------------------------
 */
    
	/**
	 * GET /disco/{discoUri}/events
	 * Returns list of RMap:Event URIs related to the DiSCO URI as JSON
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/events")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapDiSCOEventListAsJSon(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapDiSCOEvents(discoUri, ListType.JSON);
	    return eventList;
    }
    

	/**
	 * GET /disco/{discoUri}/events
	 * Returns list of RMap:Event URIs related to the DiSCO URI as plain text
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/events")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapDiSCOEventListAsText(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response eventList = responseManager.getRMapDiSCOEvents(discoUri, ListType.PLAIN_TEXT);
	    return eventList;
    }
	
/*
 * ------------------------------
 * 
 *	  CHANGE DISCO STATUS
 *  
 *-------------------------------
 */
    
	/**
	 * DELETE /disco/{discoUri}
	 * Sets status of target RMap:DiSCO to "tombstoned".  It will still be stored in the triplestore
	 * but won't be visible through the API.
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @DELETE
    @Path("/{discoUri}")
    public Response deleteRMapDiSCO(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = responseManager.tombstoneRMapDiSCO(discoUri);
	    return response;
    }

	/**
	 * PUT /disco/{discoUri}
	 * Sets status of target RMap:DiSCO to "inactive".  It will still be stored in the triplestore
	 * and will still be visible through the API for certain requests.
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    //TODO:using PUT temporarily to distinguish but we didn't decide on an HTTP verb for this update.
    @PUT
    @Path("/{discoUri}")
    public Response inactivateRMapDiSCO(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = responseManager.inactivateRMapDiSCO(discoUri);
	    return response;
    }
    
/*
 * ------------------------------
 * 
 *	  GET DISCO VERSION LISTS
 *  
 *-------------------------------
 */
    
	/**
	 * GET /disco/{discoUri}/allversions
	 * Returns list of all RMap:DiSCO version URIs as JSON
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/allversions")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapDiSCOVersionListAsJSon(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response versionList = responseManager.getRMapDiSCOVersions(discoUri, ListType.JSON, false);
	    return versionList;
    }
    

	/**
	 * GET /disco/{discoUri}/allversions
	 * Returns list of all RMap:DiSCO version URIs as plain text
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/allversions")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapDiSCOVersionListAsText(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response versionList = responseManager.getRMapDiSCOVersions(discoUri, ListType.PLAIN_TEXT, false);
	    return versionList;
    }

    
	/**
	 * GET /disco/{discoUri}/agentversions
	 * Returns list of discoUri agent's RMap:DiSCO version URIs as JSON
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/agentversions")
    @Produces("application/json;charset=UTF-8;")
    public Response getRMapDiSCOAgentVersionListAsJSon(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response versionList = responseManager.getRMapDiSCOVersions(discoUri, ListType.JSON, true);
	    return versionList;
    }
    

	/**
	 * GET /disco/{discoUri}/agentversions
	 * Returns list of discoUri agent's RMap:DiSCO version URIs as plain text
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @GET
    @Path("/{discoUri}/agentversions")
    @Produces("text/plain;charset=UTF-8;")
    public Response getRMapDiSCOAgentVersionListAsText(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response versionList = responseManager.getRMapDiSCOVersions(discoUri, ListType.PLAIN_TEXT, true);
	    return versionList;
    }
}