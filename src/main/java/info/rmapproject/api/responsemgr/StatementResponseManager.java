package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.ObjType;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;

/**
 * 
 * Creates HTTP responses for Statement REST API requests
 * @author khanson
 *
 */
public class StatementResponseManager {

	public StatementResponseManager() {
	}		
	
	private static final String DEFAULT_STATUS_FILTER ="all";
	private static final NonRdfType DEFAULT_NONRDF_TYPE = NonRdfType.JSON;
	
	/**
	 * Displays Statement Service Options
	 * @return
	 * @throws RMapApiException
	 */
	public Response getStatementServiceOptions() throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)	
					.build();
				
			reqSuccessful=true;

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
	 * Displays Statement Service Options Header
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getStatementServiceHead() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {		
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>;rel=\"" + DC.DESCRIPTION.toString() + "\"";		
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)	
					.build();
			
		reqSuccessful=true;
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
	 * Retrieves RMap Agents related to subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param status
	 * @param systemAgents (comma separated)
	 * @param dateFrom
	 * @param dateTo
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStatementRelatedAgents(String subject, String predicate, 
												String object, String status, 
												String systemAgents, String dateFrom, 
												String dateTo, NonRdfType returnType) throws RMapApiException	{
		return this.getStatementRelatedObjs(subject, predicate, object, status, systemAgents, dateFrom, dateTo, returnType, ObjType.AGENTS);
	}	
		
	/**
	 * Retrieves RMap DiSCOs related to subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param status
	 * @param systemAgents (comma separated)
	 * @param dateFrom
	 * @param dateTo
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStatementRelatedDiSCOs(String subject, String predicate, 
												String object, String status, 
												String systemAgents, String dateFrom, 
												String dateTo, NonRdfType returnType) throws RMapApiException	{
		return this.getStatementRelatedObjs(subject, predicate, object, status, systemAgents, dateFrom, dateTo, returnType, ObjType.DISCOS);
	}	
	
	/**
	 * Retrieves RMap Objects related to subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param status
	 * @param systemAgents (comma separated)
	 * @param dateFrom
	 * @param dateTo
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStatementRelatedObjs(String subject, String predicate, 
											String object, String status, 
											String systemAgents, String dateFrom, 
											String dateTo, NonRdfType returnType, 
											ObjType objectType) throws RMapApiException	{
		Response response = null;
		RMapService rmapService = null;
		try {
			if (subject==null || subject.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_SUBJECT_PROVIDED); 
			}
			if (predicate==null || predicate.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_PREDICATE_PROVIDED); 
			}
			if (object==null || object.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_OBJECT_PROVIDED); 
			}
			if (returnType==null) {returnType = DEFAULT_NONRDF_TYPE;}
			if (status==null) {status = DEFAULT_STATUS_FILTER;}
			if (objectType == null)	{objectType = ObjType.ALL;}
			

			subject = URLDecoder.decode(subject, "UTF-8");
			subject = RestApiUtils.removeUriAngleBrackets(subject);
			URI rmapSubject = new URI(subject);

			predicate = URLDecoder.decode(predicate, "UTF-8");
			predicate = RestApiUtils.removeUriAngleBrackets(predicate);
			URI rmapPredicate = new URI(predicate);

			object = URLDecoder.decode(object, "UTF-8");
			RMapValue rmapObject = RestApiUtils.convertObjectStringToRMapValue(object);
			RMapStatus rmapStatus = RestApiUtils.convertToRMapStatus(status);
			List <URI> systemAgentList = RestApiUtils.convertUriCsvToUriList(systemAgents);
			Date dDateFrom = RestApiUtils.convertStringDateToDate(dateFrom);
			Date dDateTo = RestApiUtils.convertStringDateToDate(dateTo);
						
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
						
			List<URI> matchingObjects = new ArrayList<URI>();
			
			if (objectType.equals(ObjType.DISCOS)) {
				matchingObjects = rmapService.getStatementRelatedDiSCOs(rmapSubject, rmapPredicate, rmapObject, 
																		rmapStatus, systemAgentList, dDateFrom, dDateTo);
			}
			else if (objectType.equals(ObjType.AGENTS)) {
				matchingObjects = rmapService.getStatementRelatedAgents(rmapSubject, rmapPredicate, rmapObject, 
																		rmapStatus, systemAgentList, dDateFrom, dDateTo);				
			}
			if (matchingObjects == null){
				throw new RMapApiException(ErrorCode.ER_CORE_COULDNT_RETRIEVE_STMT_RELATEDOBJS);
			}
			
			if (matchingObjects.size()==0){
				throw new RMapApiException(ErrorCode.ER_STMT_NOT_FOUND);
			}
			
			String outputString = "";
			if (returnType == NonRdfType.PLAIN_TEXT)	{
				outputString= URIListHandler.uriListToPlainText(matchingObjects);
			}
			else	{
				outputString= URIListHandler.uriListToJson(matchingObjects, objectType.getObjTypeLabel());		
			}
			response = Response.status(Response.Status.OK)
						.entity(outputString)
						.type(HttpTypeMediator.getResponseNonRdfMediaType(returnType))
						.build();
	    }
		catch (URISyntaxException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch (UnsupportedEncodingException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapObjectNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);			
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
			if (rmapService!=null){
				rmapService.closeConnection();
			}
		}
		return response;
	}

	/**
	 * Retrieves RMap System Agents that asserted a statement with subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param status
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStatementAssertingAgents(String subject, String predicate, String object, String status, 
				String dateFrom, String dateTo, NonRdfType returnType) throws RMapApiException	{
		Response response = null;
		RMapService rmapService = null;
		try {
			if (subject==null || subject.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_SUBJECT_PROVIDED); 
			}
			if (predicate==null || predicate.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_PREDICATE_PROVIDED); 
			}
			if (object==null || object.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_STMT_OBJECT_PROVIDED); 
			}
			if (status==null) {status = DEFAULT_STATUS_FILTER;}
			if (returnType==null) {returnType = DEFAULT_NONRDF_TYPE;}
			
			subject = URLDecoder.decode(subject, "UTF-8");
			predicate = URLDecoder.decode(predicate, "UTF-8");
			object = URLDecoder.decode(object, "UTF-8");
			
			URI rmapSubject = new URI(subject);
			URI rmapPredicate = new URI(predicate);
			RMapValue rmapObject = RestApiUtils.convertObjectStringToRMapValue(object);
			RMapStatus rmapStatus = RestApiUtils.convertToRMapStatus(status);
			Date dDateFrom = RestApiUtils.convertStringDateToDate(dateFrom);
			Date dDateTo = RestApiUtils.convertStringDateToDate(dateTo);
						
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			List<URI> matchingObjects = new ArrayList<URI>();
			matchingObjects = rmapService.getStatementAssertingAgents(rmapSubject, rmapPredicate, rmapObject, rmapStatus, dDateFrom, dDateTo);
			if (matchingObjects == null){
				throw new RMapApiException(ErrorCode.ER_CORE_COULDNT_RETRIEVE_STMT_ASSERTINGAGTS);
			}
			
			String outputString = "";
			if (returnType == NonRdfType.PLAIN_TEXT)	{	
				outputString= URIListHandler.uriListToPlainText(matchingObjects);
			}
			else	{
				outputString= URIListHandler.uriListToJson(matchingObjects, ObjType.AGENTS.getObjTypeLabel());	
			}
			response = Response.status(Response.Status.OK)
						.entity(outputString)
						.build();
	    }
		catch (URISyntaxException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch (UnsupportedEncodingException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapObjectNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);			
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
			if (rmapService!=null){
				rmapService.closeConnection();
			}
		}
		return response;
	}

}
