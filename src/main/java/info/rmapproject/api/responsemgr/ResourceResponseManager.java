package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.ObjType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;
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

	
	public ResourceResponseManager() {
	}		

	private static final String DEFAULT_STATUS_FILTER ="all";
	private static final RdfType DEFAULT_RDF_TYPE = RdfType.TURTLE;
	private static final NonRdfType DEFAULT_NONRDF_TYPE = NonRdfType.JSON;
	
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
	public Response getRMapResourceRelatedObjs(String strResourceUri, ObjType objType, 
												NonRdfType returnType, String status, String sysAgents, 
												String dateFrom, String dateTo) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (objType == null)	{objType = ObjType.ALL;}
			if (returnType==null) {returnType = DEFAULT_NONRDF_TYPE;}
			if (status == null)	{status = DEFAULT_STATUS_FILTER;}
			
			URI uriResourceUri = null;
			try {
				strResourceUri = URLDecoder.decode(strResourceUri, "UTF-8");
				uriResourceUri = new URI(strResourceUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			RMapStatus rmapStatus = RestApiUtils.convertToRMapStatus(status);
			List <URI> systemAgentList = RestApiUtils.convertUriCsvToUriList(sysAgents);
			Date dDateFrom = RestApiUtils.convertStringDateToDate(dateFrom);
			Date dDateTo = RestApiUtils.convertStringDateToDate(dateTo);
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			List <URI> uriList = null;
			String outputString="";

			//TODO: put these jsonTypes in here for now, but need to settle on what these should be and poss enum them.
			 switch (objType) {
	            case DISCOS:
					uriList = rmapService.getResourceRelatedDiSCOs(uriResourceUri, rmapStatus, systemAgentList, dDateFrom, dDateTo);
	                break;
	            case AGENTS:
					uriList = rmapService.getResourceRelatedAgents(uriResourceUri, systemAgentList, dDateFrom, dDateTo);
	                break;
	            default:
					uriList = rmapService.getResourceRelatedAll(uriResourceUri, rmapStatus, systemAgentList, dDateFrom, dDateTo);
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
				outputString= URIListHandler.uriListToJson(uriList, objType.getObjTypeLabel());		
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
	
	
	public Response getRMapResourceTriples(String strResourceUri, RdfType returnType, String status, String sysAgents, 
											String dateFrom, String dateTo) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			if (strResourceUri==null || strResourceUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}
			if (returnType == null)	{returnType = DEFAULT_RDF_TYPE;}
			if (status == null)	{status = DEFAULT_STATUS_FILTER;}
			
			URI uriResourceUri = null;
			try {
				strResourceUri = URLDecoder.decode(strResourceUri, "UTF-8");
				uriResourceUri = new URI(strResourceUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			List <RMapTriple> stmtList = null;

			RMapStatus rmapStatus = RestApiUtils.convertToRMapStatus(status);
			List <URI> systemAgentList = RestApiUtils.convertUriCsvToUriList(sysAgents);
			Date dDateFrom = RestApiUtils.convertStringDateToDate(dateFrom);
			Date dDateTo = RestApiUtils.convertStringDateToDate(dateTo);
			
			stmtList = rmapService.getResourceRelatedTriples(uriResourceUri, rmapStatus, systemAgentList, dDateFrom, dDateTo);
			 
			if (stmtList==null)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_RDFSTMTLIST_EMPTY); 
			}	
			
			if (stmtList.size() == 0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMTS_FOUND_FOR_RESOURCE); 				
			}

			//rdf handler
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			String rdfFormat = returnType.getRdfType();
			OutputStream rdf = rdfHandler.triples2Rdf(stmtList, rdfFormat);
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
