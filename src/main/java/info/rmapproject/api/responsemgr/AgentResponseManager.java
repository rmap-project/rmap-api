package info.rmapproject.api.responsemgr;


import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.utils.Constants;
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
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFType;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.utils.Terms;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.openrdf.model.vocabulary.DC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Creates HTTP responses for RMap Agent REST API requests
 * @author khanson
 *
 */

public class AgentResponseManager extends ResponseManager {
		
	/**
	 * Constructor autowires the RMapService and RDFHandler
	 * @param rmapService
	 * @param rdfHandler
	 * @throws RMapApiException
	 */
	@Autowired
	public AgentResponseManager(RMapService rmapService, RDFHandler rdfHandler) throws RMapApiException {
		super(rmapService, rdfHandler);
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
	public Response getRMapAgent(String strAgentUri, RDFType returnType) throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (returnType==null)	{returnType=Constants.DEFAULT_RDF_TYPE;}

			URI uriAgentId = null;
			try {
				strAgentUri = URLDecoder.decode(strAgentUri, "UTF-8");
				uriAgentId = new URI(strAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
					
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_RMAP_SERVICE);
			}
			
    		RMapAgent rmapAgent = rmapService.readAgent(uriAgentId);
			if (rmapAgent ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_AGENT_RETURNED_NULL);
			}
			
    		OutputStream agentOutput = rdfHandler.agent2Rdf(rmapAgent, returnType);
			if (agentOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	

    		RMapStatus status = rmapService.getAgentStatus(uriAgentId);
    		if (status==null){
    			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		String linkRel = "<" + status.getPath().toString() + ">" + ";rel=\"" + Terms.RMAP_HASSTATUS_PATH + "\"";

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
			
    		RMapStatus status = rmapService.getAgentStatus(uriAgentId);
    		if (status==null){
    			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		String linkRel = "<" + status.getPath().toString() + ">" + ";rel=\"" + Terms.RMAP_HASSTATUS_PATH + "\"";

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
	 * Retrieves list of RMap:Event URIs associated with the RMap:Agent URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param agentUri
	 * @param returnType
	 * @param uriInfo
	 * @param origReqQuery
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentEvents(String agentUri, 
										NonRdfType returnType, 
										UriInfo uriInfo) throws RMapApiException {
		
		boolean reqSuccessful = false;
		Response response = null;
		try {			
			if (agentUri==null || agentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (returnType==null)	{returnType=Constants.DEFAULT_NONRDF_TYPE;}
			
			URI uriAgentUri = null;
			try {
				agentUri = URLDecoder.decode(agentUri, "UTF-8");
				uriAgentUri = new URI(agentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			RMapSearchParams params = super.generateSearchParamObj(uriInfo);
			
			Integer currPage = params.getPage();
			Integer limit=params.getLimit();
			//we are going to get one extra record to see if we need a "next"
			params.setLimit(limit+1);
						 			
			List <URI> uriList = rmapService.getAgentEventsInitiated(uriAgentUri, params);	
			
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	

			String outputString="";		
			if (returnType==NonRdfType.PLAIN_TEXT)	{		
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, Terms.RMAP_EVENT_PATH);		
			}
    					
			ResponseBuilder responseBldr = Response.status(Response.Status.OK)
											.entity(outputString.toString())
											.location(new URI (Utils.makeAgentUrl(agentUri)));
			if (uriList.size()>limit || currPage>1) {
				String pageLinks = super.generatePaginationLinks(Utils.makeAgentUrl(agentUri), uriInfo, currPage, uriList.size()>limit);
				responseBldr.header("Link",pageLinks);
			}
			response = responseBldr.build();	
			    		
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
	 * @param params
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentDiSCOs(String agentUri, NonRdfType returnType, UriInfo uriInfo) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (agentUri==null || agentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			//assign default value when null
			if (returnType==null) {returnType=Constants.DEFAULT_NONRDF_TYPE;}
			
			URI uriAgentUri = null;
			try {
				agentUri = URLDecoder.decode(agentUri, "UTF-8");
				uriAgentUri = new URI(agentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			RMapSearchParams params = super.generateSearchParamObj(uriInfo);
			Integer currPage = params.getPage();
			Integer limit=params.getLimit();
			//we are going to get one extra record to see if we need a "next"
			params.setLimit(limit+1);
	
			List <URI> uriList = rmapService.getAgentDiSCOs(uriAgentUri, params);		
			
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	

			String outputString="";
			if (returnType==NonRdfType.PLAIN_TEXT)	{		
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, Terms.RMAP_DISCO_PATH);		
			}
    		
			ResponseBuilder responseBldr = Response.status(Response.Status.OK)
											.entity(outputString.toString())
											.location(new URI (Utils.makeAgentUrl(agentUri)));
			if (uriList.size()>limit || currPage>1) {
				String pageLinks = super.generatePaginationLinks(Utils.makeAgentUrl(agentUri), uriInfo, currPage, uriList.size()>limit);
				responseBldr.header("Link",pageLinks);
			}
			response = responseBldr.build();	
    		
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
