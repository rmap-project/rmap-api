package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.rdfhandler.RDFType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EventResponseManagerTest extends ResponseManagerTest {

	@Autowired
	protected EventResponseManager eventResponseManager;
	
	@Before
	public void setUp() throws Exception {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testEventResponseManager() {
		assertTrue (eventResponseManager instanceof EventResponseManager);
	}
	
	@Test
	public void testGetEventServiceOptions() {
		Response response = null;
		try {
			response = eventResponseManager.getEventServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetEventServiceHead() {
		Response response = null;
		try {
			response = eventResponseManager.getEventServiceHead();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}
	
	@Test
	public void testGetRMapEvent() {
		//create RMapStatement
		RMapEventCreation event = null;
		RMapIri discoIri;
		try {			
			InputStream rdf = new ByteArrayInputStream(genericDiscoRdf.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, RDFType.RDFXML, "");
			
			discoIri = rmapDisco.getId();
			//TODO: System agent param is a default setting until we have proper auth handling.
			event = (RMapEventCreation) rmapService.createDiSCO(rmapDisco, super.reqAgent);
			
			RMapIri eventUri = event.getId();
			
			assertNotNull(eventUri);
			
			String sEventUri = eventUri.toString();
			assertTrue(sEventUri.length()>0);
			assertTrue(sEventUri.contains("ark:"));
			
			//getRMapStatement
			Response response = null;
			response = eventResponseManager.getRMapEvent(URLEncoder.encode(sEventUri, "UTF-8"),RDFType.RDFXML);
			//response = responseManager.getRMapEvent("ark%3A%2F27927%2Ftf9yhn14ef","RDFXML");
	
			assertNotNull(response);
			//String location = response.getLocation().toString();
			String body = response.getEntity().toString();
			//assertTrue(location.contains("event"));
			
			assertTrue(body.contains("<eventTargetType xmlns=\"http://rmap-project.org/rmap/terms/\" rdf:resource=\"http://rmap-project.org/rmap/terms/DiSCO\"/>"));
			assertEquals(200, response.getStatus());
			
			rmapService.deleteDiSCO(discoIri.getIri(), super.reqAgent);

		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}
	}

	/*@Test
	public void testGetRMapEventRelatedObjs() {
		fail("Not yet implemented");
	}*/

}
