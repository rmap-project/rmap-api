package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.event.RMapEventCreation;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class EventResponseManagerTest extends ResponseManagerTest {

	protected EventResponseManager responseManager = null;
	@Before
	public void setUp() throws Exception {
		try {
			super.setUp();
			responseManager = new EventResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testEventResponseManager() {
		assertTrue (responseManager instanceof EventResponseManager);
	}
	
	@Test
	public void testGetEventServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getEventServiceOptions();
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
			response = responseManager.getEventServiceHead();
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
		try {			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			InputStream rdf = new ByteArrayInputStream(genericDiscoRdf.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, RestApiUtils.getDiscoBaseUrl(), "RDFXML");
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			
			//TODO: System agent param is a default setting until we have proper auth handling.
			event = (RMapEventCreation) rmapService.createDiSCO(new URI(AGENT_URI.toString()), rmapDisco);
			
		}
		catch (Exception ex){
			ex.printStackTrace();	
			fail("Failed to create DiSCO. Exception thrown " + ex.getMessage());
		}
		
		RMapUri eventUri = event.getId();
		
		assertNotNull(eventUri);
		
		String sEventUri = eventUri.toString();
		assertTrue(sEventUri.length()>0);
		assertTrue(sEventUri.contains("ark:"));
		
		//getRMapStatement
		Response response = null;
		try {
			response = responseManager.getRMapEvent(URLEncoder.encode(sEventUri, "UTF-8"),RdfType.RDFXML);
			//response = responseManager.getRMapEvent("ark%3A%2F27927%2Ftf9yhn14ef","RDFXML");
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("event"));
		assertTrue(body.contains("<eventTargetType xmlns=\"http://rmap-project.org/rmap/terms/\">http://rmap-project.org/rmap/terms/1.0/DiSCO</eventTargetType>"));
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testGetRMapEventRelatedObjs() {
		fail("Not yet implemented");
	}

}
