package info.rmapproject.api.responsemgr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.rmapproject.api.authentication.AuthUserToAgentMediator;
import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.api.lists.RdfMediaType;
import info.rmapproject.api.lists.RdfType;
import info.rmapproject.api.utils.RestApiUtils;
import info.rmapproject.core.model.RMapUri;
import info.rmapproject.core.model.disco.RMapDiSCO;
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

public class DiscoResponseManagerTest {

	protected String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<rdf:RDF "  
			+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
			+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
			+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  
			+ " xmlns:ore=\"http://www.openarchives.org/ore/terms/\""  
			+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
			+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
			+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
			+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
			+ "<rmap:DiSCO>"  
			+ "<dcterms:creator rdf:resource=\"http://orcid.org/0000-0000-0000-0000\"/>"
			+ "<dc:description>"  
			+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
			+ "</dc:description>"  
			+ "<ore:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
			+ "<ore:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
	    	+ "</rmap:DiSCO>"  
	    	+ "<fabio:JournalArticle rdf:about=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\">"  
	    	+ "<dc:title>Toward Scalable Systems for Big Data Analytics: A Technology Tutorial</dc:title>"  
	    	+ "<dc:creator>Yonggang Wen</dc:creator>"  
	    	+ "<dc:creator>Tat-Seng Chua</dc:creator>"  
	    	+ "<dcterms:creator rdf:nodeID=\"N65580\"/>"  
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
	    	+ "<rdf:Description rdf:nodeID=\"N65580\">"
	    	+ "<foaf:name>Xuelong Li</foaf:name>"
	    	+ "<rdf:type rdf:resource=\"http://purl.org/dc/terms/Agent\"/>"
	    	+ "</rdf:Description>"	
	    	+ "</rdf:RDF>";

	protected String discoRDFNoCreator = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
			+ "<rdf:RDF "  
			+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
			+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
			+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""  
			+ " xmlns:ore=\"http://www.openarchives.org/ore/terms/\""  
			+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
			+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
			+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
			+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
			+ "<rmap:DiSCO>"  
			+ "<dc:description>"  
			+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
			+ "</dc:description>"  
			+ "<ore:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
			+ "<ore:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
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
	
	
	protected String discoTurtleRdf = 
			"@prefix dc: <http://purl.org/dc/elements/1.1/> ."
			+ "@prefix frbr: <http://purl.org/vocab/frbr/core#> ."
			+ "@prefix cito: <http://purl.org/spar/cito/> ."
			+ "@prefix dcterms: <http://purl.org/dc/terms/> ."
			+ "@prefix foaf: <http://xmlns.com/foaf/0.1/> ."
			+ "@prefix scoro: <http://purl.org/spar/scoro/> ."
			+ "@prefix rmap: <http://rmap-project.org/rmap/terms/> ."
			+ "<http://dx.doi.org/10.5281/zenodo.13962>"
			+ "  a <http://purl.org/dc/dcmitype/Software> ;"
			+ " dc:identifier \"http://zenodo.org/record/13962\" ;"
			+ "	frbr:supplementOf \"https://github.com/ComputationalRadiationPhysics/mallocMC/tree/2.0.1crp\" ;"
			+ "  cito:cites \"http://dx.doi.org/10.1109/InPar.2012.6339604\", \"http://www.icg.tugraz.at/project/mvp/downloads\" ;"
			+ "  dcterms:isVersionOf \"http://dx.doi.org/10.5281/zenodo.10307\" ;"
			+ "  dc:title \"mallocMC: 2.0.1crp: Bugfixes\" ;"
			+ "  dcterms:abstract \"\"\"<p>This release fixes several"
			+ "            bugs that occurred after the release of"
			+ "            2.0.0crp.</p>\\n\\n<p>We closed all issues documented in"
			+ "            Milestone <em>Bugfixes</em>.</p>\"\"\" ;"
			+ "  dcterms:description \"\"\"This library started as a fork of"
			+ "            ScatterAlloc, see citations"
			+ "            http://dx.doi.org/10.1109/InPar.2012.6339604\"\"\" ;"
			+ "  dcterms:creator <http://orcid.org/0000-0002-6459-0842>, ["
			+ "    foaf:name \"Axel Huebl\" ;"
			+ "    a dcterms:Agent"
			+ "  ], ["
			+ "    foaf:name \"René Widera\" ;"
			+ "    a dcterms:Agent"
			+ "  ] ;"
			+ "  scoro:contact-person <http://orcid.org/0000-0002-6459-0842> ;"
			+ "  scoro:data-manager <http://orcid.org/0000-0002-6459-0842> ;"
			+ "  scoro:project-leader ["
			+ "    foaf:name \"Axel Huebl\" ;"
			+ "    a dcterms:Agent"
			+ "  ] ;"
			+ "  scoro:project-member ["
			+ "    foaf:name \"René Widera\" ;"
			+ "    a dcterms:Agent"
			+ "  ] ;"
			+ "  dc:subject \"CUDA, HPC, Manycore, GPU, Policy Based Design\" ."
			+ "<http://orcid.org/0000-0002-6459-0842>"
			+ "  foaf:name \"Carlchristian Eckert\" ;"
			+ "  a dcterms:Agent ."
			+ "[]"
			+ "  a <http://rmap-project.org/rmap/terms/DiSCO> ;"
			+ "  dcterms:creator <http://datacite.org> ;"
			+ "  rmap:aggregates <http://dx.doi.org/10.5281/zenodo.13962> .";
	
