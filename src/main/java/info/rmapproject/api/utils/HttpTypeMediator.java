package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.BasicOutputType;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.lists.RdfType;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
/**
 * Various methods for mapping the content-type or accept-type in the HTTP request to the internal list of acceptable types.
 * @author khanson
 */
public class HttpTypeMediator {
	
	/**
	 * Maps the accept-type to the matching response type
	 * @param headers
	 * @return BasicOutputType
	 * @throws RMapApiException
	 */
	public static BasicOutputType getTypeForResponse(HttpHeaders headers) throws RMapApiException {
		BasicOutputType outputType = null;
		try {
			List<MediaType> acceptTypes=headers.getAcceptableMediaTypes();
			for (MediaType acceptType : acceptTypes)	{
				outputType = BasicOutputType.get(acceptType.toString());
				if (outputType!=null){
					break;
				}    		
			}
			if (outputType == null){
				outputType = BasicOutputType.PLAIN_TEXT; //default
			}
		} catch (Exception ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_COULD_NOT_MAP_CONTENTTYPE_PARAMETER_TO_TYPE);
		}
    	return outputType;
	}
	
	/**
	 * 
	 * @param headers
	 * @return RdfType
	 * @throws RMapApiException
	 */
	public static RdfType getRdfTypeOfResponse(HttpHeaders headers) throws RMapApiException	{
		RdfType returnType = null;
		try {
			List<MediaType> acceptTypes=headers.getAcceptableMediaTypes();
			for (MediaType acceptType : acceptTypes)	{
				RdfMediaType matchingType = RdfMediaType.get(acceptType.toString());
				if (matchingType!=null){
					returnType=matchingType.getReturnType();
					break;
				}    		
			}
			
			if (returnType==null){
				returnType=RdfType.RDFXML;
			}
		} catch (Exception ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_COULD_NOT_MAP_ACCEPT_PARAMETER_TO_TYPE);
		}
	
		return returnType;
	}
	
	/**
	 * 
	 * @param headers
	 * @return RdfType
	 * @throws RMapApiException
	 */
	public static RdfType getRdfTypeOfRequest(HttpHeaders headers) throws RMapApiException	{
		RdfType requestType = null;
		try {
			String contentType = headers.getHeaderString(HttpHeaders.CONTENT_TYPE);
			RdfMediaType matchingType = RdfMediaType.get(contentType);
			if (matchingType!=null){
				requestType=matchingType.getReturnType();
			}    		
			
			if (requestType==null){
				throw new RMapApiException(ErrorCode.ER_CANNOT_ACCEPT_CONTENTTYPE_PROVIDED);
			}
		} catch (Exception ex){
			throw RMapApiException.wrap(ex,ErrorCode.ER_COULD_NOT_MAP_CONTENTTYPE_PARAMETER_TO_TYPE);
		}
		return requestType;
	}
	
	
}
