package info.rmapproject.api.authentication;

import info.rmapproject.api.utils.URLUtils;
import info.rmapproject.core.model.agent.RMapAgent;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rdfhandler.RDFHandlerFactoryIOC;
import info.rmapproject.core.rdfhandler.impl.openrdf.RioRDFHandler;
import info.rmapproject.core.rmapservice.RMapService;
import info.rmapproject.core.rmapservice.RMapServiceFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestore;
import info.rmapproject.core.rmapservice.impl.openrdf.triplestore.SesameTriplestoreFactoryIOC;
import info.rmapproject.core.rmapservice.impl.openrdf.vocabulary.RMAP;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;

public class CreateDefaultUsers {
	
	private static String systemAgent = "ark:/22573/rmd3jq0";
	
	private String rmapAgentRdf = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
								+ "<rdf:RDF"
								+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
								+ " xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""
								+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""
								+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">"
								+ "<rdf:Description rdf:about=\"ark:/22573/rmd3jq0\">"
								+ "	<rdf:type rdf:resource=\"http://rmap-project.org/rmap/terms/Agent\"/>"
								+ "	<dcterms:creator rdf:resource=\"ark:/22573/rmd3jpk\"/>"
								+ "	<rmap:agentRepresentation rdf:resource=\"ark:/22573/rmd3jpk\"/>"
								+ "</rdf:Description>"
								+ "<rdf:Description rdf:about=\"ark:/22573/rmd3jpk\">"
								+ "	<foaf:name>RMap System Agent</foaf:name>"
								+ "</rdf:Description>"
								+ "</rdf:RDF>";
	
	String porticoAgent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
							+ "<rdf:RDF "
							+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
							+ "    xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""
							+ "    xmlns:dcterms=\"http://purl.org/dc/terms/\""
							+ "    xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">	"								    
							+ "    <rmap:Agent> "
							+ "        <dcterms:creator rdf:resource=\"http://orcid.org/0000-0002-9354-8328\"/>"
							+ "        <rmap:agentRepresentation rdf:resource=\"http://isni.org/isni/0000000406115044\"/>"
							+ "    </rmap:Agent>"
							+ "    <foaf:Organization rdf:about=\"http://isni.org/isni/0000000406115044\"> "
							+ "        <foaf:name>Portico</foaf:name>"
							+ "        <foaf:homepage>http://portico.org</foaf:homepage>"
							+ "    </foaf:Organization>"
							+ "</rdf:RDF>";

	private String ieeeAgent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
									+ "<rdf:RDF "
									+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
									+ "    xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""
									+ "    xmlns:dcterms=\"http://purl.org/dc/terms/\""
									+ "    xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">	"								    
									+ "    <rmap:Agent> "
									+ "        <dcterms:creator rdf:resource=\"http://orcid.org/0000-0002-9354-8328\"/>"
									+ "        <rmap:agentRepresentation rdf:resource=\"http://isni.org/isni/000000010941358X\"/>"
									+ "    </rmap:Agent>"
									+ "    <foaf:Organization rdf:about=\"http://isni.org/isni/000000010941358X\"> "
									+ "        <foaf:name>IEEE</foaf:name>"
									+ "        <foaf:homepage>https://www.ieee.org</foaf:homepage>"
									+ "    </foaf:Organization>"
									+ "</rdf:RDF>";

	private String jhuAgent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
								+ "<rdf:RDF "
								+ "    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
								+ "    xmlns:rmap=\"http://rmap-project.org/rmap/terms/\""
								+ "    xmlns:dcterms=\"http://purl.org/dc/terms/\""
								+ "    xmlns:foaf=\"http://xmlns.com/foaf/0.1/\">	"								    
								+ "    <rmap:Agent> "
								+ "        <dcterms:creator rdf:resource=\"http://orcid.org/0000-0002-9354-8328\"/>"
								+ "        <rmap:agentRepresentation rdf:resource=\"http://isni.org/isni/0000000101404355\"/>"
								+ "    </rmap:Agent>"
								+ "    <foaf:Organization rdf:about=\"http://isni.org/isni/0000000101404355\"> "
								+ "        <foaf:name>Johns Hopkins University</foaf:name>"
								+ "        <foaf:homepage>http://www.jhu.edu/</foaf:homepage>"
								+ "    </foaf:Organization>"
								+ "</rdf:RDF>";

	private RMapService rmapService = null;
	private AuthUserToAgentMediator authUserToAgentMediator;

	public CreateDefaultUsers(AuthUserToAgentMediator authUserToAgentMediator) {
		this.authUserToAgentMediator=authUserToAgentMediator;
	}
	
	public void createDefaultUsers() throws Exception{
		URI systemAgentUri = new URI(systemAgent);
		rmapService = RMapServiceFactoryIOC.getFactory().createService();
		checkRmapSystemAgent();
		createUser("portico", porticoAgent, systemAgentUri);
		createUser("ieee", ieeeAgent, systemAgentUri);
		createUser("jhu",jhuAgent, systemAgentUri);
	}
	
	public void createUser(String username, String agentRdf, URI systemAgentUri) throws Exception {
		if (authUserToAgentMediator.getRMapAgentForUser(username) == null)	{
			RDFHandler rdfHandler = RDFHandlerFactoryIOC.getFactory().createRDFHandler();
			InputStream rdf = new ByteArrayInputStream(agentRdf.getBytes(StandardCharsets.UTF_8));	
			RMapAgent rmapAgent = rdfHandler.rdf2RMapAgent(systemAgentUri, rdf, URLUtils.getAgentBaseUrl(), "RDFXML");
			rmapService.createAgent(systemAgentUri, rmapAgent);
			String newAgentURI = rmapAgent.getId().toString();
			authUserToAgentMediator.setRMapAgentForUser(username, newAgentURI);
		}
	}
	
	private void checkRmapSystemAgent() throws Exception{
		//check for rmap system agent, if it doesn't exist, create one from XML
		SesameTriplestore ts = SesameTriplestoreFactoryIOC.getFactory().createTriplestore();
		ValueFactory vf = ts.getValueFactory();
		org.openrdf.model.URI rmapSysAgentUri = vf.createURI("ark:/22573/rmd3jq0");
		
		Statement typeStmt = ts.getStatement(rmapSysAgentUri, RDF.TYPE, RMAP.AGENT);
		if (typeStmt==null)	{
			//need to create rmap system agent
			InputStream rdf = new ByteArrayInputStream(rmapAgentRdf.getBytes(StandardCharsets.UTF_8));
			
			RioRDFHandler rdfHandler = new RioRDFHandler();
			List <Statement> statements = rdfHandler.convertRDFToStmtList(rdf, "RDFXML", "");
			for (Statement stmt : statements){
				ts.addStatement(stmt.getSubject(), stmt.getPredicate(), stmt.getObject(), rmapSysAgentUri);
			}
			ts.commitTransaction();
		}
	}
	
}
