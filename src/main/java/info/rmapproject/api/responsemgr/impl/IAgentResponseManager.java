package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.openrdf.model.vocabulary.DC;

public class IAgentResponseManager {

	private final Logger log = Logger.getLogger(this.getClass());
	
	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static String BASE_AGENT_URL = "http://rmapdns.ddns.net:8080/api/agent/";
	//private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";


	public IAgentResponseManager() {
	}		
	
	

	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getAgentServiceOptions()	{
		Response response = null;
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
	
		return response;    
	}
	
	
	/**
	 * @return HTTP Response
	 * 
	 */
	public Response getAgentServiceHead()	{
    	Response response = null;
    	String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
    
    	return response;
	}
	
	
	/**
	 * 
	 * @param strAgentUri
	 * @param acceptType
	 * @return HTTP Response
	 */
	
	public Response getRMapAgent(String strAgentUri, String acceptsType)	{
		
		Response response = null;
		
		try {
    		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
    		URI uriAgentUri = new URI(strAgentUri);
    		RMapAgent rmapAgent = rmapService.readAgent(uriAgentUri);
    		
    		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
    		OutputStream agentOutput = rdfHandler.agent2Rdf(rmapAgent, acceptsType);	
    		
    		//TODO:Not sure what we're doing for API calls yet...
    		/*
    		String latestAgentUrl = rmapService.getAgentLatestVersion(uriAgentUri).getId().toString();
        	String linkRel = "<" + latestAgentUrl + ">" + ";rel=\"latest-version\"";

    		String prevAgentVersUrl = rmapService.getAgentPreviousVersion(uriAgentUri).getId().toString();
        	if (prevAgentVersUrl != null) {
        		linkRel.concat(",<" + prevAgentVersUrl + ">" + ";rel=\"predecessor-version\"");
        	}

    		String succAgentVersUrl = rmapService.getAgentNextVersion(uriAgentUri).getId().toString();
        	if (succAgentVersUrl != null) {
        		linkRel.concat(",<" + succAgentVersUrl + ">" + ";rel=\"successor-version\"");
        	}
        	*/  	
        	
        	//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a Agent.
    		
    		/*List <RMapEvent> lstEvents = rmapAgent.getRelatedEvents();
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
    		}*/
    		    		
        	response = Response.status(Response.Status.OK)
        				.entity(agentOutput)
        				.location(new URI (BASE_AGENT_URL + strAgentUri))
        				//.header("Link",linkRel)						//switch this to link() or links()?
        				.build();	
    		   	
    	}    	
    	//TODO:Add more exceptions as they become available!
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Agent could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}    
    	catch(RMapDeletedObjectException ex) {
    		log.fatal("Agent has been deleted. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}     
    	catch(RMapTombstonedObjectException ex) {
    		log.fatal("Agent has been tombstoned. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.GONE).build();
    	}   		
    	catch(Exception ex)	{ //catch the rest
    		log.fatal("Error trying to retrieve Agent: " + strAgentUri + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    	}
    	return response;
    }

}
