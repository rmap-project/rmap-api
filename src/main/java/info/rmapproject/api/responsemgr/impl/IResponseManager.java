package info.rmapproject.api.responsemgr.impl;

import info.rmapproject.api.responsemgr.ResponseManager;

import java.io.InputStream;

import javax.ws.rs.core.Response;

/**
 * 
 * @author khanson
 *
 */
public class IResponseManager implements ResponseManager {

	protected IDiscoResponseManager discoRespMgr = new IDiscoResponseManager();
	protected IStatementResponseManager stmtRespMgr = new IStatementResponseManager();
	protected IEventResponseManager eventRespMgr = new IEventResponseManager();
	protected IAgentResponseManager agentRespMgr = new IAgentResponseManager();
	protected IProfileResponseManager profileRespMgr = new IProfileResponseManager();
	protected IResourceResponseManager resourceRespMgr = new IResourceResponseManager();

	public IResponseManager() {};
	
	/**
	 * DiSCO Service functions...
	 */
	public Response getRMapDiSCO(String strDiscoUri, String acceptType) {
		return this.discoRespMgr.getRMapDiSCO(strDiscoUri, acceptType);
	}
	
	public Response createRMapDiSCO(InputStream discoRdf, String contentType) {
		return this.discoRespMgr.createRMapDiSCO(discoRdf, contentType);
	}
		
	public Response updateRMapDiSCO(String origDiscoUri, InputStream discoRdf, String contentType) {
		return this.discoRespMgr.updateRMapDiSCO(origDiscoUri, discoRdf, contentType);
	}
	
	public Response getDiSCOServiceHead()	{
		return this.discoRespMgr.getDiSCOServiceHead();
	}

	public Response getDiSCOServiceOptions()	{
		return this.discoRespMgr.getDiSCOServiceOptions();
	}
	

	/**
	 * Event Service functions...
	 */
	public Response getEventServiceHead()	{
		return this.eventRespMgr.getEventServiceHead();
	}
	
	public Response getEventServiceOptions()	{
		return this.eventRespMgr.getEventServiceOptions();
	}

	public Response getRMapEvent(String eventId, String acceptsType) {
		return this.eventRespMgr.getRMapEvent(eventId, acceptsType);
	}
	
	public Response getRMapEventRelatedObjs(String strEventId, String objType, String returnType)	{
		return this.eventRespMgr.getRMapEventRelatedObjs(strEventId, objType, returnType);
	}


	/**
	 * Agent Service functions...
	 */
	public Response getAgentServiceHead()	{
		return this.agentRespMgr.getAgentServiceHead();
	}
	
	public Response getAgentServiceOptions()	{
		return this.agentRespMgr.getAgentServiceOptions();
	}
	
	public Response getRMapAgent(String strAgentUri, String acceptsType)	{
		return this.agentRespMgr.getRMapAgent(strAgentUri, acceptsType);
	}
	
	/**
	 * Statement Service functions
	 */
	public Response getStatementServiceHead(){
		return this.stmtRespMgr.getStatementServiceHead();
	}
	
	public Response getStatementServiceOptions(){
		return this.stmtRespMgr.getStatementServiceOptions();
	}

	public Response getRMapStatement(String strStatementUri, String acceptsType){
		return this.stmtRespMgr.getRMapStatement(strStatementUri, acceptsType);
	}
	
	public Response getRMapStatementRelatedEvents(String strStatementId, String returnType){
		return this.stmtRespMgr.getRMapStatementRelatedEvents(strStatementId, returnType);		
	}
	
	/**
	 * Resource Service functions
	 */
	
	public Response getResourceServiceHead()	{
		return this.resourceRespMgr.getResourceServiceHead();
	}

	public Response getResourceServiceOptions()	{
		return this.resourceRespMgr.getResourceServiceOptions();
	}
	
	public Response getRMapResourceRelatedObjs(String strResourceId, String objType, String returnType, String rmapStatus) {
		return this.resourceRespMgr.getRMapResourceRelatedObjs(strResourceId, objType, returnType, rmapStatus);
	}
	
	
	/**
	 * Profile Service functions...
	 */
	public Response getRMapProfile(String strProfileUri, String acceptType) {
		return this.profileRespMgr.getRMapProfile(strProfileUri, acceptType);
	}
	
	public Response createRMapProfile(InputStream profileRdf, String contentType) {
		return this.profileRespMgr.createRMapProfile(profileRdf, contentType);
	}
		
	public Response updateRMapProfile(String origProfileUri, InputStream profileRdf, String contentType) {
		return this.profileRespMgr.updateRMapProfile(origProfileUri, profileRdf, contentType);
	}
	
	public Response getProfileServiceHead()	{
		return this.profileRespMgr.getProfileServiceHead();
	}

	public Response getProfileServiceOptions()	{
		return this.profileRespMgr.getProfileServiceOptions();
	}
		
}
