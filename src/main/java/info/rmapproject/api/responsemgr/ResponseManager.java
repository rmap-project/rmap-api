package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.model.RMapIri;
import info.rmapproject.core.model.RMapLiteral;
import info.rmapproject.core.model.RMapValue;
import info.rmapproject.core.model.request.DateRange;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rmapservice.RMapService;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

public abstract class ResponseManager {

	protected static final String FROM_PARAM="from";
	protected static final String UNTIL_PARAM="until";
	protected static final String LIMIT_PARAM="limit";
	protected static final String PAGE_PARAM="page";
	protected static final String AGENTS_PARAM="agents";
	protected static final String STATUS_PARAM="status";
	protected static final String PAGENUM_PLACEHOLDER = "**$#pagenum#$**";
	
	
	protected final RMapService rmapService;
	protected final RDFHandler rdfHandler;
	
	/**
	 * Constructor receives rmapService and rdfHandler
	 * @param rmapService
	 * @param rdfHandler
	 * @throws RMapApiException
	 */
	protected ResponseManager(RMapService rmapService, RDFHandler rdfHandler) throws RMapApiException {
		if (rmapService ==null){
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_RMAP_SERVICE);
		}
		if (rdfHandler ==null){
			throw new RMapApiException(ErrorCode.ER_FAILED_TO_INIT_RDFHANDLER_SERVICE);
		}
		this.rmapService = rmapService;
		this.rdfHandler = rdfHandler;
	}
	
	/**
	 * Creates path with a placeholder for the page number to be used in pagination links
	 * @param path
	 * @param queryParams
	 * @param defaultLimit
	 * @return
	 * @throws RMapApiException
	 */
	protected String getPaginatedLinkTemplate(String path, MultivaluedMap<String,String> queryParams, Integer defaultLimit) 
			throws RMapApiException{
		try {
			//First build a template query string to return to the user.
			String from = queryParams.getFirst(FROM_PARAM);
			//until is required when paginating, this adds the current date datetime if none specified
			String until = queryParams.getFirst(UNTIL_PARAM);
			if (until==null || until.trim().length()==0){
				DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
				Date thisMoment = Calendar.getInstance().getTime();        
				String untilNow = df.format(thisMoment);
				until=untilNow;
			}
			String status = queryParams.getFirst(STATUS_PARAM);
			String agents = queryParams.getFirst(AGENTS_PARAM);
			
			//limit is also required when paginating - if none is specified in the query, use the default
			String limit = queryParams.getFirst(LIMIT_PARAM);
			if (limit==null || limit.trim().length()==0){
				limit=defaultLimit.toString();
			}
			String page = queryParams.getFirst(PAGE_PARAM);
						
			StringBuilder newReqUrl = new StringBuilder();
			
			if (from!=null) {
				newReqUrl.append("&" + FROM_PARAM + "=" + from);
			}
			if (until!=null){
				newReqUrl.append("&" + UNTIL_PARAM + "=" + until);			
			}
			if (status!=null){
				newReqUrl.append("&" + STATUS_PARAM + "=" + status);
			}
			if (agents!=null){
				newReqUrl.append("&" + AGENTS_PARAM + "=" + agents);
			}
			if (limit!=null){
				newReqUrl.append("&" + LIMIT_PARAM + "=" + limit);		
			}
			if (page!=null){
				newReqUrl.append("&" + PAGE_PARAM + "=" + PAGENUM_PLACEHOLDER);					
			}
			if (newReqUrl.length()>0){				
				newReqUrl.substring(1); //remove extra "&" at start
			}
			newReqUrl.insert(0, path + "?");
			
			return newReqUrl.toString();
	
		} catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_BAD_PARAMETER_IN_REQUEST);
		}
		
	}
			
	/**
	 * Creates pagination links for linkRef in response header. Note that duplicate parameters or irrelevant parameters will be ignored.
	 * @param path
	 * @param origQuery
	 * @param page
	 * @param includeNext
	 * @return
	 */
	protected String generatePaginationLinks(String path, MultivaluedMap<String,String> queryParams, 
											Integer defaultLimit, boolean includeNext) throws RMapApiException{
		
		try {
			Integer pageNum = 1;
			
			String page = queryParams.getFirst(PAGE_PARAM);
			page = page.trim();
			pageNum = Integer.parseInt(page);
			
			String newReqUrl = getPaginatedLinkTemplate(path, queryParams, defaultLimit);
			
			//now build the pagination links
		    StringBuilder paginationLinks = new StringBuilder();
		    if (pageNum>1){
		    	String firstUrl = newReqUrl.toString();
		    	firstUrl = firstUrl.replace(PAGENUM_PLACEHOLDER, "1");
		    	paginationLinks.append("<" + firstUrl + ">" + ";rel=\"first\"");
		    	
		    	String previousUrl = path + newReqUrl.toString();
		    	Integer previousPage = pageNum-1;
		    	previousUrl = previousUrl.replace(PAGENUM_PLACEHOLDER, previousPage.toString());
		    	paginationLinks.append("<" + previousUrl + ">" + ";rel=\"previous\"");
		    }
		    
		    if (includeNext){
		    	String nextUrl = newReqUrl.toString();
		    	Integer nextPage = pageNum+1;
		    	nextUrl = nextUrl.replace(PAGENUM_PLACEHOLDER, nextPage.toString());
		    	paginationLinks.append("<" + nextUrl + ">" + ";rel=\"next\"");	    	
		    }	    
			return paginationLinks.toString();
	
		} catch (Exception ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_BAD_PARAMETER_IN_REQUEST);
		}
	}

	/**
	 * Creates search parameters object from the queryParams. Note that duplicate parameters or irrelevant parameters will be ignored.
	 * @param uriInfo
	 * @param includeNext
	 * @return
	 */
	protected RMapSearchParams generateSearchParamObj(MultivaluedMap<String,String> queryParams) throws RMapApiException{
		RMapSearchParams params = new RMapSearchParams();
		if (queryParams==null || queryParams.size()==0){
			return params; //default params
		}
		try {
			String from = queryParams.getFirst(FROM_PARAM);
			String until = queryParams.getFirst(UNTIL_PARAM);
			String status = queryParams.getFirst(STATUS_PARAM);
			String agents = queryParams.getFirst(AGENTS_PARAM);
			String limit = queryParams.getFirst(LIMIT_PARAM);
			String page = queryParams.getFirst(PAGE_PARAM);
			
			if (from!=null || until!=null){
				DateRange dateRange = new DateRange(from, until);
				params.setDateRange(dateRange);
			}
			if (status!=null){
				params.setStatusCode(status);
			}
			if (agents!=null){
				params.setSystemAgents(agents);
			}
			if (limit!=null){
				params.setLimit(limit);				
			}
			if (page!=null){
				params.setPage(page);				
			}
		}
		catch (RMapDefectiveArgumentException ex) {
			throw RMapApiException.wrap(ex, ErrorCode.ER_BAD_PARAMETER_IN_REQUEST);
		}
		
		return params;
	}
		
	/**
	 * Converts a string of text passed in as the "object" through the API request to a valid RMapValue
	 * determining whether it is a typed literal, URI etc.
	 * @param sPathString
	 * @return
	 * @throws RMapApiException
	 */
	public RMapValue convertPathStringToRMapValue(String sPathString) throws RMapApiException{
		RMapValue object = null;
		try {
			sPathString = URLDecoder.decode(sPathString, "UTF-8");
	
			if (sPathString.startsWith("\"")) {
				String literal = sPathString.substring(1, sPathString.lastIndexOf("\""));
				String literalProp = sPathString.substring(sPathString.lastIndexOf("\"")+1);
				
				if (literalProp.contains("^^")) {
					String sType = literalProp.substring(literalProp.indexOf("^^")+2);
					RMapIri type = null;
					sType = sType.trim();
	
					sType = removeUriAngleBrackets(sType);
					type = new RMapIri(new URI(sType));
					object = new RMapLiteral(literal, type);
				}
				else if (literalProp.contains("@")) {
					String language = literalProp.substring(literalProp.indexOf("@")+1);
					language = language.trim();
					object = new RMapLiteral(literal, language);
				}
				else {
					object = new RMapLiteral(literal);
				}
			}
			else { //should be a URI
				object = new RMapIri(new URI(sPathString));
			}	
		}
		catch (URISyntaxException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch (UnsupportedEncodingException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
	
		return object;
	}
	
	/**
	 * Converts a string of text passed in as a "resource" (including subject or predicate) through the API request to a valid java.net.URI
	 * @param sPathString
	 * @return
	 * @throws RMapApiException
	 */
	public URI convertPathStringToURI(String sPathString) throws RMapApiException{
		URI uri = null;
		try {
			sPathString = URLDecoder.decode(sPathString, "UTF-8");
			sPathString = removeUriAngleBrackets(sPathString);
			uri = new URI(sPathString);
		}
		catch (URISyntaxException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		catch (UnsupportedEncodingException ex){
			throw RMapApiException.wrap(ex, ErrorCode.ER_PARAM_WONT_CONVERT_TO_URI);
		}
		return uri;
	}
	
	/**
	 * Checks for angle brackets around a string URI and removes them if found
	 * @param sUri
	 * @return
	 */
	public String removeUriAngleBrackets(String sUri) {
		//remove any angle brackets on a string Uri
		if (sUri.startsWith("<")) {
			sUri = sUri.substring(1);
		}
		if (sUri.endsWith(">")) {
			sUri = sUri.substring(0,sUri.length()-1);
		}
		return sUri;
	}
	
	
}
