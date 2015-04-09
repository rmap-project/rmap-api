package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.FilterObjType;
import info.rmapproject.api.utils.ListType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for Resource REST API requests
 *
 */
public class ResourceResponseManager {

	private static RMapService rmapService = null;
	
	public ResourceResponseManager() {
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
	 * Displays Resource Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getResourceServiceOptions() throws RMapApiException {
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/resource"),DC.DESCRIPTION.toString())
					.build();

		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;  
	}


	/**
	 * Displays Resource Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getResourceServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/resource"),DC.DESCRIPTION.toString())
					.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		return response; 
	}

	/**
	 * Get RMap Resource related objects, output in format requested (currently JSON or PLAIN TEXT)
	 * @param strResourceUri
	 * @param objType
	 * @param returnType
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapResourceRelatedObjs(String strResourceUri, FilterObjType objType, ListType returnType, RMapStatus status) throws RMapApiException {
		Response response = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (status == null)	{status = RMapStatus.ACTIVE;}
			if (objType == null)	{objType = FilterObjType.ALL;}
			
			URI uriResourceUri = null;
			try {
				strResourceUri = URLDecoder.decode(strResourceUri, "UTF-8");
				uriResourceUri = new URI(strResourceUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			List <URI> uriList = null;
			String outputString="";
			String jsonType="";

			//TODO: put these jsonTypes in here for now, but need to settle on what these should be and poss enum them.
			 switch (objType) {
	            case STATEMENTS:
					uriList = rmapService.getResourceRelatedStmts(uriResourceUri, status);
					jsonType = "rmap:Stmts";
	                break;
	            case DISCOS:
					uriList = rmapService.getResourceRelatedDiSCOs(uriResourceUri, status);
					jsonType = "rmap:Discos";
	                break;
	            case AGENTS:
					uriList = rmapService.getResourceRelatedAgents(uriResourceUri, status);
					jsonType = "rmap:Agents";
	                break;
	            default:
					uriList = rmapService.getResourceRelatedAll(uriResourceUri, status);
					jsonType = "rmap:Objects";
	                break;
			}
			 
			if (uriList==null)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
			 
			if (returnType == ListType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, jsonType);				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.location(new URI (URLUtils.makeResourceUrl(strResourceUri)))
						.build();    			
	        
		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapObjectNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);			
		}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_RESOURCE_BAD_ARGUMENT);			
		}
    	catch(RMapException ex) {  
        	throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
    	}  
		catch(Exception ex)	{
        	throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
    	return response;
	}	
	
	
}
