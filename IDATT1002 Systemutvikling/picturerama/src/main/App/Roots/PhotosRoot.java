package Roots;

import Components.FileLogger;
import Components.PhotoContainer;
import Components.PopUpWindow;
import Components.UserInfo;
import Css.Css;
import Css.FeedbackType;
import Database.Hibernate;
import Database.HibernateClasses.Album;
import Database.HibernateClasses.Photo;
import Database.HibernateClasses.Tags;
import Main.ApplicationManager;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class for the Photos root
 */
final class PhotosRoot extends SceneRoot {

	private final List<Photo> PHOTO_LIST = new ArrayList<>();
	private final ScrollPane SCROLL_PANE = new ScrollPane();
	private final VBox SCROLL_PANE_VBOX = new VBox();
	private final List<CheckBox> CHECKBOX_ARRAY_LIST = new ArrayList<>();
	private final List<PhotoContainer> PHOTO_CONTAINER_LIST = new ArrayList<>();
	private final TextField SEARCH_TEXT_FIELD = new TextField();
	private final HBox SELECT_ALL_HBOX = new HBox();
	private final CheckBox SELECT_ALL_CHECKBOX = new CheckBox();
	private final Button ADD_TO_ALBUM_BUTTON = new Button("Add to album");
	private final ChoiceBox<String> CHOICE_BOX = new ChoiceBox<>();
	private final Button DELETE_BUTTON = new Button("Delete selected photos");
	private final Label FEEDBACK_LABEL = new Label();
	private final Button ADD_PHOTO_BUTTON = new Button("Add photo");

	/**
	 * Sets up the photos root and adds all the users photos to the photo list
	 */
	PhotosRoot() {
		super();
		PHOTO_LIST.addAll(UserInfo.getUser().getPhotos());
		this.setLayout();
	}

	/**
	 * Sets up the layout of the photos root overrides the setLayout method of SceneRoot
	 * Used in constructor
	 */
	@Override
	void setLayout() {
		super.setLayout();
		super.setPageTitle("Photos");

		setupImagesInAScrollPane();
		setupSearchBar();
		setupAlbumButtons();
		setupSelectAllHBox();
		setupDeleteButton();
		ADD_PHOTO_BUTTON.setOnAction(s -> ApplicationManager.setRoot(new UploadRoot()));

		super.getGridPane().add(SCROLL_PANE, 0, 1, 4, 1);
		super.getGridPane().add(SEARCH_TEXT_FIELD, 0, 0, 2, 1);
		super.getGridPane().add(FEEDBACK_LABEL, 2, 0, 1, 1);
		super.getGridPane().add(SELECT_ALL_HBOX, 2, 0, 1, 1);
		super.getGridPane().add(ADD_TO_ALBUM_BUTTON, 0, 2, 1, 1);
		super.getGridPane().add(DELETE_BUTTON, 2, 2, 1, 1);
		super.getGridPane().add(ADD_PHOTO_BUTTON, 0, 3, 3, 1);
		super.getGridPane().setMaxWidth(700.0D);
		super.getGridPane().getStylesheets().add("file:src/main/App/Css/SelectAllCheckBoxStyle.css");
		super.getGridPane().getStylesheets().add("file:src/main/App/Css/SearchField.css");

		GridPane.setHalignment(FEEDBACK_LABEL, HPos.LEFT);
		GridPane.setHalignment(SELECT_ALL_HBOX, HPos.RIGHT);

		Css.setTextField(700, 20, 17, SEARCH_TEXT_FIELD);
		Css.setScrollPane(SCROLL_PANE);
		Css.setButton(700, 25, 20, ADD_PHOTO_BUTTON);
	}

