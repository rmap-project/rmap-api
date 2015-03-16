package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.api.responsemgr.ResponseManager;
import info.rmapproject.api.responsemgr.ResponseManagerFactory;
import info.rmapproject.core.exception.RMapException;

/**
 * @author khanson, smorrissey
 *
 */
public class IResponseManagerFactory implements ResponseManagerFactory {

	private static ResponseManager service = new IResponseManager();
	
	public IResponseManagerFactory() {}


	public ResponseManager createService() throws RMapException {
		return service;
	}

}
