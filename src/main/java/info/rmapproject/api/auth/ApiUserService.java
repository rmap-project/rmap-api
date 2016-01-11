package info.rmapproject.api.auth;

import info.rmapproject.api.exception.RMapApiException;

import java.net.URI;

import org.apache.cxf.configuration.security.AuthorizationPolicy;

public interface ApiUserService {

	/**
	 * Gets current authorization policy
	 * @return
	 * @throws RMapApiException
	 */
	public AuthorizationPolicy getCurrentAuthPolicy()
			throws RMapApiException;

	/**
	 * Get current user Access Key
	 * @return
	 * @throws RMapApiException
	 */
	public String getAccessKey() throws RMapApiException;

	/**
	 * Get current user Secret
	 * @return
	 * @throws RMapApiException
	 */
	public String getSecret() throws RMapApiException;

	/**
	 * Retrieves RMap:Agent URI associated with the current user for use in the event
	 * if the User has an Agent ID that is not yet in the triplestore an Agent is created for them. 
	 * @return
	 * @throws RMapApiException
	 */
	public URI getSystemAgentUriForEvent() throws RMapApiException;
	
	/**
	 * Retrieves RMap:Agent URI associated with the user/pass provided for use in the event
	 * if the User has an Agent ID that is not yet in the triplestore an Agent is created for them. 
	 * @param key
	 * @param secret
	 * @return
	 * @throws RMapApiException
	 */
	public URI getSystemAgentUriForEvent(String key, String secret) throws RMapApiException;

	/**
	 * Validates the key/secret combination. If it is invalid an Exception is thrown.
	 * @param accessKey
	 * @param secret
	 * @throws RMapApiException
	 */
	public void validateKey(String accessKey, String secret) throws RMapApiException;


}