	protected DiscoResponseManager responseManager = null;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		try {
			responseManager = new DiscoResponseManager();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown " + e.getMessage());
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
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
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

    	Response response=null;
    	RdfType returnType = null;
    	
   		RdfMediaType matchingType = RdfMediaType.get("application/xml");
   		if (matchingType!=null){
    		returnType=matchingType.getReturnType();
    	}

		
		//createDisco
		
		//RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
		//InputStream rdf = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
		//RMapDiSCO rmapDisco = rdfHandler.rdf2RMapDiSCO(rdf, RestApiUtils.getDiscoBaseUrl(), "RDFXML");
		String discoURI = "ark:/22573/rmd1s3d2v";
		
		RMapService rmapService = RMapServiceFactoryIOC.getFactory().createService();
		
		//TODO: System agent param is fudged... need to correct this code when proper authentication handling available.
		//rmapService.createDiSCO(new RMapUri(RestApiUtils.getDefaultSystemAgentURI()), rmapDisco);
		//discoURI = rmapDisco.getId().toString();		
        //assertNotNull(discoURI);
		
		try {
			response = responseManager.getRMapDiSCO(URLEncoder.encode(discoURI, "UTF-8"),returnType);
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("disco"));
		assertTrue(body.contains("DiSCO"));
		assertEquals(200, response.getStatus());
	}
	

	/**
	 * Tests whether can retrieve response for updated DiSCO 
	 */
	@Test
	public void testGetRMapDiscoThatHasBeenUpdated() throws Exception{

    	Response response=null;
    	RdfType returnType = null;
    	
   		RdfMediaType matchingType = RdfMediaType.get("application/xml");
   		if (matchingType!=null){
    		returnType=matchingType.getReturnType();
    	}
   		
   		String discoURI = "ark:/27927/r8rqxpynv0";
		
		try {
			String encodedUri = URLEncoder.encode(discoURI, "UTF-8");
			response = responseManager.getRMapDiSCO(encodedUri,returnType);
		} catch (Exception e) {
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}

		assertNotNull(response);
		//String location = response.getLocation().toString();
		String body = response.getEntity().toString();
		//assertTrue(location.contains("disco"));
		assertTrue(body.contains("DiSCO"));
		assertEquals(200, response.getStatus());
	}
	


	/**
	 * Tests whether appropriate not found error is generated when you get a disco that 
	 * doesn't exist in the database.  
	 */
	@Test
	public void testGetRMapDiscoThatDoesntExist() throws Exception{

    	@SuppressWarnings("unused")
		Response response=null;
    	RdfType returnType = null;
    	
   		RdfMediaType matchingType = RdfMediaType.get("application/xml");
   		if (matchingType!=null){
    		returnType=matchingType.getReturnType();
    	}
   		
   		String discoURI = "ark:/27927/doesnotexist";
		boolean correctErrorThrown = false;
   		
		try {
			String encodedUri = URLEncoder.encode(discoURI, "UTF-8");
			response = responseManager.getRMapDiSCO(encodedUri,returnType);
		} catch (RMapApiException e) {
			assertEquals(e.getErrorCode(), ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
			e.printStackTrace();			
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
	public void testCreateTurtleDisco() {
		Response response = null;
		try {
			//create new ORMapAgent
			//createAgentforTest();

			AuthUserToAgentMediator userToAgentObj = new AuthUserToAgentMediator();
			URI uriSystemAgent = userToAgentObj.getRMapAgentForUser("portico");
			
			InputStream stream = new ByteArrayInputStream(discoTurtleRdf.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, RdfType.TURTLE, uriSystemAgent);
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}
	
		assertNotNull(response);
		assertEquals(201, response.getStatus());

	}
	
	

	@Test
	public void testCreateRdfXmlDisco() {
		Response response = null;
		try {
			//create new ORMapAgent
			//createAgentforTest();

			AuthUserToAgentMediator userToAgentObj = new AuthUserToAgentMediator();
			URI uriSystemAgent = userToAgentObj.getRMapAgentForUser("portico");
			
			InputStream stream = new ByteArrayInputStream(discoRDF.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, RdfType.RDFXML, uriSystemAgent);
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
			e.printStackTrace();			
			fail("Exception thrown " + e.getMessage());
		}
	
		assertNotNull(response);
		assertEquals(201, response.getStatus());

	}
	
	
	@Test
	public void testCreateDiscoNoCreator() {
		@SuppressWarnings("unused")
		Response response = null;
		boolean correctErrorThrown = false;
		try {
			AuthUserToAgentMediator userToAgentObj = new AuthUserToAgentMediator();
			URI uriSystemAgent = userToAgentObj.getRMapAgentForUser("portico");
			
			InputStream stream = new ByteArrayInputStream(discoRDFNoCreator.getBytes(StandardCharsets.UTF_8));
			response = responseManager.createRMapDiSCO(stream, RdfType.RDFXML, uriSystemAgent);
			
		} catch (RMapApiException e) {
			assertEquals(e.getErrorCode(), ErrorCode.ER_CORE_GENERIC_RMAP_EXCEPTION);
			e.printStackTrace();			
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


}
