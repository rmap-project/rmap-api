package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AgentResponseManagerTest extends ResponseManagerTest {

	@Autowired
	protected AgentResponseManager agentResponseManager;
	
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testAgentResponseManager() {
		assertTrue (agentResponseManager instanceof AgentResponseManager);
	}

	@Test
	public void testGetAgentServiceOptions() {
		Response response = null;
		try {
			response = agentResponseManager.getAgentServiceOptions();
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
			response = agentResponseManager.getAgentServiceHead();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	/*
	@Test
	public void testGetRMapAgent() {
		fail("Not yet implemented");
	}*/

	/*
	@Test
	public void testCreateRMapAgent() {
		fail("Not yet implemented");
	}*/

}
