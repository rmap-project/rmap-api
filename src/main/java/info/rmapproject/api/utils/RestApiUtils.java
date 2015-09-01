package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Properties;

public class RestApiUtils {
	
	private static final String BASE_URL_KEY = "baseURL";
	private static final String DEFAULT_SYSAGENT_KEY = "defaultSysAgent";

    private static Properties props = new Properties();
    private static boolean isInitialized = false;
	
	public static void init() throws RMapApiException {
		InputStream input = null;
		String propertiesFile = "/rmap_api.properties";
		try {	
			input = RestApiUtils.class.getResourceAsStream(propertiesFile);
			if (input==null)	{
				throw new RMapApiException(ErrorCode.ER_RMAP_API_PROPERTIES_FILENOTFOUND);
			}
			props.load(input);
			input.close();
			isInitialized = true;
		} catch (IOException e) {
				throw new RMapApiException(ErrorCode.ER_RMAP_API_PROPERTIES_FORMATERROR);
		} 
	}
	
	public static String getBaseUrl() throws RMapApiException {
		String baseUrl = null;
		try {
			if (!isInitialized){
				init();
			}
			baseUrl = props.getProperty(BASE_URL_KEY);
			if (baseUrl == null || baseUrl.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_RMAP_API_PROPERTIES_BASEURL_MISSING);
			}		
			baseUrl = baseUrl.trim();
			while (baseUrl.endsWith("/"))	{
				baseUrl = baseUrl.substring(0, baseUrl.length()-1);	
			}
		}catch(RMapApiException ex) {
			throw RMapApiException.wrap(ex);
		}
		catch(Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
					
		return baseUrl;
	}
	
	public static String getStmtBaseUrl() throws RMapApiException {
		String stmtBaseUrl = getBaseUrl() + "/stmts/";
		return stmtBaseUrl;
	}
	
	public static String getDiscoBaseUrl() throws RMapApiException {
		String discoBaseUrl = getBaseUrl() + "/discos/";
		return discoBaseUrl;
	}

	public static String getEventBaseUrl() throws RMapApiException {
		String eventBaseUrl = getBaseUrl() + "/events/";
		return eventBaseUrl;
	}
	
	public static String getAgentBaseUrl() throws RMapApiException {
		String agentBaseUrl = getBaseUrl() + "/agents/";
		return agentBaseUrl;
	}	
	
	public static String getResourceBaseUrl() throws RMapApiException {
		String resourceBaseUrl = getBaseUrl() + "/resources/";
		return resourceBaseUrl;
	}
	
	/**
	 * Series of procedures for constructing API URLs - basically adding the base URL for the RMap API to 
	 * an encoded identifier. 
	 * @param uri
	 * @return
	 * @throws RMapException
	 */
	public static String makeStmtUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getStmtBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeDiscoUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getDiscoBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeEventUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getEventBaseUrl(),uri);
		return stmtUrl;
	}
	
	public static String makeAgentUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeProfileUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return stmtUrl;
	}
	
	public static String makeResourceUrl(String uri) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getResourceBaseUrl(),uri);
		return stmtUrl;
	}
	
	public static String appendEncodedUriToURL(String baseURL, String objUri) throws RMapApiException {
		String url = null;
		try {
			url = baseURL + URLEncoder.encode(objUri,"UTF-8");
		}
		catch (Exception e)	{
			throw new RMapApiException(ErrorCode.ER_CANNOT_ENCODE_URL);
		}
		return url;
	}
	
	public static RMapStatus convertToRMapStatus(String status) throws RMapApiException {
		RMapStatus rmapStatus = null;
		if (status==null)	{
			status="active";
		}
		switch(status) {
			case "active": rmapStatus = RMapStatus.ACTIVE;
				break;
			case "deleted": rmapStatus = RMapStatus.TOMBSTONED;
				break;
			case "inactive": rmapStatus = RMapStatus.INACTIVE;
				break;
			case "all": rmapStatus = null;
				break;
			default: 
				throw new RMapApiException(ErrorCode.ER_STATUS_TYPE_NOT_RECOGNIZED);		
		}
		return rmapStatus;
	}
	
	
	/*
	 * TODO: this is here as a temporary measure to make it easy to share a system agent ID between all classes...
	 * just until we have proper authentication.
	 * 
	 * REMOVE THIS!
	 */
	public static URI getDefaultSystemAgentURI() throws RMapApiException {
		URI uriDefaultSysAgentURI = null;
		try {
			if (!isInitialized){
				init();
			}
			
			String defaultSysAgentURI = props.getProperty(DEFAULT_SYSAGENT_KEY);
			if (defaultSysAgentURI == null || defaultSysAgentURI.length()==0)	{
				throw new RMapApiException(ErrorCode.ER_NO_DEFAULT_SYSTEM_AGENT_SET);
			}		
			defaultSysAgentURI = defaultSysAgentURI.trim();
			while (defaultSysAgentURI.endsWith("/"))	{
				defaultSysAgentURI = defaultSysAgentURI.substring(0, defaultSysAgentURI.length()-1);	
			}
			uriDefaultSysAgentURI = new URI(defaultSysAgentURI);
		}
		catch(RMapApiException ex){
			throw RMapApiException.wrap(ex);			
		}
		catch(Exception ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);
		}
		
		return uriDefaultSysAgentURI;
	}
	
	/**
	 * Converts a string of text passed in as the "object" through the API request to a valid RMapValue
	 * determining whether it is a typed literal, URI etc.
	 * @param sObject
	 * @return
	 * @throws RMapApiException
	 * @throws URISyntaxException 
	 */
	public static RMapValue convertObjectStringToRMapValue(String sObject) throws RMapApiException{
		RMapValue object = null;
		
		if (sObject.startsWith("\"")) {
			String literal = sObject.substring(1, sObject.lastIndexOf("\""));
			String literalProp = sObject.substring(sObject.lastIndexOf("\"")+1);
			
			if (literalProp.contains("^^")) {
				String sType = literalProp.substring(literalProp.indexOf("^^")+2);
				RMapUri type = null;
				sType = sType.trim();

				try {
					type = new RMapUri(new URI(sType));
				}
				catch (Exception ex){
					throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
				}
				
				object = new RMapLiteral(literal, type);
			}
			else if (literalProp.contains("@")) {
				String language = literalProp.substring(literalProp.indexOf("@")+1);
				language = language.trim();
				object = new RMapLiteral(literal, language);
			}
			else {
				object = new RMapLiteral(literal);
			}
		}
		else { //should be a URI
			try {
				object = new RMapUri(new URI(sObject));
			}
			catch (Exception ex){
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);					
			}
		}
		
		return object;
	}
	
	
}
