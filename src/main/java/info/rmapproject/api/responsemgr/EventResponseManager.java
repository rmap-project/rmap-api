package info.rmapproject.api.responsemgr;

import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

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
 * Creates HTTP responses for Event REST API requests
 *
 */
public class EventResponseManager {

	private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";
	private final Logger log = LogManager.getLogger(this.getClass());
	
	public EventResponseManager() {
	}		
	
	
	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getEventServiceOptions()	{
		Response response = null;
	
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
				.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
	
		return response;
	}
	

	/**
	 * @return HTTP Response
	 * 
	 */
	public Response getEventServiceHead()	{
		Response response = null;
	
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>" + ";rel=\"" + DC.DESCRIPTION + "\"";

		response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
	
		return response;    
	}
	
	
	/**
	 * 
	 * @param eventId
	 * @param acceptsType
	 * @return HTTP Response
	 * Get RMap Event formatted according to RDF type passed in
	 * 
	 */
    
	public Response getRMapEvent(String strEventId, String acceptsType)	{
		Response response = null;
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriEventUri = new URI(strEventId);
    		RMapEvent rmapEvent = rmapService.readEvent(uriEventUri);
    		
    		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
    		OutputStream eventOutput = rdfHandler.event2Rdf(rmapEvent, acceptsType);	
    		
    		if (rmapEvent!=null){
			    			
				response = Response.status(Response.Status.OK)
							.entity(eventOutput.toString())
							.location(new URI (BASE_EVENT_URL + strEventId))
							.build();
    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Event could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve event: " + strEventId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}

	/**
	 * 
	 * @param eventId
	 * @param acceptsType
	 * @return HTTP Response
	 * Get RMap Event related objects, output in format requested (currently JSON or PLAIN TEXT)
	 * 
	 */
	public Response getRMapEventRelatedObjs(String strEventId, String objType, String returnType)	{
		Response response = null;
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriEventUri = new URI(strEventId);
			List <URI> uriList = null;
			String outputString="";
			String jsonType="";

			//TODO: put these jsonTypes in here for now, but need to settle on what these should be and poss enum them.
			if (objType == "STATEMENTS") {
				uriList = rmapService.getEventRelatedStatements(uriEventUri);
				jsonType = "rmap:Statements";
			}
			else if (objType == "RESOURCES") {
				uriList = rmapService.getEventRelatedResources(uriEventUri);
				jsonType = "rmap:Resources";
			}
			else if (objType == "DISCOS") {
				uriList = rmapService.getEventRelatedDiSCOS(uriEventUri);
				jsonType = "rmap:Discos";
			}
			else if (objType == "AGENTS") {
				uriList = rmapService.getEventRelatedAgents(uriEventUri);
				jsonType = "rmap:Agents";
			}
    		
			if (returnType.equals("JSON"))	{
				outputString= URIListHandler.uriListToJson(uriList, jsonType);				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (BASE_EVENT_URL + strEventId))
							.build();    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Event could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve event details: " + strEventId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
}
