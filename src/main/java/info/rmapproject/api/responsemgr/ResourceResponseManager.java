package info.rmapproject.api.responsemgr;

import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for Resource REST API requests
 *
 */
public class ResourceResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());
	
	public ResourceResponseManager() {
	}		

	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getResourceServiceOptions()	{
		Response response = null;
	
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>" + ";rel=\"" + DC.DESCRIPTION + "\"";
	
		response = Response.status(Response.Status.OK)
				.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
	
		return response;
	}
	

	/**
	 * @return HTTP Response
	 * 
	 */
	public Response getResourceServiceHead()	{
		Response response = null;
	
		String linkRel = "<http://rmapdns.ddns.net:8080/swagger/docs/event>" + ";rel=\"" + DC.DESCRIPTION + "\"";
	
		response = Response.status(Response.Status.OK)
					.entity("{\"description\":\"will show copy of swagger content\"}")
					.header("Allow", "HEAD,OPTIONS,GET")
					.header("Link",linkRel)
					.build();
	
		return response;    
	}
	

	/**
	 * 
	 * @param eventId
	 * @param acceptsType
	 * @return HTTP Response
	 * Get RMap Event related objects, output in format requested (currently JSON or PLAIN TEXT)
	 * 
	 */
	public Response getRMapResourceRelatedObjs(String strResourceId, String objType, String returnType, String rmapStatus)	{
		Response response = null;
		if (strResourceId==null || strResourceId.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		try {
			strResourceId = URLDecoder.decode(strResourceId, "UTF-8");
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriResourceUri = new URI(URLDecoder.decode(strResourceId,"UTF-8")); 
			List <URI> uriList = null;
			String outputString="";
			String jsonType="";
			RMapStatus status = null;	
			
			//TODO:need to map better... procedurize etc.
			if (rmapStatus == "active"){
				status = RMapStatus.ACTIVE;
			}
			else if (rmapStatus == "inactive") {
				status = RMapStatus.INACTIVE;
			}
			else if (rmapStatus == "deleted") {
				status = RMapStatus.DELETED;
			}
			else if (rmapStatus == "tombstoned") {
				status = RMapStatus.TOMBSTONED;
			}
			else {
				status = RMapStatus.ACTIVE;
			}

			//TODO: put these jsonTypes in here for now, but need to settle on what these should be and poss enum them.
			if (objType == "STATEMENTS") {
				uriList = rmapService.getResourceRelatedStmts(uriResourceUri, status);
				jsonType = "rmap:Stmts";
			}
			else if (objType == "DISCOS") {
				uriList = rmapService.getResourceRelatedDiSCOs(uriResourceUri, status);
				jsonType = "rmap:Discos";
			}
			else if (objType == "AGENTS") {
				uriList = rmapService.getResourceRelatedAgents(uriResourceUri, status);
				jsonType = "rmap:Agents";
			}
			else if (objType == "ALL") {
				uriList = rmapService.getResourceRelatedAll(uriResourceUri, status);
				jsonType = "rmap:Objects";
			}
    		
			if (returnType.equals("JSON"))	{
				outputString= URIListHandler.uriListToJson(uriList, jsonType);				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (URLUtils.makeResourceUrl(strResourceId)))
							.build();    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Event could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve event details: " + strResourceId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
	
	
	
	
	
}
