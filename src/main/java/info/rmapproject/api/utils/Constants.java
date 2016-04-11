package info.rmapproject.api.utils;

import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfMediaType;

public final class Constants  {
	  /**File path for error message text*/
	  public static final String RMAP_API_PROPS_FILE = "rmapapi";
	  public static final String API_PATH_KEY = "rmapapi.path";
	  
	  /**File path for error message text*/
	  public static final String ERROR_MSGS_PROPS_FILE = "api_error_msgs";
	  
	  public static final String BASE_URL = "";

	  public static final RdfMediaType DEFAULT_RDF_TYPE = RdfMediaType.TEXT_TURTLE;
	  public static final NonRdfType DEFAULT_NONRDF_TYPE = NonRdfType.JSON;

	  private Constants(){
		    //this prevents even the native class from calling this ctor as well :
		    throw new AssertionError();
		  }
}
