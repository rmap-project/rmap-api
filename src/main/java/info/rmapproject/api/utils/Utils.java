package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.MissingResourceException;
/**
 * 
 * @author khanson
 *
 */
public class Utils {
	private static String apiPath;
	
    /**
     * Initialize properties file
     * @throws RMapApiException
     */
    private static boolean isInitialized = false;
    
	protected static void init() throws RMapApiException{
		try {
			apiPath = ConfigUtils.getPropertyValue(Constants.RMAP_API_PROPS_FILE, Constants.API_PATH_KEY);
			isInitialized=true;
		}
		catch(MissingResourceException me){
			throw new RMapApiException(ErrorCode.ER_RMAP_API_PROPERTIES_FILENOTFOUND);
			}
		catch (Exception e){RMapApiException.wrap(e, ErrorCode.ER_UNKNOWN_SYSTEM_ERROR);}
	}
		
	/**
	 * Get Base URL from properties file
	 * @return
	 * @throws RMapApiException
	 */
	public static String getApiPath() throws RMapApiException {
		if (!isInitialized){
			init();
		}
		return apiPath;
	}
	/**
	 * Get stmts API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getStmtBaseUrl() throws RMapApiException {
		String stmtBaseUrl = getApiPath() + "/stmts/";
		return stmtBaseUrl;
	}
	
	/**
	 * Get DiSCO API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getDiscoBaseUrl() throws RMapApiException {
		String discoBaseUrl = getApiPath() + "/discos/";
		return discoBaseUrl;
	}

	
	/**
	 * Get Event API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getEventBaseUrl() throws RMapApiException {
		String eventBaseUrl = getApiPath() + "/events/";
		return eventBaseUrl;
	}

	/**
	 * Get Agent API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getAgentBaseUrl() throws RMapApiException {
		String agentBaseUrl = getApiPath() + "/agents/";
		return agentBaseUrl;
	}	

	
	/**
	 * Get Resource API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getResourceBaseUrl() throws RMapApiException {
		String resourceBaseUrl = getApiPath() + "/resources/";
		return resourceBaseUrl;
	}

	/**
	 * Appends DiSCO URI to DiSCO API URL
	 * @param uri
	 * @return
	 * @throws RMapApiException
	 */
	public static String makeDiscoUrl(String uri) throws RMapApiException {
		String discoUrl = appendEncodedUriToURL(getDiscoBaseUrl(),uri);
		return discoUrl;
	}
	
	/**
	 * Appends Event URI to Event API URL
	 * @param uri
	 * @return
	 * @throws RMapApiException
	 */
	public static String makeEventUrl(String uri) throws RMapApiException {
		String eventUrl = appendEncodedUriToURL(getEventBaseUrl(),uri);
		return eventUrl;
	}
	
	/**
	 * Appends Agent URI to Agent API URL
	 * @param uri
	 * @return
	 * @throws RMapApiException
	 */
	public static String makeAgentUrl(String uri) throws RMapApiException {
		String agentUrl = appendEncodedUriToURL(getAgentBaseUrl(),uri);
		return agentUrl;
	}

	/**
	 * Appends Resource URI to Resource API URL
	 * @param uri
	 * @return
	 * @throws RMapApiException
	 */
	public static String makeResourceUrl(String uri) throws RMapApiException {
		String resourceUrl = appendEncodedUriToURL(getResourceBaseUrl(),uri);
		return resourceUrl;
	}

	/**
	 * Appends Resource URI to Resource API URL
	 * @param uri
	 * @return
	 * @throws RMapApiException
	 */
	public static String makeStmtUrl(String s, String p, String o) throws RMapApiException {
		String stmtUrl = appendEncodedUriToURL(getStmtBaseUrl(),s) + "/";
		stmtUrl = appendEncodedUriToURL(stmtUrl,p) + "/";
		stmtUrl = appendEncodedUriToURL(stmtUrl,o);
		return stmtUrl;
	}
	
	
	/**
	 * Appends encoded URI to an API URL
	 * @param baseURL
	 * @param objUri
	 * @return
	 * @throws RMapApiException
	 */
	public static String appendEncodedUriToURL(String baseURL, String objUri) throws RMapApiException {
		String url = null;
		try {
			//may already been encoded, so let's decode first to make sure we aren't double encoding
			objUri = URLDecoder.decode(objUri,"UTF-8");
			//now encode!
			url = baseURL + URLEncoder.encode(objUri,"UTF-8");
		}
		catch (Exception e)	{
			throw new RMapApiException(ErrorCode.ER_CANNOT_ENCODE_URL);
		}
		return url;
	}

	
}
