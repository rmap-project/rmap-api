package info.rmapproject.api.auth;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.exception.RMapApiExceptionHandler;

import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {

	private ApiUserService apiUserService;

    @Autowired
    public void setApiUserService(ApiUserService apiUserService) throws RMapApiException {
    	if (apiUserService==null) {
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_API_USER_SERVICE);			
    	} else {
    		this.apiUserService = apiUserService;
		}
	}
    
    public AuthenticationInterceptor() {
        super(Phase.RECEIVE);
    }

    public void handleMessage(Message message) {
	    
	    try {    
	    	AuthorizationPolicy policy = apiUserService.getCurrentAuthPolicy();
	    	String accessKey = policy.getUserName();
	    	String secret = policy.getPassword();
	    
			if (accessKey==null || accessKey.length()==0
					|| secret==null || secret.length()==0)	{
		    	throw new RMapApiException(ErrorCode.ER_NO_USER_TOKEN_PROVIDED);
			}		
		
			apiUserService.validateKey(accessKey, secret);
		
	    } catch (RMapApiException ex){ 
	    	//generate a response to intercept default message
	    	RMapApiExceptionHandler exceptionhandler = new RMapApiExceptionHandler();
	    	Response response = exceptionhandler.toResponse(ex);
	    	message.getExchange().put(Response.class, response);   	
	    }
		
    }
		
}
