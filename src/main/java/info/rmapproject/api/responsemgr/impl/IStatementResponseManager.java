package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.statement.RMapStatement;
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

public class IStatementResponseManager {

	private static String BASE_STATEMENT_URL = "http://rmapdns.ddns.net:8080/api/stmt/";
	//private static String BASE_EVENT_URL = "http://rmapdns.ddns.net:8080/api/event/";
	private final Logger log = LogManager.getLogger(this.getClass());
	
	public IStatementResponseManager() {
	}		
	
	
	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getStatementServiceOptions()	{
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
	public Response getStatementServiceHead()	{
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
	 * Get RMap Statement formatted according to RDF type passed in
	 * 
	 */
    
	public Response getRMapStatement(String strStatementId, String acceptsType)	{
		Response response = null;
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriStatementUri = new URI(strStatementId);
    		RMapStatement rmapStatement = rmapService.readStatement(uriStatementUri);
    		
    		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
    		OutputStream statementOutput = rdfHandler.statement2Rdf(rmapStatement, acceptsType);	
    		
    		//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a disco.
    		//TODO: need to fix this after we decide how to do this - hoping for an easier method than what I can get at currently... 
    		/*String linkRel = "";
    		List <RMapEvent> lstEvents = rmapStatement.getRelatedEvents();
    		for (URI event : lstEvents){
    			if (event.getEventType() == RMapEventType.CREATION){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"");
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
    		   		
    		
    		if (rmapStatement!=null){
			    			
				response = Response.status(Response.Status.OK)
							.entity(statementOutput)
							.location(new URI (BASE_STATEMENT_URL + strStatementId))
							.build();
    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Statement could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve statement: " + strStatementId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}

	/**
	 * 
	 * @param s
	 * @param p
	 * @param o
	 * @param acceptsType
	 * @return
	 */

	public Response getRMapStatementID(RMapResource s, RMapUri p, RMapValue o, String acceptsType)	{
		Response response = null;
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI stmtURI = null;
			
			//TODO: can't complete this yet... need to think about how to pass in s-p-o as appropriate type.		
			/*
			RMapResource subject = s;
			RMapUri predicate = p;
			RMapValue object = o;
    		*/
			stmtURI = rmapService.getStatementID(s, p, o);
			
    		//TODO: missing some relationship terms here... need to add them in. Hardcoded for now.
        	//TODO: actually - need to read this in from the triple... an update will either inactivate or create a disco.
    		//TODO: need to fix this after we decide how to do this - hoping for an easier method than what I can get at currently... 
    		/*String linkRel = "";
    		List <RMapEvent> lstEvents = rmapStatement.getRelatedEvents();
    		for (URI event : lstEvents){
    			if (event.getEventType() == RMapEventType.CREATION){
   	        		linkRel.concat(",<" + BASE_EVENT_URL + event + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"");
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
    		   		
    		
    		if (stmtURI!=null){			    			
				response = Response.status(Response.Status.OK)
							.entity(stmtURI.toString())
							.location(new URI (BASE_STATEMENT_URL + stmtURI.toString()))
							.build();
    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Statement could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve statement. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
	
	/**
	 * 
	 * @param strStatementId
	 * @param returnType
	 * @return HTTP Response - list of URIs for events associated with the Stmt Id provided.
	 */
	public Response getRMapStatementRelatedEvents(String strStatementId, String returnType)	{
		Response response = null;
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriStatementUri = new URI(strStatementId);
			String outputString="";
			
			List <URI> uriList = rmapService.getStatementEvents(uriStatementUri);
			
			if (returnType.equals("JSON"))	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString)
							.location(new URI (BASE_STATEMENT_URL + strStatementId))
							.build();    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Event could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve event details for: " + strStatementId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
	
}
