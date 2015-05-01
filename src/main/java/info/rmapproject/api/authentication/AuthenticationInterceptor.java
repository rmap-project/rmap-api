package info.rmapproject.api.authentication;

import info.rmapproject.api.exception.ErrorCode;

import java.util.Map;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Required;

public class AuthenticationInterceptor extends AbstractPhaseInterceptor<Message> {
	
	private Map<String,String> users;

    @Required
    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    public AuthenticationInterceptor() {
        super(Phase.RECEIVE);
    }

    public void handleMessage(Message message) {
        AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);
	    if (policy == null) {
	        throw new RuntimeException(ErrorCode.ER_NO_USER_TOKEN_PROVIDED.toString());
	        }
	    String expectedPassword = users.get(policy.getUserName());
	    if (expectedPassword == null || !expectedPassword.equals(policy.getPassword())) {
	        throw new RuntimeException(ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED.toString());
	    }	    
    }
		
}
