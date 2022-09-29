package Roots;

import Components.Authentication;
import Components.FileLogger;
import Css.Css;
import Css.FeedbackType;
import Main.ApplicationManager;
import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.util.Duration;

import javax.persistence.PersistenceException;
import java.util.logging.Level;

/**
 * Class for the login root
 */
public final class LoginRoot extends SceneRoot {

	private final Label USERNAME_LABEL = new Label("Username:");
	private final TextField USERNAME_FIELD = new TextField();
	private final Label PASSWORD_LABEL = new Label("Password:");
	private final PasswordField PASSWORD_FIELD = new PasswordField();
	private final Label LOG_IN_LABEL = new Label();
	private final Button LOG_IN_BUTTON = new Button("Log in");
	private final Button SIGN_UP_BUTTON = new Button("Sign up");
	private final ProgressIndicator LOADING_ANIMATION = new ProgressIndicator();

	/**
	 * LoginRoot constructor
	 * Uses SceneRoot constructor to create an object of the LoginRoot class
	 */
	public LoginRoot() {
		super();
		this.setLayout();
	}

	Label getLOG_IN_LABEL() {
		// Used in DeleteUserRoot to show successful deletion when redirected to the LoginRoot
		return LOG_IN_LABEL;
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
		super.setPageTitle("Log in");
		super.getGridPane().add(USERNAME_LABEL, 5, 0);
		super.getGridPane().add(USERNAME_FIELD, 5, 1);
		super.getGridPane().add(PASSWORD_LABEL, 5, 2);
		super.getGridPane().add(PASSWORD_FIELD, 5, 3);
		super.getGridPane().add(LOG_IN_BUTTON, 5, 4);
		super.getGridPane().add(LOADING_ANIMATION, 6, 4);
		super.getGridPane().add(SIGN_UP_BUTTON, 5, 5);
		super.getGridPane().add(LOG_IN_LABEL, 5, 6);

		USERNAME_FIELD.setPromptText("Username");
		PASSWORD_FIELD.setPromptText("Password");

		//Sets styling for the layout components
		Css.setButton(700, 25, 20, LOG_IN_BUTTON, SIGN_UP_BUTTON);
		Css.setTextField(700, 20, 17, USERNAME_FIELD, PASSWORD_FIELD);
		Css.setLabel(13, USERNAME_LABEL, PASSWORD_LABEL);
		Css.setLoadingAnimation(LOADING_ANIMATION);

		LOG_IN_BUTTON.setDefaultButton(true);
		LOG_IN_BUTTON.setOnAction(e -> login());
		SIGN_UP_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new SignUpRoot()));
	}

	/**
	 * Method that is ran when logging in to the application
	 * Used in setLayout
	 */
	private void login() {
		LOADING_ANIMATION.setVisible(true);
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(p -> {
			try {
				if (USERNAME_FIELD.getText().trim().length() == 0 || PASSWORD_FIELD.getText().trim().length() == 0) {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Username or password is missing", 13, LOG_IN_LABEL);
					LOADING_ANIMATION.setVisible(false);
				} else if (Authentication.logIn(USERNAME_FIELD.getText(), PASSWORD_FIELD.getText())) {
					ApplicationManager.setRoot(new MenuRoot());
				} else {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Username or password is wrong", 13, LOG_IN_LABEL);
					LOADING_ANIMATION.setVisible(false);
				}
			} catch (ExceptionInInitializerError | PersistenceException e) {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Could not connect to database", 13, LOG_IN_LABEL);
				LOADING_ANIMATION.setVisible(false);
				FileLogger.getLogger().log(Level.FINE, e.getMessage());
				FileLogger.closeHandler();
			}
		});
		pause.play();
	}
}
