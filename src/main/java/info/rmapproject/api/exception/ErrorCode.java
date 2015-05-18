package info.rmapproject.api.exception;

import javax.ws.rs.core.Response.Status;

/**
 * @author khanson
 * Custom error codes for RMap API
 */
public enum ErrorCode {		
	//400**** Bad Request
	ER_NO_OBJECT_URI_PROVIDED (Status.BAD_REQUEST,4001001),
	ER_NO_ACCEPT_TYPE_PROVIDED (Status.BAD_REQUEST,4001002),
	ER_PARAM_WONT_CONVERT_TO_URI (Status.BAD_REQUEST,4001003),
	ER_GET_AGENT_BAD_ARGUMENT (Status.BAD_REQUEST, 4001004),
	ER_GET_DISCO_BAD_ARGUMENT (Status.BAD_REQUEST, 4001005),
	ER_GET_EVENT_BAD_ARGUMENT (Status.BAD_REQUEST, 4001006),
	ER_GET_RESOURCE_BAD_ARGUMENT (Status.BAD_REQUEST, 4001007),
	ER_GET_STMT_BAD_ARGUMENT (Status.BAD_REQUEST, 4001008),
	ER_NO_DISCO_RDF_PROVIDED (Status.BAD_REQUEST, 4001009),
	ER_NO_AGENT_RDF_PROVIDED (Status.BAD_REQUEST, 4001010),
	ER_NO_CONTENT_TYPE_PROVIDED (Status.BAD_REQUEST, 4001011),
	ER_NO_STMT_SUBJECT_PROVIDED (Status.BAD_REQUEST, 4001012),
	ER_NO_STMT_PREDICATE_PROVIDED (Status.BAD_REQUEST, 4001013),
	ER_NO_STMT_OBJECT_PROVIDED (Status.BAD_REQUEST, 4001014),
	ER_NO_RELATED_OBJECT_TYPE_PROVIDED (Status.BAD_REQUEST, 4001015),
	ER_STATUS_TYPE_NOT_RECOGNIZED (Status.BAD_REQUEST, 4001016),
	
	//401**** Unauthorized
	ER_USER_NOT_AUTHENTICATED (Status.UNAUTHORIZED, 401001),
	ER_NO_USER_TOKEN_PROVIDED (Status.UNAUTHORIZED, 401002),
	ER_INVALID_USER_TOKEN_PROVIDED (Status.UNAUTHORIZED, 401003),
	ER_NO_SYSTEMAGENT_PROVIDED (Status.UNAUTHORIZED, 401004), 
	ER_INVALID_SYSTEMAGENT_PROVIDED (Status.UNAUTHORIZED, 401005), 
	ER_SYSTEMAGENT_DELETED (Status.UNAUTHORIZED, 401006), 
	
