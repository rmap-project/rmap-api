package info.rmapproject.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class RMapApiExceptionHandler implements ExceptionMapper<RMapApiException>
{
	private final Logger log = LogManager.getLogger(this.getClass());
	
    @Override
    public Response toResponse(RMapApiException exception)
    {
    	Response response = null;
    	try{
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
	    	response = Response.status(errType).entity(rmapApiMsg + "; " + exMsg).build(); 
	        log.fatal(rmapApiMsg + "; " + exMsg, exception);
    	}
    	catch (Exception ex) {
    		String msg = "Error during exception handling. Customized message could not be constructed. System message: " + ex.getMessage();
	    	response = Response.status(500).entity(msg).build();    
	        log.fatal(msg, ex);
    	}
    	return response;
    }
}
