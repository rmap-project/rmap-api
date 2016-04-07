package info.rmapproject.api.auth;

import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.model.request.RMapRequestAgent;

import java.net.URI;

import org.apache.cxf.configuration.security.AuthorizationPolicy;

/**
 * Manages interaction between rmap-auth and the API.  Used to validate API keys, and     
 * retrieve agent information to be associated with RMap objects.
 * @author khanson
 *
 */
public interface ApiUserService {

	/**
	 * Gets current authorization policy (contains authentication information)
	 * @return the AuthorizationPolicy
	 * @throws RMapApiException
	 */
	public AuthorizationPolicy getCurrentAuthPolicy()
			throws RMapApiException;

	/**
	 * Get current user Access Key
	 * @return current access key
	 * @throws RMapApiException
	 */
	public String getAccessKey() throws RMapApiException;

	/**
	 * Get current user Secret
	 * @return current user secret
	 * @throws RMapApiException
	 */
	public String getSecret() throws RMapApiException;

	/**
	 * Retrieves RMap:Agent URI associated with the current user if the User has 
	 * an Agent ID that is not yet in the triplestore an Agent is created for them. 
	 * @return URI of current RMap System Agent
	 * @throws RMapApiException
	 */
	public URI getCurrentSystemAgentUri() throws RMapApiException;

	/**
	 * Retrieves RMap:Agent URI associated with the user/pass provided for use in the event
	 * if the User has an Agent ID that is not yet in the triplestore an Agent is created for them. 
	 * @param key
	 * @param secret
	 * @return URI of RMap System Agent
	 * @throws RMapApiException
	 */
	public URI getSystemAgentUri(String key, String secret) throws RMapApiException;
	
	/**
	 * Where a user has specified that they want the Key URI to be included in the event, this will
	 * retrieve the key URI using the current login information.  If the user does not want to include
	 * the key in the event this will return NULL
	 * @return URI of current API key
	 * @throws RMapApiException
	 */
	public URI getApiKeyForEvent() throws RMapApiException;

	/**
	 * Where a user has specified that they want the Key URI to be included in the event, this will
	 * retrieve the key URI using the login information provided.  If the user does not want to include
	 * the key in the event this will return NULL
	 * if the user has
	 * @return URI of API key
	 * @throws RMapApiException
	 */
	public URI getApiKeyUriForEvent(String key, String secret) throws RMapApiException;
	
	/**
	 * Validates the key/secret combination. If it is invalid an Exception is thrown.
	 * @param accessKey
	 * @param secret
	 * @throws RMapApiException
	 */
	public void validateKey(String accessKey, String secret) throws RMapApiException;
	
	/**
	 * Constructs current Request Agent object based on authenticated user 
	 * @param accessKey
	 * @param secret
	 * @throws RMapApiException
	 */
	public RMapRequestAgent getCurrentRequestAgent() throws RMapApiException;


}