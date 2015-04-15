package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.exception.RMapException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

public class URLUtils {
	
	private static final String BASE_URL_KEY = "baseURL";
	private static final String DEFAULT_SYSAGENT_KEY = "defaultSysAgent";

    private static Properties props = new Properties();
    private static boolean isInitialized = false;
	
	public static void init() throws RMapApiException {
		InputStream input = null;
		String propertiesFile = "/rmap_api.properties";
		try {	
			input = URLUtils.class.getResourceAsStream(propertiesFile);
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
		String stmtBaseUrl = getBaseUrl() + "/stmt/";
		return stmtBaseUrl;
	}
	
	public static String getDiscoBaseUrl() throws RMapApiException {
		String discoBaseUrl = getBaseUrl() + "/disco/";
		return discoBaseUrl;
	}

	public static String getEventBaseUrl() throws RMapApiException {
		String eventBaseUrl = getBaseUrl() + "/event/";
		return eventBaseUrl;
	}
	
	public static String getAgentBaseUrl() throws RMapApiException {
		String agentBaseUrl = getBaseUrl() + "/agent/";
		return agentBaseUrl;
	}	
	
	public static String getProfileBaseUrl() throws RMapApiException {
		String profileBaseUrl = getBaseUrl() + "/profile/";
		return profileBaseUrl;
	}	
	
	public static String getResourceBaseUrl() throws RMapApiException {
		String resourceBaseUrl = getBaseUrl() + "/resource/";
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
	

	
}
