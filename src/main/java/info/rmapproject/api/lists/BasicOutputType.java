package info.rmapproject.api.lists;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

public enum BasicOutputType {
	JSON(MediaType.APPLICATION_JSON), 
	PLAIN_TEXT(MediaType.TEXT_PLAIN);
		
	private final String mediaType;

	private BasicOutputType (String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getMediaType()  {
		return mediaType;
	}

    public static BasicOutputType get(String mediaType) { 
    	Map<String, BasicOutputType> lookup = new HashMap<String, BasicOutputType>();
        for(BasicOutputType mt : EnumSet.allOf(BasicOutputType.class)) {
            lookup.put(mt.getMediaType(), mt);
        }
        return lookup.get(mediaType); 
    }	
}
