package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.BasicOutputType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapStatementNotFoundException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.statement.RMapStatement;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
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

	private static RMapService rmapService = null;

	public StatementResponseManager() {
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
	 * Displays Statement Service Options
	 * @return
	 * @throws RMapApiException
	 */
	public Response getStatementServiceOptions() throws RMapApiException {
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/statement"),DC.DESCRIPTION.toString())
					.build();

		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;  
	}


	/**
	 * Displays Statement Service Options Header
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getStatementServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/statement"),DC.DESCRIPTION.toString())
					.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		return response; 
	}
	
	

	/**
	 * Retrieves RMap Statement in requested RDF format and forms an HTTP response.
	 * @param strStatementUri
	 * @param acceptType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getRMapStatement(String strStatementUri, String acceptsType) throws RMapApiException	{
		Response response = null;
		try {
			if (strStatementUri==null || strStatementUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (acceptsType==null || acceptsType.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_ACCEPT_TYPE_PROVIDED); 
			}
			
			URI uriStatementUri = null;
			String strStatementUriDecoded = null;
			try {
				strStatementUriDecoded = URLDecoder.decode(strStatementUri, "UTF-8");
				uriStatementUri = new URI(strStatementUriDecoded);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
    		RMapStatement rmapStatement = rmapService.readStatement(uriStatementUri);
			if (rmapStatement ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_STMT_RETURNED_NULL);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			
    		OutputStream statementOutput = rdfHandler.statement2Rdf(rmapStatement, acceptsType);
			if (statementOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}	

    		RMapStatus status = rmapService.getStatementStatus(uriStatementUri);
    		if (status==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}

    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";
    		String eventUrl = URLUtils.getStmtBaseUrl() + strStatementUri + "/events";
        	linkRel = linkRel.concat(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
    				   	
		    response = Response.status(Response.Status.OK)
						.entity(statementOutput.toString())
						.location(new URI(URLUtils.makeStmtUrl(strStatementUriDecoded)))
        				.header("Link",linkRel)						//switch this to link() or links()?
        				.type("application/vnd.rmap-project.statement; version=1.0-beta") //TODO move version number to a property?
						.build();

		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapStatementNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_STMT_OBJECT_NOT_FOUND);			
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
		return response;
	}


	/**
	 * Retrieves RMap Statement header and forms an HTTP response.
	 * @param strStatementUri
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getRMapStatementHeader(String strStatementUri) throws RMapApiException	{
		Response response = null;
		try {
			if (strStatementUri==null || strStatementUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			
			URI uriStatementUri = null;
			String strDecodedStatementUri = null;
			try {
				strDecodedStatementUri = URLDecoder.decode(strStatementUri, "UTF-8");
				uriStatementUri = new URI(strStatementUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
    		RMapStatus status = rmapService.getStatementStatus(uriStatementUri);
    		if (status==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		
    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";
    		String eventUrl = URLUtils.getStmtBaseUrl() + strStatementUri + "/events";
    		linkRel = linkRel.concat(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
    				   	
		    response = Response.status(Response.Status.OK)
						.location(new URI(URLUtils.makeStmtUrl(strDecodedStatementUri)))
        				.header("Link",linkRel)						//switch this to link() or links()?
        				.type("application/vnd.rmap-project.statement; version=1.0-beta") //TODO move version number to a property?
						.build();

		}
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapStatementNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_STMT_OBJECT_NOT_FOUND);			
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
		return response;
	}
	
	

	/**
	 * Searches for RMap Statement based on subject, object, predicate defined and returns 
	 * in requested RDF format as an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	public Response getRMapStatementID(String subject, String predicate, String object) throws RMapApiException	{
		Response response = null;
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
			
			RMapUri rmapSubject = null;
			RMapUri rmapPredicate = null;
			RMapValue rmapObject = null;
			
			try {
				subject = URLDecoder.decode(subject, "UTF-8");
				predicate = URLDecoder.decode(predicate, "UTF-8");
				object = URLDecoder.decode(object, "UTF-8");
				rmapSubject = new RMapUri(new URI(subject));
				rmapPredicate = new RMapUri(new URI(predicate));
			}
			catch (Exception ex){
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			try {
				rmapObject = new RMapUri(new URI(object));
			}
			catch (URISyntaxException e) { //it's not a URI, make it a literal
				rmapObject = new RMapLiteral(object);
			}
			
			initRMapService();
			
			URI stmtURI = rmapService.getStatementID(rmapSubject, rmapPredicate, rmapObject);
			if (stmtURI == null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STMTID_RETURNED_NULL);
			}
			
			response = Response.status(Response.Status.OK)
						.entity(stmtURI.toString())
						.location(new URI (URLUtils.makeStmtUrl(stmtURI.toString())))
						.build();
	    }
		catch(RMapApiException ex)	{
        	throw RMapApiException.wrap(ex);
		}
		catch(RMapStatementNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_STMT_OBJECT_NOT_FOUND);			
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
		return response;
	}
	

	/**
	 * Retrieves list of RMap:Event URIs associated with the RMap:Statement URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param strStatementUri
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	public Response getRMapStatementRelatedEvents(String strStatementUri, BasicOutputType returnType) throws RMapApiException {
		Response response = null;
		try {
			if (strStatementUri==null || strStatementUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}

			URI uriStatementUri = null;
			try {
				strStatementUri = URLDecoder.decode(strStatementUri, "UTF-8");
				uriStatementUri = new URI(strStatementUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			
			List <URI> uriList = rmapService.getStatementEvents(uriStatementUri);
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==BasicOutputType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (URLUtils.makeStmtUrl(strStatementUri)))
							.build();    			
	        }
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapDiSCONotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_STMT_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_STMT_BAD_ARGUMENT);
		}
    	catch(RMapException ex) { 
    		throw RMapApiException.wrap(ex, ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
    	}
		catch(Exception ex)	{
    		throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
    	return response;
	}
	
}
