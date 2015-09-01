package info.rmapproject.api.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapValue;

import org.junit.Test;

public class RestApiUtilsTest {

	@Test
	public void testGetBaseUrl() throws RMapApiException {
		String baseURL = RestApiUtils.getBaseUrl();
		assertFalse(baseURL.endsWith("/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetStmtBaseUrl() throws RMapApiException {
		String baseURL = RestApiUtils.getStmtBaseUrl();
		assertFalse(baseURL.endsWith("/stmt/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetDiscoBaseUrl() throws RMapApiException {
		String baseURL = RestApiUtils.getDiscoBaseUrl();
		assertFalse(baseURL.endsWith("/disco/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetAgentBaseUrl() throws RMapApiException {
		String baseURL = RestApiUtils.getAgentBaseUrl();
		assertFalse(baseURL.endsWith("/agent/"));
		assertTrue(baseURL.startsWith("http"));		
	}

	@Test
	public void testGetResourceBaseUrl() throws RMapApiException {
		String baseURL = RestApiUtils.getResourceBaseUrl();
		assertFalse(baseURL.endsWith("/resource/"));
		assertTrue(baseURL.startsWith("http"));		
	}
	
	@Test 
	public void testConvertObjectStringToRMapValue() throws RMapApiException {
		String objectJustLiteral = "\"This is a literal\"";
		String objectWithType = "\"2015-09-01\"^^http://www.w3.org/2001/XMLSchema#date";
		String objectWithLanguage = "\"This is a literal\"@en";
		
		RMapValue object = RestApiUtils.convertObjectStringToRMapValue(objectJustLiteral);
		RMapLiteral litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("This is a literal"));
		
		object = RestApiUtils.convertObjectStringToRMapValue(objectWithType);
		litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("2015-09-01"));
		assertTrue(litObj.getDatatype().toString().equals("http://www.w3.org/2001/XMLSchema#date"));

		object = RestApiUtils.convertObjectStringToRMapValue(objectWithLanguage);
		litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("This is a literal"));
		assertTrue(litObj.getLanguage().equals("en"));
		
	}
	
}
