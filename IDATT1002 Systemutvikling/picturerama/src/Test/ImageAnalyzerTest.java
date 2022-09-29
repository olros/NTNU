import Components.FileLogger;
import Components.ImageAnalyzer;
import Components.UserInfo;
import Database.HibernateClasses.Photo;
import Database.HibernateClasses.User;
import java.io.IOException;
import java.util.logging.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Class for testing of
 * ImageAnalyzer class
 */
class ImageAnalyzerTest {

  private static User user;
  private static Photo photo;

  @BeforeAll
  static void setup() {
    try {
      user = new User();
      user.setUsername("_Test_");
      user.setId(32);
      UserInfo.initializeUser(user);
      photo = ImageAnalyzer.analyze("TestPicture", "file:src/Test/Assets/test_image.jpg");
    } catch(IOException e) {
      FileLogger.getLogger().log(Level.ALL, e.getMessage());
      FileLogger.closeHandler();
    }
  }

  /**
   * Test to check if the photo object is not null.
   */
  @Test
  void analyse_PhotoExist_NotNull() {
    assertNotNull(photo);
   }

  /**
   * Check if the title of the photo is valid.
   */
  @Test
   void getTitle_CorrectPhotoTitle_isEqual() {
    assertEquals("TestPicture", photo.getTitle());
   }

  /**
   * Check if the photo was initialized with the correct url.
   */
  @Test
   void getUrl_CorrectUrl_isEqual() {
    assertEquals("file:src/Test/Assets/test_image.jpg", photo.getUrl());
   }

  @Test
   void getTime_ValidTime_NotNull() {
    assertNotNull(photo.getTime());
   }

   @Test
   void getCamera_ValidCamera_NotNull() {
    assertNotNull(photo.getCamera());
   }

   @Test
   void getExposureTime_ValidPhotoExposureTime_NotNull() {
    assertNotNull(photo.getExposureTime());
   }

  @Test
  void getAperture_ValidPhotoApertureTime_NotNull() {
    assertNotNull(photo.getAperture());
  }

  @Test
  void getHeight_PhotoHeightNotNull_isNotEqual() {
    assertNotEquals(0, photo.getHeight(), 0);
  }

  @Test
  void getWidth_PhotoWidthNotNull_isNotEqual() {
    assertNotEquals(0, photo.getWidth(), 0);
  }

  @Test
  void getFileSize_PhotoFileSizeNotNull_isNotEqual() {
    assertNotEquals(0, photo.getFileSize(), 0);
  }

  @Test
  void getFileType_ValidPhotoFileType_NotNull() {
    assertNotNull(photo.getFileType());
  }

  @Test
  void getID_CorrectPhotoUserID_IsEqual() {
    assertEquals(user.getId(), photo.getUserId());
  }

  /**
   * Tests that analyze-method throws IOException
   * when we pass null and null as arguments.
   */
  @Test
  void analyze_TitleAndUrlIsNull_IOExceptionThrown() {
    assertThrows(IOException.class, () -> {
      ImageAnalyzer.analyze(null, null);
    });
  }
}
