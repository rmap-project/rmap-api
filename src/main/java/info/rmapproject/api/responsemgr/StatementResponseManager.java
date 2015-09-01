package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.ObjType;
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
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStmtRelatedAgents(String subject, String predicate, String object, String status, NonRdfType returnType) throws RMapApiException	{
		return this.getStmtRelatedObjs(subject, predicate, object, status, returnType, ObjType.AGENTS);
	}	
		
	/**
	 * Retrieves RMap DiSCOs related to subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStmtRelatedDiSCOs(String subject, String predicate, String object, String status, NonRdfType returnType) throws RMapApiException	{
		return this.getStmtRelatedObjs(subject, predicate, object, status, returnType, ObjType.DISCOS);
	}	
	
	/**
	 * Retrieves RMap Objects related to subject/predicate/object provided and forms an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getStmtRelatedObjs(String subject, String predicate, String object, String status, NonRdfType returnType, ObjType objectType) throws RMapApiException	{
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
			if (objectType == null)	{objectType = ObjType.ALL;}
			
			subject = URLDecoder.decode(subject, "UTF-8");
			predicate = URLDecoder.decode(predicate, "UTF-8");
			object = URLDecoder.decode(object, "UTF-8");
			
			URI rmapSubject = new URI(subject);
			URI rmapPredicate = new URI(predicate);
			RMapValue rmapObject = RestApiUtils.convertObjectStringToRMapValue(object);
			RMapStatus rmapStatus = RestApiUtils.convertToRMapStatus(status);
						
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			
			List<URI> matchingObjects = new ArrayList<URI>();
			
			if (objectType.equals(ObjType.DISCOS)) {
				matchingObjects = rmapService.getStmtRelatedDiSCOs(rmapSubject, rmapPredicate, rmapObject, rmapStatus);
			}
			else if (objectType.equals(ObjType.AGENTS)) {
				matchingObjects = rmapService.getStmtRelatedAgents(rmapSubject, rmapPredicate, rmapObject, rmapStatus);				
			}
			if (matchingObjects == null){
				throw new RMapApiException(ErrorCode.ER_CORE_COULDNT_RETRIEVE_STMT_RELATEDOBJS);
			}
			
			String outputString = "";
			if (returnType == NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(matchingObjects, objectType.getObjTypeLabel());		
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(matchingObjects);
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
