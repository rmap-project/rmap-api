package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.agent.RMapProfile;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.model.event.RMapEventDerivation;
import info.rmapproject.core.model.event.RMapEventType;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapEventMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for Profile REST API requests
 *
 */

public class IProfileResponseManager {

	private final Logger log = Logger.getLogger(this.getClass());
	
	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static URI SYSAGENT_URI; //defaults to IEEE user for now until authentication in place!
	private static String BASE_PROFILE_URL = "http://rmapdns.ddns.net:8080/api/profile/";
	private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";

	static{
		try {
			SYSAGENT_URI = new URI("http://orcid.org/00000-00000-00000-00000");
		}
		catch (Exception e){}
	}

	public IProfileResponseManager() {
	}		
	
	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getProfileServiceOptions()	{
		Response response = null;
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/profile>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
					.header("Link",linkRel)
					.build();
	
		return response;    
	}
	
	
	/**
	 * @return HTTP Response
	 * 
	 */
	public Response getProfileServiceHead()	{
    	Response response = null;
    	String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/profile>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
					.header("Link",linkRel)
					.build();
    
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
		
		try {
    		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
    		URI uriProfileUri = new URI(strProfileUri);
    		RMapProfile rmapProfile = rmapService.readProfile(uriProfileUri);
    		
    		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
    		OutputStream profileOutput = rdfHandler.profile2Rdf(rmapProfile, acceptsType);	
    		
    		String latestProfileUrl = rmapService.getProfileLatestVersion(uriProfileUri).getId().toString();
        	String linkRel = "<" + latestProfileUrl + ">" + ";rel=\"latest-version\"";

    		String prevProfileVersUrl = rmapService.getProfilePreviousVersion(uriProfileUri).getId().toString();
        	if (prevProfileVersUrl != null) {
        		linkRel.concat(",<" + prevProfileVersUrl + ">" + ";rel=\"predecessor-version\"");
        	}

    		String succProfileVersUrl = rmapService.getProfileNextVersion(uriProfileUri).getId().toString();
        	if (succProfileVersUrl != null) {
        		linkRel.concat(",<" + succProfileVersUrl + ">" + ";rel=\"successor-version\"");
        	}
        	       	
        	
    		//rmapService.getProfileEvents(uriProfileUri)
        	//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a Profile.
    		List <RMapEvent> lstEvents = rmapProfile.getRelatedEvents();
    		for (RMapEvent event : lstEvents){
    			if (event.getEventType() == RMapEventType.CREATION){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event.getId() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"");
    			}
    			else if (event.getEventType() == RMapEventType.DELETION){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event.getId() + ">" + ";rel=\"" + "wasDeletedBy" + "\"");
    			}
    			else if (event.getEventType() == RMapEventType.INACTIVATION){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event.getId() + ">" + ";rel=\"" + "wasInactivatedBy" + "\"");
    			}
    			else if (event.getEventType() == RMapEventType.TOMBSTONE){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event.getId() + ">" + ";rel=\"" + "wasTombstonedBy" + "\"");
    			}
    			else if (event.getEventType() == RMapEventType.UPDATE){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event.getId() + ">" + ";rel=\"" + "wasUpdatedBy" + "\"");
    			}
    		}
    		    		
        	response = Response.status(Response.Status.OK)
        				.entity(profileOutput)
        				.location(new URI (BASE_PROFILE_URL + strProfileUri))
        				.header("Link",linkRel)						//switch this to link() or links()?
        				.build();	
    		   	
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
    	}
    	return response;
    }
		
	
	
	/**
	 * 
	 * @param profileRdf
	 * @return HTTP Response
	 */
	public Response createRMapProfile(InputStream profileRdf, String contentType) {
	Response response = null;
	try	{
		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
		RMapProfile rmapProfile = rdfHandler.rdf2RMapProfile(profileRdf, BASE_PROFILE_URL, contentType);
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
        				.entity(profileURI)
        				.location(new URI (BASE_PROFILE_URL + profileURI)) //switch this to location()
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
	}
	return response;  
	}
	
	
	
	public Response updateRMapProfile(String origProfileUri, InputStream profileRdf, String contentType) {
		Response response = null;
		
		try	{
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore(); 
			RMapUri profileURI = null;		
						
			RMapProfile newRmapProfile = rdfHandler.rdf2RMapProfile(profileRdf, BASE_PROFILE_URL, contentType);
			
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
	        	String linkRel = "<" + BASE_EVENT_URL + profileEvent.getId().toString() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

	    		String prevProfileVersUrl = rmapService.getProfilePreviousVersion(new URI(profileURI.toString())).getId().toString();
	        	if (prevProfileVersUrl != null) {
	        		linkRel.concat(",<" + prevProfileVersUrl + ">" + ";rel=\"predecessor-version\"");
	        	}
	        	
	        	response = Response.status(Response.Status.CREATED)
	        				.entity(profileURI)
	        				.location(new URI (BASE_PROFILE_URL + profileURI)) //switch this to location()
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
    	}
    	return response;
				
	}
	
}
