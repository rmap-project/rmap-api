package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class AgentResponseManagerTest {

	protected AgentResponseManager responseManager = null;
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new AgentResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testAgentResponseManager() {
		assertTrue (responseManager instanceof AgentResponseManager);
	}

	@Test
	public void testGetAgentServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getAgentServiceOptions();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetAgentServiceHead() {
		Response response = null;
		try {
			response = responseManager.getAgentServiceHead();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetRMapAgent() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateRMapAgent() {
		fail("Not yet implemented");
	}

}
