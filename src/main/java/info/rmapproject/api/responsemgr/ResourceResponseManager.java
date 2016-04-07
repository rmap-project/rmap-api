package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.utils.Constants;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFType;
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
 * @author khanson
 * Creates HTTP responses for Resource REST API requests
 *
 */
public class ResourceResponseManager extends ResponseManager {
	
	/**
	 * Constructor autowires the RMapService and RDFHandler
	 * @param rmapService
	 * @param rdfHandler
	 * @throws RMapApiException
	 */
	@Autowired
	public ResourceResponseManager(RMapService rmapService, RDFHandler rdfHandler) throws RMapApiException {
		super(rmapService, rdfHandler);
	}
	
	/**
	 * Displays Resource Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getResourceServiceOptions() throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/resource>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
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
	 * Displays Resource Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getResourceServiceHead() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {			
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/resource>;rel=\"" + DC.DESCRIPTION.toString() + "\"";	
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
	 * Get RMap Resource related objects, output in format requested (currently JSON or PLAIN TEXT)
	 * @param strResourceUri
	 * @param objType
	 * @param returnType
	 * @param status
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapResourceRelatedObjs(String strResourceUri, RMapObjectType objType, 
												NonRdfType returnType, RMapSearchParams params) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (objType == null)	{objType = RMapObjectType.DISCO;}
			if (returnType==null) {returnType = Constants.DEFAULT_NONRDF_TYPE;}
						
			URI uriResourceUri = null;
			try {
				strResourceUri = URLDecoder.decode(strResourceUri, "UTF-8");
				uriResourceUri = new URI(strResourceUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			List <URI> uriList = null;
			String outputString="";
						
			switch (objType) {
	            case DISCO:
					uriList = rmapService.getResourceRelatedDiSCOs(uriResourceUri, params);
					break;
	            case AGENT:
					uriList = rmapService.getResourceAssertingAgents(uriResourceUri, params);
					break;
	            case EVENT:
					uriList = rmapService.getResourceRelatedEvents(uriResourceUri, params);
					break;
	            default:
					uriList = rmapService.getResourceRelatedDiSCOs(uriResourceUri, params);
					break;
			}
			 
			if (uriList==null)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
			 
			if (returnType == NonRdfType.PLAIN_TEXT)	{		
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
			else	{
				outputString= URIListHandler.uriListToJson(uriList, objType.getPath().toString());		
			}
			
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.build();    
			
			reqSuccessful = true;			
	        
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
    	return response;
	}	
	
	
	public Response getRMapResourceTriples(String strResourceUri, RDFType returnType, RMapSearchParams params) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (returnType == null)	{returnType = Constants.DEFAULT_RDF_TYPE;}
			
			URI uriResourceUri = null;
			try {
				strResourceUri = URLDecoder.decode(strResourceUri, "UTF-8");
				uriResourceUri = new URI(strResourceUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			List <RMapTriple> stmtList = null;
			stmtList = rmapService.getResourceRelatedTriples(uriResourceUri, params);
			 
			if (stmtList==null)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_RDFSTMTLIST_EMPTY); 
			}	
			
			if (stmtList.size() == 0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMTS_FOUND_FOR_RESOURCE); 				
			}
			OutputStream rdf = rdfHandler.triples2Rdf(stmtList, returnType);
			if (rdf == null){
				throw new RMapApiException(ErrorCode.ER_CORE_CANT_CREATE_STMT_RDF);					
			}
						
			response = Response.status(Response.Status.OK)
						.entity(rdf.toString())
						.build();    	
			
			reqSuccessful = true;		
	        
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
		return response;
	}
	
	
}
