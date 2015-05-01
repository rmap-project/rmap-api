package info.rmapproject.api.authentication;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

public class AuthUserToAgentMediatorTest {
	protected AuthUserToAgentMediator authUserToAgentMediator = null;
	
	@Before
	public void setUp() throws Exception {
		try {
			authUserToAgentMediator = new AuthUserToAgentMediator();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	

	@Test
	public void testGet() {
		URI systemAgentId = null;
		try {
			systemAgentId = authUserToAgentMediator.getRMapAgentForUser("portico");
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

	assertNotNull(systemAgentId);
	}
}
