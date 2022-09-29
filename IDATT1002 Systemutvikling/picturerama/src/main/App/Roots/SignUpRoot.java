package Roots;

import Components.Authentication;
import Components.FileLogger;
import Css.Css;
import Css.FeedbackType;
import Database.Hibernate;
import Database.HibernateClasses.User;
import Main.ApplicationManager;
import javafx.animation.PauseTransition;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.logging.Level;

/**
 * Class for the SignUpRoot
 */
final class SignUpRoot extends SceneRoot {
	private final Label USERNAME_LABEL = new Label("Username: ");
	private final Label PASSWORD_LABEL = new Label("Password: ");
	private final Label CONFIRM_PASSWORD_LABEL = new Label("Confirm password: ");
	private final PasswordField PASSWORD_FIELD = new PasswordField();
	private final PasswordField CONFIRM_PASSWORD_FIELD = new PasswordField();
	private final TextField USERNAME_FIELD = new TextField();
	private final Label SIGN_UP_FEEDBACK_LABEL = new Label();
	private final Button SIGN_UP_BUTTON = new Button("Sign up");
	private final Button LOG_IN_BUTTON = new Button("Log in");
	private final ProgressBar PASSWORD_STRENGTH_BAR = new ProgressBar(0);
	private final ProgressIndicator LOADING_ANIMATION = new ProgressIndicator();

	/**
	 * Creates an object of the class SignUpRoot
	 */
	SignUpRoot() {
		super();
		this.setLayout();
	}

