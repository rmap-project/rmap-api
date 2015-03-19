package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

public class DiscoResponseManagerTest {

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

	@Test
	public void testCreateDisco() {
		Response response = null;
		String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
						+ "<rdf:RDF "  
						+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
						+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
						+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  
						+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
						+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
						+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
						+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
						+ "<rmap:DiSCO>"  
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
		
		try {
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, "RDFXML");
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}
	
		assertNotNull(response);
		assertEquals(201, response.getStatus());

	}
	
	
	

}
