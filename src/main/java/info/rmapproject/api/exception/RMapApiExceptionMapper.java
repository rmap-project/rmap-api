package info.rmapproject.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class RMapApiExceptionMapper implements ExceptionMapper<RMapApiException>
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
    @Override
    public Response toResponse(RMapApiException exception)
    {
    	ErrorCode errorCode = exception.getErrorCode();
    	Status errType = errorCode.getStatus();
    	String rmapApiMsg = ErrorMessage.getUserText(errorCode);
    	if (rmapApiMsg == null)	{
    		rmapApiMsg = "";
    	}
    	String exMsg = exception.getMessage();
    	if (exMsg == null)	{
    		exMsg = "";
    	}
    	Response response = Response.status(errType).type("text/plain").entity(rmapApiMsg + "; " + exMsg).build(); 
        log.fatal(rmapApiMsg + "; " + exMsg, exception);
    	
    	return response;
    }
}
