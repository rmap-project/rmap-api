package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.ListReturnType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapAgentNotFoundException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

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
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
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
			response = Response.status(Response.Status.OK)
						.entity("{\"description\":\"will show copy of swagger content\"}")
						.header("Allow", "HEAD,OPTIONS,GET")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
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
			response = Response.status(Response.Status.OK)
						.header("Allow", "HEAD,OPTIONS,GET")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
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
	public Response getRMapAgent(String strAgentUri, String acceptsType) throws RMapApiException	{
		Response response = null;
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (acceptsType==null || acceptsType.length()==0)	{
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
			
    		OutputStream agentOutput = rdfHandler.agent2Rdf(rmapAgent, acceptsType);
			if (agentOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	
   				   	
		    response = Response.status(Response.Status.OK)
						.entity(agentOutput.toString())
						.location(new URI(URLUtils.makeAgentUrl(strAgentUri)))
        				.type("application/vnd.rmap-project.statement; version=1.0-beta") //TODO move version number to a property?
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
		return response;
    }
	
	/**
	 * Retrieves list of RMap:Profile URIs associated with the Agent URI
	 * @param strAgentUri
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getRMapAgentRelatedProfiles(String strAgentUri, ListReturnType returnType) throws RMapApiException	{

		Response response = null;
		
		try {			
			if (strAgentUri==null || strAgentUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	

			URI uriAgentUri = null;
			try {
				strAgentUri = URLDecoder.decode(strAgentUri, "UTF-8");
				uriAgentUri = new URI(strAgentUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			initRMapService();
			
			String outputString="";
			
			List <URI> uriList = rmapService.getAgentRelatedProfiles(uriAgentUri);
			if (uriList == null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_PROFILELIST_RETURNED_NULL);
			}
			if (uriList.size() == 0) {
				throw new RMapApiException(ErrorCode.ER_NO_AGENT_RELATED_PROFILES_FOUND);				
			}
			
			if (returnType == ListReturnType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Profiles");				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString == null || outputString.length()==0){	
				throw new RMapApiException(ErrorCode.ER_CORE_GET_PROFILELIST_RETURNED_NULL);    			
	        }		    			
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.location(new URI (URLUtils.makeAgentUrl(strAgentUri)))
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
		return response;
	}
	
}
