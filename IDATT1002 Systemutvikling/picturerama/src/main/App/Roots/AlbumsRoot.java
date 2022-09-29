package Roots;

import Components.AlbumContainer;
import Components.FileLogger;
import Components.PopUpWindow;
import Components.UserInfo;
import Css.Css;
import Database.Hibernate;
import Database.HibernateClasses.Album;
import Main.ApplicationManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Class for the albums root
 */
final class AlbumsRoot extends SceneRoot {

	private final ScrollPane SCROLL_PANE = new ScrollPane();
	private final VBox SCROLL_PANE_VBOX = new VBox();
	private final Button NEW_ALBUM_BUTTON = new Button("New album");
	private final Button DELETE_ALBUM_BUTTON = new Button("Delete selected albums");
	private final Text feedbackText = new Text();
	private final List<AlbumContainer> ALBUM_CONTAINER_LIST = new ArrayList<>();

	/**
	 * Constructor that initializes the albums root
	 * Calls the set layout method
	 */
	AlbumsRoot() {
		super();
		this.setLayout();
	}

	/**
	 * Overrides the setLayout in SceneRoot and adds the structure of the albums root
	 * Uses addAlbumsScrollPane
	 * Uses addScrollPane
	 * Uses addButtonsToBorderPane
	 */
	@Override
	void setLayout() {
		super.setLayout();
		super.setGridPane();
		super.setPageTitle("Albums");

		this.addScrollPane();
		this.addAlbumsScrollPane();
		this.addButtonsToBorderPane();
	}

	/**
	 * Adds all the albums of the user in to the scroll pane of the root
	 * Used in constructor
	 */
	private void addAlbumsScrollPane() {
		SCROLL_PANE_VBOX.getChildren().clear();
		ALBUM_CONTAINER_LIST.clear();
		try {
			if (UserInfo.getUser().getAlbums().isEmpty()) {
				showNoAlbum();
			} else {
				UserInfo.getUser().getAlbums().forEach(album -> {
					AlbumContainer albumContainer = new AlbumContainer(album);
					albumContainer.getAlbumButton().setOnAction(e -> ApplicationManager.setRoot(new AlbumDetailsRoot(album)));
					ALBUM_CONTAINER_LIST.add(albumContainer);
					SCROLL_PANE_VBOX.getChildren().add(albumContainer.getAlbumContainerHBox());
				});
			}
		} catch (NullPointerException e) {
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
	}

	/**
	 * Shows a message that tells the user that there are no albums created
	 */
	private void showNoAlbum() {
		Css.setTextFont(17, feedbackText);
		SCROLL_PANE_VBOX.getChildren().add(feedbackText);
		SCROLL_PANE_VBOX.setAlignment(Pos.CENTER);
		feedbackText.setText("No albums registered: Add an album by pressing the \"New album\" button");
		feedbackText.setTextAlignment(TextAlignment.RIGHT);
		DELETE_ALBUM_BUTTON.setDisable(true);
	}

	/**
	 * Creates the scroll pane of the scene and adds it to the application
	 * Used in constructor
	 */
	private void addScrollPane() {
		SCROLL_PANE_VBOX.setPadding(new Insets(10, 10, 10, 10));
		SCROLL_PANE_VBOX.setSpacing(10);
		SCROLL_PANE.setContent(SCROLL_PANE_VBOX);
		SCROLL_PANE.setPrefSize(700, 700);
		SCROLL_PANE.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight());
		SCROLL_PANE.fitToWidthProperty().set(true);
		SCROLL_PANE.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		Css.setScrollPane(SCROLL_PANE);
		super.getGridPane().add(SCROLL_PANE, 0, 0);
	}

	/**
	 * Adds the buttons to the root
	 * Used in constructor
	 */
	private void addButtonsToBorderPane() {
		HBox hBox = new HBox();
		hBox.getChildren().addAll(NEW_ALBUM_BUTTON, DELETE_ALBUM_BUTTON);
		hBox.setAlignment(Pos.BASELINE_CENTER);
		hBox.setSpacing(20);
		hBox.setPadding(new Insets(10, 10, 10, 10));
		BorderPane.setAlignment(hBox, Pos.CENTER);
		super.getBorderPane().setBottom(hBox);

		NEW_ALBUM_BUTTON.setOnAction(e -> createNewAlbumButtonPressed());
		DELETE_ALBUM_BUTTON.setOnAction(e -> deleteSelectedAlbums());
		Css.setButton(340, 50, 18, NEW_ALBUM_BUTTON, DELETE_ALBUM_BUTTON);
	}

