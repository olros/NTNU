package Roots;

import Components.FileLogger;
import Components.ImageAnalyzer;
import Components.UserInfo;
import Css.Css;
import Css.FeedbackType;
import Database.Hibernate;
import Database.HibernateClasses.Photo;
import Main.ApplicationManager;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Class for the upload root
 */
final class UploadRoot extends SceneRoot {
	private final Label TITLE_LABEL = new Label("Title: ");
	private final TextField TITLE_FIELD = new TextField();
	private final Label URL_LABEL = new Label("URL: ");
	private final TextField URL_FIELD = new TextField();
	private final Button UPLOAD_BUTTON = new Button("Upload image");
	private final Label FEEDBACK_LABEL = new Label();
	private final ProgressIndicator LOADING_ANIMATION = new ProgressIndicator();
	private final Button FILE_EXPLORER = new Button("Select local image");
	private final Button VIEW_PHOTOS_BUTTON = new Button("View photos");
	private String selectedDirectory;

	/**
	 * Constructor that sets up the layout of the upload root
	 */
	UploadRoot() {
		super();
		this.setLayout();
	}

	/**
	 * Method that gets Cloudinary properties
	 * Used in uploadComplete
	 *
	 * @return returns a map with the properties
	 */
	private static Map getProperties() {
		Map result = new HashMap();
		try (InputStream input = new FileInputStream("config.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			result.put("cloud_name", prop.getProperty("cloudinary_cloud_name"));
			result.put("api_key", prop.getProperty("cloudinary_api_key"));
			result.put("api_secret", prop.getProperty("cloudinary_api_secret"));
		} catch (IOException ex) {
			FileLogger.getLogger().log(Level.FINE, ex.getMessage());
			FileLogger.closeHandler();
		}
		return result;
	}

	/**
	 * Overrides SceneRoot method.
	 * Assigns layout components to RootBuilders GridPane
	 * Sets styling to layout components
	 * Sets functionality to button nodes
	 * Used in constructor
	 */
	@Override
	void setLayout() {
		super.setLayout();
		super.setPageTitle("Upload");
		//Sets PromptText for TextFields
		TITLE_FIELD.setPromptText("Title here...");
		URL_FIELD.setPromptText("URL here...");
		URL_FIELD.setTooltip(new Tooltip("https://example.com/image.jpg"));
		super.getGridPane().add(TITLE_LABEL, 5, 0);
		super.getGridPane().add(TITLE_FIELD, 5, 1);
		super.getGridPane().add(URL_LABEL, 5, 2);
		super.getGridPane().add(URL_FIELD, 5, 3);
		super.getGridPane().add(FILE_EXPLORER, 5, 4);
		super.getGridPane().add(UPLOAD_BUTTON, 5, 5);
		super.getGridPane().add(VIEW_PHOTOS_BUTTON, 5, 6);
		super.getGridPane().add(LOADING_ANIMATION, 6, 5);
		super.getGridPane().add(FEEDBACK_LABEL, 5, 7);
		super.getGridPane().setAlignment(Pos.TOP_CENTER);

		//Sets styling on layout components
		Css.setButton(700, 25, 20, UPLOAD_BUTTON, FILE_EXPLORER);
		Css.setLabel(13, TITLE_LABEL, URL_LABEL);
		Css.setTextField(700, 20, 17, TITLE_FIELD, URL_FIELD);
		Css.setLoadingAnimation(LOADING_ANIMATION);
		Css.setButton(700, 25, 20, VIEW_PHOTOS_BUTTON);

		VIEW_PHOTOS_BUTTON.setOnAction(s -> ApplicationManager.setRoot(new PhotosRoot()));
		UPLOAD_BUTTON.setOnAction(e -> uploadComplete());
		FILE_EXPLORER.setOnAction(e -> {
			try {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Upload local image");
				File defaultDirectory = new File(System.getProperty("user.home"));
				chooser.setInitialDirectory(defaultDirectory);
				selectedDirectory = chooser.showOpenDialog(ApplicationManager.getStage()).getAbsolutePath();
				URL_FIELD.setText(selectedDirectory);
			} catch (Exception exp) {
				URL_FIELD.clear();
			}
		});
	}

	/**
	 * Checks if title or url are missing
	 * Used in uploadComplete
	 *
	 * @return boolean value, true if trimmed TextFields are equal to 0
	 */
	private boolean checkField() {
		if (TITLE_FIELD.getText().trim().length() == 0 || URL_FIELD.getText().trim().length() == 0) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Title or URL are missing", 13, FEEDBACK_LABEL);
			return false;
		}
		return true;
	}

	/**
	 * Upload the image path to the database
	 * Sets feedbackLabel to error message if something went wrong
	 */
	private void uploadComplete() {
		LOADING_ANIMATION.setVisible(true);
		PauseTransition pause = new PauseTransition();
		pause.setOnFinished(e -> {
			if (checkField()) {
				try {
					String photo_url;
					if (!URL_FIELD.getText().contains("https")) {
						Cloudinary cloudinary = new Cloudinary(getProperties());
						File file = new File(URL_FIELD.getText());
						Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
						photo_url = uploadResult.get("url").toString();
					} else {
						photo_url = URL_FIELD.getText();
					}
					Photo photo = ImageAnalyzer.analyze(TITLE_FIELD.getText(), photo_url);
					UserInfo.getUser().getPhotos().add(photo);
					Hibernate.updateUser(UserInfo.getUser());
					TITLE_FIELD.clear();
					URL_FIELD.clear();
					Css.playFeedBackLabelTransition(FeedbackType.SUCCESSFUL, photo.getTitle() + " was stored", 13, FEEDBACK_LABEL);
				} catch (IOException ex) {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Something went wrong when retrieving the image from the url.", 13, FEEDBACK_LABEL);
					FileLogger.getLogger().log(Level.FINE, ex.getMessage());
					FileLogger.closeHandler();
				} catch (NullPointerException ex) {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Something went wrong when analyzing the image.", 13, FEEDBACK_LABEL);
					FileLogger.getLogger().log(Level.FINE, ex.getMessage());
					FileLogger.closeHandler();
				} finally {
					LOADING_ANIMATION.setVisible(false);
				}
			} else {
				LOADING_ANIMATION.setVisible(false);
			}
		});
		pause.play();
	}
}
