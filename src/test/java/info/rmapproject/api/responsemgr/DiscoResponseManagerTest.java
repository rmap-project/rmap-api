package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class DiscoResponseManagerTest {

	protected String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<rdf:RDF "  
			+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
			+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
			+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  
			+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
			+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
			+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
			+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
			+ "<rmap:DiSCO>"  
			+ "<dcterms:creator rdf:resource=\"http://orcid.org/00000-00000-00000-00000\"/>"
			+ "<dc:description>"  
			+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
			+ "</dc:description>"  
			+ "<rmap:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
			+ "<rmap:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
	    	+ "</rmap:DiSCO>"  
	    	+ "<fabio:JournalArticle rdf:about=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\">"  
	    	+ "<dc:title>Toward Scalable Systems for Big Data Analytics: A Technology Tutorial</dc:title>"  
	    	+ "<dc:creator>Yonggang Wen</dc:creator>"  
	    	+ "<dc:creator>Tat-Seng Chua</dc:creator>"  
	    	+ "<dc:creator>Xuelong Li</dc:creator>"  
	    	+ "<dc:subject>Hadoop</dc:subject>"  
	    	+ "<dc:subject>Big data analytics</dc:subject>"  
	    	+ "<dc:subject>data acquisition</dc:subject>"  
	    	+ "</fabio:JournalArticle>"  
	    	+ "<rdf:Description rdf:about=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\">"  
	    	+ "<dc:format>application/zip</dc:format>"  
	    	+ "<dc:description>Zip file containing an AVI movie and a README file in Word format.</dc:description>"  
	    	+ "<dc:hasPart rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#big%32data%32intro.avi\"/>"  
	    	+ "<dc:hasPart rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#README.docx\"/>"  
	    	+ "</rdf:Description>"  
	    	+ "<rdf:Description rdf:about=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip#big%32data%32intro.avi\">"  
	    	+ "<dc:format>video/x-msvideo</dc:format>"  
	    	+ "<dc:extent>194KB</dc:extent>"  
	    	+ "</rdf:Description>"  
	    	+ "</rdf:RDF>";
	
	
	protected DiscoResponseManager responseManager = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new DiscoResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
	}
		

	@Test
	public void testDiSCOResponseManager() {
		assertTrue (responseManager instanceof DiscoResponseManager);
	}

	
	@Test
	public void testGetDiSCOServiceHead() {
		Response response = null;
		try {
			response = responseManager.getDiSCOServiceHead();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}
	
	@Test
	public void testGetDiSCOServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getDiSCOServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}
	
		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}	
	

	/**
	 * Tests whether appropriate 200 OK response is generated when you get a statement that 
	 * exists in the database.  
	 */
	@Test
	public void testGetRMapDisco() throws Exception{

		//createDisco
		
		RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
		InputStream rdf = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
		RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, URLUtils.getDiscoBaseUrl(), "RDFXML");
		String discoURI = "";
				
		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
		
		//TODO: System agent param is fudged... need to correct this code when proper authentication handling available.
		rmapService.createDiSCO(new RMapUri(URLUtils.getDefaultSystemAgentURI()), rmapDisco);
		discoURI = rmapDisco.getId().toString();		
        assertNotNull(discoURI);
		
		Response response = null;
		try {
			response = responseManager.getRMapDiSCO(URLEncoder.encode(discoURI, "UTF-8"),"RDFXML");
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		assertTrue(location.contains("disco"));
		assertTrue(body.contains("DiSCO"));
		assertEquals(200, response.getStatus());
	}
	
	
	
	

	@Test
	public void testCreateDisco() {
		Response response = null;
		try {
			//create new ORMapAgent
			createAgentforTest();
			
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, "RDFXML");
			
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			System.out.print(e.getMessage());
			e.printStackTrace();			
		}
	
		assertNotNull(response);
		assertEquals(201, response.getStatus());

	}
	
	
	@Test
	public void testCreateDiscoNoCreator() {
		Response response = null;
		try {
			createAgentforTest();

			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, "RDFXML");
			
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			System.out.print(e.getMessage());
			e.printStackTrace();			
		}
	
		assertNotNull(response);
		//need to change this to bad request when the error becomes available.
		assertEquals(500, response.getStatus());

	}

	public void createAgentforTest() {
		//create new ORMapAgent
		java.net.URI SYSAGENT_URI = null;
		try {
			SYSAGENT_URI = URLUtils.getDefaultSystemAgentURI();
		} catch (Exception e) {
			e.printStackTrace();
			fail("cant retrieve default system agent URI");
		}

		SesameTriplestore ts = null;
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		} catch (Exception e) {
			e.printStackTrace();
			fail("cant create triplestore");
		}

		//yep, agent creates itself… just for now.
		org.openrdf.model.URI agentURI = ORAdapter.uri2OpenRdfUri(SYSAGENT_URI);
		
		ORMapAgentMgr agentMgr = new ORMapAgentMgr();
		if (!agentMgr.isAgentId(agentURI, ts))	{
			ORMapAgent agent = new ORMapAgent(agentURI, agentURI);
			//create through ORMapAgentMgr

			agentMgr.createAgentTriples (agent, ts);
			try {
				ts.commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
				fail("cant commit");
			}
		}
		assertTrue(agentMgr.isAgentId(agentURI, ts));
	}
	

}
