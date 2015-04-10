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
	ER_GET_PROFILE_BAD_ARGUMENT (Status.BAD_REQUEST, 4001007),
	ER_GET_RESOURCE_BAD_ARGUMENT (Status.BAD_REQUEST, 4001008),
	ER_GET_STMT_BAD_ARGUMENT (Status.BAD_REQUEST, 4001009),
	ER_NO_DISCO_RDF_PROVIDED (Status.BAD_REQUEST, 4001010),
	ER_NO_PROFILE_RDF_PROVIDED (Status.BAD_REQUEST, 4001011),
	ER_NO_CONTENT_TYPE_PROVIDED (Status.BAD_REQUEST, 4001012),
	ER_NO_STMT_SUBJECT_PROVIDED (Status.BAD_REQUEST, 4001013),
	ER_NO_STMT_PREDICATE_PROVIDED (Status.BAD_REQUEST, 4001014),
	ER_NO_STMT_OBJECT_PROVIDED (Status.BAD_REQUEST, 4001015),
	ER_NO_RELATED_OBJECT_TYPE_PROVIDED (Status.BAD_REQUEST, 4001016),
	
	//404**** Not Found
	ER_AGENT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041001), 
	ER_DISCO_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041002), 
	ER_EVENT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041003), 
	ER_PROFILE_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041004), 
	ER_RESOURCE_NOT_FOUND (Status.NOT_FOUND,4041005), 
	ER_STMT_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041006), 
	ER_OBJECT_NOT_FOUND (Status.NOT_FOUND,4041007), 
	ER_NO_AGENT_RELATED_PROFILES_FOUND (Status.NOT_FOUND,4041008),
	
	//410**** Gone (deleted or tombstoned object)
	ER_DISCO_DELETED (Status.GONE,4101001), 
	ER_DISCO_TOMBSTONED (Status.GONE,4101002),
	ER_PROFILE_DELETED (Status.GONE,4101003), 
	ER_PROFILE_TOMBSTONED (Status.GONE,4101004),
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

	//5002*** Internal Server Errors due to uncaught error in Core RMap Service
	ER_CORE_READ_AGENT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002001),
	ER_CORE_READ_DISCO_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002002),
	ER_CORE_READ_EVENT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002003),
	ER_CORE_READ_PROFILE_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002004),
	ER_CORE_READ_RESOURCE_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002005),
	ER_CORE_READ_STMT_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002006),
	ER_CORE_CREATE_RDFHANDLER_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002007),
	ER_CORE_RDFHANDLER_OUTPUT_ISNULL (Status.INTERNAL_SERVER_ERROR,5002008),
	ER_CORE_GET_STATUS_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002009),
	ER_CORE_GET_EVENTLIST_EMPTY (Status.INTERNAL_SERVER_ERROR,5002010),
	ER_CORE_GET_PROFILELIST_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002011),
	ER_CORE_DISCO_VERSION_ID_MALFORMED (Status.INTERNAL_SERVER_ERROR, 5002012),
	ER_CORE_RDF_TO_DISCO_FAILED (Status.INTERNAL_SERVER_ERROR, 5002013),
	ER_CORE_RDF_TO_PROFILE_FAILED (Status.INTERNAL_SERVER_ERROR, 5002014),
	ER_CORE_CREATEDISCO_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002015),
	ER_CORE_CREATEPROFILE_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002016),
	ER_CORE_UPDATEDISCO_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002017),
	ER_CORE_UPDATEPROFILE_NOT_COMPLETED (Status.INTERNAL_SERVER_ERROR, 5002018),
	ER_CORE_GET_DISCOID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002019),
	ER_CORE_GET_PROFILEID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002020),
	ER_CORE_DISCOURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002021),
	ER_CORE_PROFILEURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002022),
	ER_CORE_GET_EVENTID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002023),
	ER_CORE_EVENTURI_STRING_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002024),
	ER_CORE_GET_STMTID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR, 5002025),
	ER_CORE_GET_DISCO_VERSIONLIST_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002026),
	ER_CORE_GET_EVENTRELATEDLIST_EMPTY (Status.INTERNAL_SERVER_ERROR, 5002027),
	ER_CORE_GET_IDENTITYLIST_EMPTY (Status.INTERNAL_SERVER_ERROR,5002028),
	ER_CORE_GET_PREFERREDID_RETURNED_NULL (Status.INTERNAL_SERVER_ERROR,5002029),
	
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

