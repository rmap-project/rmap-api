package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.core.exception.RMapException;
import info.rmapproject.core.idservice.IdServiceFactoryIOC;
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

	@Test
	public void testGetRMapStatement() {
		//create RMapStatement
		java.net.URI id1 =null;
		try {
			id1 = IdServiceFactoryIOC.getFactory().createService().createId();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		URI subject = ORAdapter.uri2OpenRdfUri(id1);
		URI predicate = RDF.TYPE;
		URI object = RMAP.STATEMENT;
		ORMapStatementMgr mgr = new ORMapStatementMgr();
		String contextString = mgr.createContextURIString(subject.stringValue(),
				predicate.stringValue(), object.stringValue());
		URI context = ORAdapter.getValueFactory().createURI(contextString);
		Statement stmt = vf.createStatement(subject, predicate, object,context);	
		mgr.createTriple(ts, stmt);
		try{
		ts.commitTransaction();
		} catch(Exception e){
			fail("Exception thrown " + e.getMessage());			
		}
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
		assertEquals(200, response.getStatus());
	}

	@Test
	public void testGetRMapStatementID() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRMapStatementRelatedEvents() {
		fail("Not yet implemented");
	}

}
