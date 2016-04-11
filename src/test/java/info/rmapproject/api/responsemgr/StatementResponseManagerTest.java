package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.core.model.RMapStatus;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFType;
import info.rmapproject.core.utils.Terms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * @author khanson
 * Procedures to test StatementResponseManager
 */

public class StatementResponseManagerTest extends ResponseManagerTest {
	@Autowired
	protected StatementResponseManager statementResponseManager;
	
	@Before
	public void setUp() throws Exception {
		try {
			super.setUp();			
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
			//createDisco
			InputStream rdf = new ByteArrayInputStream(genericDiscoRdf.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, RDFType.RDFXML, "");
			String discoURI = rmapDisco.getId().toString();
	        assertNotNull(discoURI);
			rmapService.createDiSCO(rmapDisco, super.reqAgent);
			
			RMapSearchParams params = new RMapSearchParams();
			params.setStatusCode(RMapStatus.ACTIVE);


			MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
			queryParams.add("page", "1");
			
			//get disco as related to statement
			response = statementResponseManager.getStatementRelatedDiSCOs("http://dx.doi.org/10.1109/ACCESS.2014.2332453", 
															"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
															"http://purl.org/spar/fabio/JournalArticle", NonRdfType.JSON, queryParams);

			assertNotNull(response);
			assertEquals(response.getStatus(),200);
			assertEquals(response.getEntity(),"{\"" + Terms.RMAP_DISCO_PATH + "\":[\"" + discoURI + "\"]}");
			
			rmapService.deleteDiSCO(new URI(discoURI), super.reqAgent);
			
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
	}
	
	
	@Test
	public void testGetStatementAssertingAgents() {
		Response response = null;
		try {			
			//createDisco
			InputStream rdf = new ByteArrayInputStream(genericDiscoRdf.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, RDFType.RDFXML, "");
			String discoURI = rmapDisco.getId().toString();
	        assertNotNull(discoURI);
			rmapService.createDiSCO(rmapDisco, super.reqAgent);

			MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
			response = 
					statementResponseManager.getStatementAssertingAgents("http://dx.doi.org/10.1109/ACCESS.2014.2332453", 
														"http://www.w3.org/1999/02/22-rdf-syntax-ns#type", 
														"http://purl.org/spar/fabio/JournalArticle", NonRdfType.JSON, queryParams);
			assertNotNull(response);
			assertEquals(response.getStatus(),200);
			assertEquals(response.getEntity(),"{\""+ Terms.RMAP_AGENT_PATH + "\":[\"" + super.testAgentURI + "\"]}");
			rmapService.deleteDiSCO(new URI(discoURI), super.reqAgent);
		} catch (Exception e) {
			e.printStackTrace();	
		}

	}

}
