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
    	Status errType = null;
    	String exMsg = null;
    	String rmapApiMsg = null;
    	
    	ErrorCode errorCode = exception.getErrorCode();
    	if (errorCode != null){
	    	errType = errorCode.getStatus();
	    	rmapApiMsg = errorCode.getMessage();
	    	exMsg = exception.getMessage();
    	}

    	if (errType == null)	{
    		errType = Status.INTERNAL_SERVER_ERROR;
    	}
    	if (rmapApiMsg == null)	{
    		rmapApiMsg = "";
    	}
    	if (exMsg == null)	{
    		exMsg = "";
    	}
	
    	String rootCause = ExceptionUtils.getRootCauseMessage(exception);
    	if (rootCause == null)	{
    		rootCause = "";
    	}
    	Response response = Response.status(errType).type("text/plain").entity(rmapApiMsg + " : " + exMsg + " : " + rootCause).build(); 
        log.fatal(rmapApiMsg + "; " + exMsg, exception);
    	return response;
    }
}
