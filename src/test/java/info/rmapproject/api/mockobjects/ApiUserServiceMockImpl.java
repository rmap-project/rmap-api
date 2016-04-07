package info.rmapproject.api.mockobjects;

import info.rmapproject.api.auth.ApiUserService;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.auth.exception.RMapAuthException;
import info.rmapproject.auth.model.ApiKey;
import info.rmapproject.auth.model.User;
import info.rmapproject.auth.service.RMapAuthService;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.request.RMapRequestAgent;
import info.rmapproject.core.rmapservice.RMapService;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({ "classpath*:/spring-*-context.xml" })
public class ApiUserServiceMockImpl implements ApiUserService {

	private AuthorizationPolicy policy;
	private static final String TEST_USER = "rmaptest";
	private static final String TEST_PASS = "rmaptest";

	@Autowired 
	private RMapService rmapService;
	
	@Autowired
	private RMapAuthService rmapAuthService;
	
		
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getCurrentAuthPolicy()
	 */
	@Override
	public AuthorizationPolicy getCurrentAuthPolicy() throws RMapApiException {
		this.policy = new AuthorizationPolicy();
		this.policy.setUserName(TEST_USER);
		this.policy.setPassword(TEST_PASS);
	    return this.policy;
	}
	
	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getAccessKey()
	 */
	@Override
	public String getAccessKey() throws RMapApiException {
		//NOTE: if need both key and secret, better to retrieve AuthPolicy to prevent multiple calls to retrieve the Policy.
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getUserName();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSecret()
	 */
	@Override
	public String getSecret() throws RMapApiException {
		//NOTE: if need both key and secret, better to retrieve AuthPolicy to prevent multiple calls to retrieve the Policy.
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getPassword();
	}
		


    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUriForEvent()
	 */
	public URI getCurrentSystemAgentUri() throws RMapApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
		String key = policy.getUserName();
		String secret = policy.getPassword();
		return getSystemAgentUri(key, secret);
	}
	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUri(String, String)
	 */
	@Override
	public URI getSystemAgentUri(String key, String secret) throws RMapApiException {
		URI sysAgentUri = null;
		
		try {
			ApiKey apiKey = rmapAuthService.getApiKeyByKeySecret(key, secret);
			User user = rmapAuthService.getUserById(apiKey.getUserId());

			if (user.hasRMapAgent()){
				//there is an agent id already, pass it back!
				sysAgentUri = new URI(user.getRmapAgentUri());
			}
			else if (user.isDoRMapAgentSync()) {
				//there is no agent id, but the record is flagged for synchronization - create the agent!
				RMapEvent event = rmapAuthService.createOrUpdateAgentFromUser(user.getUserId());	
				sysAgentUri = event.getAssociatedAgent().getIri();
			}		
			else {
				//there is no agent id and no flag to create one
				throw new RMapApiException(ErrorCode.ER_USER_HAS_NO_AGENT);
			}
					
		} catch (RMapAuthException ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_USER_AGENT_COULD_NOT_BE_RETRIEVED);
		}  catch (URISyntaxException ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_INVALID_AGENTID_FOR_USER);
		} 
		
		return sysAgentUri;
	}
		
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getApiKeyUriForEvent()
	 */
	@Override
	public URI getApiKeyForEvent() throws RMapApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
		String key = policy.getUserName();
		String secret = policy.getPassword();
		return getApiKeyUriForEvent(key, secret);
	}

    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getApiKeyUriForEvent(String, String)
	 */
	@Override
	public URI getApiKeyUriForEvent(String key, String secret) throws RMapApiException {
		URI apiKeyUri = null;
		
		try {
			ApiKey apiKey = rmapAuthService.getApiKeyByKeySecret(key, secret);
			if (apiKey.isIncludeInEvent()){
				apiKeyUri = new URI(apiKey.getKeyUri());
			}
			else {
				//key should not be referenced in event, return null
				return null;
			}
					
		} catch (RMapAuthException ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_USER_AGENT_COULD_NOT_BE_RETRIEVED);
		}  catch (URISyntaxException ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_INVALID_KEYURI_FOR_USER);
		} 
		
		return apiKeyUri;
	}

	@Override
	public void validateKey(String accessKey, String secret)
			throws RMapApiException {
		try {
			rmapAuthService.validateApiKey(accessKey, secret);
		}
		catch (RMapAuthException e) {
			throw RMapApiException.wrap(e, ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED);
		}	
	}

	@Override
	public RMapRequestAgent getCurrentRequestAgent() throws RMapApiException {
		RMapRequestAgent agent = new RMapRequestAgent(getCurrentSystemAgentUri(), getApiKeyForEvent());
		return agent;
	}

}
