package info.rmapproject.api.authentication;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.ErrorMessage;
import info.rmapproject.auth.exception.RMapAuthException;
import info.rmapproject.auth.service.AuthService;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Required;

public class AuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {

	private AuthService authService;
    
    @Required
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    public AuthenticationInterceptor() {
        super(Phase.RECEIVE);
    }

    public void handleMessage(Message message) {
        AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);
	    if (policy == null) {
	        throw new RuntimeException(ErrorMessage.getUserText(ErrorCode.ER_NO_USER_TOKEN_PROVIDED));
	        }
	    
	    String accessKey = policy.getUserName();
	    String secret = policy.getPassword();
	    
		if (accessKey==null || accessKey.length()==0
				|| secret==null || secret.length()==0)	{
	        throw new RuntimeException(ErrorMessage.getUserText(ErrorCode.ER_NO_USER_TOKEN_PROVIDED));
		}		
	    
		try {
			authService.validateApiKey(accessKey, secret);
		}
		catch (RMapAuthException e) {
			throw new RuntimeException(ErrorMessage.getUserText(ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED), e);
		}
    }
		
}
