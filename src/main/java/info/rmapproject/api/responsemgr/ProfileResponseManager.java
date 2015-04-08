package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.ListType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapProfileNotFoundException;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;

/**
 * 
 * Creates HTTP responses for Profile REST API requests
 * @author khanson
 * 
 */

public class ProfileResponseManager {

	private static RMapService rmapService = null;

	/**
	 * Creates new RMapService object if not already initiated.
	 * @throws RMapApiException
	 * @throws RMapException
	 */	
	private static void initRMapService() throws RMapApiException, RMapException {
		if (rmapService == null){
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
		}
	}

	public ProfileResponseManager() {
	}		
	

	/**
	 * Displays Profile Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getProfileServiceOptions() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
						.entity("{\"description\":\"will show copy of swagger content\"}")
						.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
						.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		
		return response;    
	}
	
	
	/**
	 * Displays Profile Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getProfileServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
						.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
						.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/agent"),DC.DESCRIPTION.toString())
						.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;    
	}
	
	
	
	/**
	 * Retrieves RMap Profile in requested RDF format and forms an HTTP response.
	 * @param strProfileUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapProfile(String strProfileUri, String acceptType) throws RMapApiException	{
		Response response = null;/*
		try {	
			if (strProfileUri==null || strProfileUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (acceptType==null || acceptType.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_ACCEPT_TYPE_PROVIDED); 
			}
			
			URI uriProfileUri = null;
			try {
				strProfileUri = URLDecoder.decode(strProfileUri, "UTF-8");
				uriProfileUri = new URI(strProfileUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			initRMapService();
			
			RMapProfile rmapProfile = rmapService.readProfile(uriProfileUri);

			if (rmapProfile ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_PROFILE_RETURNED_NULL);
			}

			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}

    		OutputStream profileOutput = rdfHandler.profile2Rdf(rmapProfile, acceptType);
			if (profileOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}		
				       	
        	RMapStatus status = rmapService.getProfileStatus(uriProfileUri);
    		if (status==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
    		}
    		
    		String linkRel = "<" + status.toString() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"";
    		String eventUrl = URLUtils.getProfileBaseUrl() + "/events";
        	linkRel.concat(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
    		    		
        	response = Response.status(Response.Status.OK)
        				.entity(profileOutput.toString())
        				.location(new URI (URLUtils.makeProfileUrl(strProfileUri)))
        				.link(new URI (eventUrl), PROV.HAS_PROVENANCE.toString())
        				.build();		

		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
		} 
		catch(RMapProfileNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_PROFILE_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) {
			if (ex.getCause() instanceof RMapDeletedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_DELETED);  			
			}
			else if (ex.getCause() instanceof RMapTombstonedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_TOMBSTONED);  			
			}
			else if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}*/
		return response;	
    }
		
	
	
	/**
	 * Creates new RMap:Profile from valid client-provided RDF.
	 * @param profileRdf
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response createRMapProfile(InputStream profileRdf, String contentType) throws RMapApiException {
	Response response = null;
	/*	
		try	{
			if (profileRdf == null || profileRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_PROFILE_RDF_PROVIDED);
			} 
			if (contentType == null || contentType.length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			RMapProfile rmapProfile = rdfHandler.rdf2RMapProfile(profileRdf, URLUtils.getProfileBaseUrl(), contentType);
			if (rmapProfile == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_PROFILE_FAILED);
			}  
			
			initRMapService();

			//TODO This is temporary - SYSAGENT will eventually come from oauth module
			URI SYSAGENT_URI; 
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();
			
			RMapEventCreation profileEvent = (RMapEventCreation)rmapService.createProfile(new RMapUri(SYSAGENT_URI), rmapProfile);
			if (profileEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_CREATEPROFILE_NOT_COMPLETED);
			} 

			URI uProfileURI = rmapProfile.getId();  
			if (uProfileURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_PROFILEID_RETURNED_NULL);
			} 
			String sProfileURI = uProfileURI.toString();  
			if (sProfileURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_PROFILEURI_STRING_EMPTY);
			} 

			URI uEventURI = profileEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = URLUtils.makeEventUrl(sEventURI); 
			String newProfileUrl = URLUtils.makeProfileUrl(sProfileURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

			response = Response.status(Response.Status.CREATED)
					.entity(sProfileURI)
					.location(new URI(newProfileUrl)) //switch this to location()
					.header("Link",linkRel)    //switch this to link()
					.build();  
					
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
		} 
		catch(RMapException ex) { 
			throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  			
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
*/
	return response;  
	}
	

	/**
	 * Sets status of RMap:Profile to be tombstoned.  
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response tombstoneRMapProfile(String profileUri) throws RMapApiException {
		return changeRMapProfileStatus(profileUri, "TOMBSTONED");
	}

	
	/**
	 * Sets status of RMap:Profile to inactive.  
	 * @param profileUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response inactivateRMapProfile(String profileUri) throws RMapApiException {
		return changeRMapProfileStatus(profileUri, "INACTIVE");
	}
	

	/**
	 * Sets status of RMap:Profile to tombstoned or inactive, depending on newStatus defined.  
	 * @param profileUri
	 * @param newStatus
	 * @return Response
	 * @throws RMapApiException
	 */
	private Response changeRMapProfileStatus(String profileUri, String newStatus) throws RMapApiException {
		Response response = null;
/*
		try	{		
			if (profileUri==null || profileUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriProfileUri = null;
			try {
				profileUri = URLDecoder.decode(profileUri, "UTF-8");
				uriProfileUri = new URI(profileUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
						
			//TODO This is temporary - SYSAGENT will eventually come from oauth module
			URI SYSAGENT_URI; 
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();

			RMapEvent profileEvent = null;
			if (newStatus == "TOMBSTONED")	{
				profileEvent = (RMapEvent)rmapService.deleteProfile(uriProfileUri, new RMapUri(SYSAGENT_URI));					
			}
			else if (newStatus == "INACTIVE")	{
				//TODO:this is incorrect - currently no inactivate profile method, so this is a placeholder!
				profileEvent = (RMapEvent)rmapService.updateProfile(new RMapUri(SYSAGENT_URI), uriProfileUri, null);						
			}
				
			if (profileEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_UPDATEPROFILE_NOT_COMPLETED);
			} 
			
			URI uEventURI = profileEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = URLUtils.makeEventUrl(sEventURI); 
			String origProfileUrl = URLUtils.makeProfileUrl(profileUri); 
			String linkRel = "";
			
			if (newStatus == "TOMBSTONED")	{
				//TODO: EVENT_TYPE_TOMBSTONE a place holder - need to consider what this should be.
				linkRel = "<" + newEventURL + ">" + ";rel=\"" + RMAP.EVENT_TYPE_TOMBSTONE + "\"";
			}
			else if (newStatus == "INACTIVE")	{
				//TODO: EVENT_TYPE_INACTIVATION a place holder - need to consider what this should be.
				linkRel = "<" + newEventURL + ">" + ";rel=\"" + RMAP.EVENT_TYPE_INACTIVATION + "\"";
			}
			
			response = Response.status(Response.Status.OK)
					.location(new URI(origProfileUrl)) 
					.header("Link",linkRel)    //switch this to link()
					.build();   
    	
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
		} 
		catch(RMapProfileNotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_PROFILE_OBJECT_NOT_FOUND);
		} 
		catch(RMapException ex) { 
			if (ex.getCause() instanceof RMapDeletedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_DELETED);  			
			}
			else if (ex.getCause() instanceof RMapTombstonedObjectException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_TOMBSTONED);  			
			}
			else if (ex.getCause() instanceof RMapObjectNotFoundException){
				throw RMapApiException.wrap(ex,ErrorCode.ER_OBJECT_NOT_FOUND);  			
			}
			else {
				throw RMapApiException.wrap(ex,ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);  					
			}
		}  
		catch(Exception ex)	{
			throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
	*/
	return response;		
		
	}


	/**
	 * Retrieves list of RMap:Event URIs associated with the RMap:Profile URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param profileUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapProfileEvents(String profileUri, ListType returnType) throws RMapApiException {

		Response response = null;
		/*
		try {
			//assign default value when null
			if (returnType==null)	{returnType=ListType.PLAIN_TEXT;}
			
			if (profileUri==null || profileUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriProfileUri = null;
			try {
				profileUri = URLDecoder.decode(profileUri, "UTF-8");
				uriProfileUri = new URI(profileUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			List <URI> uriList = rmapService.getProfileEvents(uriProfileUri);						
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTLIST_EMPTY); 
			}	
									
			if (returnType==ListType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
		    			
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.location(new URI (URLUtils.makeProfileUrl(profileUri)))
						.build();
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapProfileNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_PROFILE_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
		}
    	catch(RMapException ex) { 
    		throw RMapApiException.wrap(ex, ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
    	}
		catch(Exception ex)	{
    		throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
	*/
    	return response;
	}
	

	/**
	 * Retrieves list of Related Identity URIs associated with the RMap:Profile URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param profileUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapProfileRelatedIdentities(String profileUri, ListType returnType) throws RMapApiException {

		Response response = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=ListType.PLAIN_TEXT;}
			
			if (profileUri==null || profileUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriProfileUri = null;
			try {
				profileUri = URLDecoder.decode(profileUri, "UTF-8");
				uriProfileUri = new URI(profileUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			List <URI> uriList = rmapService.getProfileRelatedIdentities(uriProfileUri);						
			if (uriList==null || uriList.size()==0)	{ 
				//if the object is found, should always have at least one event
				throw new RMapApiException(ErrorCode.ER_CORE_GET_IDENTITYLIST_EMPTY); 
			}	
									
			if (returnType==ListType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Identities");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}
			    			
			response = Response.status(Response.Status.OK)
						.entity(outputString.toString())
						.location(new URI (URLUtils.makeProfileUrl(profileUri)))
						.build();
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapProfileNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_PROFILE_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
		}
    	catch(RMapException ex) { 
    		throw RMapApiException.wrap(ex, ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
    	}
		catch(Exception ex)	{
    		throw RMapApiException.wrap(ex,ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
    	return response;
	}

	/**
	 * Retrieves Preferred Identity URI associated with the RMap:Profile URI provided
	 * @param profileUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapProfilePreferredIdentity(String profileUri) throws RMapApiException {

		Response response = null;
		try {			
			if (profileUri==null || profileUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriProfileUri = null;
			try {
				profileUri = URLDecoder.decode(profileUri, "UTF-8");
				uriProfileUri = new URI(profileUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			URI prefIdUri = rmapService.getProfilePreferredIdentity(uriProfileUri);						
			if (prefIdUri==null)	{ 
				throw new RMapApiException(ErrorCode.ER_CORE_GET_PREFERREDID_RETURNED_NULL); 
			}	
    		  			
			response = Response.status(Response.Status.OK)
						.entity(prefIdUri)
						.location(new URI (URLUtils.makeProfileUrl(profileUri)))
						.build();
	        
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapProfileNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_PROFILE_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_PROFILE_BAD_ARGUMENT);
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
