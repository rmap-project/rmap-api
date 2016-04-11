package info.rmapproject.api.lists;


import info.rmapproject.core.rdfhandler.RDFType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RdfMediaType {
	APPLICATION_LDJSON ("application/ld+json", RDFType.JSONLD),
	APPLICATION_RMAPDISCO_LDJSON ("application/vnd.rmap-project.disco+ld+json", RDFType.JSONLD),
	APPLICATION_XML ("application/xml", RDFType.RDFXML),
	APPLICATION_RDFXML ("application/rdf+xml", RDFType.RDFXML),
	APPLICATION_RMAPDISCO_RDFXML ("application/vnd.rmap-project.disco+rdf+xml", RDFType.RDFXML),
	TEXT_TURTLE ("text/turtle", RDFType.TURTLE),
	APPLICATION_RMAPDISCO_TURTLE ("application/vnd.rmap-project.disco+turtle", RDFType.TURTLE);

	private final String mimeType;
	private final RDFType exchangeFormat;

	private RdfMediaType (String mimeType, RDFType exchangeFormat) {
		this.mimeType = mimeType;
		this.exchangeFormat = exchangeFormat;
	}

	public String getMimeType()  {
		return mimeType;
	}

	public RDFType getRdfType()  {
		return exchangeFormat;
	}

    public static RdfMediaType get(String mimeType) { 
    	Map<String, RdfMediaType> lookup = new HashMap<String, RdfMediaType>();
        for(RdfMediaType mt : EnumSet.allOf(RdfMediaType.class)) {
            lookup.put(mt.getMimeType(), mt);
        }
        return lookup.get(mimeType); 
    }

}