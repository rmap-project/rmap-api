package info.rmapproject.api.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ErrorCodeTest {

	@Test
	public void testGetMessage() {
		String message = ErrorCode.ER_AGENT_OBJECT_NOT_FOUND.getMessage();
		assertEquals(message, "Requested RMap:Agent not found");
	}

}
