package info.rmapproject.api.responsemgr;

import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.model.event.RMapEvent;
import info.rmapproject.core.model.impl.openrdf.ORAdapter;
import info.rmapproject.core.model.impl.openrdf.ORMapAgent;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;

import org.junit.Before;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class ResponseManagerTest {

	protected URI AGENT_URI;
	protected URI IDPROVIDER_URI;
	protected URI AUTH_ID;
	protected Value NAME;
	protected RMapService rmapService;
	protected java.net.URI testAgentURI; //used to pass back into rmapService since all of these use java.net.URI

	protected String genericDiscoRdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
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
	
	public ResponseManagerTest() {
		super();
	}

	@Before
	public void setUp() throws Exception {
		this.rmapService=RMapServiceFactoryIOC.getFactory().createService();
		this.testAgentURI = createTestAgent();
		
	}

	protected java.net.URI createTestAgent() {
		AGENT_URI = ORAdapter.getValueFactory().createURI("ark:/22573/rmaptestagent");
		IDPROVIDER_URI = ORAdapter.getValueFactory().createURI("http://orcid.org/");
		AUTH_ID = ORAdapter.getValueFactory().createURI("http://rmap-project.org/identities/rmaptestauthid");
		NAME = ORAdapter.getValueFactory().createLiteral("RMap test Agent");
	
		java.net.URI agentUri = null ;
		
		//create through ORMapAgentMgr
		try {
			agentUri = new java.net.URI(AGENT_URI.toString());
						
			if (!rmapService.isAgentId(agentUri)){
				
				RMapAgent agent = new ORMapAgent(AGENT_URI, IDPROVIDER_URI, AUTH_ID, NAME);
				@SuppressWarnings("unused")
				RMapEvent event = rmapService.createAgent(agent.getId().getIri(), agent);
				agentUri=agent.getId().getIri();
				
				if (rmapService.isAgentId(agentUri)){
					System.out.println("Test Agent successfully created!  URI is ark:/29297/rmaptestagent");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return agentUri;
	}

}