package info.rmapproject.api.mockobjects;

import info.rmapproject.api.auth.ApiUserService;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.auth.exception.RMapAuthException;
import info.rmapproject.auth.model.ApiKey;
import info.rmapproject.auth.model.User;
import info.rmapproject.auth.service.RMapAuthService;
import info.rmapproject.auth.service.RMapAuthServiceFactory;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.cxf.configuration.security.AuthorizationPolicy;

public class ApiUserServiceMockImpl implements ApiUserService {

	private AuthorizationPolicy policy;
	private static final String TEST_USER = "rmaptest";
	private static final String TEST_PASS = "rmaptest";

		
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
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getUserName();
	}

	/* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSecret()
	 */
	@Override
	public String getSecret() throws RMapApiException {
	    AuthorizationPolicy policy = getCurrentAuthPolicy();
	    return policy.getPassword();
	}
		

	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUriForEvent()
	 */
	public URI getSystemAgentUriForEvent() throws RMapApiException {
		String key = getAccessKey();
		String secret = getSecret();
		return getSystemAgentUriForEvent(key, secret);
	}
	
    /* (non-Javadoc)
	 * @see info.rmapproject.api.auth.ApiUserServiceInt#getSystemAgentUriForEvent(String, String)
	 */
	@Override
	public URI getSystemAgentUriForEvent(String key, String secret) throws RMapApiException {
		URI sysAgentUri = null;
		RMapService rmapService = null;
		
		try {
			RMapAuthService authService = RMapAuthServiceFactory.createService();
			ApiKey apiKey = authService.getApiKeyByKeySecret(key, secret);
			User user = authService.getUserById(apiKey.getUserId());
			
			//check the hasRMapAgent boolean - true if has agent
			if (!user.getHasRMapAgent()) {
				throw new RMapApiException(ErrorCode.ER_USER_HAS_NO_AGENT);				
			}

			//check there is a URI assigned to the User
			String agentUri = user.getRmapAgentUri();
			if (agentUri==null || agentUri.length()==0){
				throw new RMapApiException(ErrorCode.ER_USER_AGENT_NOT_FORMED_IN_DB);
			}
			
			//make sure that agent URI is a valid URI
			sysAgentUri = new URI(agentUri);
			
			rmapService = RMapServiceFactoryIOC.getFactory().createService();
			
			//if agent isnt in the triplestore, create it!
			if (!rmapService.isAgentId(sysAgentUri)){ 

				//create the agent in the triplestore
				String agentAuthId = user.getAuthKeyUri();
				String primaryIdProvider = user.getPrimaryIdProvider();
				String name = user.getName();
				
				//check the other elements are populated
				if (agentAuthId==null || agentAuthId.length()==0
						|| primaryIdProvider==null || primaryIdProvider.length()==0){
					throw new RMapApiException(ErrorCode.ER_USER_AGENT_NOT_FORMED_IN_DB);
				}
				
				rmapService.createAgent(sysAgentUri, name, new URI(primaryIdProvider), new URI(agentAuthId), sysAgentUri);
					
			}
			
		} catch (URISyntaxException uriEx) {
			throw RMapApiException.wrap(uriEx, ErrorCode.ER_USER_AGENT_NOT_FORMED_IN_DB);
		} catch (RMapException | RMapDefectiveArgumentException e) {
			throw RMapApiException.wrap(e);
		} finally {
			if (rmapService!=null){
				rmapService.closeConnection();
			}
		}
		
		return sysAgentUri;
	}

	@Override
	public void validateKey(String accessKey, String secret)
			throws RMapApiException {
		RMapAuthService authService = RMapAuthServiceFactory.createService();
		try {
			authService.validateApiKey(accessKey, secret);
		}
		catch (RMapAuthException e) {
			throw RMapApiException.wrap(e, ErrorCode.ER_INVALID_USER_TOKEN_PROVIDED);
		}	
	}

}
