package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.HttpTypeMediator;
import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.RestApiUtils;
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
import java.net.URLEncoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.vocabulary.DC;

/**
 * 
 * Creates HTTP responses for RMap DiSCO REST API requests
 * @author khanson
 *
 */

public class DiscoResponseManager {

	public DiscoResponseManager() {
	}		
	
	private final Logger log = LogManager.getLogger(this.getClass()); 


	/**
	 * Displays DiSCO Service Options
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getDiSCOServiceOptions() throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET,POST,PATCH,DELETE")
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
	 * Displays DiSCO Service Options Header
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getDiSCOServiceHead() throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		try {				
			String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/disco>;rel=\"" + DC.DESCRIPTION.toString() + "\"";
			response = Response.status(Response.Status.OK)
					.header("Allow", "HEAD,OPTIONS,GET,POST,PATCH,DELETE")
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
	 * Retrieves RMap DiSCO in requested RDF format and forms an HTTP response.
	 * @param strDiscoUri
	 * @param acceptType
	 * @return Response
	 * @throws RMapApiException
	 */	
	public Response getRMapDiSCO(String strDiscoUri, RdfType returnType) throws RMapApiException	{
		Response response = getRMapDiSCO(strDiscoUri, returnType, false);
		return response;
	}
	
	
	/**
	 * Retrieves latest version of RMap DiSCO in requested RDF format and forms an HTTP response.
	 * @param strDiscoUri
	 * @param returnType
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response getLatestRMapDiSCOVersion(String strDiscoUri, RdfType returnType) throws RMapApiException	{
		Response response = getRMapDiSCO(strDiscoUri, returnType, true);
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
	private Response getRMapDiSCO(String strDiscoUri, RdfType returnType, Boolean viewLatestVersion) throws RMapApiException	{
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {			
						
			log.info("DiSCO " + strDiscoUri + " requested.");
			
			if (strDiscoUri==null || strDiscoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}		
			if (returnType==null)	{
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

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			RMapDiSCO rmapDisco = null;

			if (viewLatestVersion)	{
				rmapDisco = rmapService.getDiSCOLatestVersion(uriDiscoUri);
			}
			else {
				rmapDisco = rmapService.readDiSCO(uriDiscoUri);
			}

			log.info("DiSCO " + strDiscoUri + " object retrieved.");
			
			if (rmapDisco ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_READ_DISCO_RETURNED_NULL);
			}

			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}

			OutputStream discoOutput = rdfHandler.disco2Rdf(rmapDisco, returnType.getRdfType());
			if (discoOutput ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_RDFHANDLER_OUTPUT_ISNULL);
			}		

			log.info("DiSCO " + strDiscoUri + " converted to RDF.");
			
			String linkRel = buildGetDiscoLinks(uriDiscoUri);

			response = Response.status(Response.Status.OK)
					.entity(discoOutput.toString())
					.location(new URI(RestApiUtils.makeDiscoUrl(strDiscoUri)))
					.header("Link",linkRel)						//switch this to link() or links()?
					.type(HttpTypeMediator.getResponseMediaType("disco", returnType)) //TODO move version number to a property?
					.build(); 
			
			reqSuccessful = true;

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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
		boolean reqSuccessful = false;
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
					.location(new URI(RestApiUtils.makeDiscoUrl(strDiscoUri)))
					.header("Link",linkRel)						//switch this to link() or links()?
					.build();  
			
			reqSuccessful = true;
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
		finally{
			if (!reqSuccessful && response!=null) response.close();
		}
		return response;	
	}



	/**
	 * Creates new RMap:DiSCO from valid client-provided RDF.
	 * @param discoRdf
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response createRMapDiSCO(InputStream discoRdf, RdfType contentType, URI sysAgentUri) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try	{ 

			log.info("New DiSCO create request initiated (id=" + discoRdf.hashCode() + ")");
			
			if (discoRdf == null || discoRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_DISCO_RDF_PROVIDED);
			} 
			if (contentType == null){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}
			if (sysAgentUri==null)	{
				throw new RMapApiException(ErrorCode.ER_NO_SYSTEMAGENT_PROVIDED); 
			}	
						
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			if (rdfHandler ==null){
				throw new RMapApiException(ErrorCode.ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL);
			}
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, RestApiUtils.getDiscoBaseUrl(), contentType.getRdfType());
			if (rmapDisco == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_DISCO_FAILED);
			}  

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			RMapEventCreation discoEvent = (RMapEventCreation)rmapService.createDiSCO(new RMapUri(sysAgentUri), rmapDisco);
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

			log.info("New DiSCO created (id=" + discoRdf.hashCode() + ") with URI " + sDiscoURI);
			
			URI uEventURI = discoEvent.getId();  
			if (uEventURI==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_EVENTID_RETURNED_NULL);
			} 
			String sEventURI = uEventURI.toString();  
			if (sEventURI.length() == 0){
				throw new RMapApiException(ErrorCode.ER_CORE_EVENTURI_STRING_EMPTY);
			} 
			
			String newEventURL = RestApiUtils.makeEventUrl(sEventURI); 
			String newDiscoUrl = RestApiUtils.makeDiscoUrl(sDiscoURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";

			response = Response.status(Response.Status.CREATED)
						.entity(sDiscoURI)
						.location(new URI(newDiscoUrl)) //switch this to location()
						.header("Link",linkRel)    //switch this to link()
						.build(); 
			
			reqSuccessful = true;  
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
	public Response updateRMapDiSCO(String origDiscoUri, InputStream discoRdf, RdfType contentType, URI sysAgentUri) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try	{		
			if (origDiscoUri==null || origDiscoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (discoRdf == null || discoRdf.toString().length()==0){
				throw new RMapApiException(ErrorCode.ER_NO_DISCO_RDF_PROVIDED);
			} 
			if (contentType == null){
				throw new RMapApiException(ErrorCode.ER_NO_CONTENT_TYPE_PROVIDED);
			}
			if (sysAgentUri==null)	{
				throw new RMapApiException(ErrorCode.ER_NO_SYSTEMAGENT_PROVIDED); 
			}	

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
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
			RMapDiSCO newRmapDisco = rdfHandler.rdf2RMapDiSCO(discoRdf, RestApiUtils.getDiscoBaseUrl(), contentType.getRdfType());
			if (newRmapDisco == null) {
				throw new RMapApiException(ErrorCode.ER_CORE_RDF_TO_DISCO_FAILED);
			}  
			
			RMapEvent discoEvent = rmapService.updateDiSCO(new RMapUri(sysAgentUri), 
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

			String newEventURL = RestApiUtils.makeEventUrl(sEventURI); 
			String prevDiscoUrl = RestApiUtils.makeDiscoUrl(origDiscoUri); 
			String newDiscoUrl = RestApiUtils.makeDiscoUrl(sDiscoURI); 

			String linkRel = "<" + newEventURL + ">" + ";rel=\"" + PROV.WASGENERATEDBY + "\"";
			linkRel = linkRel.concat(",<" + prevDiscoUrl + ">" + ";rel=\"predecessor-version\"");
			
			response = Response.status(Response.Status.CREATED)
						.entity(sDiscoURI)
						.location(new URI(newDiscoUrl)) 
						.header("Link",linkRel)    //switch this to link()
						.build();   
			
			reqSuccessful = true;
    	
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
		}
	return response;	
	}
	
	
	/**
	 * Sets status of RMap:DiSCO to tombstoned.  
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response tombstoneRMapDiSCO(String discoUri, URI sysAgentUri) throws RMapApiException {
		return changeRMapDiSCOStatus(discoUri, "TOMBSTONED", sysAgentUri);
	}

	/**
	 * Sets status of RMap:DiSCO to inactive.  
	 * @param discoUri
	 * @return Response
	 * @throws RMapApiException
	 */
	public Response inactivateRMapDiSCO(String discoUri, URI sysAgentUri) throws RMapApiException {
		return changeRMapDiSCOStatus(discoUri, "INACTIVE", sysAgentUri);
	}
	

