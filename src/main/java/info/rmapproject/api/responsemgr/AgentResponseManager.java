package info.rmapproject.api.responsemgr;


import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.ObjType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.Utils;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.agent.RMapAgent;
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
import java.util.Date;
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

	private static final RdfType DEFAULT_RDF_TYPE = RdfType.TURTLE;
	private static final NonRdfType DEFAULT_NONRDF_TYPE = NonRdfType.JSON;
	
	public AgentResponseManager() {
	}		
	
	/**
	 * Displays Agent Service Options
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getAgentServiceOptions() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/agent>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
						.entity("{\"description\":\"will show copy of swagger content\"}")
						.header("Allow", "HEAD,OPTIONS,GET,POST,DELETE")
						.header("Link",linkRel)	
						.build();
			reqSuccessful = true;
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		finally{
			if (!reqSuccessful && response!=null) response.close();
		}
		return response;    
	}
	
	
	/**
	 * Displays Agent Service Options Header
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getAgentServiceHead() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/agent>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
						.header("Allow", "HEAD,OPTIONS,GET,POST,DELETE")
						.header("Link",linkRel)	
						.build();
			reqSuccessful = true;
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		finally{
			if (!reqSuccessful && response!=null) response.close();
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
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (returnType==null)	{returnType=DEFAULT_RDF_TYPE;}

			URI uriAgentId = null;
			try {
				strAgentUri = URLDecoder.decode(strAgentUri, "UTF-8");
				uriAgentId = new URI(strAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
					
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
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
						.location(new URI(Utils.makeAgentUrl(strAgentUri)))
						.header("Link",linkRel)    //switch this to link()
        				.type(HttpTypeMediator.getResponseRdfMediaType("agent", returnType)) //TODO move version number to a property?
						.build();   
		    
			reqSuccessful = true; 	
		    
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
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
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
    		RMapStatus status = rmapService.getAgentStatus(uriAgentId);
    		if (status==null){
    			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";

		    response = Response.status(Response.Status.OK)
						.location(new URI(Utils.makeAgentUrl(strAgentUri)))
						.header("Link",linkRel)    //switch this to link()
						.build();   

			reqSuccessful = true;
		    
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
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
						 						
			RMapAgent rmapAgent = rdfHandler.rdf2RMapAgent(agentRdf, "", contentType.toString());
			if (rmapAgent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_AGENT_FAILED);
			}  
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
						
			RMapEventCreation agentEvent = (RMapEventCreation)rmapService.createAgent(rmapAgent, sysAgentUri);
			if (agentEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_CREATEAGENT_NOT_COMPLETED);
			} 

			URI uAgentURI = rmapAgent.getId().getIri();  
			if (uAgentURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_AGENTID_RETURNED_NULL);
			} 
			String sAgentURI = uAgentURI.toString();  
			if (sAgentURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_AGENTURI_STRING_EMPTY);
			} 

			URI uEventURI = agentEvent.getId().getIri();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = Utils.makeEventUrl(sEventURI); 
			String newAgentUrl = Utils.makeAgentUrl(sAgentURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

			response = Response.status(Response.Status.CREATED)
					.entity(sAgentURI)
					.location(new URI(newAgentUrl)) //switch this to location()
					.header("Link",linkRel)    //switch this to link()
					.build();  
			
			reqSuccessful = true;
					
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
	return response;  
	}
	
	

	/**
	 * Retrieves list of RMap:Event URIs associated with the RMap:Agent URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param agentUri
	 * @param returnType
	 * @param dateFrom
	 * @param dateTo
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentEvents(String agentUri, 
			NonRdfType returnType, 
			String dateFrom,
			String dateTo) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=DEFAULT_NONRDF_TYPE;}
			
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
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			String outputString="";

			Date dDateFrom = Utils.convertStringDateToDate(dateFrom);
			Date dDateTo = Utils.convertStringDateToDate(dateTo);
			List <URI> uriList = rmapService.getAgentEventsInitiated(uriAgentUri, dDateFrom, dDateTo);	
			
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.PLAIN_TEXT)	{		
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, ObjType.EVENTS.getObjTypeLabel());		
			}
    		
    		response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (Utils.makeAgentUrl(agentUri)))
							.build();
    		
    		reqSuccessful=true;
	        
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
    	return response;
	}
	

	

	/**
	 * Retrieves list of RMap:DiSCO URIs associated with the RMap:Agent URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param agentUri
	 * @param returnType
	 * @param status
	 * @param dateFrom
	 * @param dateTo
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentDiSCOs(String agentUri, 
			NonRdfType returnType, 
			String status,
			String dateFrom,
			String dateTo) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=DEFAULT_NONRDF_TYPE;}
			
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
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			String outputString="";
			
			RMapStatus rmapStatus = Utils.convertToRMapStatus(status);
			Date dDateFrom = Utils.convertStringDateToDate(dateFrom);
			Date dDateTo = Utils.convertStringDateToDate(dateTo);
			List <URI> uriList = rmapService.getAgentDiSCOs(uriAgentUri, rmapStatus, dDateFrom, dDateTo);		
			
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.PLAIN_TEXT)	{		
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, ObjType.DISCOS.getObjTypeLabel());		
			}
    		
    		response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (Utils.makeAgentUrl(agentUri)))
							.build();
    		
    		reqSuccessful=true;
	        
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
    	return response;
	}
	
	
}
