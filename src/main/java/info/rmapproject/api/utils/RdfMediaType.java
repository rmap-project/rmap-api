package info.rmapproject.api.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RdfMediaType {
	APPLICATION_LDJSON ("application/ld+json", RdfReturnType.JSONLD),
	APPLICATION_RMAPDISCO_LDJSON ("application/vnd.rmap-project.disco+ld+json", RdfReturnType.JSONLD),
	APPLICATION_XML ("application/xml", RdfReturnType.RDFXML),
	APPLICATION_RDFXML ("application/rdf+xml", RdfReturnType.RDFXML),
	APPLICATION_RMAPDISCO_RDFXML ("application/vnd.rmap-project.disco+rdf+xml", RdfReturnType.RDFXML),
	APPLICATION_NQUADS ("application/n-quads", RdfReturnType.RDFNQUADS),
	APPLICATION_RMAPDISCO_NQUADS ("application/vnd.rmap-project.disco+n-quads", RdfReturnType.RDFNQUADS),
	TEXT_TURTLE ("text/turtle", RdfReturnType.TURTLE),
	APPLICATION_RMAPDISCO_TURTLE ("application/vnd.rmap-project.disco+turtle", RdfReturnType.TURTLE);

	private final String acceptType;
	private final RdfReturnType returnType;

	private RdfMediaType (String acceptType, RdfReturnType returnType) {
		this.acceptType = acceptType;
		this.returnType = returnType;
	}

	public String getAcceptType()  {
		return acceptType;
	}

	public RdfReturnType getReturnType()  {
		return returnType;
	}

    public static RdfMediaType get(String type) { 
    	Map<String, RdfMediaType> lookup = new HashMap<String, RdfMediaType>();
        for(RdfMediaType mt : EnumSet.allOf(RdfMediaType.class)) {
            lookup.put(mt.getAcceptType(), mt);
        }
        return lookup.get(type); 
    }
	
	
	
}