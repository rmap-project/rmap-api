package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.agent.RMapProfile;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for Profile REST API requests
 *
 */

public class ProfileResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());
	
	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static URI SYSAGENT_URI; //defaults to IEEE user for now until authentication in place!

	static{
		try {
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();
		}
		catch (Exception e){}
	}

	public ProfileResponseManager() {
	}		
	
	/**
	 * @return HTTP Response
	 * Displays Agent Service Options 
	 * 
	 */
	
	public Response getProfileServiceOptions() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
						.entity("{\"description\":\"will show copy of swagger content\"}")
						.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
						.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		
		return response;    
	}
	
	
	/**
	 * @return HTTP Response
	 * Displays Profile Service Options	Header 
	 */
	public Response getProfileServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
						.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
						.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;    
	}
	
	
	
	/**
	 * 
	 * @param strProfileUri
	 * @param acceptType
	 * @return HTTP Response
	 */
	
	public Response getRMapProfile(String strProfileUri, String acceptsType)	{
		Response response = null;
		if (strProfileUri==null || strProfileUri.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		/*
		try {
			
			strProfileUri = URLDecoder.decode(strProfileUri, "UTF-8");
    		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
    		URI uriProfileUri = new URI(strProfileUri);
    		RMapProfile rmapProfile = rmapService.readProfile(uriProfileUri);
    		
    		if (rmapProfile != null)	{
    			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
	    		OutputStream profileOutput = rdfHandler.profile2Rdf(rmapProfile, acceptsType);
	        	       	
	        	RMapStatus status = rmapService.getProfileStatus(uriProfileUri);
	    		if (status==null){
					throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
	    		}
	    		String linkRel = "<" + status.toString() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";
	    		String eventUrl = URLUtils.getProfileBaseUrl() + "/events";
	        	linkRel.concat(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
	    		    		
	        	response = Response.status(Response.Status.OK)
	        				.entity(profileOutput.toString())
	        				.location(new URI (URLUtils.makeProfileUrl(strProfileUri)))
	        				.link(new URI (eventUrl), PROV.HAS_PROVENANCE.toString())
	        				.build();		
	        	}
	        else {
	        	throw new RMapException();
	        }
	        
    		  
    	}    	
    	//TODO:Add more exceptions as they become available!
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Profile could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}    
    	catch(RMapDeletedObjectException ex) {
    		log.fatal("Profile has been deleted. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}     
    	catch(RMapTombstonedObjectException ex) {
    		log.fatal("Profile has been tombstoned. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}   		
    	catch(Exception ex)	{ //catch the rest
    		log.fatal("Error trying to retrieve Profile: " + strProfileUri + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    	}*/
    	return response;
    }
		
	
	
	/**
	 * 
	 * @param profileRdf
	 * @return HTTP Response
	 */
	public Response createRMapProfile(InputStream profileRdf, String contentType) {
	Response response = null;
	/*
	try	{
		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
		RMapProfile rmapProfile = rdfHandler.rdf2RMapProfile(profileRdf, URLUtils.getProfileBaseUrl(), contentType);
		String profileURI = "";
				
		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
		SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();  
		
		//TODO: This is a fudge for system agent, need to correct this code when proper agent handling available.
		
		RMapAgent rmapAgent = new ORMapAgent(ORAdapter.uri2OpenRdfUri(SYSAGENT_URI));
		    		
		RMapEventCreation profileEvent = (RMapEventCreation)rmapService.createProfile(rmapAgent, rmapProfile.getAggregratedResources(), 
														rmapProfile.getCreator(), rmapProfile.getRelatedStatements(), rmapProfile.getDescription());
		
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List <RMapUri> lstCreatedObjs = profileEvent.getCreatedObjectIds();	    
		for (RMapUri createdObjUri : lstCreatedObjs)	{
			if (eventmgr.isProfileId(ORAdapter.rMapUri2OpenRdfUri(createdObjUri), ts)) {
				profileURI = createdObjUri.toString();
				break;
			}
		}
		    
        if (profileURI.length() > 0){
        	String linkRel = "<" + profileEvent.getId().toString() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
        	response = Response.status(Response.Status.CREATED)
        				.entity(profileURI.toString())
        				.location(new URI (URLUtils.makeProfileUrl(profileURI))) //switch this to location()
        				.header("Link",linkRel)						//switch this to link()
        				.build();		        	
        }
        else	{
    		throw new Exception();
        }	        	
	}
	//TODO:Add the exceptions as they become available.
	catch(Exception ex)	{
		log.fatal("Profile creation unsuccessful to due Internal Server Error. Error: " + ex.getMessage());
    	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}*/
	return response;  
	}
	
	
	
	public Response updateRMapProfile(String origProfileUri, InputStream profileRdf, String contentType) {
		Response response = null;
		if (origProfileUri==null || origProfileUri.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		/*
		try {
			origProfileUri = URLDecoder.decode(origProfileUri, "UTF-8");
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore(); 
			RMapUri profileURI = null;		
						
			RMapProfile newRmapProfile = rdfHandler.rdf2RMapProfile(profileRdf, URLUtils.getProfileBaseUrl(), contentType);
			
			//TODO: This is a fudge for system agent, need to correct this code when proper agent handling available.
			
			RMapAgent rmapAgent = new ORMapAgent(ORAdapter.uri2OpenRdfUri(SYSAGENT_URI));
			    					
			RMapEventDerivation profileEvent = (RMapEventDerivation)rmapService.updateProfile(rmapAgent, new URI(origProfileUri), newRmapProfile.getAggregratedResources(), 
											newRmapProfile.getRelatedStatements(), newRmapProfile.getCreator(), newRmapProfile.getDescription());
			
			//This chunk of code just gets the Profile ID of the newly created Profile to return to user.
			ORMapEventMgr eventmgr = new ORMapEventMgr();
			List <RMapUri> lstCreatedObjs = profileEvent.getCreatedObjectIds();	    
			for (RMapUri createdObjUri : lstCreatedObjs)	{
				if (eventmgr.isProfileId(ORAdapter.rMapUri2OpenRdfUri(createdObjUri), ts)) {
					profileURI = createdObjUri;
					break;
				}
			}
			    
	        if (profileURI != null){
	        	String linkRel = "<" + URLUtils.makeProfileUrl(profileEvent.getId().toString()) + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

	    		String prevProfileVersUrl = rmapService.getProfilePreviousVersion(new URI(profileURI.toString())).getId().toString();
	        	if (prevProfileVersUrl != null) {
	        		linkRel.concat(",<" + prevProfileVersUrl + ">" + ";rel=\"predecessor-version\"");
	        	}
	        	
	        	response = Response.status(Response.Status.CREATED)
	        				.entity(profileURI.toString())
	        				.location(new URI (URLUtils.makeProfileUrl(profileURI))) //switch this to location()
	        				.header("Link",linkRel)						//switch this to link()
	        				.build();		        	
	        }
	        else	{
	    		throw new Exception();
	        }	        	
		}
    	//TODO:Add more exceptions as they become available!
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Profile could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}    
    	catch(RMapDeletedObjectException ex) {
    		log.fatal("Profile has been deleted. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}     
    	catch(RMapTombstonedObjectException ex) {
    		log.fatal("Profile has been tombstoned. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}   		
    	catch(Exception ex)	{ //catch the rest
    		log.fatal("Error trying to update Profile: " + origProfileUri + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    	}*/
    	return response;
				
	}
	
}
