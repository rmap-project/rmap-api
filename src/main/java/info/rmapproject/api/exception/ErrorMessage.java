package info.rmapproject.api.exception;

import java.io.InputStream;
import java.util.Properties;
/**
 * 
 * @author khanson
 * Class retrieves customized error messages for the API.
 *
 */
public class ErrorMessage {

	/**
	 * @param errorCode
	 * @return String
	 * Returns the message that corresponds to the error code.
	 */
	public static String getUserText(ErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        Properties props = new Properties();
		InputStream input = null;
		String errorMsg = "";
		
        //TODO:switch this based on language / locale
		String propertiesFile = "/error_msgs.properties";
		try {	
			input = ErrorMessage.class.getResourceAsStream(propertiesFile);
			props.load(input);
			input.close();
			String key = errorCode.getStatus().getStatusCode() + "_" + errorCode;
			errorMsg = props.getProperty(key);
		}
		catch(Exception e){
			errorMsg = getDefaultText(errorCode);
			if (errorMsg == null){
				errorMsg = "";
			}
		}
		return errorMsg;
	}
	
	/**
	 * @param errorCode
	 * @return String 
	 * If all else fails, a simple default error is returned in English.
	 */
	private static String getDefaultText(ErrorCode errorCode){
		String defaultText = "";
		switch (errorCode.getStatus()) {
		case GONE:  defaultText = "The requested item has been deleted.";
        	break;
		case NOT_FOUND:  defaultText = "The requested item cannot be found.";
    		break;
		case BAD_REQUEST:  defaultText = "The request was not formatted correctly. Please check the request and try again.";
			break;
		case INTERNAL_SERVER_ERROR:  defaultText = "A system error occurred.";
    		break;
        default: defaultText = "An error occurred.";
        	break;	
		}
		return defaultText;
	}
	
	
}
