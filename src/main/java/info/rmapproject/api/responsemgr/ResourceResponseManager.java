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
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapObjectType;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rmapservice.RMapService;

import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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
	 * @param uriInfo
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapResourceRelatedObjs(String strResourceUri, 
												RMapObjectType objType, 
												NonRdfType returnType, 
												MultivaluedMap<String,String> queryParams) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (objType == null)	{objType = RMapObjectType.DISCO;}
			if (returnType==null) {returnType = Constants.DEFAULT_NONRDF_TYPE;}
						
			URI uriResourceUri = convertPathStringToURI(strResourceUri);
			RMapSearchParams params = generateSearchParamObj(queryParams);
			
			Integer limit=params.getLimit();
			//we are going to get one extra record to see if we need a "next"
			params.setLimit(limit+1);
			
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
				//if the object is found, should always have at least one object
				throw new RMapApiException(ErrorCode.ER_CORE_GET_URILIST_EMPTY); 
			}	
			
			ResponseBuilder responseBldr = null;
			
			//if the list is longer than the limit and there is currently no page defined, then do 303 with pagination
			if (!queryParams.containsKey(PAGE_PARAM)
					&& uriList.size()>limit){  
				//start See Other response to indicate need for pagination
				String otherUrl = getPaginatedLinkTemplate(Utils.makeResourceUrl(strResourceUri), queryParams, limit);
				otherUrl = otherUrl.replace(PAGENUM_PLACEHOLDER, params.getPage().toString());
				responseBldr = Response.status(Response.Status.SEE_OTHER)
						.entity(ErrorCode.ER_RESPONSE_TOO_LONG_NEED_PAGINATION.getMessage())
						.location(new URI(otherUrl));		
			}
			else { 
				//show results list as normal
				if (returnType==NonRdfType.PLAIN_TEXT)	{		
					outputString= URIListHandler.uriListToPlainText(uriList);
				}
				else	{
					outputString= URIListHandler.uriListToJson(uriList, objType.getPath().toString());		
				}
				responseBldr = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.type(HttpTypeMediator.getResponseNonRdfMediaType(returnType));	;		

				if (uriList.size()>limit || params.getPage()>1) {
					boolean showNextLink=uriList.size()>limit;
					String pageLinks = 
							generatePaginationLinks(Utils.makeResourceUrl(strResourceUri), queryParams, limit, showNextLink);
					responseBldr.header("Link",pageLinks);
				}
			}
			
			response = responseBldr.build();	
		
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
	
	/**
	 * Generate HTTP Response for list of RDF triples that reference the resource URI provided.
	 * Graph is filtered according to query params provided.
	 * @param strResourceUri
	 * @param returnType
	 * @param queryParams
	 * @return
	 * @throws RMapApiException
	 */
	public Response getRMapResourceTriples(String strResourceUri, RdfMediaType returnType,
											MultivaluedMap<String,String> queryParams) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (returnType == null)	{returnType = Constants.DEFAULT_RDF_TYPE;}
			
			URI uriResourceUri = convertPathStringToURI(strResourceUri);
			RMapSearchParams params = generateSearchParamObj(queryParams);

			Integer limit=params.getLimit();
			//we are going to get one extra record to see if we need a "next"
			params.setLimit(limit+1);
			
			//get resource triples
			List <RMapTriple> stmtList = rmapService.getResourceRelatedTriples(uriResourceUri, params);
			if (stmtList==null)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_RDFSTMTLIST_EMPTY); 
			}	
			if (stmtList.size() == 0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMTS_FOUND_FOR_RESOURCE); 				
			}
			
			
			ResponseBuilder responseBldr = null;
			
			//if the list is longer than the limit and there is currently no page defined, then do 303 with pagination
			if (!queryParams.containsKey(PAGE_PARAM)
					&& stmtList.size()>limit){  
				//start See Other response to indicate need for pagination
				String otherUrl = getPaginatedLinkTemplate(Utils.makeResourceUrl(strResourceUri), queryParams, limit);
				otherUrl = otherUrl.replace(PAGENUM_PLACEHOLDER, params.getPage().toString());
				responseBldr = Response.status(Response.Status.SEE_OTHER)
						.entity(ErrorCode.ER_RESPONSE_TOO_LONG_NEED_PAGINATION.getMessage())
						.location(new URI(otherUrl));		
			}
			else { 				
				//convert to RDF
				OutputStream rdf = rdfHandler.triples2Rdf(stmtList, returnType.getRdfType());
				if (rdf == null){
					throw new RMapApiException(ErrorCode.ER_CORE_CANT_CREATE_STMT_RDF);					
				}
				
				responseBldr = Response.status(Response.Status.OK)
						.entity(rdf.toString())
						.type(returnType.getMimeType());		

				if (stmtList.size()>limit || params.getPage()>1) {
					boolean showNextLink=stmtList.size()>limit;
					String pageLinks = 
							generatePaginationLinks(Utils.makeResourceUrl(strResourceUri), queryParams, limit, showNextLink);
					responseBldr.header("Link",pageLinks);
				}
			}
			
			response = responseBldr.build();	
			
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