	//404**** Not Found
	ER_AGENT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041001), 
	ER_DISCO_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041002), 
	ER_EVENT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041003), 
	ER_RESOURCE_NOT_FOUND (Status.NOT_FOUND,4041004), 
	ER_STMT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041005), 
	ER_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041006), 
	ER_NO_RELATED_AGENTS_FOUND (Status.NOT_FOUND,4041007),
	ER_NO_STMTS_FOUND_FOR_RESOURCE (Status.NOT_FOUND, 4041008),
	
	//406**** Format Not Acceptable
	ER_CANNOT_ACCEPT_FORMAT_PROVIDED (Status.NOT_ACCEPTABLE,4061001),
	
	//410**** Gone (deleted or tombstoned object)
	ER_DISCO_DELETED (Status.GONE,4101001), 
	ER_DISCO_TOMBSTONED (Status.GONE,4101002),
	ER_AGENT_DELETED (Status.GONE,4101003), 
	ER_AGENT_TOMBSTONED (Status.GONE,4101004),
	ER_STMT_DELETED (Status.GONE,4101005), 
	ER_STMT_TOMBSTONED (Status.GONE,4101006),
	ER_OBJECT_DELETED (Status.GONE,4101007), 
	ER_OBJECT_TOMBSTONED (Status.GONE,4101008),
	
	//500**** Internal Server Errors
	//5001*** Internal Server Errors that probably originate in API code
	ER_FAILED_TO_INIT_API_RESP_MGR (Status.INTERNAL_SERVER_ERROR, 5001001),
	ER_RETRIEVING_API_HEAD(Status.INTERNAL_SERVER_ERROR,5001002),
	ER_RETRIEVING_API_OPTIONS(Status.INTERNAL_SERVER_ERROR,5001003),
	ER_CREATE_RMAP_SERVICE_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5001004),
	ER_RMAP_API_PROPERTIES_FILENOTFOUND (Status.INTERNAL_SERVER_ERROR, 5001005),
	ER_RMAP_API_PROPERTIES_FORMATERROR (Status.INTERNAL_SERVER_ERROR, 5001006),
	ER_RMAP_API_PROPERTIES_BASEURL_MISSING (Status.INTERNAL_SERVER_ERROR, 5001007),
	ER_BUILD_JSON_URILIST_FAILED (Status.INTERNAL_SERVER_ERROR, 5001008),
	ER_CANNOT_ENCODE_URL (Status.INTERNAL_SERVER_ERROR, 5001009),
	ER_COULD_NOT_MAP_ACCEPT_PARAMETER_TO_TYPE (Status.INTERNAL_SERVER_ERROR,5001010),
	ER_NO_DEFAULT_SYSTEM_AGENT_SET (Status.INTERNAL_SERVER_ERROR,5001011),
	ER_CANNOT_ACCEPT_CONTENTTYPE_PROVIDED (Status.INTERNAL_SERVER_ERROR,5001012),
	ER_COULD_NOT_MAP_CONTENTTYPE_PARAMETER_TO_TYPE (Status.INTERNAL_SERVER_ERROR,5001013),
	ER_PROCESSING_SYSTEMAGENT (Status.INTERNAL_SERVER_ERROR, 5001014),
	ER_NO_RMAPSYSTEMAGENT (Status.INTERNAL_SERVER_ERROR, 5001015),
	ER_COULDNT_RETRIEVE_DISCO_VERSION_LINKS (Status.INTERNAL_SERVER_ERROR, 5001016),

	//5002*** Internal Server Errors due to uncaught error in Core RMap Service
	ER_CORE_READ_AGENT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002001),
	ER_CORE_READ_DISCO_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002002),
	ER_CORE_READ_EVENT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002003),
	ER_CORE_READ_RESOURCE_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002004),
	ER_CORE_READ_STMT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002005),
	ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002006),
	ER_CORE_RDFHANDLER_OUTPUT_ISNULL (Status.INTERNAL_SERVER_ERROR,5002007),
	ER_CORE_GET_STATUS_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002008),
	ER_CORE_GET_EVENTLIST_EMPTY (Status.INTERNAL_SERVER_ERROR,5002009),
	ER_CORE_GET_RELATEDAGENTLIST_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002010),
	ER_CORE_COULD_NOT_RETRIEVE_DISCO_VERSION (Status.INTERNAL_SERVER_ERROR, 5002011),
	ER_CORE_RDF_TO_DISCO_FAILED (Status.INTERNAL_SERVER_ERROR, 5002012),
	ER_CORE_RDF_TO_AGENT_FAILED (Status.INTERNAL_SERVER_ERROR, 5002013),
	ER_CORE_CREATEDISCO_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002014),
	ER_CORE_CREATEAGENT_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002015),
	ER_CORE_UPDATEDISCO_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002016),
	ER_CORE_UPDATEAGENT_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002017),
	ER_CORE_GET_DISCOID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002018),
	ER_CORE_GET_AGENTID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002019),
	ER_CORE_DISCOURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002020),
	ER_CORE_AGENTURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002021),
	ER_CORE_GET_EVENTID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002022),
	ER_CORE_EVENTURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002023),
	ER_CORE_GET_STMTID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002024),
	ER_CORE_GET_DISCO_VERSIONLIST_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002025),
	ER_CORE_GET_EVENTRELATEDLIST_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002026),
	ER_CORE_PROCESSING_SYSTEMAGENT (Status.INTERNAL_SERVER_ERROR, 5002027),
	ER_CORE_GET_RDFSTMTLIST_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002028),
	ER_CORE_CANT_CREATE_STMT_RDF (Status.INTERNAL_SERVER_ERROR, 5002029),
	
	//5009*** Generic Internal Server Errors
	ER_CORE_GENERIC_RMAP_EXCEPTION (Status.INTERNAL_SERVER_ERROR,5009000),
	ER_UNKNOWN_SYSTEM_ERROR (Status.INTERNAL_SERVER_ERROR,5009001); 

	private final int number;
	private final Status status;

	private ErrorCode (Status status, int number) {
		this.number = number;
		this.status = status;
	}

	public int getNumber()  {
		return number;
	}

	public Status getStatus()  {
		return status;
	}
}

