import Components.UserInfo;
import Database.HibernateClasses.Album;
import Database.HibernateClasses.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for album.
 */
class AlbumTest {

    private static User user;
    private static Album album;

    @BeforeAll
    static void setup(){
        user = new User();
        user.setUsername("testAlbumUser");
        user.setId(10);
        UserInfo.initializeUser(user);

        album = new Album();
        album.setName("Album 1");
        album.setUserId(user.getId());
        album.setId(100);
        user.getAlbums().add(album);
    }

    /**
     * Checks whether the album has the correct user id.
     */
    @Test
    void getID_AlbumCorrectID_isEqual() {
        assertEquals(album.getUserId(), user.getId());
    }

    @Test
    void getAlbums_AddNewAlbum_True() {
        assertTrue(user.getAlbums().add(new Album("Album 2", user.getId())));
    }
}
