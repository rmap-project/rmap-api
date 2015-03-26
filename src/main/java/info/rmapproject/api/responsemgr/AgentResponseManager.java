package info.rmapproject.api.responsemgr;

import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

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
 * Creates HTTP responses for Agent REST API requests
 *
 */
public class AgentResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());

	//TODO SYSAGENT will eventually come from oauth module, BASE_URLS will be in properties file
	private static URI SYSAGENT_URI; //defaults to IEEE user for now until authentication in place!

	static{
		try {
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();
		}
		catch (Exception e){}
	}


	public AgentResponseManager() {
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

    		if (rmapAgent!=null){
    			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
	    		OutputStream agentOutput = rdfHandler.agent2Rdf(rmapAgent, acceptsType);	
	    		
	    		//TODO:Not sure what we're doing for API calls yet...
	    		/*
	    		String latestAgentUrl = URLUtils.makeAgentUrl(rmapService.getAgentLatestVersion(uriAgentUri).getId().toString());
	        	String linkRel = "<" + latestAgentUrl + ">" + ";rel=\"latest-version\"";
	
	    		String prevAgentVersUrl = URLUtils.makeAgentUrl(rmapService.getAgentPreviousVersion(uriAgentUri).getId().toString());
	        	if (prevAgentVersUrl != null) {
	        		linkRel.concat(",<" + prevAgentVersUrl + ">" + ";rel=\"predecessor-version\"");
	        	}
	
	    		String succAgentVersUrl = URLUtils.makeAgentUrl(rmapService.getAgentNextVersion(uriAgentUri).getId().toString());
	        	if (succAgentVersUrl != null) {
	        		linkRel.concat(",<" + succAgentVersUrl + ">" + ";rel=\"successor-version\"");
	        	}
	        	*/  	
	        	
	        	//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
	        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a Agent.
	    		
	    		/*List <URI> lstEvents = rmapAgent.getRelatedEvents();
	    		for (URI eventUri : lstEvents){
	    			String event = URLUtils.makeEventUrl(eventUri.toString());
	    			if (event.getEventType() == RMapEventType.CREATION){
	   	        		linkRel.concat(",<" + event + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"");
	    			}
	    			else if (event.getEventType() == RMapEventType.DELETION){
	   	        		linkRel.concat(",<" + event + ">" + ";rel=\"" + "wasDeletedBy" + "\"");
	    			}
	    			else if (event.getEventType() == RMapEventType.INACTIVATION){
	   	        		linkRel.concat(",<" + event + ">" + ";rel=\"" + "wasInactivatedBy" + "\"");
	    			}
	    			else if (event.getEventType() == RMapEventType.TOMBSTONE){
	   	        		linkRel.concat(",<" + event + ">" + ";rel=\"" + "wasTombstonedBy" + "\"");
	    			}
	    			else if (event.getEventType() == RMapEventType.UPDATE){
	   	        		linkRel.concat(",<" + event + ">" + ";rel=\"" + "wasUpdatedBy" + "\"");
	    			}
	    		}*/
	    		    		
	        	response = Response.status(Response.Status.OK)
	        				.entity(agentOutput.toString())
	        				.location(new URI (URLUtils.makeAgentUrl(strAgentUri)))
	        				//.header("Link",linkRel)						//switch this to link() or links()?
	        				.build();	
    		}
        	else	{
    			throw new Exception();
        	}
    		   	
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

	public Response createRMapAgent(InputStream agentRdf, String contentType) {
		
		Response response = null;
		/*
		try {
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			RMapAgent rmapAgent = rdfHandler.rdf2RMapAgent(agentRdf, URLUtils.getAgentBaseUrl(), contentType);
			String discoURI = "";
								
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			
			//TODO: System agent param is fudged... need to correct this code when proper authentication handling available.
			RMapEventCreation agentEvent = (RMapEventCreation)rmapService.createAgent(new RMapUri(SYSAGENT_URI), rmapAgent);
    		
    		
    		
		}
		catch(Exception ex)
			{
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}*/
		return response;
	}	
	
}
