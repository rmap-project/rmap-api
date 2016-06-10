package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.utils.Constants;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.Utils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapEventNotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rmapservice.RMapService;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Creates HTTP responses for Event REST API requests
 * @author khanson
 *
 */
public class EventResponseManager extends ResponseManager {
	
	/**
	 * Constructor autowires the RMapService and RDFHandler
	 * @param rmapService
	 * @param rdfHandler
	 * @throws RMapApiException
	 */
	@Autowired
	public EventResponseManager(RMapService rmapService, RDFHandler rdfHandler) throws RMapApiException {
		super(rmapService, rdfHandler);
	}
	
	
	/**
	 * Displays Event Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getEventServiceOptions() throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
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
	 * Displays Event Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getEventServiceHead() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
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
	 * Retrieves RMap Event in requested RDF format and forms an HTTP response.
	 * @param strEventUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapEvent(String strEventUri, RdfMediaType returnType) throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strEventUri==null || strEventUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (returnType==null)	{returnType=Constants.DEFAULT_RDF_TYPE;}
			
			URI uriEventUri = null;
			try {
				strEventUri = URLDecoder.decode(strEventUri, "UTF-8");
				uriEventUri = new URI(strEventUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
    		RMapEvent rmapEvent = rmapService.readEvent(uriEventUri);
			if (rmapEvent ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_EVENT_RETURNED_NULL);
			}
			
    		OutputStream eventOutput = rdfHandler.event2Rdf(rmapEvent, returnType.getRdfType());
			if (eventOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	

			response = Response.status(Response.Status.OK)
						.entity(eventOutput.toString())
						.location(new URI(Utils.makeEventUrl(strEventUri)))
        				.type(HttpTypeMediator.getResponseRMapMediaType("event", returnType.getRdfType())) //TODO move version number to constants
						.build();
			
			reqSuccessful = true;

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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
	public Response getRMapEventRelatedObjs(String strEventUri, RMapObjectType objType, NonRdfType returnType) throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strEventUri==null || strEventUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (returnType==null)	{returnType=Constants.DEFAULT_NONRDF_TYPE;}

			URI uriEventUri = null;
			try {
				strEventUri = URLDecoder.decode(strEventUri, "UTF-8");
				uriEventUri = new URI(strEventUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			String outputString="";

			List <URI> uriList = null;
			/**
			 * NOT USED AT THE MOMENT
			if (objType == ObjType.RESOURCES) {
				uriList = rmapService.getEventRelatedResources(uriEventUri);
			}
			**/			
			switch (objType) {
				case DISCO: 	
					uriList = rmapService.getEventRelatedDiSCOS(uriEventUri);
					break;
				case AGENT: 	
					uriList = rmapService.getEventRelatedAgents(uriEventUri);
					break;
				case OBJECT: 	
					uriList = rmapService.getEventRelatedDiSCOS(uriEventUri);
					uriList.addAll(rmapService.getEventRelatedAgents(uriEventUri));
					break;
				default:
					uriList = rmapService.getEventRelatedDiSCOS(uriEventUri);
					uriList.addAll(rmapService.getEventRelatedAgents(uriEventUri));
					break;
			}

								
			if (uriList==null)	{ 
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTRELATEDLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.PLAIN_TEXT)	{	
				outputString= URIListHandler.uriListToPlainText(uriList);	
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, objType.getPath().toString());		
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (Utils.makeEventUrl(strEventUri)))
							.build();    			
	        }
			
			reqSuccessful = true;
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
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
    	return response;
	}
	
}
