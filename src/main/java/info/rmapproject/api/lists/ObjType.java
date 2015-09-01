package info.rmapproject.api.lists;

public enum ObjType {
	ALL ("rmap:Object"), 
	STATEMENTS ("rdfs:Statement"), 
	AGENTS ("rmap:Agent"), 
	EVENTS ("rmap:Event"), 
	DISCOS ("rmap:DiSCO"),  
	RESOURCES("rdfs:Resource");
	
	
	private final String strObjTypeLabel;

	private ObjType (String strObjTypeLabel) {
		this.strObjTypeLabel = strObjTypeLabel;
	}
	public String getObjTypeLabel()  {
		return strObjTypeLabel;
	}
	
}
