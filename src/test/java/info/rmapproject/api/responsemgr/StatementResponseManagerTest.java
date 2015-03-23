package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.rmapservice.impl.openrdf.ORMapStatementMgr;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

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
	public void testGetRMapStatement() {
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

		//getRMapStatement
		Response response = null;
		try {
			response = responseManager.getRMapStatement(stmtid.toString(),"RDFXML");
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
			response = responseManager.getRMapStatementID(subject.stringValue(), predicate.stringValue(), 
															object.stringValue(), "RDFXML");
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
			response = responseManager.getRMapStatementID(subject, predicate, object, "RDFXML");
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
