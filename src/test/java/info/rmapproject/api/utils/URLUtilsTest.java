package info.rmapproject.api.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class URLUtilsTest {

	@Test
	public void testGetBaseUrl() {
		String baseURL = URLUtils.getBaseUrl();
		assertFalse(baseURL.endsWith("/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetStmtBaseUrl() {
		String baseURL = URLUtils.getStmtBaseUrl();
		assertFalse(baseURL.endsWith("/stmt/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetDiscoBaseUrl() {
		String baseURL = URLUtils.getDiscoBaseUrl();
		assertFalse(baseURL.endsWith("/disco/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetAgentBaseUrl() {
		String baseURL = URLUtils.getAgentBaseUrl();
		assertFalse(baseURL.endsWith("/agent/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetProfileBaseUrl() {
		String baseURL = URLUtils.getProfileBaseUrl();
		assertFalse(baseURL.endsWith("/profile/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetResourceBaseUrl() {
		String baseURL = URLUtils.getResourceBaseUrl();
		assertFalse(baseURL.endsWith("/resource/"));
		assertTrue(baseURL.startsWith("http"));		
	}

}
