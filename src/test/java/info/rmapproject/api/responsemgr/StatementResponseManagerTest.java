package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.lists.NonRdfType;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
/**
 * @author khanson
 * Procedures to test StatementResponseManager
 */
public class StatementResponseManagerTest extends ResponseManagerTest {
	
	protected StatementResponseManager statementResponseManager = null;
	
	@Before
	public void setUp() throws Exception {
		try {
			super.setUp();
			statementResponseManager = (StatementResponseManager)context.getBean("statementResponseManager", StatementResponseManager.class);   
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown " + e.getMessage());
		}
		
	}

	@Test
	public void testStatementResponseManager() {
		assertTrue (statementResponseManager instanceof StatementResponseManager);
	}
	
	@Test
	public void testGetStatementServiceOptions() {
		Response response = null;
		try {
			response = statementResponseManager.getStatementServiceOptions();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetStatementServiceHead() {
		Response response = null;
		try {
			response = statementResponseManager.getStatementServiceHead();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}
	
	@Test
	public void testGetStatementRelatedDiSCOs() {
		Response response = null;
		try {
			response = statementResponseManager.getStatementRelatedDiSCOs("http://dx.doi.org/10.1109/TPEL.2012.2200506", 
															"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
															"http://purl.org/dc/dcmitype/Text", "all", null, null, null, NonRdfType.JSON);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		assertNotNull(response);
		assertEquals(response.getStatus(),200);
		assertEquals(response.getEntity(),"{\"rmap:DiSCO\":[\"ark:/22573/rmd18m7nn5\"]}");
		
	}
	
	@Test
	public void testGetStatementRelatedAgents() {
		Response response = null;
		try {
			response = statementResponseManager.getStatementRelatedAgents("http://isni.org/isni/000000010941358X", 
															"http://xmlns.com/foaf/0.1/name", 
															"\"IEEE\"", null, null, null, NonRdfType.JSON);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		assertNotNull(response);
		assertEquals(response.getStatus(),200);
		assertEquals(response.getEntity(),"{\"rmap:Agent\":[\"ark:/22573/rmd18nd2p4\"]}");
	}
	
	@Test
	public void testGetStatementAssertingAgents() {
		Response response = null;
		try {
			response = statementResponseManager.getStatementAssertingAgents("http://dx.doi.org/10.1109/TPEL.2012.2200506", 
																	"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
																	"http://purl.org/dc/dcmitype/Text", "all", null, null, NonRdfType.JSON);
		} catch (Exception e) {
			e.printStackTrace();	
		}
		assertNotNull(response);
		assertEquals(response.getStatus(),200);
		assertEquals(response.getEntity(),"{\"rmap:Agent\":[\"ark:/22573/rmd18nd2m3\"]}");
		
	}

}
