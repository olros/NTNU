import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import Components.Encrypter;
import Components.UserInfo;
import Database.HibernateClasses.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UserInfoTest {

  private static User user;

  @BeforeAll
  static void setup(){
    String encrypted = Encrypter.encrypt("password", null);
    user = new User();
    user.setUsername("test");
    user.setHash(Encrypter.getHash(encrypted));
    user.setSalt(Encrypter.getSalt(encrypted));
  }

  @Test
  void initializeUserAndGetUser_LoggedIn_True(){
    UserInfo.initializeUser(user);
    assertEquals(UserInfo.getUser(), user);
  }

  @Test
  void logOut_userLoggedIn_True(){
    UserInfo.logOut();
    assertNull(UserInfo.getUser());
  }
}
