package info.rmapproject.api.responsemgr;

import java.io.InputStream;

import javax.ws.rs.core.Response;

public interface ResponseManager {
	
	//DiSCO API Responses
	public Response getRMapDiSCO(String strDiscoUri, String acceptsType);
	public Response createRMapDiSCO(InputStream discoRdf, String contentType);
	public Response updateRMapDiSCO(String origDiscoUri, InputStream discoRdf, String contentType);
	public Response getDiSCOServiceHead();
	public Response getDiSCOServiceOptions();
	
	//Event API Responses
	public Response getRMapEvent(String eventId, String acceptsType);
	public Response getEventServiceHead();
	public Response getEventServiceOptions();
	public Response getRMapEventRelatedObjs(String strEventId, String objType, String returnType);
	
	//Agent API Responses
	public Response getRMapAgent(String strAgentUri, String acceptsType);
	public Response getAgentServiceHead();
	public Response getAgentServiceOptions();

	
	//Statement API Responses
	public Response getRMapStatement(String strStmtUri, String acceptsType);
	public Response getStatementServiceHead();
	public Response getStatementServiceOptions();
	public Response getRMapStatementRelatedEvents(String strStatementId, String returnType);
	
	//Resource API Responses
	public Response getResourceServiceHead();
	public Response getResourceServiceOptions();
	public Response getRMapResourceRelatedObjs(String strResourceId, String objType, String returnType, String rmapStatus);
		
	//Profile API Responses
	public Response getRMapProfile(String strProfileUri, String acceptsType);
	public Response createRMapProfile(InputStream profileRdf, String contentType);
	public Response updateRMapProfile(String origProfileUri, InputStream profileRdf, String contentType);
	public Response getProfileServiceHead();
	public Response getProfileServiceOptions();
	
	
	
}
