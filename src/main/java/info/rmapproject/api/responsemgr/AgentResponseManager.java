package info.rmapproject.api.responsemgr;


import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventCreation;
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
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;

/**
 * 
 * Creates HTTP responses for RMap Agent REST API requests
 * @author khanson
 *
 */

public class AgentResponseManager {

	private static RMapService rmapService = null;
	
	public AgentResponseManager() {
	}		
	
	
	/**
	 * Creates new RMapService object if not already initiated.
	 * @throws RMapApiException
	 * @throws RMapException
	 */	
	private static void initRMapService() throws RMapApiException, RMapException {
		if (rmapService == null){
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
		}
	}
	
	/**
	 * Displays Agent Service Options
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getAgentServiceOptions() throws RMapApiException	{
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/agent>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
						.entity("{\"description\":\"will show copy of swagger content\"}")
						.header("Allow", "HEAD,OPTIONS,GET,POST,DELETE")
						.header("Link",linkRel)	
						.build();

		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;    
	}
	
	
	/**
	 * Displays Agent Service Options Header
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getAgentServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/agent>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
						.header("Allow", "HEAD,OPTIONS,GET,POST,DELETE")
						.header("Link",linkRel)	
						.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		return response;    
	}
		
	/**
	 * Retrieves RMap Agent in requested RDF format and forms an HTTP response.
	 * @param strAgentUri
	 * @param acceptType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getRMapAgent(String strAgentUri, RdfType returnType) throws RMapApiException	{
		Response response = null;
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (returnType==null)	{
				throw new RMapApiException(ErrorCode.ER_NO_ACCEPT_TYPE_PROVIDED); 
			}

			URI uriAgentId = null;
			try {
				strAgentUri = URLDecoder.decode(strAgentUri, "UTF-8");
				uriAgentId = new URI(strAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
					
			initRMapService();
			
    		RMapAgent rmapAgent = rmapService.readAgent(uriAgentId);
			if (rmapAgent ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_AGENT_RETURNED_NULL);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			
    		OutputStream agentOutput = rdfHandler.agent2Rdf(rmapAgent, returnType.toString());
			if (agentOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	

    		RMapStatus status = rmapService.getAgentStatus(uriAgentId);
    		if (status==null){
    			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";

		    response = Response.status(Response.Status.OK)
						.entity(agentOutput.toString())
						.location(new URI(RestApiUtils.makeAgentUrl(strAgentUri)))
						.header("Link",linkRel)    //switch this to link()
        				.type("application/vnd.rmap-project.agent; version=1.0-beta") //TODO move version number to a property?
						.build();    	
		    
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		} 
		catch(RMapAgentNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_AGENT_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) {
			if (ex.getCause() instanceof RMapDeletedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_DELETED);  			
			}
			else if (ex.getCause() instanceof RMapTombstonedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_TOMBSTONED);  			
			}
			else if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
		return response;
    }
	
	
	

	/**
	 * Retrieves status of specific RMap Agent as HTTP response.
	 * @param strAgentUri
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getRMapAgentHeader(String strAgentUri) throws RMapApiException	{
		Response response = null;
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		

			URI uriAgentId = null;
			try {
				strAgentUri = URLDecoder.decode(strAgentUri, "UTF-8");
				uriAgentId = new URI(strAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
					
			initRMapService();
			
    		RMapStatus status = rmapService.getAgentStatus(uriAgentId);
    		if (status==null){
    			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";

		    response = Response.status(Response.Status.OK)
						.location(new URI(RestApiUtils.makeAgentUrl(strAgentUri)))
						.header("Link",linkRel)    //switch this to link()
						.build();   
		    
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		} 
		catch(RMapAgentNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_AGENT_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) {
			if (ex.getCause() instanceof RMapDeletedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_DELETED);  			
			}
			else if (ex.getCause() instanceof RMapTombstonedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_TOMBSTONED);  			
			}
			else if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
		return response;
    }
	
	

	
	/**
	 * Creates new RMap:Agent from valid client-provided RDF.
	 * @param agentRdf
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response createRMapAgent(InputStream agentRdf, RdfType contentType, URI sysAgentUri) throws RMapApiException {
	Response response = null;
	
		try	{
			if (agentRdf == null || agentRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_AGENT_RDF_PROVIDED);
			} 
			if (contentType == null){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}
			if (sysAgentUri == null){
				throw new RMapApiException(ErrorCode.ER_NO_SYSTEMAGENT_PROVIDED);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
						 						
			RMapAgent rmapAgent = rdfHandler.rdf2RMapAgent(sysAgentUri, agentRdf, RestApiUtils.getAgentBaseUrl(), contentType.toString());
			if (rmapAgent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_AGENT_FAILED);
			}  

			initRMapService();
						
			RMapEventCreation agentEvent = (RMapEventCreation)rmapService.createAgent(sysAgentUri, rmapAgent);
			if (agentEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_CREATEAGENT_NOT_COMPLETED);
			} 

			URI uAgentURI = rmapAgent.getId();  
			if (uAgentURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_AGENTID_RETURNED_NULL);
			} 
			String sAgentURI = uAgentURI.toString();  
			if (sAgentURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_AGENTURI_STRING_EMPTY);
			} 

			URI uEventURI = agentEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = RestApiUtils.makeEventUrl(sEventURI); 
			String newAgentUrl = RestApiUtils.makeAgentUrl(sAgentURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

			response = Response.status(Response.Status.CREATED)
					.entity(sAgentURI)
					.location(new URI(newAgentUrl)) //switch this to location()
					.header("Link",linkRel)    //switch this to link()
					.build();  
					
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		} 
		catch(RMapException ex) { 
			throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  			
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
	return response;  
	}
	

	/**
	 * Sets status of RMap:Agent to tombstoned. 
	 * @param agentUri
	 * @param newStatus
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response tombstoneRMapAgent(String agentUri, URI sysAgentUri) throws RMapApiException {
		Response response = null;

		try	{		
			if (agentUri==null || agentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (sysAgentUri == null){
				throw new RMapApiException(ErrorCode.ER_NO_SYSTEMAGENT_PROVIDED);
			}
			
			URI uriAgentUri = null;
			try {
				agentUri = URLDecoder.decode(agentUri, "UTF-8");
				uriAgentUri = new URI(agentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			initRMapService();
			
			RMapEvent agentEvent = null;
			agentEvent = (RMapEvent)rmapService.deleteAgent(sysAgentUri, uriAgentUri);					
				
			if (agentEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_UPDATEAGENT_NOT_COMPLETED);
			} 
			
			URI uEventURI = agentEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = RestApiUtils.makeEventUrl(sEventURI); 
			String origAgentUrl = RestApiUtils.makeAgentUrl(agentUri); 
			String linkRel = "";

			//TODO: EVENT_TYPE_TOMBSTONE a place holder - need to consider what this should be.
			linkRel = "<" + newEventURL + ">" + ";rel=\"" + RMAP.EVENT_TYPE_TOMBSTONE + "\"";
						
			response = Response.status(Response.Status.OK)
					.location(new URI(origAgentUrl)) 
					.header("Link",linkRel)    //switch this to link()
					.build();   
    	
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		} 
		catch(RMapAgentNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_AGENT_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) { 
			if (ex.getCause() instanceof RMapDeletedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_DELETED);  			
			}
			else if (ex.getCause() instanceof RMapTombstonedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_TOMBSTONED);  			
			}
			else if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
	return response;		
		
	}
	

	/**
	 * Retrieves list of RMap:Event URIs associated with the RMap:Agent URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param agentUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentEvents(String agentUri, NonRdfType returnType) throws RMapApiException {

		Response response = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=NonRdfType.PLAIN_TEXT;}
			
			if (agentUri==null || agentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriAgentUri = null;
			try {
				agentUri = URLDecoder.decode(agentUri, "UTF-8");
				uriAgentUri = new URI(agentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			List <URI> uriList = rmapService.getAgentEvents(uriAgentUri);						
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
    		
    		response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (RestApiUtils.makeAgentUrl(agentUri)))
							.build();
	        
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapAgentNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_AGENT_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		}
    	catch(RMapException ex) { 
    		throw RMapApiException.wrap(ex, ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
    	}
		catch(Exception ex)	{
    		throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
    	return response;
	}
	
	
	
	/**
	 * Retrieves list of RMap:Agent URIs associated with the Agent URI
	 * @param nonRmapAgentUri
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentRepresentations(String nonRmapAgentUri, String creatorUri, NonRdfType returnType) throws RMapApiException	{

		Response response = null;
		
		try {			
			if (nonRmapAgentUri==null || nonRmapAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	

			URI uriAgentUri = null;
			try {
				nonRmapAgentUri = URLDecoder.decode(nonRmapAgentUri, "UTF-8");
				uriAgentUri = new URI(nonRmapAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			initRMapService();
			
			String outputString="";
			
			List <URI> uriList = null;
			if (creatorUri != null){
				URI uriCreatorUri = null;
				try {
					creatorUri = URLDecoder.decode(creatorUri, "UTF-8");
					uriCreatorUri = new URI(creatorUri);
				}
				catch (Exception ex)  {
					throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
				}
				uriList = rmapService.getAgentRepresentations(uriAgentUri, uriCreatorUri);
			}
			else {
				uriList= rmapService.getAgentRepresentationsAnyCreator(uriAgentUri);
			}
						
			if (uriList == null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_RELATEDAGENTLIST_RETURNED_NULL);
			}
			
			if (uriList.size() == 0) {
				throw new RMapApiException(ErrorCode.ER_NO_RELATED_AGENTS_FOUND);				
			}
			
			if (returnType == NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Profiles");				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString == null || outputString.length()==0){	
				throw new RMapApiException(ErrorCode.ER_CORE_GET_RELATEDAGENTLIST_RETURNED_NULL);    			
	        }		    			
    		
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.location(new URI (RestApiUtils.makeAgentUrl(nonRmapAgentUri)))
						.build();    			
		} 
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_AGENT_BAD_ARGUMENT);
		} 
		catch(RMapAgentNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_AGENT_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) {
			if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
        	throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		finally{
		    rmapService.closeConnection();
		}
		return response;
	}
	
}
