package info.rmapproject.api.utils;

import info.rmapproject.core.exception.RMapException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

public class URLUtils {
	
	private static final String BASE_URL_KEY = "baseURL";
	private static final String DEFAULT_SYSAGENT_KEY = "defaultSysAgent";

    private static Properties props = new Properties();
    private static boolean isInitialized = false;
	
	public static void init() throws RMapException {
		InputStream input = null;
		String propertiesFile = "/rmap_api.properties";
		try {	
			input = URLUtils.class.getResourceAsStream(propertiesFile);
			if (input==null)	{
				throw new RMapException("RMap API configuration file " + propertiesFile + " cannot be found.");
			}
			props.load(input);
			input.close();
			isInitialized = true;
		} catch (IOException e) {
				throw new RMapException("Properties file " + propertiesFile + " cannot be read. Error: " + e.getMessage());
		} 
	}
	
	public static String getBaseUrl() throws RMapException {
		if (!isInitialized){
			init();
		}
		String baseUrl = props.getProperty(BASE_URL_KEY);
		if (baseUrl == null || baseUrl.length()==0)	{
			throw new RMapException("Base URL property not set");
		}		
		baseUrl = baseUrl.trim();
		while (baseUrl.endsWith("/"))	{
			baseUrl = baseUrl.substring(0, baseUrl.length()-1);	
		}
		
		return baseUrl;
	}
	
	public static String getStmtBaseUrl() throws RMapException {
		String stmtBaseUrl = getBaseUrl() + "/stmt/";
		return stmtBaseUrl;
	}
	
	public static String getDiscoBaseUrl() throws RMapException {
		String discoBaseUrl = getBaseUrl() + "/disco/";
		return discoBaseUrl;
	}

	public static String getEventBaseUrl() throws RMapException {
		String eventBaseUrl = getBaseUrl() + "/event/";
		return eventBaseUrl;
	}
	
	public static String getAgentBaseUrl() throws RMapException {
		String agentBaseUrl = getBaseUrl() + "/agent/";
		return agentBaseUrl;
	}	
	
	public static String getProfileBaseUrl() throws RMapException {
		String profileBaseUrl = getBaseUrl() + "/profile/";
		return profileBaseUrl;
	}	
	
	public static String getResourceBaseUrl() throws RMapException {
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
	public static String makeStmtUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getStmtBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeDiscoUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getDiscoBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeEventUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getEventBaseUrl(),uri);
		return stmtUrl;
	}
	
	public static String makeAgentUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return stmtUrl;
	}

	public static String makeProfileUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return stmtUrl;
	}
	
	public static String makeResourceUrl(String uri) throws RMapException {
		String stmtUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return stmtUrl;
	}

	public static String appendEncodedUriToURL(String baseURL, String objUri) {
		String url = null;
		try {
			url = baseURL + URLEncoder.encode(objUri,"UTF-8");
		}
		catch (UnsupportedEncodingException ee){
			throw new RMapException("Cannot encode URI");			
		}
		catch (Exception e)	{
			throw new RMapException("Error building URL");
		}
		return url;
	}
	/*
	 * TODO: this is here as a temporary measure to make it easy to share a system agent ID between all classes...
	 * just until we have proper authentication.
	 * 
	 * REMOVE THIS!
	 */
	public static URI getDefaultSystemAgentURI() throws RMapException, Exception{
		if (!isInitialized){
			init();
		}
		
		String defaultSysAgentURI = props.getProperty(DEFAULT_SYSAGENT_KEY);
		if (defaultSysAgentURI == null || defaultSysAgentURI.length()==0)	{
			throw new RMapException("Default System Agent property not set");
		}		
		defaultSysAgentURI = defaultSysAgentURI.trim();
		while (defaultSysAgentURI.endsWith("/"))	{
			defaultSysAgentURI = defaultSysAgentURI.substring(0, defaultSysAgentURI.length()-1);	
		}
		
		return new URI(defaultSysAgentURI);
	}
	

	
}
