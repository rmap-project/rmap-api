package info.rmapproject.api.lists;



public enum RdfType {
	JSONLD ("JSONLD"), RDFXML("RDFXML"), TURTLE("TURTLE"), RDFNQUADS("RDFNQUADS");
	
	private final String rdfType;

	private RdfType (String rdfType) {
		this.rdfType = rdfType;
	}
	public String getRdfType()  {
		return rdfType;
	}
	
}
