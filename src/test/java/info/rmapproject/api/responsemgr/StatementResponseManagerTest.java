package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.RMapResource;
import info.rmapproject.core.model.RMapTriple;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.disco.RMapDiSCO;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapAgentMgr;
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
			e.printStackTrace();
			fail("Exception thrown " + e.getMessage());
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
			response = responseManager.getStatementServiceHead();
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
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
			createAgentforTest();
			
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			InputStream rdf = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, URLUtils.getDiscoBaseUrl(), "RDFXML");
			RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
			
			//TODO: System agent param is a default setting until we have proper auth handling.
			rmapService.createDiSCO(new RMapUri(URLUtils.getDefaultSystemAgentURI()), rmapDisco);
			
			RMapTriple stmt = rmapDisco.getRelatedStatements().get(1);
			RMapResource subject = stmt.getSubject();
			RMapUri predicate = stmt.getPredicate();
			RMapValue object = stmt.getObject();
			stmtUri = rmapService.getStatementID(subject, predicate, object).toString();			
		}
		catch (Exception ex){
			ex.printStackTrace();	
			fail("Failed to create DiSCO. Exception thrown " + ex.getMessage());
		}
			
		assertNotNull(stmtUri);
		assertTrue(stmtUri.length()>0);
		assertTrue(stmtUri.contains("ark:"));
		
		//getRMapStatement
		Response response = null;
		try {
			response = responseManager.getRMapStatement(URLEncoder.encode(stmtUri, "UTF-8"),"RDFXML");
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		assertTrue(location.contains("stmt"));
		assertTrue(body.contains("<rdf:predicate rdf:resource=\"http://purl.org/dc/elements/1.1/title\"/>"));
		assertEquals(200, response.getStatus());
	}
	
	/**
	 * Tests whether appropriate 404 Not Found response is returned
	 * when an incorrect statement ID is provided
	 */
	@Test
	public void testGetRMapStatementWhereNoMatch() {
		// pass in fake ID to see if not found response is correct.
		@SuppressWarnings("unused")
		Response response = null;
		boolean correctExceptionThrown = false;
		try {
			response = responseManager.getRMapStatement(URLEncoder.encode("ark:/29292/nomatchhere", "UTF-8"),"RDFXML");
		} catch (RMapApiException e) {
			assertEquals(e.getErrorCode(),ErrorCode.ER_STMT_OBJECT_NOT_FOUND);
			System.out.print(e.getMessage());
			correctExceptionThrown=true;
		} catch (Exception e) {
			e.printStackTrace();			
		}
		if (!correctExceptionThrown){
			fail("Did not throw object not found exception!");
		}
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
		@SuppressWarnings("unused")
		Response response = null;
		boolean correctErrorThrown = false;
		String subject = null;
		String predicate = null;
		String object = null;
		try {
			subject = URLEncoder.encode("testnomatch:testnomatch","UTF-8");
			predicate = URLEncoder.encode(RDF.TYPE.stringValue(),"UTF-8");
			object = URLEncoder.encode(RMAP.STATEMENT.stringValue(),"UTF-8");
			//getRMapStatement using s, o, p
			response = responseManager.getRMapStatementID(subject, predicate, object);

		} catch (RMapApiException e) {
			e.printStackTrace();		
			assertEquals(e.getErrorCode(), ErrorCode.ER_STMT_OBJECT_NOT_FOUND);	
			correctErrorThrown=true;
		}  catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		} 
		
		if (!correctErrorThrown)	{
			fail("An exception should have been thrown!"); 
		}
	}
	
	@Test
	public void testGetRMapStatementRelatedEvents() {
		fail("Not yet implemented");
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
