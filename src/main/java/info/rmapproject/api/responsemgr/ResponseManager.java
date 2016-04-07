package info.rmapproject.api.responsemgr;

import info.rmapproject.api.exception.ErrorCode;
import info.rmapproject.api.exception.RMapApiException;
import info.rmapproject.core.exception.RMapDefectiveArgumentException;
import info.rmapproject.core.model.request.DateRange;
import info.rmapproject.core.model.request.RMapSearchParams;
import info.rmapproject.core.rdfhandler.RDFHandler;
import info.rmapproject.core.rmapservice.RMapService;

import java.nio.charset.Charset;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public abstract class ResponseManager {

	protected static final String FROM_PARAM="from";
	protected static final String UNTIL_PARAM="until";
	protected static final String LIMIT_PARAM="limit";
	protected static final String PAGE_PARAM="page";
	protected static final String AGENTS_PARAM="agents";
	protected static final String STATUS_PARAM="status";
	
	
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
	 * Creates pagination links for linkRef in response header
	 * @param path
	 * @param origQuery
	 * @param page
	 * @param includeNext
	 * @return
	 */
	protected String generatePaginationLinks(String path, UriInfo uriInfo, Integer page, boolean includeNext){

		String query = uriInfo.getRequestUri().getQuery(); 
		String numberPlaceholder = "**#pagenum#**";
		
		List<NameValuePair> queryParams = URLEncodedUtils.parse(query, Charset.forName("UTF-8"));
		StringBuilder newReqUrl = new StringBuilder(path + "?");
		boolean firstpass = true;
		boolean pageParamExists = false;
	    for(NameValuePair param : queryParams) {
	    	if (!firstpass) {
	    		newReqUrl.append("&");
	    	}
	    	if(param.getName().equals("page")){
	    		pageParamExists=true;
	    		newReqUrl.append(param.getName() + "=" + numberPlaceholder);
	    	} else {
	    		newReqUrl.append(param.getName() + "=" + param.getValue());		    		
	    	}
	    	firstpass=false;
	    }
	    if (!pageParamExists){
	    	newReqUrl.append("&page=" + numberPlaceholder);
	    }
	    
	    StringBuilder paginationLinks = new StringBuilder();
	    if (page>1){
	    	String firstUrl = newReqUrl.toString();
	    	firstUrl = firstUrl.replace(numberPlaceholder, "1");
	    	paginationLinks.append("<" + firstUrl + ">" + ";rel=\"first\"");
	    	
	    	String previousUrl = path + newReqUrl.toString();
	    	Integer previousPage = page-1;
	    	previousUrl = previousUrl.replace(numberPlaceholder, previousPage.toString());
	    	paginationLinks.append("<" + previousUrl + ">" + ";rel=\"previous\"");
	    }
	    
	    if (includeNext){
	    	String nextUrl = newReqUrl.toString();
	    	Integer nextPage = page+1;
	    	nextUrl = nextUrl.replace(numberPlaceholder, nextPage.toString());
	    	paginationLinks.append("<" + nextUrl + ">" + ";rel=\"next\"");	    	
	    }	    
		return paginationLinks.toString();
	}

	/**
	 * Creates search parameters object from UriInfo
	 * @param uriInfo
	 * @param includeNext
	 * @return
	 */
	protected RMapSearchParams generateSearchParamObj(UriInfo uriInfo) throws RMapApiException{
		RMapSearchParams params = new RMapSearchParams();
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
		
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
		
	
	
	
}
