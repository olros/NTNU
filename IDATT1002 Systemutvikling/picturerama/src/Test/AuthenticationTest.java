import Components.Authentication;
import Database.Hibernate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class AuthenticationTest {

    @Test
    void registerNewUser_UsernameNull_False() {
        assertFalse(Authentication.register(null, null));
    }

    @Test
    void registerNewUser_UsernameIsNotTaken_True() {
        assertTrue(Authentication.register("jUnitTest", "test"));
    }

    @Test
    void registerNewUser_UsernameIsTaken_False() {
        assertFalse(Authentication.register("jUnitTest", "test2"));
    }

    @Test
    void login_UserDoesNotExist_False() {
        assertFalse(Authentication.logIn("DoNotMakeThisUsername", "DoNotMakeThisPassword"));
    }

    @Test
    void login_UserDoesExist_True() {
        assertTrue(Authentication.logIn("jUnitTest", "test"));
    }

    @AfterAll
    static void cleanUp() {
        Hibernate.deleteUser("jUnitTest");
    }
}