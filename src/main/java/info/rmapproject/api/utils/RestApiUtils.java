package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
/**
 * 
 * @author khanson
 *
 */
public class RestApiUtils {
	
	private static final String BASE_URL_KEY = "baseURL";

    private static Properties props = new Properties();
    private static boolean isInitialized = false;
	
    /**
     * Initialize properties file
     * @throws RMapApiException
     */
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
	
	/**
	 * Get Base URL from properties file
	 * @return
	 * @throws RMapApiException
	 */
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
	/**
	 * Get stmts API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getStmtBaseUrl() throws RMapApiException {
		String stmtBaseUrl = getBaseUrl() + "/stmts/";
		return stmtBaseUrl;
	}
	
	/**
	 * Get DiSCO API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getDiscoBaseUrl() throws RMapApiException {
		String discoBaseUrl = getBaseUrl() + "/discos/";
		return discoBaseUrl;
	}

	
	/**
	 * Get Event API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getEventBaseUrl() throws RMapApiException {
		String eventBaseUrl = getBaseUrl() + "/events/";
		return eventBaseUrl;
	}

	/**
	 * Get Agent API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getAgentBaseUrl() throws RMapApiException {
		String agentBaseUrl = getBaseUrl() + "/agents/";
		return agentBaseUrl;
	}	

	
	/**
	 * Get Resource API base URL
	 * @return
	 * @throws RMapApiException
	 */
	public static String getResourceBaseUrl() throws RMapApiException {
		String resourceBaseUrl = getBaseUrl() + "/resources/";
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
	 * Maps a string status from the http request to a RMapStatus
	 * @param status
	 * @return
	 * @throws RMapApiException
	 */
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

				sType = removeUriAngleBrackets(sType);
				
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
	
	/**
	 * Converts a CSV passed through the URI as an encoded parameter into a URI list
	 * e.g. systemAgent list as string to List<URI>
	 * @param uriCsv
	 * @return
	 * @throws RMapApiException
	 */
	public static List<URI> convertUriCsvToUriList(String uriCsv) throws RMapApiException {
		//if empty return null - null is acceptable value for this optional param
		if(uriCsv == null || uriCsv.length()==0) {return null;}
		
		try {
			//first make sure it's not encoded
			uriCsv = URLDecoder.decode(uriCsv, "UTF-8");
		}
		catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_CANNOT_DECODE_URL);
		}
		
		//split string by commas
		String[] agentList = uriCsv.split(",");
		List<URI> uriList = new ArrayList<URI>(); 
		
		try {
			//convert to URI list
			for (String sAgent:agentList) {
				sAgent = sAgent.trim();
				if (sAgent.length()>0){
					URI uriAgent = new URI(sAgent);
					uriList.add(uriAgent);
				}
			}
		}
		catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		return uriList;
	}
	
	/**
	 * Converts a date passed through the API as a string into a java Date.
	 * @param sDate
	 * @return
	 * @throws RMapApiException
	 */
	public static Date convertStringDateToDate(String sDate) throws RMapApiException {
		//if empty return null - null is acceptable value for this optional param
		if(sDate == null || sDate.length()==0) {return null;}
		Date dDate = null;
		
		try {
			//first make sure it's not encoded
			sDate = URLDecoder.decode(sDate, "UTF-8");
		}
		catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_CANNOT_DECODE_URL);
		}
		
		sDate = sDate.trim();
		
		if (sDate.length()!= 8) {
			throw new RMapApiException(ErrorCode.ER_INVALID_DATE_PROVIDED);
		}
		
		try {
			sDate = sDate.substring(0,4) + "-" + sDate.substring(4,6) + "-" + sDate.substring(6);
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dDate = dateFormat.parse(sDate);
		} catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_INVALID_DATE_PROVIDED);			
		}
		
		return dDate;
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
