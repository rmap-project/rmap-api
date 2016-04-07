package info.rmapproject.api.exception;

import java.net.URI;

import info.rmapproject.api.utils.Utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Converts RMap API exceptions to HTTP responses
 * @author khanson
 *
 */
@Provider
public class RMapApiExceptionHandler implements ExceptionMapper<RMapApiException>
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
	/**
	 * Converts RMap API Exceptions to HTTP responses.
	 * @param RMap API Exception
	 */
    @Override
    public Response toResponse(RMapApiException exception)
    {
    	//to build up the error message
    	StringBuilder errMsg = new StringBuilder();
    	
    	Status errType = null;
    	String exMsg = null;
    	String rmapApiMsg = null;
    	
    	ErrorCode errorCode = exception.getErrorCode();
    	if (errorCode != null){
	    	errType = errorCode.getStatus();
	    	rmapApiMsg = errorCode.getMessage();
	    	exMsg = exception.getMessage();
    	}
    	
    	//set default error status as 500
    	if (errType == null)	{
    		errType = Status.INTERNAL_SERVER_ERROR;
    	}
    	
    	//append message associated with RMap API error code
    	if (rmapApiMsg != null && rmapApiMsg.length()>0)	{
    		errMsg.append(rmapApiMsg);
    	}
	
    	String rootCause = ExceptionUtils.getRootCauseMessage(exception);
    	
    	//append system message (typically relevant where non-RMapApiException thrown)
    	//only if rootCause isn't same message!
    	if (exMsg != null && exMsg.length()>0 && !rootCause.contains(exMsg))	{
    		if (errMsg.length()>0){
    			errMsg.append("; ");
    		}
    		errMsg.append(exMsg);
    	}

    	//Append root cause message
    	if (rootCause != null && rootCause.length()>0)	{
    		if (errMsg.length()>0){
    			errMsg.append("; ");
    		}
    		errMsg.append(rootCause);
    	}    	
    	
    	Response response = null;
    	if (errType==Status.CONFLICT){
    		//extract redirect URL
    		try {
				String discoUrl = exMsg;
				discoUrl = discoUrl.substring(discoUrl.lastIndexOf("<") + 1, discoUrl.lastIndexOf(">"));
				discoUrl = Utils.makeDiscoUrl(discoUrl);
	    		response = Response.status(errType)
		    						.link(new URI(discoUrl), "latest-version") 
		    						.type("text/plain")
		    						.entity(errMsg.toString()).build(); 
			} catch (Exception e) {
				// continue... we are already handling an error!
			}   		
    	}

    	if (response==null){
    		//no redirect URL
    		response = Response.status(errType).type("text/plain").entity(errMsg.toString()).build(); 
    	}
	
    	log.fatal(errMsg.toString(), exception);
    	return response;
    }
}
