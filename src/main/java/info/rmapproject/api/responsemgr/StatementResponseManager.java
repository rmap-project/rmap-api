package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;

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
	 * Retrieves RMap Statement header and forms an HTTP response.
	 * @param strStatementUri
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
//	public Response getRMapStatementHeader(String strStatementUri) throws RMapApiException	{
//		Response response = null;
//		RMapService rmapService = null;
//		try {
//			if (strStatementUri==null || strStatementUri.length()==0)	{
//				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
//			}		
//			
//			URI uriStatementUri = null;
//			String strDecodedStatementUri = null;
//			try {
//				strDecodedStatementUri = URLDecoder.decode(strStatementUri, "UTF-8");
//				uriStatementUri = new URI(strStatementUri);
//			}
//			catch (Exception ex)  {
//				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
//			}
//
//			rmapService = RMapServiceFactoryIOC.getFactory().createService();
//			if (rmapService ==null){
//				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
//			}
//			
//    		RMapStatus status = rmapService.getStatementStatus(uriStatementUri);
//    		if (status==null){
//				throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
//    		}
//    		
//    		String linkRel = "<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";
//    		String eventUrl = RestApiUtils.getStmtBaseUrl() + strStatementUri + "/events";
//    		linkRel = linkRel.concat(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
//    				   	
//		    response = Response.status(Response.Status.OK)
//						.location(new URI(RestApiUtils.makeStmtUrl(strDecodedStatementUri)))
//        				.header("Link",linkRel)						//switch this to link() or links()?
//        				.type("application/vnd.rmap-project.statement; version=1.0-beta") //TODO move version number to a property?
//						.build();
//
//		}
//		catch(RMapApiException ex)	{
//        	throw RMapApiException.wrap(ex);
//		}
//		/*catch(RMapStatementNotFoundException ex) {
//			throw RMapApiException.wrap(ex,ErrorCode.ER_STMT_OBJECT_NOT_FOUND);			
//		}*/
//		catch(RMapDefectiveArgumentException ex){
//			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_STMT_BAD_ARGUMENT);			
//		}
//    	catch(RMapException ex) {  
//        	throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
//    	}  
//		catch(Exception ex)	{
//        	throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
//		}
//		finally{
//			if (rmapService != null) {
//				rmapService.closeConnection();
//			}
//		}
//		return response;
//	}
	
	

	/**
	 * Searches for RMap Statement based on subject, object, predicate defined and returns 
	 * in requested RDF format as an HTTP response.
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return HTTP Response
	 * @throws RMapApiException
	 */	
	/*public Response getRMapStatementID(String subject, String predicate, String object) throws RMapApiException	{
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
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			URI stmtURI = rmapService.getStatementID(rmapSubject, rmapPredicate, rmapObject);
			if (stmtURI == null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STMTID_RETURNED_NULL);
			}
			
			response = Response.status(Response.Status.OK)
						.entity(stmtURI.toString())
						.location(new URI (RestApiUtils.makeStmtUrl(stmtURI.toString())))
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
		finally{
			if (rmapService!=null){
				rmapService.closeConnection();
			}
		}
		return response;
	}*/
	

	/**
	 * Retrieves list of RMap:Event URIs associated with the RMap:Statement URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param strStatementUri
	 * @param returnType
	 * @return HTTP Response
	 * @throws RMapApiException
	 */
	/*public Response getRMapStatementRelatedEvents(String strStatementUri, NonRdfType returnType) throws RMapApiException {
		Response response = null;
		RMapService rmapService = null;
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

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			String outputString="";
			
			List <URI> uriList = rmapService.getStatementEvents(uriStatementUri);
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (RestApiUtils.makeStmtUrl(strStatementUri)))
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
		finally{
			if (rmapService != null){
		    rmapService.closeConnection();
			}
		}
    	return response;
	}*/
	
}
