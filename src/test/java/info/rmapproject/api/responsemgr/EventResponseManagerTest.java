package info.rmapproject.api.responsemgr;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class EventResponseManagerTest {

	protected EventResponseManager responseManager = null;
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new EventResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testEventResponseManager() {
		assertTrue (responseManager instanceof EventResponseManager);
	}
	
	@Test
	public void testGetEventServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getEventServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetEventServiceHead() {
		Response response = null;
		try {
			response = responseManager.getEventServiceHead();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}
	
	@Test
	public void testGetRMapEvent() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRMapEventRelatedObjs() {
		fail("Not yet implemented");
	}

}
