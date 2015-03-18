package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.disco.RMapDiSCO;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for DiSCO REST API requests
 *
 */

public class IDiscoResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());
	
	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static URI SYSAGENT_URI; //defaults to IEEE user for now until authentication in place!
	private static String BASE_DISCO_URL = "http://rmapdns.ddns.net:8080/api/disco/";
	private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";

	static{
		try {
			SYSAGENT_URI = new URI("http://orcid.org/00000-00000-00000-00000");
		}
		catch (Exception e){}
	}

	public IDiscoResponseManager() {
	}		
	
	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getDiSCOServiceOptions()	{
		Response response = null;
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>" + ";rel=\"" + DC.DESCRIPTION + "\"";

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
	public Response getDiSCOServiceHead()	{
    	Response response = null;
    	String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
					.header("Link",linkRel)
					.build();
    
    	return response;
	}
	
	
	
	/**
	 * 
	 * @param strDiscoUri
	 * @param acceptType
	 * @return HTTP Response
	 */
	
	public Response getRMapDiSCO(String strDiscoUri, String acceptsType)	{
		
		Response response = null;
		
		try {
    		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
    		URI uriDiscoUri = new URI(strDiscoUri);
    		RMapDiSCO rmapDisco = rmapService.readDiSCO(uriDiscoUri);
    		
    		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
    		OutputStream discoOutput = rdfHandler.disco2Rdf(rmapDisco, acceptsType);	
    		
    		String latestDiscoUrl = rmapService.getDiSCOLatestVersion(uriDiscoUri).getId().toString();
        	String linkRel = "<" + latestDiscoUrl + ">" + ";rel=\"latest-version\"";

    		String prevDiscoVersUrl = rmapService.getDiSCOPreviousVersion(uriDiscoUri).getId().toString();
        	if (prevDiscoVersUrl != null) {
        		linkRel.concat(",<" + prevDiscoVersUrl + ">" + ";rel=\"predecessor-version\"");
        	}

    		String succDiscoVersUrl = rmapService.getDiSCONextVersion(uriDiscoUri).getId().toString();
        	if (succDiscoVersUrl != null) {
        		linkRel.concat(",<" + succDiscoVersUrl + ">" + ";rel=\"successor-version\"");
        	}
        	       	
        	
    		//rmapService.getDiSCOEvents(uriDiscoUri)
        	//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a disco.
    		List <RMapEvent> lstEvents = rmapDisco.getRelatedEvents();
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
        				.entity(discoOutput)
        				.location(new URI (BASE_DISCO_URL + strDiscoUri))
        				.header("Link",linkRel)						//switch this to link() or links()?
        				.build();	
    		   	
    	}    	
    	//TODO:Add more exceptions as they become available!
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("DiSCO could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}    
    	catch(RMapDeletedObjectException ex) {
    		log.fatal("DiSCO has been deleted. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}     
    	catch(RMapTombstonedObjectException ex) {
    		log.fatal("DiSCO has been tombstoned. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}   		
    	catch(Exception ex)	{ //catch the rest
    		log.fatal("Error trying to retrieve DiSCO: " + strDiscoUri + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    	}
    	return response;
    }
		
	
	
	/**
	 * 
	 * @param discoRdf
	 * @return HTTP Response
	 */
	public Response createRMapDiSCO(InputStream discoRdf, String contentType) {
	Response response = null;
	try	{
		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
		RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, BASE_DISCO_URL, contentType);
		String discoURI = "";
				
		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
		SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();  
		
		//TODO: This is a fudge for system agent, need to correct this code when proper agent handling available.
		
		RMapAgent rmapAgent = new ORMapAgent(ORAdapter.uri2OpenRdfUri(SYSAGENT_URI));
		    		
		RMapEventCreation discoEvent = (RMapEventCreation)rmapService.createDiSCO(rmapAgent, rmapDisco.getAggregratedResources(), 
														rmapDisco.getCreator(), rmapDisco.getRelatedStatements(), rmapDisco.getDescription());
		
		ORMapEventMgr eventmgr = new ORMapEventMgr();
		List <RMapUri> lstCreatedObjs = discoEvent.getCreatedObjectIds();	    
		for (RMapUri createdObjUri : lstCreatedObjs)	{
			if (eventmgr.isDiscoId(ORAdapter.rMapUri2OpenRdfUri(createdObjUri), ts)) {
				discoURI = createdObjUri.toString();
				break;
			}
		}
		    
        if (discoURI.length() > 0){
        	String linkRel = "<" + discoEvent.getId().toString() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
        	response = Response.status(Response.Status.CREATED)
        				.entity(discoURI)
        				.location(new URI (BASE_DISCO_URL + discoURI)) //switch this to location()
        				.header("Link",linkRel)						//switch this to link()
        				.build();		        	
        }
        else	{
    		throw new Exception();
        }	        	
	}
	//TODO:Add the exceptions as they become available.
	catch(Exception ex)	{
		log.fatal("DiSCO creation unsuccessful to due Internal Server Error. Error: " + ex.getMessage());
    	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	return response;  
	}
	
	
	
	public Response updateRMapDiSCO(String origDiscoUri, InputStream discoRdf, String contentType) {
		Response response = null;
		
		try	{
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore(); 
			RMapUri discoURI = null;		
						
			RMapDiSCO newRmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, BASE_DISCO_URL, contentType);
			
			//TODO: This is a fudge for system agent, need to correct this code when proper agent handling available.
			
			RMapAgent rmapAgent = new ORMapAgent(ORAdapter.uri2OpenRdfUri(SYSAGENT_URI));
			    					
			RMapEventDerivation discoEvent = (RMapEventDerivation)rmapService.updateDiSCO(rmapAgent, new URI(origDiscoUri), newRmapDisco.getAggregratedResources(), 
											newRmapDisco.getRelatedStatements(), newRmapDisco.getCreator(), newRmapDisco.getDescription());
			
			//This chunk of code just gets the DiSCO ID of the newly created DiSCO to return to user.
			ORMapEventMgr eventmgr = new ORMapEventMgr();
			List <RMapUri> lstCreatedObjs = discoEvent.getCreatedObjectIds();	    
			for (RMapUri createdObjUri : lstCreatedObjs)	{
				if (eventmgr.isDiscoId(ORAdapter.rMapUri2OpenRdfUri(createdObjUri), ts)) {
					discoURI = createdObjUri;
					break;
				}
			}
			    
	        if (discoURI != null){
	        	String linkRel = "<" + BASE_EVENT_URL + discoEvent.getId().toString() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

	    		String prevDiscoVersUrl = rmapService.getDiSCOPreviousVersion(new URI(discoURI.toString())).getId().toString();
	        	if (prevDiscoVersUrl != null) {
	        		linkRel.concat(",<" + prevDiscoVersUrl + ">" + ";rel=\"predecessor-version\"");
	        	}
	        	
	        	response = Response.status(Response.Status.CREATED)
	        				.entity(discoURI)
	        				.location(new URI (BASE_DISCO_URL + discoURI)) //switch this to location()
	        				.header("Link",linkRel)						//switch this to link()
	        				.build();		        	
	        }
	        else	{
	    		throw new Exception();
	        }	        	
		}
    	//TODO:Add more exceptions as they become available!
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("DiSCO could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}    
    	catch(RMapDeletedObjectException ex) {
    		log.fatal("DiSCO has been deleted. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}     
    	catch(RMapTombstonedObjectException ex) {
    		log.fatal("DiSCO has been tombstoned. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}   		
    	catch(Exception ex)	{ //catch the rest
    		log.fatal("Error trying to update DiSCO: " + origDiscoUri + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    	}
    	return response;
				
	}
	
}
