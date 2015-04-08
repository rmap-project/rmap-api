package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.ListType;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapDeletedObjectException;
import info.rmapproject.core.exception.RMapDiSCONotFoundException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.exception.RMapTombstonedObjectException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.model.event.RMapEventDerivation;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.PROV;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.openrdf.model.vocabulary.DC;

/**
 * 
 * Creates HTTP responses for RMap DiSCO REST API requests
 * @author khanson
 *
 */

public class DiscoResponseManager {


	private static RMapService rmapService = null;

	public DiscoResponseManager() {
	}		

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


	/**
	 * Displays DiSCO Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getDiSCOServiceOptions() throws RMapApiException {
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/disco"),DC.DESCRIPTION.toString())
					.build();

		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_OPTIONS);
		}
		return response;  
	}


	/**
	 * Displays DiSCO Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getDiSCOServiceHead() throws RMapApiException	{
		Response response = null;
		try {				
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET,POST,PUT,DELETE")
					.link(new URI("http://rmapdns.ddns.net:8080/swagger/docs/disco"),DC.DESCRIPTION.toString())
					.build();
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_RETRIEVING_API_HEAD);
		}
		return response; 
	}


	/**
	 * Retrieves RMap DiSCO in requested RDF format and forms an HTTP response.
	 * @param strDiscoUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapDiSCO(String strDiscoUri, String acceptsType) throws RMapApiException	{
		Response response = getRMapDiSCO(strDiscoUri, acceptsType, false);
		return response;
	}
	
	
	/**
	 * Retrieves latest version of RMap DiSCO in requested RDF format and forms an HTTP response.
	 * @param strDiscoUri
	 * @param acceptsType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getLatestRMapDiSCOVersion(String strDiscoUri, String acceptsType) throws RMapApiException	{
		Response response = getRMapDiSCO(strDiscoUri, acceptsType, true);
		return response;
	}


	/**
	 * Using URI Provided, retrieves either the latest version or requested version of an RMap DiSCO 
	 * in RDF format specified and forms an HTTP response.
	 * @param strDiscoUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	private Response getRMapDiSCO(String strDiscoUri, String acceptsType, Boolean viewLatestVersion) throws RMapApiException	{
		Response response = null;
		try {			
			if (strDiscoUri==null || strDiscoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (acceptsType==null || acceptsType.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_ACCEPT_TYPE_PROVIDED); 
			}
			if (viewLatestVersion==null){viewLatestVersion=false;}

			URI uriDiscoUri = null;
			try {
				strDiscoUri = URLDecoder.decode(strDiscoUri, "UTF-8");
				uriDiscoUri = new URI(strDiscoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			initRMapService();
			
			RMapDiSCO rmapDisco = null;

			if (viewLatestVersion)	{
				rmapDisco = rmapService.readDiSCO(uriDiscoUri);
			}
			else {
				rmapDisco = rmapService.getDiSCOLatestVersion(uriDiscoUri);
			}
			
			if (rmapDisco ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_DISCO_RETURNED_NULL);
			}

			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}

			OutputStream discoOutput = rdfHandler.disco2Rdf(rmapDisco, acceptsType);
			if (discoOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}		
			
			String linkRel = buildGetDiscoLinks(uriDiscoUri);

			response = Response.status(Response.Status.OK)
					.entity(discoOutput.toString())
					.location(new URI(URLUtils.makeDiscoUrl(strDiscoUri)))
					.header("Link",linkRel)						//switch this to link() or links()?
					.type("application/vnd.rmap-project.disco; version=1.0-beta") //TODO move version number to a property?
					.build();  	

		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
		} 
		catch(RMapDiSCONotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
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
		return response;		
	}



	/**
	 * Retrieves RMap DiSCO metadata and returns it in an HTTP header-only response.
	 * @param strDiscoUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapDiSCOHeader(String strDiscoUri) throws RMapApiException	{
		Response response = null;
		try {			
			if (strDiscoUri==null || strDiscoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			URI uriDiscoUri = null;
			try {
				strDiscoUri = URLDecoder.decode(strDiscoUri, "UTF-8");
				uriDiscoUri = new URI(strDiscoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			String linkRel = buildGetDiscoLinks(uriDiscoUri);

			response = Response.status(Response.Status.OK)
					.location(new URI(URLUtils.makeDiscoUrl(strDiscoUri)))
					.header("Link",linkRel)						//switch this to link() or links()?
					.build();  
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
		} 
		catch(RMapDiSCONotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
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
	 * Creates new RMap:DiSCO from valid client-provided RDF.
	 * @param discoRdf
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response createRMapDiSCO(InputStream discoRdf, String contentType) throws RMapApiException {
		Response response = null;
		try	{ 
			if (discoRdf == null || discoRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_DISCO_RDF_PROVIDED);
			} 
			if (contentType == null || contentType.length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}

			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, URLUtils.getDiscoBaseUrl(), contentType);
			if (rmapDisco == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_DISCO_FAILED);
			}  

			initRMapService();

			//TODO This is temporary - SYSAGENT will eventually come from oauth module
			URI SYSAGENT_URI; 
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();

			RMapEventCreation discoEvent = (RMapEventCreation)rmapService.createDiSCO(new RMapUri(SYSAGENT_URI), rmapDisco);
			if (discoEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_CREATEDISCO_NOT_COMPLETED);
			} 

			URI uDiscoURI = rmapDisco.getId();  
			if (uDiscoURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_DISCOID_RETURNED_NULL);
			} 
			String sDiscoURI = uDiscoURI.toString();  
			if (sDiscoURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_DISCOURI_STRING_EMPTY);
			} 

			URI uEventURI = discoEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = URLUtils.makeEventUrl(sEventURI); 
			String newDiscoUrl = URLUtils.makeDiscoUrl(sDiscoURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

			response = Response.status(Response.Status.CREATED)
						.entity(sDiscoURI)
						.location(new URI(newDiscoUrl)) //switch this to location()
						.header("Link",linkRel)    //switch this to link()
						.build();   
			
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
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
	 * Updates RMap:DiSCO.  Does this by inactivating the previous version of the DiSCO and 
	 * creating a new version using valid client-provided RDF.
	 * @param origDiscoUri
	 * @param discoRdf
	 * @param contentType
	 * @return Response
	 * @throws RMapApiException
	 */

