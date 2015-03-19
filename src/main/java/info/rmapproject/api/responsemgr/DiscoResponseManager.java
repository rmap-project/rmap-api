package info.rmapproject.api.responsemgr;

import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.model.event.RMapEventDerivation;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

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

public class DiscoResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());
	
	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static URI SYSAGENT_URI; //defaults to IEEE user for now until authentication in place!
	private static String BASE_DISCO_URL = "http://rmapdns.ddns.net:8080/api/disco/";
	private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";

	static{
		try {
			SYSAGENT_URI = new URI("http://orcid.org/0000-0003-2069-1219");
		}
		catch (Exception e){}
	}

	public DiscoResponseManager() {
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
        	//TODO: Need to fix this... awkward to get the effect of an event.
    		/*
    		List<URI> lstEvents = rmapService.getDiSCOEvents(uriDiscoUri);
    		for (URI event : lstEvents){
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
    		}*/
    		
    		RMapStatus status = rmapService.getDiSCOStatus(uriDiscoUri);

    		//TODO: fix this linkrel, it's wrong!
    		if (status == RMapStatus.ACTIVE)
       		linkRel.concat(",<" + status + ">" + ";rel=\"rmap:Status\"");
    		    		
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
		
		//TODO: System agent param is fudged... need to correct this code when proper authentication handling available.
		RMapEventCreation discoEvent = (RMapEventCreation)rmapService.createDiSCO(new RMapUri(SYSAGENT_URI), rmapDisco);
		discoURI = rmapDisco.getId().toString();		
		
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
			String discoURI = null;		
						
			RMapDiSCO newRmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, BASE_DISCO_URL, contentType);
			
			//TODO: This is a fudge for system agent, need to correct this code when proper agent handling available.
			
			RMapEventDerivation discoEvent = (RMapEventDerivation)rmapService.updateDiSCO(new RMapUri(SYSAGENT_URI), 
																							new URI(origDiscoUri), 
																							newRmapDisco);
				
			//This chunk of code just gets the DiSCO ID of the newly created DiSCO to return to user.
			discoURI = newRmapDisco.getId().toString();
			    
	        if (discoURI.length() > 0){
	        	String linkRel = "<" + BASE_EVENT_URL + discoEvent.getId().toString() + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
	        	//TODO:is predecessor version appropriate?
	        	linkRel.concat(",<" + origDiscoUri + ">" + ";rel=\"predecessor-version\"");
	        	
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