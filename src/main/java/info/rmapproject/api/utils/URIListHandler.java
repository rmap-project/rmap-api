package info.rmapproject.api.utils;

import java.net.URI;
import java.util.List;

/**
 * 
 * @author khanson
 * Some methods to convert a list of URIs to either JSON or a plain text list.
 *
 */
public class URIListHandler {

	/**
	 * 
	 * @param lstURIs
	 * @return list of URIs as String
	 * Make a text-only list from list of URIs
	 * 
	 */
	public static String uriListToPlainText(List <URI> lstURIs) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(""); //ensure at least an empty string is returned.
		if (lstURIs != null && lstURIs.size()>0){
			String newline = "";
			for (URI uri : lstURIs)	{
				builder.append(newline);
				builder.append(uri.toString());
				newline=System.getProperty("line.separator");
				}
		}
			
		return builder.toString();		
	}

	/**
	 * 
	 * @param lstURIs
	 * @param strType
	 * @return list of URIs as JSON
	 * Makes a JSON array of URIs from list
	 * Empty or null list comes back as {"arrayLabel":[]}
	 * 
	 */
	public static String uriListToJson(List <URI> lstURIs, String strLabel) {
		//TODO: if list is empty - returns e.g. {"rmap:Stmts":""} - need to look at correct handling.
		StringBuilder builder = new StringBuilder();

		builder.append("{\"" + strLabel + "\":");
		builder.append("[");

		if (lstURIs != null && lstURIs.size()>0){
			String separator = "";
			for (URI uri : lstURIs)	{
				builder.append(separator);
				builder.append("\"" + uri.toString() + "\"");
				separator=",";
			}	
		}
		builder.append("]");		
		builder.append("}");
		
		return builder.toString();		
	}
	
	
	
	
}
