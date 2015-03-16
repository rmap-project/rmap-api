package info.rmapproject.api.responsemgr;

import info.rmapproject.core.exception.RMapException;

/**
 *  @author khanson, smorrissey
 *
 */
public interface ResponseManagerFactory {
	
	public ResponseManager createService() throws RMapException;
}

