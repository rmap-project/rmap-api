package info.rmapproject.api.utils;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.NonRdfType;
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

	private static String MEDIATYPE_VERSION = "1.0-beta";
	
	
	//private static final Logger log = LogManager.getLogger(HttpTypeMediator.class);
	/**
	 * Maps the accept-type to the matching response type
	 * @param headers
	 * @return BasicOutputType
	 * @throws RMapApiException
	 */
	public static NonRdfType getNonRdfResponseType(HttpHeaders headers) throws RMapApiException {
		NonRdfType outputType = null;
		try {
			List<MediaType> acceptTypes=headers.getAcceptableMediaTypes();
			for (MediaType acceptType : acceptTypes)	{
				outputType = NonRdfType.get(acceptType.toString());
				if (outputType!=null){
					break;
				}    		
			}
			if (outputType == null){
				outputType = NonRdfType.JSON; //default
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
	public static RdfType getRdfResponseType(HttpHeaders headers) throws RMapApiException	{
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
				returnType=RdfType.TURTLE; //default response type
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
			MediaType contentType = headers.getMediaType();
			RdfMediaType matchingType = null;
			if (contentType!=null){
				String sContentType = contentType.getType() + "/" + contentType.getSubtype();
				matchingType = RdfMediaType.get(sContentType);
			}
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
	
	/**
	 * Determine media type that will be returned in the response
	 * @param rmapType
	 * @param rdfType
	 * @return
	 */
	public static String getResponseMediaType(String rmapType, RdfType rdfType){
		String mediatype;
		
        switch (rdfType) {
            case JSONLD: mediatype = "application/vnd.rmap-project." + rmapType + "+ld+json; version=" + MEDIATYPE_VERSION;
                     break;
            case RDFNQUADS: mediatype = "application/vnd.rmap-project." + rmapType + "+n-quads; version=" + MEDIATYPE_VERSION;
            	break;
            case RDFXML: mediatype = "application/vnd.rmap-project." + rmapType + "+rdf+xml; version=" + MEDIATYPE_VERSION;
            	break;
            case TURTLE: mediatype = "text/vnd.rmap-project." + rmapType + "+turtle; version=" + MEDIATYPE_VERSION;
            	break;
            default: mediatype = "application/vnd.rmap-project." + rmapType + "+rdf+xml; version=" + MEDIATYPE_VERSION;
            	break;
        }

		return mediatype;
	}
	
	
	
}