	/**
	 * Sets status of RMap:DiSCO to tombstoned or inactive, depending on newStatus defined.  
	 * @param discoUri
	 * @param newStatus
	 * @return Response
	 * @throws RMapApiException
	 */
	private Response changeRMapDiSCOStatus(String discoUri, String newStatus, URI sysAgentUri) throws RMapApiException {
		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;

		try	{		
			if (discoUri==null || discoUri.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_OBJECT_URI_PROVIDED); 
			}	
			if (sysAgentUri==null)	{
				throw new RMapApiException(ErrorCode.ER_NO_SYSTEMAGENT_PROVIDED); 
			}		
			
			URI uriDiscoUri = null;
			try {
				discoUri = URLDecoder.decode(discoUri, "UTF-8");
				uriDiscoUri = new URI(discoUri);
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
			}

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}	
			
			RMapEvent discoEvent = null;
			if (newStatus == "TOMBSTONED")	{
				discoEvent = (RMapEvent)rmapService.deleteDiSCO(uriDiscoUri, new RMapUri(sysAgentUri));					
			}
			else if (newStatus == "INACTIVE")	{
				//TODO:this is incorrect - currently no inactivate disco method, so this is a placeholder!
				discoEvent = (RMapEvent)rmapService.inactivateDiSCO(new RMapUri(sysAgentUri), uriDiscoUri);						
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

			String newEventURL = RestApiUtils.makeEventUrl(sEventURI); 
			String origDiscoUrl = RestApiUtils.makeDiscoUrl(discoUri); 
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
			
			reqSuccessful = true;
    	
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
	public Response getRMapDiSCOVersions(String discoUri, NonRdfType returnType, Boolean retAgentVersionsOnly) throws RMapApiException {

		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			//assign default values when null
			if (returnType==null)	{returnType=NonRdfType.PLAIN_TEXT;}
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
			
			String outputString="";
			List <URI> uriList = null;

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
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
									
			if (returnType == NonRdfType.JSON)	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:DiSCOs");				
			}
			else	{
				outputString= URIListHandler.uriListToPlainText(uriList);
			}

		    			
			response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (RestApiUtils.makeStmtUrl(discoUri)))
							.build();
			
