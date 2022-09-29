package Components;

import Css.Css;
import Database.HibernateClasses.Photo;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Class that is used to display photos in the search root
 */
public final class PhotoContainer {

	private final Photo PHOTO;
	private Image image;
	private ImageView imageView;
	private CheckBox checkBox;
	private Button photoButton;
	private HBox photoContainerHBox;

	/**
	 * Constructor that takes a photo object and initializes the photo container with the photo in it
	 *
	 * @param photo a photo object
	 */
	public PhotoContainer(Photo photo) {
		this.PHOTO = photo;
		setupPhotoContainer(photo);
	}

	public Photo getPhoto() {
		return PHOTO;
	}

	public Image getImage() {
		return image;
	}

	public ImageView getImageView() {
		return imageView;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public Button getPhotoButton() {
		return photoButton;
	}

	public HBox getPhotoContainerHBox() {
		return photoContainerHBox;
	}

	/**
	 * Makes a photo container that is a button that contains a photo, a photo title and a checkbox
	 *
	 * @param photo a photo object
	 */
	private void setupPhotoContainer(Photo photo) {
		image = new Image(photo.getUrl(), 150, 150, true, true, true);
		imageView = new ImageView(image);

		photoButton = new Button(photo.getTitle(), imageView);
		photoButton.setOnAction(action -> {
			PhotoViewer photoViewer = new PhotoViewer(photo);
			photoViewer.display();
		});
		checkBox = new CheckBox();
		checkBox.getStyleClass().add("check-box");
		photoContainerHBox = new HBox(photoButton, checkBox);
		photoContainerHBox.getStylesheets().add("file:src/main/App/Css/CheckBoxStyle.css");
		photoContainerHBox.setSpacing(10);
		photoContainerHBox.setAlignment(Pos.CENTER_LEFT);
		Css.setContainer(photoButton, photoContainerHBox, 160, 160);
	}
}