	public Response updateRMapDiSCO(String origDiscoUri, InputStream discoRdf, String contentType) throws RMapApiException {
		Response response = null;

		try	{		
			if (origDiscoUri==null || origDiscoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (discoRdf == null || discoRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_DISCO_RDF_PROVIDED);
			} 
			if (contentType == null || contentType.length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}
			
			URI uriOrigDiscoUri = null;
			try {
				origDiscoUri = URLDecoder.decode(origDiscoUri, "UTF-8");
				uriOrigDiscoUri = new URI(origDiscoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			RMapDiSCO newRmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, URLUtils.getDiscoBaseUrl(), contentType);
			if (newRmapDisco == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_DISCO_FAILED);
			}  

			initRMapService();

			//TODO This is temporary - SYSAGENT will eventually come from oauth module
			URI SYSAGENT_URI;
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();
			
			RMapEventDerivation discoEvent = (RMapEventDerivation)rmapService.updateDiSCO(new RMapUri(SYSAGENT_URI), 
					uriOrigDiscoUri, 
					newRmapDisco);
			
			if (discoEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_UPDATEDISCO_NOT_COMPLETED);
			} 
			
			URI uDiscoURI = newRmapDisco.getId();  
			if (uDiscoURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_DISCOID_RETURNED_NULL);
			}
			String sDiscoURI = uDiscoURI.toString();  
			if (sDiscoURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_DISCOURI_STRING_EMPTY);
			} 
			
			URI uEventURI = discoEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = URLUtils.makeEventUrl(sEventURI); 
			String prevDiscoUrl = URLUtils.makeDiscoUrl(origDiscoUri); 
			String newDiscoUrl = URLUtils.makeDiscoUrl(sDiscoURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
			linkRel.concat(",<" + prevDiscoUrl + ">" + ";rel=\"predecessor-version\"");
			
			response = Response.status(Response.Status.CREATED)
						.entity(sDiscoURI)
						.location(new URI(newDiscoUrl)) 
						.header("Link",linkRel)    //switch this to link()
						.build();   
    	
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
		} 
		catch(RMapDiSCONotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
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
	return response;	
	}
	
	
	/**
	 * Sets status of RMap:DiSCO to tombstoned.  
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response tombstoneRMapDiSCO(String discoUri) throws RMapApiException {
		return changeRMapDiSCOStatus(discoUri, "TOMBSTONED");
	}

	
	/**
	 * Sets status of RMap:DiSCO to inactive.  
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response inactivateRMapDiSCO(String discoUri) throws RMapApiException {
		return changeRMapDiSCOStatus(discoUri, "INACTIVE");
	}
	

	/**
	 * Sets status of RMap:DiSCO to tombstoned or inactive, depending on newStatus defined.  
	 * @param discoUri
	 * @param newStatus
	 * @return Response
	 * @throws RMapApiException
	 */
	private Response changeRMapDiSCOStatus(String discoUri, String newStatus) throws RMapApiException {
		Response response = null;

		try	{		
			if (discoUri==null || discoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriDiscoUri = null;
			try {
				discoUri = URLDecoder.decode(discoUri, "UTF-8");
				uriDiscoUri = new URI(discoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
						
			//TODO This is temporary - SYSAGENT will eventually come from oauth module
			URI SYSAGENT_URI; 
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();

			RMapEvent discoEvent = null;
			if (newStatus == "TOMBSTONED")	{
				discoEvent = (RMapEvent)rmapService.deleteDiSCO(uriDiscoUri, new RMapUri(SYSAGENT_URI));					
			}
			else if (newStatus == "INACTIVE")	{
				//TODO:this is incorrect - currently no inactivate disco method, so this is a placeholder!
				discoEvent = (RMapEvent)rmapService.updateDiSCO(new RMapUri(SYSAGENT_URI), uriDiscoUri, null);						
			}
				
			if (discoEvent == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_UPDATEDISCO_NOT_COMPLETED);
			} 
			
			URI uEventURI = discoEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 

			String newEventURL = URLUtils.makeEventUrl(sEventURI); 
			String origDiscoUrl = URLUtils.makeDiscoUrl(discoUri); 
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
					.location(new URI(origDiscoUrl)) 
					.header("Link",linkRel)    //switch this to link()
					.build();   
    	
		}
		catch(RMapApiException ex)	{
			throw RMapApiException.wrap(ex);
		}  
		catch(RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
		} 
		catch(RMapDiSCONotFoundException ex) {
			throw RMapApiException.wrap(ex,ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
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
	return response;		
		
	}

	
	/**
	 * Gets list of RMap:DiSCO version URIs and returns them as JSON or Plain Text. 
	 * Set retAgentVersionsOnly to true to return the list of version that match the 
	 * system Agent of the discoUri parameter.
	 * @param discoUri
	 * @param listType
	 * @param retAgentVersionsOnly
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapDiSCOVersions(String discoUri, ListType returnType, Boolean retAgentVersionsOnly) throws RMapApiException {

		Response response = null;
		try {
			//assign default values when null
			if (returnType==null)	{returnType=ListType.PLAIN_TEXT;}
			if (retAgentVersionsOnly==null)	{retAgentVersionsOnly=false;}
			
			//check discoUri param for null
			if (discoUri==null || discoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriDiscoUri = null;
			try {
				discoUri = URLDecoder.decode(discoUri, "UTF-8");
				uriDiscoUri = new URI(discoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			List <URI> uriList = null;
		
			if (retAgentVersionsOnly)	{
				uriList = rmapService.getDiSCOAllAgentVersions(uriDiscoUri);				
			}
			else	{
				uriList = rmapService.getDiSCOAllVersions(uriDiscoUri);						
			}

			if (uriList==null || uriList.size()==0)	{ 
				//should always have at least one version... the one being requested!
				throw new RMapApiException(ErrorCode.ER_CORE_GET_DISCO_VERSIONLIST_EMPTY); 
			}	
									
			if (returnType == ListType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:DiSCOs");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}

		    			
			response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (URLUtils.makeStmtUrl(discoUri)))
							.build();

		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapDiSCONotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
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
	 * Retrieves list of RMap:Event URIs associated with the RMap:DiSCO URI provided and returns 
	 * the results as a JSON or Plain Text list.
	 * @param discoUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getRMapDiSCOEvents(String discoUri, ListType returnType) throws RMapApiException {

		Response response = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=ListType.PLAIN_TEXT;}
			
			if (discoUri==null || discoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			
			URI uriDiscoUri = null;
			try {
				discoUri = URLDecoder.decode(discoUri, "UTF-8");
				uriDiscoUri = new URI(discoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}
			
			initRMapService();
			
			String outputString="";
			List <URI> uriList = rmapService.getDiSCOEvents(uriDiscoUri);						
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
							.location(new URI (URLUtils.makeDiscoUrl(discoUri)))
							.build();
	        
		}
    	catch(RMapApiException ex) { 
    		throw RMapApiException.wrap(ex);
    	}  
    	catch(RMapDiSCONotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
    	}
    	catch(RMapObjectNotFoundException ex) {
    		throw RMapApiException.wrap(ex, ErrorCode.ER_OBJECT_NOT_FOUND);
    	}
		catch(RMapDefectiveArgumentException ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_GET_DISCO_BAD_ARGUMENT);
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
	 * Retrieves the string of links to DiSCO versions, Status and Events for HTTP Response header Link property.
	 * @param uriDiscoUri
	 * @return String
	 * @throws RMapApiException
	 * @throws RMapException
	 * @throws RMapDefectiveArgumentException 
	 */
	private String buildGetDiscoLinks(URI uriDiscoUri) throws RMapApiException, RMapException, RMapDefectiveArgumentException {
		StringBuilder links = new StringBuilder("");
		//TODO: refactor this - too much repetition... but version code may be changing, so leave for now.

		initRMapService();

		//get the DiSCO status link
		RMapStatus status = rmapService.getDiSCOStatus(uriDiscoUri);
		if (status==null){
			throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
		}
		links.append("<" + status.toString() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"");

		//get DiSCO version links
		RMapDiSCO latestDisco = rmapService.getDiSCOLatestVersion(uriDiscoUri);
		if (latestDisco!=null) {
			URI latestUri = latestDisco.getId();
			if (latestUri==null || latestUri.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_CORE_DISCO_VERSION_ID_MALFORMED);  				
			}
			links.append(",<" + URLUtils.makeDiscoUrl(latestUri.toString()) + ">" + ";rel=\"latest-version\"");  			
		}
		RMapDiSCO previousDisco = rmapService.getDiSCOPreviousVersion(uriDiscoUri);
		if (previousDisco!=null) {
			URI prevUri = previousDisco.getId();
			if (prevUri==null || prevUri.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_CORE_DISCO_VERSION_ID_MALFORMED);  				
			}
			links.append(",<" + URLUtils.makeDiscoUrl(prevUri.toString()) + ";rel=\"predecessor-version\"");
		}
		RMapDiSCO nextDisco = rmapService.getDiSCONextVersion(uriDiscoUri);
		if (nextDisco!=null){
			URI nextUri = nextDisco.getId();
			if (nextUri==null || nextUri.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_CORE_DISCO_VERSION_ID_MALFORMED);  				
			}
			links.append(",<" + URLUtils.makeDiscoUrl(nextUri.toString()) + ">" + ";rel=\"successor-version\"");
		}

		//get DiSCO event link
		String eventUrl = URLUtils.getDiscoBaseUrl() + "/events";
		links.append(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");

		return links.toString();	

	}

}