	/**
	 * Overrides SceneRoot method.
	 * Assigns layout components to RootBuilders GridPane
	 * Sets styling to layout components
	 * Sets functionality to button nodes
	 * Used in the constructor
	 */
	@Override
	void setLayout() {
		super.setLayout();
		super.setPageTitle("Sign up");
		//Sets PromptText to TextFields
		USERNAME_FIELD.setPromptText("Username here...");
		PASSWORD_FIELD.setPromptText("Password here...");
		PASSWORD_FIELD.setTooltip(new Tooltip("Password has to be at least 8 characters long"));
		CONFIRM_PASSWORD_FIELD.setPromptText("Password here...");
		super.getGridPane().add(USERNAME_LABEL, 10, 0);
		super.getGridPane().add(PASSWORD_LABEL, 10, 2);
		super.getGridPane().add(CONFIRM_PASSWORD_LABEL, 10, 4);
		super.getGridPane().add(USERNAME_FIELD, 10, 1);
		super.getGridPane().add(PASSWORD_FIELD, 10, 3);
		super.getGridPane().add(CONFIRM_PASSWORD_FIELD, 10, 5);
		super.getGridPane().add(SIGN_UP_BUTTON, 10, 6);
		super.getGridPane().add(PASSWORD_STRENGTH_BAR, 11, 3);
		super.getGridPane().add(SIGN_UP_FEEDBACK_LABEL, 10, 10);
		super.getGridPane().add(LOG_IN_BUTTON, 10, 7);
		super.getGridPane().add(LOADING_ANIMATION, 11, 6);
		//Sets ToolTip to passwordStrengthBar, used when its being hovered
		PASSWORD_STRENGTH_BAR.setTooltip(new Tooltip("Password Strength: \n Use 8 or more characters \n Use numbers \n Use capital letters"));
		PASSWORD_STRENGTH_BAR.setVisible(false);

		//Set styling on the layout components
		Css.setButton(700, 25, 20, SIGN_UP_BUTTON, LOG_IN_BUTTON);
		Css.setTextField(700, 20, 17, USERNAME_FIELD, PASSWORD_FIELD, CONFIRM_PASSWORD_FIELD);
		Css.setLabel(13, USERNAME_LABEL, PASSWORD_LABEL, CONFIRM_PASSWORD_LABEL);
		Css.setLoadingAnimation(LOADING_ANIMATION);

		SIGN_UP_BUTTON.setDefaultButton(true);
		SIGN_UP_BUTTON.setOnAction(e -> signUp());
		PASSWORD_FIELD.setOnKeyTyped(e -> passwordStrengthBarEventHandling());

		LOG_IN_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new LoginRoot()));
	}

	/**
	 * Registers a new user if all the terms are met
	 * Used in setLayout
	 */
	private void signUp() {
		LOADING_ANIMATION.setVisible(true);
		PauseTransition pause = new PauseTransition(Duration.seconds(1));
		pause.setOnFinished(e -> {
			try {
				if (feedback()) {
					if (Authentication.register(USERNAME_FIELD.getText(), PASSWORD_FIELD.getText())) {
						ApplicationManager.setRoot(new LoginRoot());
					} else {
						Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Could not connect to database", 13, SIGN_UP_FEEDBACK_LABEL);
						LOADING_ANIMATION.setVisible(false);
					}
				} else {
					LOADING_ANIMATION.setVisible(false);
				}
			} catch (ExceptionInInitializerError error) {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Could not connect to database", 13, SIGN_UP_FEEDBACK_LABEL);
				LOADING_ANIMATION.setVisible(false);
				FileLogger.getLogger().log(Level.FINE, error.getMessage());
				FileLogger.closeHandler();
			}
		});
		pause.play();
	}

	/**
	 * A method that returns a percent for the password strength.
	 * Password strength is defined by length, and if it contains a capital letter and digit.
	 * Used in passwordStrengthBarEventHandling
	 *
	 * @return double that contains the percent of how strong the written password is
	 */
	private double setPasswordStrength() {
		double passwordStrength = 0;
		boolean containsCapital = false;
		boolean containsDigit = false;
		//Checks if password length is greater than or equal to 10
		if (PASSWORD_FIELD.getText().length() >= 10) {
			passwordStrength += 0.25;
		}
		//Checks if password length is greater than or equal to 12
		if (PASSWORD_FIELD.getText().length() >= 12) {
			passwordStrength += 0.25;
		}
		for (int i = 0; i < PASSWORD_FIELD.getText().length(); i++) {
			//Checks if password contains a capital letter
			if (Character.isUpperCase(PASSWORD_FIELD.getText().charAt(i))) {
				containsCapital = true;
			}
			//Checks if password contains a digit
			if (Character.isDigit(PASSWORD_FIELD.getText().charAt(i))) {
				containsDigit = true;
			}
		}
		if (containsCapital) {
			passwordStrength += 0.25;
		}
		if (containsDigit) {
			passwordStrength += 0.25;
		}
		return passwordStrength;
	}

	/**
	 * A method that controls the password strength bar. Uses CSS for changing color of the bar.
	 * Changes from red to yellow to green, which define the strength.
	 * Used in setLayout
	 */
	private void passwordStrengthBarEventHandling() {
		if (PASSWORD_FIELD.getText().length() == 0) {
			PASSWORD_STRENGTH_BAR.setVisible(false);
		} else {
			PASSWORD_STRENGTH_BAR.setVisible(true);
		}

		PASSWORD_STRENGTH_BAR.setProgress(setPasswordStrength());
		//Sets color of passwordStrengthBar to red if strength is equal to 25%
		if (setPasswordStrength() == 0.25) {
			PASSWORD_STRENGTH_BAR.setStyle("-fx-accent: #E74C3C ");
		}
		//Sets color of passwordStrengthBar to yellow if strength is equal to 50%
		if (setPasswordStrength() == 0.5) {
			PASSWORD_STRENGTH_BAR.setStyle("-fx-accent: #F4D03F");
		}
		//Sets color of passwordStrengthBar to green if strength is equal to 75%
		if (setPasswordStrength() == 0.75) {
			PASSWORD_STRENGTH_BAR.setStyle("-fx-accent: #2ECC71 ");
		}
	}

	/**
	 * Gives feedback on password and username and different error messages.
	 * Password requirements: 8 characters or more, only digits and letters
	 * Username requirements: Only digits and letters
	 * Used in signUp
	 *
	 * @return a boolean value, true for no errors, boolean for error
	 */
	private boolean feedback() {
		//Checks if password or username trimmed is equal to 0
		if (PASSWORD_FIELD.getText().trim().length() == 0 || USERNAME_FIELD.getText().trim().length() == 0) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Password or username is missing", 13, SIGN_UP_FEEDBACK_LABEL);
			return false;
		}
		//Checks if password is shorter than 8 characters
		if (PASSWORD_FIELD.getText().length() < 8) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Password needs to contain 8 characters", 13, SIGN_UP_FEEDBACK_LABEL);
			return false;
		}
		//Checks username for illegal characters
		if (USERNAME_FIELD.getText().length() != 0) {
			for (int i = 0; i < USERNAME_FIELD.getText().length(); i++) {
				if (!Character.isLetter(USERNAME_FIELD.getText().charAt(i)) &&
						!Character.isDigit(USERNAME_FIELD.getText().charAt(i))) {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Username contains illegal character", 13, SIGN_UP_FEEDBACK_LABEL);
					return false;
				}
			}
		}
		//Checks password for illegal characters
		if (PASSWORD_FIELD.getText().length() != 0) {
			for (int i = 0; i < PASSWORD_FIELD.getText().length(); i++) {
				if (!Character.isLetter(PASSWORD_FIELD.getText().charAt(i)) &&
						!Character.isDigit(PASSWORD_FIELD.getText().charAt(i))) {
					Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Password contains illegal character", 13, SIGN_UP_FEEDBACK_LABEL);
					return false;
				}
			}
		}
		//Checks if passwords are equal
		if (!PASSWORD_FIELD.getText().equals(CONFIRM_PASSWORD_FIELD.getText())) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Your passwords don't match", 13, SIGN_UP_FEEDBACK_LABEL);
			return false;
		}

		try {
			User testUser = Hibernate.getUser(USERNAME_FIELD.getText());
			//Checks if username is available
			if (USERNAME_FIELD.getText().equals(testUser.getUsername())) {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Username is already taken", 13, SIGN_UP_FEEDBACK_LABEL);
				return false;
			}
		} catch (javax.persistence.PersistenceException e) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Error: Could not connect to database", 13, SIGN_UP_FEEDBACK_LABEL);
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}

		return true;
	}
}
