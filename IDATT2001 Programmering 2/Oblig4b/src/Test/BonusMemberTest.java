package Test;

import MemberArchive.BasicMember;
import MemberArchive.BonusMember;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class BonusMemberTest {

	@Test
	public void testInvalidParametersInConstructor() {
		try {
			BonusMember bm = new BasicMember(12, null, null); // Should throw exception
			fail(); // If I get to this line, the test has failed
		} catch (IllegalArgumentException e) {
			//Do not need to add anything here, since if the Exception is thrown, the
			//test is an success
		}
	}
}