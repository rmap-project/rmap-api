package info.rmapproject.api.lists;

import static org.junit.Assert.*;

import org.junit.Test;

public class RdfMediaTypeTest {

	@Test
	public void testGetAcceptType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetReturnType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		RdfMediaType type = RdfMediaType.get("text/turtle");
		assertEquals(type, RdfMediaType.TEXT_TURTLE);	
		RdfType returnType = type.getReturnType();
		assertEquals(returnType, RdfType.TURTLE);
	}

}
