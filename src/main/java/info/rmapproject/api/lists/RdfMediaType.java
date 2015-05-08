package info.rmapproject.api.lists;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RdfMediaType {
	APPLICATION_LDJSON ("application/ld+json", RdfType.JSONLD),
	APPLICATION_RMAPDISCO_LDJSON ("application/vnd.rmap-project.disco+ld+json", RdfType.JSONLD),
	APPLICATION_XML ("application/xml", RdfType.RDFXML),
	APPLICATION_RDFXML ("application/rdf+xml", RdfType.RDFXML),
	APPLICATION_RMAPDISCO_RDFXML ("application/vnd.rmap-project.disco+rdf+xml", RdfType.RDFXML),
	APPLICATION_NQUADS ("application/n-quads", RdfType.RDFNQUADS),
	APPLICATION_RMAPDISCO_NQUADS ("application/vnd.rmap-project.disco+n-quads", RdfType.RDFNQUADS),
	TEXT_TURTLE ("text/turtle", RdfType.TURTLE),
	APPLICATION_RMAPDISCO_TURTLE ("application/vnd.rmap-project.disco+turtle", RdfType.TURTLE);

	private final String mimeType;
	private final RdfType exchangeFormat;

	private RdfMediaType (String mimeType, RdfType exchangeFormat) {
		this.mimeType = mimeType;
		this.exchangeFormat = exchangeFormat;
	}

	public String getAcceptType()  {
		return mimeType;
	}

	public RdfType getReturnType()  {
		return exchangeFormat;
	}

    public static RdfMediaType get(String mimeType) { 
    	Map<String, RdfMediaType> lookup = new HashMap<String, RdfMediaType>();
        for(RdfMediaType mt : EnumSet.allOf(RdfMediaType.class)) {
            lookup.put(mt.getAcceptType(), mt);
        }
        return lookup.get(mimeType); 
    }

}