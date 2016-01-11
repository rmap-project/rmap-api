package info.rmapproject.api.utils;

public final class Constants  {
	  /**File path for error message text*/
	  public static final String ERROR_MSGS_PROPS_FILEPATH = "/error_msgs.properties";

	  private Constants(){
		    //this prevents even the native class from calling this ctor as well :
		    throw new AssertionError();
		  }
}
