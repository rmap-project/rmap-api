package info.rmapproject.api.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.rmapproject.api.exception.RMapApiException;

import org.junit.Test;

public class RestApiUtilsTest {

	@Test
	public void testGetBaseUrl() throws RMapApiException {
		String baseURL = Utils.getApiPath();
		assertFalse(baseURL.endsWith("/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetStmtBaseUrl() throws RMapApiException {
		String baseURL = Utils.getStmtBaseUrl();
		assertFalse(baseURL.endsWith("/stmt/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetDiscoBaseUrl() throws RMapApiException {
		String baseURL = Utils.getDiscoBaseUrl();
		assertFalse(baseURL.endsWith("/disco/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetAgentBaseUrl() throws RMapApiException {
		String baseURL = Utils.getAgentBaseUrl();
		assertFalse(baseURL.endsWith("/agent/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetResourceBaseUrl() throws RMapApiException {
		String baseURL = Utils.getResourceBaseUrl();
		assertFalse(baseURL.endsWith("/resource/"));
		assertTrue(baseURL.startsWith("http"));		
	}
	
		

}
