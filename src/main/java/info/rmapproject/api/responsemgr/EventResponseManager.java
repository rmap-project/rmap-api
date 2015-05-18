package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.event.RMapEvent;
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
 * Creates HTTP responses for Event REST API requests
 * @author khanson
 *
 */
public class EventResponseManager {

	private static RMapService rmapService = null;
	
	public EventResponseManager() {
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
	 * Displays Event Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getEventServiceOptions() throws RMapApiException {
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)	
					.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;  
	}


	/**
	 * Displays Event Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getEventServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)	
					.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		return response; 
	}
	
	

	/**
	 * Retrieves RMap Event in requested RDF format and forms an HTTP response.
	 * @param strEventUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapEvent(String strEventUri, RdfType returnType) throws RMapApiException	{
		Response response = null;
		try {
			if (strEventUri==null || strEventUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (returnType==null)	{
				throw new RMapApiException(ErrorCode.ER_NO_ACCEPT_TYPE_PROVIDED); 
			}
			
			URI uriEventUri = null;
			try {
				strEventUri = URLDecoder.decode(strEventUri, "UTF-8");
				uriEventUri = new URI(strEventUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
    		RMapEvent rmapEvent = rmapService.readEvent(uriEventUri);
			if (rmapEvent ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_EVENT_RETURNED_NULL);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			
    		OutputStream eventOutput = rdfHandler.event2Rdf(rmapEvent, returnType.toString());
			if (eventOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	

			response = Response.status(Response.Status.OK)
						.entity(eventOutput.toString())
						.location(new URI(RestApiUtils.makeEventUrl(strEventUri)))
        				.type("application/vnd.rmap-project.event; version=1.0-beta") //TODO move version number to a property?
						.build();

		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapEventNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_EVENT_OBJECT_NOT_FOUND);			
		}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_STMT_BAD_ARGUMENT);			
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
	 * Get RMap Event related objects, output in format requested (currently JSON or PLAIN TEXT)
	 * @param eventId
	 * @param objType
	 * @param returnType
	 * @return Response
	 */
	public Response getRMapEventRelatedObjs(String strEventUri, String objType, NonRdfType returnType) throws RMapApiException	{
		Response response = null;
		try {
			if (strEventUri==null || strEventUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (objType==null || objType.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_RELATED_OBJECT_TYPE_PROVIDED); 
			}

			URI uriEventUri = null;
			try {
				strEventUri = URLDecoder.decode(strEventUri, "UTF-8");
				uriEventUri = new URI(strEventUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			String jsonType="";

			List <URI> uriList = null;
			//TODO: put these jsonTypes in here for now, but need to settle on what these should be and poss enum them.
			if (objType == "STATEMENTS") {
				uriList = rmapService.getEventRelatedStatements(uriEventUri);
				jsonType = "rmap:Statement";
			}
			if (objType == "RESOURCES") {
				uriList = rmapService.getEventRelatedResources(uriEventUri);
				jsonType = "rdfs:Resource";
			}
			if (objType == "DISCOS") {
				uriList = rmapService.getEventRelatedDiSCOS(uriEventUri);
				jsonType = "rmap:Disco";
			}
			if (objType == "AGENTS") {
				uriList = rmapService.getEventRelatedAgents(uriEventUri);
				jsonType = "rmap:Agent";
			}
			
			if (uriList==null)	{ 
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTRELATEDLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, jsonType);				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (RestApiUtils.makeEventUrl(strEventUri)))
							.build();    			
	        }
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapEventNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_EVENT_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_EVENT_BAD_ARGUMENT);
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
	
}
