package info.rmapproject.api.auth;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration({ "classpath*:/spring-*-context.xml" })
public class ApiUserServiceTest {
		
	@Autowired
	private ApiUserService apiUserService;	
	
	private static final String TEST_USER_NOAGENT = "usernoagent";
	private static final String TEST_PASS_NOAGENT = "usernoagent";
	private static final String TEST_USER_WITHAGENT = "userwithagent";
	private static final String TEST_PASS_WITHAGENT = "userwithagent";
	private static final String TEST_USER_TESTSYNC = "usertestsync";
	private static final String TEST_PASS_TESTSYNC = "usertestsync";
	
	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void getSystemAgentUriForEventTest() {
		try {
			URI sysAgent = apiUserService.getCurrentSystemAgentUri();
			assertTrue(sysAgent.toString().equals("ark:/22573/rmaptestagent"));
		} catch (RMapApiException e) {
			fail("sysAgent not retrieved");
		}
		
	}
	
	@Test
	public void getSystemAgentUriForEventTestNoAgent() {
		try {
			@SuppressWarnings("unused")
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_NOAGENT,TEST_PASS_NOAGENT);
			fail("An exception should have been thrown");
		} catch (RMapApiException e) {
			assertTrue(e.getErrorCode().equals(ErrorCode.ER_USER_HAS_NO_AGENT));
		}		
	}
	
	@Test
	public void getSystemAgentUriForEventTestWithAgent() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_WITHAGENT, TEST_PASS_WITHAGENT);
			assertTrue(sysAgent.toString().equals("ark:/22573/userwithagent"));
		} catch (RMapApiException e) {
			fail("sysAgent not retrieved");
		}		
	}

	@Test
	public void getSystemAgentUriForEventTestSyncAgent() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUri(TEST_USER_TESTSYNC, TEST_PASS_TESTSYNC);
			assertTrue(sysAgent.toString().length()>0);
		} catch (RMapApiException e) {
			fail("sysAgent not retrieved");
		}		
	}
	
	
	@Test
	public void getKeyUriForEventTest() {
		try {
			URI apiKeyUri = apiUserService.getApiKeyForEvent();
			assertTrue(apiKeyUri.toString().equals("ark:/29297/fakermaptestkey"));
		} catch (RMapApiException e) {
			fail("key not retrieved");
		}
		
	}

	@Test
	public void testValidateUser() {
		try {
			apiUserService.validateKey("jhu", "jhu");
		} catch (RMapApiException e) {
			fail("validation failed");
		}		
	}
	
	

}
