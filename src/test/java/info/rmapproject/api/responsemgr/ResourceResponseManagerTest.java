package info.rmapproject.api.responsemgr;

import static org.junit.Assert.*;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class ResourceResponseManagerTest {
	
	protected ResourceResponseManager responseManager = null;
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new ResourceResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	@Test
	public void testResourceResponseManager() {
		assertTrue (responseManager instanceof ResourceResponseManager);
	}

	
	@Test
	public void testGetResourceServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getResourceServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetResourceServiceHead() {
		Response response = null;
		try {
			response = responseManager.getResourceServiceHead();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetRMapResourceRelatedObjs() {
		fail("Not yet implemented");
	}

}
