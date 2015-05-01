package info.rmapproject.api.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;


/**
 * 
 * @author khanson
 *
 */

public class LoggingAspect {
	public void log(JoinPoint joinPoint) {
	    //place holder for some detailed logging 
		try {
		Logger log = LogManager.getLogger(this.getClass());
		//do something to say who, what, why.
        log.debug("log this");
		}catch(Exception e) {
			e.printStackTrace();
			//continue
		}
	}
}
