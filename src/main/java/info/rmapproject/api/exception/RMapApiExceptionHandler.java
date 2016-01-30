package info.rmapproject.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class RMapApiExceptionHandler implements ExceptionMapper<RMapApiException>
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
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
    	
    	//append system message (typically relevant where non-RMapApiException thrown)
    	if (exMsg != null && exMsg.length()>0)	{
    		if (errMsg.length()>0){
    			errMsg.append("; ");
    		}
    		errMsg.append(exMsg);
    	}
	
    	//Append root cause message
    	String rootCause = ExceptionUtils.getRootCauseMessage(exception);
    	if (rootCause != null && rootCause.length()>0)	{
    		if (errMsg.length()>0){
    			errMsg.append("; ");
    		}
    		errMsg.append(rootCause);
    	}
	
    	Response response = Response.status(errType).type("text/plain").entity(errMsg.toString()).build(); 
        log.fatal(errMsg.toString(), exception);
    	return response;
    }
}
