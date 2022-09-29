package Components;

import Css.Css;
import Css.FeedbackType;
import Database.Hibernate;
import Database.HibernateClasses.Photo;
import Database.HibernateClasses.Tags;
import Main.ApplicationManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class that is used to display photo metadata
 */
public final class PhotoViewer {

	private final FlowPane TAG_CONTAINER = new FlowPane();
	private final Photo PHOTO;
	private final Stage STAGE;

	public PhotoViewer(Photo photo) {
		this.PHOTO = photo;
		this.STAGE = setup();
	}

	/**
	 * Setup creates a new stage and then sets it up
	 *
	 * @return stage, a new stage
	 */
	private Stage setup() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.initOwner(ApplicationManager.getStage());
		stage.setTitle(PHOTO.getTitle());
		stage.getIcons().add(new Image("file:src/main/App/Images/Logo.png"));

		Label photoTitleLabel = new Label();
		photoTitleLabel.setText(PHOTO.getTitle());
		Css.setLabel(30, photoTitleLabel);
		Label metadataLabel = new Label();
		metadataLabel.setText(PHOTO.toString());
		Css.setLabel(12, metadataLabel);
		Label tagLabel = new Label("Tags:");
		tagLabel.setPadding(new Insets(10, 0, 0, 0));
		Label addTagLabel = new Label("Add a tag to picture:");
		Css.setLabel(15, tagLabel, addTagLabel);
		Label feedbackLabel = new Label();
		Css.setFeedBackLabel(FeedbackType.ERROR, 13, feedbackLabel);

		TextField tagField = new TextField();
		tagField.setPromptText("Tag name");
		Css.setTextField(150, 20, 14, tagField);

		Button addTagButton = new Button("Add tag");
		Button closeButton = new Button("Close");
		closeButton.setCancelButton(true);
		Css.setButton(582, 25, 20, addTagButton, closeButton);

		AnchorPane layout = new AnchorPane();
		VBox bottomContainer = new VBox();
		bottomContainer.setSpacing(10);
		bottomContainer.setPadding(new Insets(10, 10, 10, 10));
		bottomContainer.setSpacing(6);
		bottomContainer.getChildren().addAll(tagLabel, TAG_CONTAINER, addTagLabel, tagField, feedbackLabel, addTagButton, closeButton);
		AnchorPane.setBottomAnchor(bottomContainer, 5.0);
		layout.getChildren().add(bottomContainer);

		VBox imageInfoContainer = new VBox();
		imageInfoContainer.getChildren().addAll(photoTitleLabel, metadataLabel);
		imageInfoContainer.maxWidth(275);

		TAG_CONTAINER.setMaxWidth(580);
		TAG_CONTAINER.setPrefWrapLength(580);
		TAG_CONTAINER.setPadding(new Insets(0, 10, 10, 0));
		TAG_CONTAINER.setHgap(4);
		TAG_CONTAINER.setVgap(4);

		PHOTO.getTags().forEach(t -> {
			//Creates a new tag container object and adds it to the tagContainer flowpane
			TagContainer tagContainerObject = new TagContainer(t.getTag());
			TAG_CONTAINER.getChildren().add(tagContainerObject.getContainer());
			setButtonFunctionality(tagContainerObject);
		});

		//Sets the functionality of the add tag button
		addTagButton.setOnAction(e -> {
			Tags tag = new Tags(tagField.getText(), PHOTO.getId());
			//Checks if there is an input in the textfield
			if (tagField.getText() == null || tagField.getText().trim().equals("")) {
				feedbackLabel.setText("Error: The tag must have a name");
			}
			//Checks if the photo already has the tag
			else if (PHOTO.getTags().contains(tag)) {
				feedbackLabel.setText("Error: This tag is already registered");
			} else {
				tag.setPhotoId(PHOTO.getId());
				PHOTO.getTags().add(tag);

				//Creates new tag container and gives it functionality
				TagContainer tagContainerObject = new TagContainer(tagField.getText());
				TAG_CONTAINER.getChildren().add(tagContainerObject.getContainer());
				this.setButtonFunctionality(tagContainerObject);

				feedbackLabel.setText("");
				tagField.clear();
			}
		});

		//All closing mechanisms
		stage.setOnCloseRequest(e -> {
			e.consume();
			updateDatabaseAndClose();
		});
		//Closes stage if it is not the focus, and updates the database
		stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (!isNowFocused) {
				updateDatabaseAndClose();
			}
		});
		closeButton.setOnAction(e -> updateDatabaseAndClose());

		ImageView imageView = new ImageView(new Image(PHOTO.getUrl(), 255, 255, true, true, true));

		//Adding child nodes to parent nodes
		AnchorPane.setLeftAnchor(imageInfoContainer, 10.0);
		AnchorPane.setRightAnchor(imageView, 10.0);
		layout.getChildren().addAll(imageInfoContainer, imageView);
		layout.setPadding(new Insets(30, 10, 10, 10));
		layout.setStyle("-fx-padding: 10 0 0 0;");

		Scene scene = new Scene(layout, 600, 560);

		stage.setMinHeight(560);
		stage.setMinWidth(600);
		stage.setScene(scene);
		return stage;
	}

	/**
	 * Sets the up the buttons of a tag container
	 *
	 * @param tagContainerObject is the tag container that is being setup
	 */
	private void setButtonFunctionality(TagContainer tagContainerObject) {
		//Programs the delete button each tag to remove the tag
		tagContainerObject.getDeleteTagButton().setOnAction(e -> {
			PHOTO.getTags().removeIf(t -> t.getTag().equals(tagContainerObject.getTagAsString()));
			TAG_CONTAINER.getChildren().removeIf(t -> t.equals(tagContainerObject.getContainer()));
		});
	}

	/**
	 * Updates the the tags of the photo in the database
	 */
	private void updateDatabaseAndClose() {
		int index = UserInfo.getUser().getPhotos().indexOf(PHOTO);
		UserInfo.getUser().getPhotos().get(index).setTags(PHOTO.getTags());
		Hibernate.updateUser(UserInfo.getUser());
		this.STAGE.close();
	}

	/**
	 * Displays the the stage created in the object
	 */
	public void display() {
		this.STAGE.showAndWait();
	}
}