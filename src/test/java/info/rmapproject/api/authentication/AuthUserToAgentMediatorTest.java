package info.rmapproject.api.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import info.rmapproject.auth.model.ApiKey;
import info.rmapproject.auth.service.AuthService;
import info.rmapproject.auth.service.AuthServiceImpl;

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
	/*
	@Test
	public void testAuthObj() {

		AuthService authService = new AuthServiceImpl();
		User user = authService.getUserById(3);
		String name = user.getName();
		assertEquals("Karen Hanson", name);
	
	}
	*/
	
	@Test
	public void testKeySecret() {
		AuthService authService = new AuthServiceImpl();
		ApiKey apiKey = authService.getApiKeyByKeySecret("jhu", "jhu");
		assertEquals(apiKey.getLabel(), "Data Conservancy");
	}
	
	
}
