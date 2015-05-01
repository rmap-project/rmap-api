package info.rmapproject.api.service;

import info.rmapproject.api.authentication.AuthUserToAgentMediator;

import java.net.URI;

import org.junit.Test;

public class DiSCOApiServiceTest {

	@Test
    public void testGetSysAgentId() throws Exception {
		
		@SuppressWarnings("unused")
		URI sysAgentId = null;
		AuthUserToAgentMediator userMediator = new AuthUserToAgentMediator();
		String username = "portico";
	    sysAgentId = userMediator.getRMapAgentForUser(username);
    	
	}
}
