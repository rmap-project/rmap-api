package info.rmapproject.api.utils;


public enum RdfReturnType {
	JSONLD ("JSONLD"), RDFXML("RDFXML"), TURTLE("TURTLE"), RDFNQUADS("RDFNQUADS");
	
	private final String rdfType;

	private RdfReturnType (String rdfType) {
		this.rdfType = rdfType;
	}
	public String getRdfType()  {
		return rdfType;
	}
}
