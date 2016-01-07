package info.rmapproject.api.service;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.responsemgr.DiscoResponseManager;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.auth.service.AuthService;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Required;


/**
 * 
 * API service for RMap DiSCO 
 * @author khanson
 *
 */

@Path("/discos")
public class DiSCOApiService {

	private DiscoResponseManager discoResponseManager;	
	private AuthService authService;
    
    @Required
    public void setAuthService(AuthService authService) throws RMapApiException {
    	if (authService==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_AUTHMOD);			
    	} else {
            this.authService = authService;
		}
    }
    
    @Required
    public void setDiscoResponseManager(DiscoResponseManager discoResponseManager) throws RMapApiException {
    	if (discoResponseManager==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_RESP_MGR);			
    	} else {
    		this.discoResponseManager = discoResponseManager;
		}
	}
			
    private URI getSysAgentId() throws RMapApiException {
    	URI sysAgentId = null; 
		try {
			Message message = JAXRSUtils.getCurrentMessage();
			//String username = userInfo.getUserPrincipal().getName();
		    AuthorizationPolicy policy = (AuthorizationPolicy)message.get(AuthorizationPolicy.class);
	    	sysAgentId = authService.getAgentUriByKeySecret(policy.getUserName(),policy.getPassword());
		}
		catch(Exception ex){
			throw new RMapApiException(ex, ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED);
		}
		return sysAgentId;
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
    @Produces("application/json;charset=UTF-8;")
    public Response getServiceInfo() throws RMapApiException {
    	//TODO: for now returns same as options, but might want html response to describe API?
    	Response response = discoResponseManager.getDiSCOServiceOptions();
	    return response;
    }
    
    
	/**
	 * HEAD /disco
     * Returns DiSCO API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @HEAD
    public Response getApiDetails() throws RMapApiException {
    	Response response = discoResponseManager.getDiSCOServiceHead();
	    return response;
    }
    

	/**
	 * OPTIONS /disco
     * Returns DiSCO API information/link, and lists HTTP options
	 * @return Response
	 * @throws RMapApiException
	 */
    @OPTIONS
    public Response apiGetApiDetailedOptions() throws RMapApiException {
    	Response response = discoResponseManager.getDiSCOServiceHead();
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
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;", "application/vnd.rmap-project.disco+rdf+xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.disco+ld+json;charset=UTF-8;",
				"application/n-quads;charset=UTF-8;", "application/vnd.rmap-project.disco+n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.disco+turtle;charset=UTF-8;"
				})
    public Response apiGetRMapDiSCO(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	RdfType returnType = HttpTypeMediator.getRdfResponseType(headers);
    	Response response=discoResponseManager.getRMapDiSCO(discoUri, returnType);
    	return response;
    }
    
    
/*
 *-------------------------------
 *
 *	 GET LATEST DISCO VERSION
 * 
 *-------------------------------
 */

	/**
	 * GET /disco/{discoUri}/latest
	 * Returns latest version of requested RMap:DiSCO as RDF/XML, JSON-LD, NQUADS or TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/latest")
    @Produces({"application/rdf+xml;charset=UTF-8;", "application/xml;charset=UTF-8;", "application/vnd.rmap-project.disco+rdf+xml;charset=UTF-8;",
				"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.disco+ld+json;charset=UTF-8;",
				"application/n-quads;charset=UTF-8;", "application/vnd.rmap-project.disco+n-quads;charset=UTF-8;",
				"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.disco+turtle;charset=UTF-8;"
				})
    public Response apiGetLatestRMapDiSCO(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	RdfType returnType = HttpTypeMediator.getRdfResponseType(headers);
    	Response response=discoResponseManager.getLatestRMapDiSCOVersion(discoUri, returnType);
    	return response;
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
    public Response apiGetDiSCOStatus(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = discoResponseManager.getRMapDiSCOHeader(discoUri);
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
	 * Creates new DiSCO from RDF/XML, JSON-LD or TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @POST
    @Path("/")
    @Consumes({"application/rdf+xml;charset=UTF-8;", "application/vnd.rmap-project.disco+rdf+xml;charset=UTF-8;",
		"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.disco+ld+json;charset=UTF-8;",
		"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.disco+turtle;charset=UTF-8;"
		})
    public Response apiCreateRMapDiSCO(@Context HttpHeaders headers, InputStream discoRdf) throws RMapApiException {
    	RdfType requestFormat = HttpTypeMediator.getRdfTypeOfRequest(headers);
    	Response createResponse = discoResponseManager.createRMapDiSCO(discoRdf, requestFormat, getSysAgentId());
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
	 * Sets original DiSCO as inactive and creates a new DiSCO from RDF/XML, JSON-LD or TURTLE
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
    @POST
    @Path("/{discoid}")
    @Consumes({"application/rdf+xml;charset=UTF-8;", "application/vnd.rmap-project.disco+rdf+xml;charset=UTF-8;",
		"application/ld+json;charset=UTF-8;", "application/vnd.rmap-project.disco+ld+json;charset=UTF-8;",
		"text/turtle;charset=UTF-8;", "application/vnd.rmap-project.disco+turtle;charset=UTF-8;"
		})
    public Response apiUpdateRMapDiSCO(@Context HttpHeaders headers, 
    										@PathParam("discoid") String origDiscoId, 
    										InputStream discoRdf) throws RMapApiException {
    	RdfType requestFormat = HttpTypeMediator.getRdfTypeOfRequest(headers);
    	Response updateResponse = discoResponseManager.updateRMapDiSCO(origDiscoId, discoRdf, requestFormat, getSysAgentId());
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
	 * Returns list of RMap:Event URIs related to the DiSCO URI as JSON or PLAINTEXT
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/events")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapDiSCOEventList(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response eventList = discoResponseManager.getRMapDiSCOEvents(discoUri, outputType);
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
    public Response apiDeleteRMapDiSCO(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = discoResponseManager.tombstoneRMapDiSCO(discoUri,getSysAgentId());
	    return response;
    }

	/**
	 * POST /disco/{discoUri}/inactivate
	 * Sets status of target RMap:DiSCO to "inactive".  It will still be stored in the triplestore
	 * and will still be visible through the API for certain requests.
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @POST
    @Path("/{discoUri}/inactivate")
    public Response apiInactivateRMapDiSCO(@PathParam("discoUri") String discoUri) throws RMapApiException {
    	Response response = discoResponseManager.inactivateRMapDiSCO(discoUri, getSysAgentId());
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
	 * Returns list of all RMap:DiSCO version URIs as JSON or PLAIN TEXT
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */    
    @GET
    @Path("/{discoUri}/allversions")
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapDiSCOVersionList(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response versionList = discoResponseManager.getRMapDiSCOVersions(discoUri, outputType, false);
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
    @Produces({"application/json;charset=UTF-8;","text/plain;charset=UTF-8;"})
    public Response apiGetRMapDiSCOAgentVersionList(@Context HttpHeaders headers, @PathParam("discoUri") String discoUri) throws RMapApiException {
    	NonRdfType outputType = HttpTypeMediator.getNonRdfResponseType(headers);
    	Response versionList = discoResponseManager.getRMapDiSCOVersions(discoUri, outputType, true);
    	return versionList;
    }
    
}