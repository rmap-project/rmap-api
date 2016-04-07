package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapValue;

import java.net.URI;
import java.net.URISyntaxException;
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
	 * Appends encoded URI to an API URL
	 * @param baseURL
	 * @param objUri
	 * @return
	 * @throws RMapApiException
	 */
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
				RMapIri type = null;
				sType = sType.trim();

				sType = removeUriAngleBrackets(sType);
				
				try {
					type = new RMapIri(new URI(sType));
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
				object = new RMapIri(new URI(sObject));
			}
			catch (Exception ex){
				throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);					
			}
		}
		
		return object;
	}
		
	/**
	 * Checks for angle brackets around a string URI and removes them if found
	 * @param sUri
	 * @return
	 */
	public static String removeUriAngleBrackets(String sUri) {
		//remove any angle brackets on a string Uri
		if (sUri.startsWith("<")) {
			sUri = sUri.substring(1);
		}
		if (sUri.endsWith(">")) {
			sUri = sUri.substring(0,sUri.length()-1);
		}
		return sUri;
	}
	
	
}
