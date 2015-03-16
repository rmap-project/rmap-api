package info.rmapproject.api.service;

import info.rmapproject.api.responsemgr.ResponseManager;
import info.rmapproject.api.responsemgr.ResponseManagerFactoryIOC;

import javax.ws.rs.core.Response;

public class TestingTesting {


	protected static ResponseManager responseManager = null;
	
	static{
		try {
			responseManager = ResponseManagerFactoryIOC.getFactory().createService();
		}
		catch (Exception e){}
	}
	
	//private static final Logger log = Logger.getLogger("rmapproject");
	public static void main(String[] args) {
		try {
			//for testing
	    	/*
	    	RMapDiSCOMgr discoMgr = new RMapDiSCOMgrImpl();
	    	 
	        String rmapDisco = discoMgr.getRMapDiSCOAsRDF("10un4ksuim", "RDFXML");	
	        System.out.print(rmapDisco);
	        */
        	
			Response response = responseManager.getRMapResourceRelatedObjs("http%3A%2F%2Fieeexplore.ieee.org%2Fielx7%2F6287639%2F6705689%2F6842585%2Fhtml%2Fmm%2F6842585-mm.zip", "ALL", "RDFXML", "");
			System.out.print(response.getEntity());
			
	    	  /*
	    	   * ResourceMgr resourceMgr = new ResourceMgrImpl();
	        String resource = resourceMgr.getResourceStmtsAsRDF(GeneralUtils.toURI("http://dx.doi.org/10.1109/ACCESS.2014.2332453"), "RDFXML");	
	        System.out.print(resource);
	      

	    	RMapDiSCOMgr discoMgr = new RMapDiSCOMgrImpl();
	    	String discoRDF = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "  
	    					+ "<rdf:RDF "  
	    					+ " xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""  
	    					+ " xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\""  
	    					+ " xmlns:rmap=\"http://rmapdns.ddns.net:8080/rmap/terms/1.0/\""  
	    					+ " xmlns:dcterms=\"http://purl.org/dc/terms/\""  
	    					+ " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""  
	    					+ " xmlns:foaf=\"http://xmlns.com/foaf/0.1/\""  
	    					+ " xmlns:fabio=\"http://purl.org/spar/fabio/\">"  
	    					+ "<rmap:Disco>"  
	    					+ "<dc:description>"  
	    					+ "This is an example DiSCO aggregating different file formats for an article on IEEE Xplore as well as multimedia content related to the article."  
	    					+ "</dc:description>"  
	    					+ "<rmap:aggregates rdf:resource=\"http://dx.doi.org/10.1109/ACCESS.2014.2332453\"/>"  
	    	    			+ "<rmap:aggregates rdf:resource=\"http://ieeexplore.ieee.org/ielx7/6287639/6705689/6842585/html/mm/6842585-mm.zip\"/>"  
	    	    	    	+ "</rmap:Disco>"  
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
	    	RMapEvent event = discoMgr.createDiSCOFromRDF(discoRDF, "RDF_XML", RMAP.RMAP_AGENT_ID);	
	        System.out.print(event.getRmapEventId());
	    	
	    		        */
	    	
			
			
		} catch (Exception generalEx){
			generalEx.printStackTrace(); //catch the rest...
		}   
	}
}