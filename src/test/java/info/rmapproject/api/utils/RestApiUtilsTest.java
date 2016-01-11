package info.rmapproject.api.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapValue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

public class RestApiUtilsTest {

	@Test
	public void testGetBaseUrl() throws RMapApiException {
		String baseURL = Utils.getBaseUrl();
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
	
	@Test 
	public void testConvertObjectStringToRMapValue() throws RMapApiException {
		String objectJustLiteral = "\"This is a literal\"";
		String objectWithType = "\"2015-09-01\"^^http://www.w3.org/2001/XMLSchema#date";
		String objectWithLanguage = "\"This is a literal\"@en";
		
		RMapValue object = Utils.convertObjectStringToRMapValue(objectJustLiteral);
		RMapLiteral litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("This is a literal"));
		
		object = Utils.convertObjectStringToRMapValue(objectWithType);
		litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("2015-09-01"));
		assertTrue(litObj.getDatatype().toString().equals("http://www.w3.org/2001/XMLSchema#date"));

		object = Utils.convertObjectStringToRMapValue(objectWithLanguage);
		litObj = (RMapLiteral)object;
		assertTrue(litObj.getValue().equals("This is a literal"));
		assertTrue(litObj.getLanguage().equals("en"));
	}
	
	@Test
	public void testConvertUriCsvToUriList() throws RMapApiException {
		String uriCsv = "ark:/1234/1234, ark:/5678/5678, ";
		List<java.net.URI> uriList = Utils.convertUriCsvToUriList(uriCsv);
		assertTrue(uriList.size()==2);
	}

	@Test
	public void convertStringDateToDate() throws RMapApiException {
		Date dDate = Utils.convertStringDateToDate("20150604");
		Calendar cal = Calendar.getInstance();
		cal.setTime(dDate);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		assertTrue(month==5);
		assertTrue(year==2015);
	}
	

}
