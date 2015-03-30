package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
/**
 * @author khanson
 * Procedures to test StatementResponseManager
 */
public class StatementResponseManagerTest {
	
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
	
	protected StatementResponseManager responseManager = null;

	protected SesameTriplestore ts = null;
	ValueFactory vf = null;
	
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new StatementResponseManager();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();
		}
		try {
			ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
			vf = ts.getValueFactory();
		} catch (Exception e) {
			throw new RMapException("Unable to create Sesame TripleStore: ", e);
		}
		
	}

	@Test
	public void testStatementResponseManager() {
		assertTrue (responseManager instanceof StatementResponseManager);
	}
	
	@Test
	public void testGetStatementServiceOptions() {
		Response response = null;
		try {
			response = responseManager.getStatementServiceOptions();
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertEquals(200, response.getStatus());	
	}

	@Test
	public void testGetStatementServiceHead() {
		Response response = null;
		try {
			response = responseManager.getStatementServiceHead();
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
	public void testGetRMapStatement() throws Exception {
		String stmtUri = null;
		//create RMapStatement
		try {
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			InputStream rdf = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, URLUtils.getDiscoBaseUrl(), "RDFXML");
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			
			//TODO: System agent param is fudged... need to correct this code when proper authentication handling available.
			rmapService.createDiSCO(new RMapUri(URLUtils.getDefaultSystemAgentURI()), rmapDisco);
			
			RMapTriple stmt = rmapDisco.getRelatedStatements().get(1);
			RMapResource subject = stmt.getSubject();
			RMapUri predicate = stmt.getPredicate();
			RMapValue object = stmt.getObject();
			stmtUri = rmapService.getStatementID(subject, predicate, object).toString();			
		}
		catch (Exception ex){
			fail("Failed to create DiSCO. Exception thrown " + ex.getMessage());
			ex.printStackTrace();	
		}
			
		assertNotNull(stmtUri);
		assertTrue(stmtUri.length()>0);
		assertTrue(stmtUri.contains("ark:"));
		
		//getRMapStatement
		Response response = null;
		try {
			response = responseManager.getRMapStatement(URLEncoder.encode(stmtUri, "UTF-8"),"RDFXML");
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		assertTrue(location.contains("stmt"));
		assertTrue(body.contains("<rdf:subject rdf:resource=\"test:test\"/>"));
		assertEquals(200, response.getStatus());
	}
	
	/**
	 * Tests whether appropriate 404 Not Found response is returned
	 * when an incorrect statement ID is provided
	 */
	@Test
	public void testGetRMapStatementWhereNoMatch() {
		// pass in fake ID to see if not found response is correct.
		Response response = null;
		try {
			response = responseManager.getRMapStatement("test:test","RDFXML");
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertTrue(response.getLocation()==null);
		assertTrue(response.getEntity()==null);
		assertEquals(404, response.getStatus());
	}

	/**
	 * Tests whether appropriate 200 OK response is generated when you get a statement that 
	 * exists in the database using the subject, object, and predicate.  
	 */
	@Test
	public void testGetRMapStatementID() {
		//create RMapStatement
		URI subject = ORAdapter.getValueFactory().createURI("test:test");
		URI predicate = RDF.TYPE;
		URI object = RMAP.STATEMENT;
		ORMapStatementMgr mgr = new ORMapStatementMgr();
		String contextString = mgr.createContextURIString(subject.stringValue(),predicate.stringValue(), object.stringValue());
		URI context = ORAdapter.getValueFactory().createURI(contextString);
		Statement stmt = vf.createStatement(subject, predicate, object,context);	
		mgr.createReifiedStatement(stmt,ts);
		try{
		ts.commitTransaction();
		} catch(Exception e){
			fail("Exception thrown " + e.getMessage());			
		}

		//get ID of the statement created
		URI stmtid = mgr.getStatementID(subject, predicate, object, ts);
		
		//getRMapStatement using s, o, p
		Response response = null;
		try {
			response = responseManager.getRMapStatementID(URLEncoder.encode(subject.stringValue(),"UTF-8"), 
															URLEncoder.encode(predicate.stringValue(),"UTF-8"), 
															URLEncoder.encode(object.stringValue(),"UTF-8"));
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		assertTrue(location.contains("stmt"));
		assertTrue(body.contains(stmtid.toString()));
		assertEquals(200, response.getStatus());		
	}


	/**
	 * Tests whether appropriate 200 OK response is generated when you get a statement that 
	 * exists in the database using the subject, object, and predicate.  
	 */
	@Test
	public void testGetRMapStatementIDWhereNoMatch() {
		//create RMapStatement
		String subject = ORAdapter.getValueFactory().createURI("testnomatch:testnomatch").stringValue();
		String predicate = RDF.TYPE.stringValue();
		String object = RMAP.STATEMENT.stringValue();
				
		//getRMapStatement using s, o, p
		Response response = null;
		try {
			response = responseManager.getRMapStatementID(subject, predicate, object);
		} catch (Exception e) {
			fail("Exception thrown " + e.getMessage());
			e.printStackTrace();			
		}

		assertNotNull(response);
		assertTrue(response.getLocation()==null);
		assertTrue(response.getEntity()==null);
		assertEquals(404, response.getStatus());
		
	}
	
	
	
	@Test
	public void testGetRMapStatementRelatedEvents() {
		fail("Not yet implemented");
	}

}
