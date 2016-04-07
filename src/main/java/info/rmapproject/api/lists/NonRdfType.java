package info.rmapproject.api.lists;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * HTTP response content types supported for non-RDF API calls
 * @author khanson
 *
 */
public enum NonRdfType {
	JSON(MediaType.APPLICATION_JSON), 
	PLAIN_TEXT(MediaType.TEXT_PLAIN);
		
	private final String mediaType;

	private NonRdfType (String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getMediaType()  {
		return mediaType;
	}

    public static NonRdfType get(String mediaType) { 
    	Map<String, NonRdfType> lookup = new HashMap<String, NonRdfType>();
        for(NonRdfType mt : EnumSet.allOf(NonRdfType.class)) {
            lookup.put(mt.getMediaType(), mt);
        }
        return lookup.get(mediaType); 
    }	
}
