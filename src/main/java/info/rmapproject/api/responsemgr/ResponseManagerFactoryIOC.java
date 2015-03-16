package info.rmapproject.api.responsemgr;

import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.utils.ConfigUtils;
import java.util.MissingResourceException;


/**
 *  @author khanson, smorrissey
 *
 */
public class ResponseManagerFactoryIOC {

	private static final String FACTORY_PROPERTIES = "responseManagerFactory";  // Name of properties file with concrete factory class name
	private static final String FACTORY_KEY = "responseManagerFactoryClass";
	private static String factoryClassName = null;
	private static ResponseManagerFactory factory = null;
	
	static{
		try {
			factoryClassName = ConfigUtils.getPropertyValue(FACTORY_PROPERTIES, FACTORY_KEY);
			factory = (ResponseManagerFactory) Class.forName(factoryClassName).newInstance();
		}
		catch(MissingResourceException me){}
		catch (Exception e){}
	}
	/**
	 * 
	 */
	private ResponseManagerFactoryIOC() {}
	
	
	public static ResponseManagerFactory getFactory() throws RMapException {
		if (factory==null){
			throw new RMapException("Factory not available");
		}
		return factory;
	}

}
