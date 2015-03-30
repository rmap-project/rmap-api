package info.rmapproject.api.responsemgr;

import info.rmapproject.api.utils.URIListHandler;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.exception.RMapObjectNotFoundException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.statement.RMapStatement;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.vocabulary.DC;
/**
 * 
 * @author khanson
 * Creates HTTP responses for Statement REST API requests
 *
 */
public class StatementResponseManager {

	private final Logger log = LogManager.getLogger(this.getClass());
	
	public StatementResponseManager() {
	}		
	
	
	/**
	 * @return HTTP Response
	 * 
	 */
	
	public Response getStatementServiceOptions()	{
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
	public Response getStatementServiceHead()	{
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
	 * Get RMap Statement formatted according to RDF type passed in
	 * 
	 */
	
	public Response getRMapStatement(String strStatementId, String acceptsType)	{
		Response response = null;
		if (strStatementId==null || strStatementId.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		try {			
			strStatementId = URLDecoder.decode(strStatementId, "UTF-8");
			
			URI uriStatementId = new URI(strStatementId);
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
    		RMapStatement rmapStatement = rmapService.readStatement(uriStatementId);

    		if (rmapStatement!=null){
    			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
	    		OutputStream statementOutput = rdfHandler.statement2Rdf(rmapStatement, acceptsType);	

	    		String linkRel = "";
	    		RMapStatus status = rmapService.getStatementStatus(uriStatementId);
	    		if (status==null){
	    			throw new Exception();
	    		}
	    		
	    		linkRel.concat(",<" + status.toString() + ">" + ";rel=\"" + RMAP.HAS_STATUS + "\"");
	    		
	    		List <URI> lstEvents = rmapService.getStatementEvents(uriStatementId);
	    		for (URI eventUri : lstEvents){
    				String event = URLUtils.makeEventUrl(eventUri.toString());
   	        		linkRel.concat(",<" + event + ">" + ";rel=\"http://www.w3.org/ns/prov#has_provenance\"");
	    		}
    		   		
			    response = Response.status(Response.Status.OK)
							.entity(statementOutput.toString())
							.location(new URI(URLUtils.makeStmtUrl(strStatementId)))
	        				.header("Link",linkRel)						//switch this to link() or links()?
	        				.type("application/vnd.rmap-project.statement; version=1.0-beta")
							.build();
	        }
    		else {
    			throw new Exception();
    		}
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Statement could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
    	catch(RMapException ex) {  //replace this with a bad request
    		log.fatal("User input not valid. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.BAD_REQUEST).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve statement: " + strStatementId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}

	/**
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param acceptsType
	 * @return
	 */

	public Response getRMapStatementID(String subject, String predicate, String object)	{
		
		if (subject==null || subject.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		if (predicate==null || predicate.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		if (object==null || object.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		
		Response response = null;
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
		catch (Exception e){
			throw new RMapException(); //replace with bad request
		}
		
		try {
			rmapObject = new RMapUri(new URI(object));
		}
		catch (URISyntaxException e) {
			rmapObject = new RMapLiteral(object);
		}
		
		try {
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI stmtURI = rmapService.getStatementID(rmapSubject, rmapPredicate, rmapObject);

    		if (stmtURI!=null){	
				response = Response.status(Response.Status.OK)
							.entity(stmtURI.toString())
							.location(new URI (URLUtils.makeStmtUrl(stmtURI.toString())))
							.build();
	        }
    		else	{
    			throw new RMapException();
    		}
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Statement could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
    	catch(RMapException ex) {  //replace this with a bad request
    		log.fatal("User input not valid. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.BAD_REQUEST).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve statement. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
	
	/**
	 * 
	 * @param strStatementId
	 * @param returnType
	 * @return HTTP Response - list of URIs for events associated with the Stmt Id provided.
	 */
	public Response getRMapStatementRelatedEvents(String strStatementId, String returnType)	{
		Response response = null;
		if (strStatementId==null || strStatementId.length()==0)	{
			throw new RMapException();  //change this to a bad request exception
		}
		try {
			strStatementId = URLDecoder.decode(strStatementId, "UTF-8");
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			URI uriStatementUri = new URI(strStatementId);
			String outputString="";
			
			List <URI> uriList = rmapService.getStatementEvents(uriStatementUri);
			
			if (returnType.equals("JSON"))	{
				outputString= URIListHandler.uriListToJson(uriList, "rmap:Events");				
			}
			else	{
				outputString = URIListHandler.uriListToPlainText(uriList);
			}
    		
    		if (outputString.length()>0){			    			
				response = Response.status(Response.Status.OK)
							.entity(outputString.toString())
							.location(new URI (URLUtils.makeStmtUrl(strStatementId)))
							.build();    			
	        }
		}
    	catch(RMapObjectNotFoundException ex) {
    		log.fatal("Event could not be found. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.NOT_FOUND).build();
    	}  
    	catch(RMapException ex) {  //replace this with a bad request
    		log.fatal("User input not valid. Error: " + ex.getMessage());
        	response = Response.status(Response.Status.BAD_REQUEST).build();
    	}  
		catch(Exception ex)	{
			log.fatal("Error trying to retrieve event details for: " + strStatementId + "Error: " + ex.getMessage());
        	response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
    	return response;
	}
	
}
