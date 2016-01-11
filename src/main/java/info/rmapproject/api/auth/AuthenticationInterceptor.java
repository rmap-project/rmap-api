package info.rmapproject.api.auth;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;

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
    
	    String accessKey = null;
	    String secret = null;
	    
	    try {
		    accessKey = apiUserService.getAccessKey();
		    secret = apiUserService.getSecret();
	    } catch (RMapApiException e){
	        throw new RuntimeException(ErrorCode.ER_COULD_NOT_RETRIEVE_AUTHPOLICY.getMessage());	    	
	    }
	    
		if (accessKey==null || accessKey.length()==0
				|| secret==null || secret.length()==0)	{
	        throw new RuntimeException(ErrorCode.ER_NO_USER_TOKEN_PROVIDED.getMessage());
		}		
		try {
			apiUserService.validateKey(accessKey, secret);
		} catch (RMapApiException e) {
			throw new RuntimeException(e.getErrorCode().getMessage(), e);
		}
    }
		
}
