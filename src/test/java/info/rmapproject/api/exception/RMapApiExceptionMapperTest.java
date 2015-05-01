package info.rmapproject.api.exception;

import javax.ws.rs.core.Response;

import org.junit.Test;

public class RMapApiExceptionMapperTest {

	@Test	
	public void test() {
		
		RMapApiException e = new RMapApiException(ErrorCode.ER_DISCO_OBJECT_NOT_FOUND);
		RMapApiExceptionHandler mapper = new RMapApiExceptionHandler();
		Response response = mapper.toResponse(e);
		System.out.print(response.getEntity().toString());
	}

}
