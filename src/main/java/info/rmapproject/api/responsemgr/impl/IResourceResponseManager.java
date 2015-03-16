package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.net.URI;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.openrdf.model.vocabulary.DC;

public class IResourceResponseManager {

	private static String BASE_RESOURCE_URL = "http://rmapdns.ddns.net:8080/api/resource/";
	private final Logger log = Logger.getLogger(this.getClass());
	
	public IResourceResponseManager() {
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
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriResourceUri = new URI(URLDecoder.decode(strResourceId)); //TODO: temporary decoder assuming passed in as URL encoded... check this.
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
							.entity(outputString)
							.location(new URI (BASE_RESOURCE_URL + strResourceId))
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
