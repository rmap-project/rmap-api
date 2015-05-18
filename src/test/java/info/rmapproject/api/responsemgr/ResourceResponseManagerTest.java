package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.lists.FilterObjType;
import info.rmapproject.api.lists.NonRdfType;
import info.rmapproject.api.lists.RdfType;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class ResourceResponseManagerTest {
	
	protected ResourceResponseManager responseManager = null;
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new ResourceResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
	

	@Test
	public void testResourceResponseManager() {
		assertTrue (responseManager instanceof ResourceResponseManager);
	}

	
	@Test
	public void testGetResourceServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getResourceServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetResourceServiceHead() {
		Response response = null;
		try {
			response = responseManager.getResourceServiceHead();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetRMapResourceRelatedObjs() {
		Response response = null;
		try {
			response = responseManager.getRMapResourceRelatedObjs("http%3A%2F%2Fdx.doi.org%2F10.1109%2FLGRS.2012.2189934", FilterObjType.ALL, NonRdfType.JSON, null);
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("resource"));
		assertTrue(body.contains("rmap:Objects"));
		assertEquals(200, response.getStatus());		
	}
	
	@Test
	public void getRMapResourceRdfStmts() {
		Response response = null;
		try {
			response = responseManager.getRMapResourceRdfStmts("http%3A%2F%2Fdx.doi.org%2F10.1109%2FLGRS.2012.2189934", RdfType.RDFXML, null);
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("resource"));
		assertTrue(body.contains("JournalArticle"));
		assertEquals(200, response.getStatus());	
	}
	
	@Test
	public void testGetRMapResourceRelatedStmts() {
		Response response = null;
		try {
			response = responseManager.getRMapResourceRelatedObjs("ark%3A%2F27927%2Fl4tfebcdx8", FilterObjType.STATEMENTS, NonRdfType.JSON, null);
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("resource"));
		assertTrue(body.contains("rmap:Stmts"));
		assertEquals(200, response.getStatus());
		
		
	}
	
	

}
