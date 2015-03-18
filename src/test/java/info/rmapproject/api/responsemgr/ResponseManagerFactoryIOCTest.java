package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
/**
 * 
 * @author khanson
 *
 */

public class ResponseManagerFactoryIOCTest {
	
	/**
	 * Test method for {@link info.rmapproject.api.responsemgr.ResponseManagerFactoryIOC#getFactory()}.
	 */
	@Test
	public void testGetFactory() {
		try {
			ResponseManager service = ResponseManagerFactoryIOC.getFactory().createService();
			assertTrue(service instanceof info.rmapproject.api.responsemgr.impl.IResponseManager);
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
