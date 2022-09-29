package Components;

import Css.Css;
import Database.HibernateClasses.Album;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

/**
 * Class that is used to display albums in the album root
 */
public class AlbumContainer {

	private final Album ALBUM;
	private CheckBox checkBox;
	private Button albumButton;
	private HBox albumContainerHBox;

	/**
	 * Constructor that takes a album object and initializes the album container
	 *
	 * @param album a album object
	 */
	public AlbumContainer(Album album) {
		this.ALBUM = album;
		setupAlbumContainer(album);
	}

	public Album getALBUM() {
		return ALBUM;
	}

	public Button getAlbumButton() {
		return albumButton;
	}

	public HBox getAlbumContainerHBox() {
		return albumContainerHBox;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public Button getPhotoButton() {
		return albumButton;
	}

	/**
	 * Makes a album container that is a button that contains an album title and a checkbox
	 *
	 * @param album a album object
	 */
	private void setupAlbumContainer(Album album) {
		albumButton = new Button(album.getName());
		checkBox = new CheckBox();
		checkBox.getStyleClass().add("check-box");
		albumContainerHBox = new HBox(albumButton, checkBox);
		albumContainerHBox.getStylesheets().add("file:src/main/App/Css/CheckBoxStyle.css");
		albumContainerHBox.setSpacing(10);
		albumContainerHBox.setAlignment(Pos.CENTER_LEFT);
		Css.setContainer(albumButton, albumContainerHBox, 80, 160);
		albumButton.setStyle("-fx-font-size: 28");
	}
}
