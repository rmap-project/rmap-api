package info.rmapproject.api.auth;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApiUserServiceTest {
	
	private ApplicationContext context;
	private static final String TEST_SPRINGCONTEXT_PATH = "testbeans.xml";
	private ApiUserService apiUserService;	
	
	private static final String TEST_USER_NOAGENT = "usernoagent";
	private static final String TEST_PASS_NOAGENT = "usernoagent";
	private static final String TEST_USER_WITHAGENT = "userwithagent";
	private static final String TEST_PASS_WITHAGENT = "userwithagent";
	
	@Before
	public void setUp() throws Exception {
		this.context = new ClassPathXmlApplicationContext(TEST_SPRINGCONTEXT_PATH);
		apiUserService = (ApiUserService)context.getBean("apiUserService", ApiUserService.class);   
	}
	
	@Test
	public void getSystemAgentUriForEventTest() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUriForEvent();
			assertTrue(sysAgent.toString().equals("ark:/22573/rmaptestagent"));
		} catch (RMapApiException e) {
			fail("sysAgent not retrieved");
		}
		
	}
	
	@Test
	public void getSystemAgentUriForEventTestNoAgent() {
		try {
			@SuppressWarnings("unused")
			URI sysAgent = apiUserService.getSystemAgentUriForEvent(TEST_USER_NOAGENT,TEST_PASS_NOAGENT);
			fail("An exception should have been thrown");
		} catch (RMapApiException e) {
			assertTrue(e.getErrorCode().equals(ErrorCode.ER_USER_HAS_NO_AGENT));
		}		
	}
	
	@Test
	public void getSystemAgentUriForEventTestWithAgent() {
		try {
			URI sysAgent = apiUserService.getSystemAgentUriForEvent(TEST_USER_WITHAGENT, TEST_PASS_WITHAGENT);
			assertTrue(sysAgent.toString().equals("ark:/22573/userwithagent"));
		} catch (RMapApiException e) {
			fail("sysAgent not retrieved");
		}		
	}
	
	
	

}