			reqSuccessful = true;

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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
	public Response getRMapDiSCOEvents(String discoUri, NonRdfType returnType) throws RMapApiException {

		boolean reqSuccessful = false;
		Response response = null;
		RMapService rmapService = null;
		try {
			//assign default value when null
			if (returnType==null)	{returnType=NonRdfType.PLAIN_TEXT;}
			
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

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
			
			String outputString="";
			List <URI> uriList = rmapService.getDiSCOEvents(uriDiscoUri);						
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
    		
    		response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (RestApiUtils.makeDiscoUrl(discoUri)))
							.build();
			
			reqSuccessful = true;
	        
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
		finally{
			if (rmapService != null) rmapService.closeConnection();
			if (!reqSuccessful && response!=null) response.close();
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
		RMapService rmapService = null;
		try{
			
			String strDiscoUri = uriDiscoUri.toString();

			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			if (rmapService ==null){
				throw new RMapApiException(ErrorCode.ER_CREATE_RMAP_SERVICE_RETURNED_NULL);
			}
	
			//get the DiSCO status link
			RMapStatus status = rmapService.getDiSCOStatus(uriDiscoUri);
			if (status==null){
				throw new RMapApiException(ErrorCode.ER_CORE_GET_STATUS_RETURNED_NULL);
			}
			links.append("<" + RMAP.NAMESPACE + status.toString().toLowerCase() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"");
	
			//get DiSCO version links
			try {
				URI latestUri = rmapService.getDiSCOIdLatestVersion(uriDiscoUri);
				if (latestUri!=null && latestUri.toString().length()>0) {
					links.append(",<" + RestApiUtils.makeDiscoUrl(latestUri.toString()) + ">" + ";rel=\"latest-version\"");  			
				}
				URI prevUri = rmapService.getDiSCOIdPreviousVersion(uriDiscoUri);
				if (prevUri!=null && prevUri.toString().length()>0) {
					links.append(",<" + RestApiUtils.makeDiscoUrl(prevUri.toString()) + ";rel=\"predecessor-version\"");
				}
				URI nextUri = rmapService.getDiSCOIdNextVersion(uriDiscoUri);
				if (nextUri!=null && nextUri.toString().length()>0) {
					links.append(",<" + RestApiUtils.makeDiscoUrl(nextUri.toString()) + ";rel=\"successor-version\"");
				}
			} catch (Exception ex){
				throw RMapApiException.wrap(ex, ErrorCode.ER_CORE_COULD_NOT_RETRIEVE_DISCO_VERSION);
			}
			
			try {
				strDiscoUri = URLEncoder.encode(strDiscoUri, "UTF-8");
			}
			catch (Exception ex)  {
				throw RMapApiException.wrap(ex, ErrorCode.ER_CANNOT_ENCODE_URL);
			}		
			
			//get DiSCO event link
			String eventUrl = RestApiUtils.getDiscoBaseUrl() + strDiscoUri + "/events";
	
			links.append(",<" + eventUrl + ">" + ";rel=\"" + PROV.HAS_PROVENANCE + "\"");
		}
		catch (Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_COULDNT_RETRIEVE_DISCO_VERSION_LINKS);
		}
		finally{
			if (rmapService != null) rmapService.closeConnection();
		}
		return links.toString();	

	}

}