	/**
	 * Opens up a new stage where you can create a new album
	 * Used in addButtonsToBorderPan
	 */
	private void createNewAlbumButtonPressed() {
		PopUpWindow popupWindow = new PopUpWindow(ApplicationManager.getStage(), 500, 100);

		popupWindow.getDialogWindow().setTitle("Add album");
		popupWindow.getDialogText().setText("Please enter the name of the album to be added:");

		TextField nameAlbumInput = new TextField();
		nameAlbumInput.setPromptText("Album name");
		Css.setTextField(700, 20, 17, nameAlbumInput);

		Button addAlbum = new Button("Add album");
		Css.setButton(500, 20, 17, addAlbum);
		popupWindow.getDialogHBox().getChildren().addAll(nameAlbumInput, addAlbum);

		addAlbum.setOnAction(e -> addAlbum(nameAlbumInput, popupWindow));
	}

	/**
	 * Creates a new album
	 *
	 * @param nameAlbumInput textfield with name of new album
	 */
	private void addAlbum(TextField nameAlbumInput, PopUpWindow popupWindow) {
		if (nameAlbumInput.getText().trim().equals("") || nameAlbumInput.getText() == null) {
			popupWindow.getDialogText().setText("Please enter a valid name");
			Button tryAgain = new Button("Try again");
			Css.setButton(500, 20, 17, tryAgain);
			popupWindow.getDialogVBox().getChildren().clear();
			popupWindow.getDialogVBox().getChildren().addAll(popupWindow.getDialogText(), tryAgain);
			tryAgain.setOnAction(event -> {
				popupWindow.getDialogVBox().getChildren().clear();
				popupWindow.getDialogText().setText("Please enter the name of the album to be added:");
				popupWindow.getDialogVBox().getChildren().addAll(popupWindow.getDialogText(), popupWindow.getDialogHBox());
				DELETE_ALBUM_BUTTON.setDisable(false);
			});
		} else {
			createAlbum(nameAlbumInput.getText().trim());
			nameAlbumInput.clear();
			popupWindow.getDialogWindow().close();
		}
	}

	/**
	 * Uploads the new album that is created to the database
	 * Used in createNewAlbumButtonPressed
	 */
	private void createAlbum(String albumName) {
		SCROLL_PANE_VBOX.getChildren().remove(feedbackText);
		Album album = new Album();
		album.setUserId(UserInfo.getUser().getId());
		album.setName(albumName);
		UserInfo.getUser().getAlbums().add(album);
		Hibernate.updateUser(UserInfo.getUser());
		AlbumContainer albumContainer = new AlbumContainer(album);
		albumContainer.getAlbumButton().setOnAction(e -> ApplicationManager.setRoot(new AlbumDetailsRoot(album)));
		addAlbumsScrollPane();
		DELETE_ALBUM_BUTTON.setDisable(false);
	}

	/**
	 * Method to delete an album and the album button gets removed from the layout.
	 */
	private void deleteSelectedAlbums() {
		ArrayList<Album> selectedAlbums = getCheckedAlbums();
		for (Album album : selectedAlbums) {
			Optional<AlbumContainer> optionalAlbumContainer = ALBUM_CONTAINER_LIST.stream().filter(c -> c.getALBUM().equals(album)).findAny();
			if (optionalAlbumContainer.isPresent()) {
				UserInfo.getUser().getAlbums().remove(album);
				AlbumContainer albumContainer = optionalAlbumContainer.get();
				albumContainer.getCheckBox().setSelected(false);
				SCROLL_PANE_VBOX.getChildren().remove(albumContainer.getAlbumContainerHBox());
			} else {
				FileLogger.getLogger().log(Level.FINE, "Album: {0} is not present in the list containers", album);
				FileLogger.closeHandler();
			}
			Hibernate.updateUser(UserInfo.getUser());
		}
		if (UserInfo.getUser().getAlbums().isEmpty()) {
			showNoAlbum();
		}
	}

	/**
	 * Helper method to get the checked album in the album root
	 * Used in deleteSelectedAlbums
	 *
	 * @return a list of checked albums
	 */
	private ArrayList<Album> getCheckedAlbums() {
		ArrayList<Album> checkedPhotos = new ArrayList<>();
		for (AlbumContainer albumContainer : ALBUM_CONTAINER_LIST) {
			if (albumContainer.getCheckBox().isSelected()) {
				checkedPhotos.add(albumContainer.getALBUM());
			}
		}
		return checkedPhotos;
	}
}
