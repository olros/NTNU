import Components.ImageAnalyzer;
import Components.UserInfo;
import Database.HibernateClasses.Photo;
import Database.HibernateClasses.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to upload and delete a photo.
 */
class PhotoTest {

    private static Photo photo;
    private static User user;

    @BeforeAll
    static void setup() {
        try {
            user = new User();
            user.setUsername("testPhotoUser");
            user.setId(10);
            UserInfo.initializeUser(user);

            photo = ImageAnalyzer.analyze("TestPicture", "file:src/Test/Assets/test_image.jpg");
            photo.setId(232);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to upload a photo.
     */
    @Test
    void add_NewPhoto_True() {
        assertTrue(user.getPhotos().add(photo));
    }

    /**
     * An IndexOutOfBoundsException is expected to be thrown when adding a photo to a non existing album.
     */
    @Test
    void add_PhotoToNonExistingAlbum_IndexOutOfBoundsExceptionThrown() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            user.getAlbums().get(0).getPhotos().add(photo);
        });
    }

    /**
     * Test to check whether the photo was uploaded to the correct user.
     */
    @Test
    void getUserId_CorrectPhotoUserID_isEqual() {
        assertEquals(photo.getUserId(), user.getId());
    }
}