	/**
	 * Sets up the scroll pane in the photos root with all the user's photos
	 * Used in setLayout
	 */
	private void setupImagesInAScrollPane() {
		if (!PHOTO_LIST.isEmpty()) {
			PHOTO_LIST.forEach(photo -> {
				PhotoContainer photoContainer = new PhotoContainer(photo);
				SCROLL_PANE_VBOX.getChildren().add(photoContainer.getPhotoContainerHBox());
				PHOTO_CONTAINER_LIST.add(photoContainer);
				CHECKBOX_ARRAY_LIST.add(photoContainer.getCheckBox());
			});
		} else {
			showNoPhotos();
		}
		SCROLL_PANE.setContent(SCROLL_PANE_VBOX);
		SCROLL_PANE.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());
		SCROLL_PANE.fitToWidthProperty().set(true);
		SCROLL_PANE.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
	}

	/**
	 * Shows a message that tells the user that it has no photos
	 */
	private void showNoPhotos() {
		Text noPhotosText = new Text("No photos uploaded: To upload press the \"Add photo\" button");
		Css.setTextFont(17, noPhotosText);
		SCROLL_PANE_VBOX.getChildren().add(noPhotosText);
		SCROLL_PANE_VBOX.setAlignment(Pos.CENTER);
		SELECT_ALL_HBOX.setDisable(true);
		DELETE_BUTTON.setDisable(true);
		ADD_TO_ALBUM_BUTTON.setDisable(true);
		SEARCH_TEXT_FIELD.setDisable(true);
	}

	/**
	 * Sets up the search bar and its functionality
	 * Used in setLayout
	 */
	private void setupSearchBar() {
		SEARCH_TEXT_FIELD.setId("searchField");
		SEARCH_TEXT_FIELD.setTooltip(new Tooltip("To search by multiple tags, use comma as separation"));
		SEARCH_TEXT_FIELD.setPromptText("Search for image...");
		SEARCH_TEXT_FIELD.setOnKeyTyped(action -> filter());
		SELECT_ALL_CHECKBOX.setOnAction(action -> CHECKBOX_ARRAY_LIST.forEach(checkBox -> checkBox.setSelected(SELECT_ALL_CHECKBOX.isSelected())));
	}

	/**
	 * Sets up the select-all button
	 * Used in setLayout
	 */
	private void setupSelectAllHBox() {
		SELECT_ALL_HBOX.getChildren().addAll(new Label("Select all:"), SELECT_ALL_CHECKBOX);
		SELECT_ALL_HBOX.setAlignment(Pos.CENTER_RIGHT);
		SELECT_ALL_CHECKBOX.getStyleClass().add("check-box");
	}

	/**
	 * Sets up button for deleting photos
	 * Used in setLayout
	 */
	private void setupDeleteButton() {
		Css.setButton(700, 25, 20, DELETE_BUTTON);
		DELETE_BUTTON.setOnAction(action -> deleteSelectedPhotos());
	}

	/**
	 * Sets up the add to album button
	 * Used in setLayout
	 */
	private void setupAlbumButtons() {
		Css.setButton(700, 25, 20, ADD_TO_ALBUM_BUTTON);
		ADD_TO_ALBUM_BUTTON.setOnAction(s -> addToAlbumPressed());
		if (UserInfo.getUser().getAlbums().isEmpty()) {
			ADD_TO_ALBUM_BUTTON.setDisable(true);
		}
	}

	/**
	 * Sets up the checkboxes and adds styling to it
	 * Used createNewAlbumButtonPressed
	 */
	private void setupChoiceBox() {
		CHOICE_BOX.getItems().clear();
		CHOICE_BOX.getStyleClass().add("choice-box");
		CHOICE_BOX.getStylesheets().add("file:src/main/App/Css/ChoiceBoxStyle.css");
		UserInfo.getUser().getAlbums().forEach(s -> CHOICE_BOX.getItems().add(s.getName()));
	}

	/**
	 * Method for the search functionality.
	 * Filters the scroll panes photos, showing a photo if its title contains the search text or one if its tags are equal to the search text.
	 * Used in setupSearchBar
	 */
	private void filter() {
		SCROLL_PANE_VBOX.getChildren().clear();
		// Checks if search input is empty, if so show all photos
		if (SEARCH_TEXT_FIELD.getText().trim().equals("")) {
			PHOTO_CONTAINER_LIST.forEach(child -> SCROLL_PANE_VBOX.getChildren().add(child.getPhotoContainerHBox()));
		} else {
			PHOTO_CONTAINER_LIST.forEach(container -> {
				// Checks if title of photo contains search input
				if (container.getPhoto().getTitle().toLowerCase().contains(SEARCH_TEXT_FIELD.getText().trim().toLowerCase())) {
					SCROLL_PANE_VBOX.getChildren().add(container.getPhotoContainerHBox());
				} else {
					String textInput = SEARCH_TEXT_FIELD.getText().trim().toLowerCase().replaceAll(" ", "");
					String[] multipleTags = textInput.split(",");
					// Checks if the photo's tags contains the given tags in search input spilt by comma
					if (getPhotoTags(container.getPhoto()).containsAll(Arrays.asList(multipleTags))) {
						SCROLL_PANE_VBOX.getChildren().add(container.getPhotoContainerHBox());
					}
				}
			});
		}
	}

	/**
	 * Helping method to retrieve all the tags to a specific photo.
	 * Used in filter
	 *
	 * @param photo gets the tags of the photo.
	 * @return all the tags to the specific photo.
	 */
	private List<String> getPhotoTags(Photo photo) {
		return photo.getTags().stream()
				.map(Tags::getTag)
				.map(String::toLowerCase)
				.collect(Collectors.toList());
	}

	/**
	 * Method that creates the popup that can create albums and creates the action popup
	 * Used in setupAlbumButtons
	 */
	private void addToAlbumPressed() {
		PopUpWindow popupWindow = new PopUpWindow(ApplicationManager.getStage(), 500, 100);
		popupWindow.getDialogWindow().setTitle("Add to album");
		popupWindow.getDialogText().setText("Please select the name of the album:");

		setupChoiceBox();

		Button addAlbum = new Button("Add to album");
		Css.setButton(500, 20, 17, addAlbum);
		addAlbum.setOnAction(e -> {
			updateUser(CHOICE_BOX.getValue());
			popupWindow.getDialogWindow().close();
		});

		popupWindow.getDialogHBox().getChildren().addAll(CHOICE_BOX, addAlbum);
	}

	/**
	 * Helper method to get the checked photos in the photos root
	 * Used in updateUser
	 * Used in deleteSelectedPhotos
	 *
	 * @return a list of checked photos
	 */
	private ArrayList<Photo> getCheckedPhotos() {
		ArrayList<Photo> checkedPhotos = new ArrayList<>();
		for (int i = 0; i < CHECKBOX_ARRAY_LIST.size(); i++) {
			if (CHECKBOX_ARRAY_LIST.get(i).isSelected()) {
				checkedPhotos.add(PHOTO_LIST.get(i));
			}
		}
		return checkedPhotos;
	}

	/**
	 * Method that updates the photos in the selected album
	 * Used in createNewAlbumButtonPressed
	 *
	 * @param albumName name of the selected album
	 */
	private void updateUser(String albumName) {
		Album album = UserInfo.getUser().getAlbums().stream().filter(a -> a.getName().equals(albumName)).findAny().orElse(null);
		ArrayList<Photo> checkedPhoto = getCheckedPhotos();
		if (checkedPhoto.isEmpty()) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Unsuccessful: No photos were chosen", 13, FEEDBACK_LABEL);
		} else if (album == null) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Unsuccessful: No album were chosen", 13, FEEDBACK_LABEL);
		} else {
			checkedPhoto.forEach(s -> s.addAlbum(album));
			Css.playFeedBackLabelTransition(FeedbackType.SUCCESSFUL, "Added to " + albumName, 13, FEEDBACK_LABEL);
		}
		Hibernate.updateUser(UserInfo.getUser());
	}

	/**
	 * Private method for deleting selected photos.
	 * Used in setupDeleteButton
	 */
	private void deleteSelectedPhotos() {
		ArrayList<Photo> selectedPhotos = getCheckedPhotos();
		if (selectedPhotos.isEmpty()) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Unsuccessful: No photos were chosen", 13, FEEDBACK_LABEL);
		} else {
			boolean successfulDeleteSelectedPhotos = true;
			for (Photo photo : selectedPhotos) {
				Optional<PhotoContainer> optionalPhotoContainer = PHOTO_CONTAINER_LIST.stream().filter(c -> c.getPhoto().equals(photo)).findAny();
				if (optionalPhotoContainer.isPresent()) {
					photo.getAlbums().forEach(album -> album.getPhotos().remove(photo));
					UserInfo.getUser().getPhotos().remove(photo);
					PhotoContainer photoContainer = optionalPhotoContainer.get();
					photoContainer.getCheckBox().setSelected(false);
					SCROLL_PANE_VBOX.getChildren().remove(photoContainer.getPhotoContainerHBox());
				} else {
					//If one of the pictures were not successfully deleted, then the operation was not successful
					successfulDeleteSelectedPhotos = false;
					FileLogger.getLogger().log(Level.FINE, "Photo: {0} is not present in the list containers", photo);
					FileLogger.closeHandler();
				}
			}
			Hibernate.updateUser(UserInfo.getUser());
			if (successfulDeleteSelectedPhotos) {
				Css.playFeedBackLabelTransition(FeedbackType.SUCCESSFUL, "Deleted successfully", 13, FEEDBACK_LABEL);
			} else {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "One or more photos could not be deleted", 13, FEEDBACK_LABEL);
			}
		}
		if (UserInfo.getUser().getPhotos().isEmpty()) {
			showNoPhotos();
		}
	}
}
