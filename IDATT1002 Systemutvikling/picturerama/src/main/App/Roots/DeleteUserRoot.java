package Roots;

import Components.Authentication;
import Components.ConfirmationBox;
import Components.FileLogger;
import Components.UserInfo;
import Css.Css;
import Css.FeedbackType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import javax.persistence.PersistenceException;
import java.util.logging.Level;

/**
 * Class for the delete user root
 */
final class DeleteUserRoot extends SceneRoot {

	private final Label INSTRUCTIONS_LABEL = new Label("Enter password to delete your user:");
	private final PasswordField PASSWORD_FIELD = new PasswordField();
	private final Label FEEDBACK_LABEL = new Label();
	private final Button DELETE_USER_BUTTON = new Button("Delete user");

	/**
	 * DeleteUserRoot constructor
	 * Uses SceneRoot constructor to create an object of the DeleteUserRoot class
	 */
	DeleteUserRoot() {
		super();
		this.setLayout();
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
		super.setPageTitle("Delete user");
		super.getGridPane().add(INSTRUCTIONS_LABEL, 0, 0);
		super.getGridPane().add(PASSWORD_FIELD, 0, 1);
		super.getGridPane().add(DELETE_USER_BUTTON, 0, 2);
		super.getGridPane().add(FEEDBACK_LABEL, 0, 3);

		PASSWORD_FIELD.setPromptText("Password");

		//Sets styling for the layout components
		Css.setButton(700, 25, 20, DELETE_USER_BUTTON);
		Css.setTextField(700, 20, 17, PASSWORD_FIELD);
		Css.setLabel(20, INSTRUCTIONS_LABEL);

		DELETE_USER_BUTTON.setDefaultButton(true);
		DELETE_USER_BUTTON.setOnAction(e -> deleteUser());
	}

	/**
	 * Deletes the current user's user
	 * If the password is correct and the user confirms the choice
	 */
	private void deleteUser() {
		try {
			String currentUsername = UserInfo.getUser().getUsername();
			if (PASSWORD_FIELD.getText().trim().length() == 0) {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Password is missing", 13, FEEDBACK_LABEL);
			} else if (Authentication.isCurrentUser(currentUsername, PASSWORD_FIELD.getText())) {
				if (Authentication.deleteUser(currentUsername, PASSWORD_FIELD.getText(), ConfirmationBox.display(350, 150, "Delete user", "Are you sure you want to delete your user?"))) {
					Css.playFeedBackLabelTransition(FeedbackType.SUCCESSFUL, "User successfully deleted", 13, Authentication.logout().getLOG_IN_LABEL());
				}
			} else {
				Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Password does not match your current password", 13, FEEDBACK_LABEL);
			}
		} catch (ExceptionInInitializerError | PersistenceException e) {
			Css.playFeedBackLabelTransition(FeedbackType.ERROR, "Could not connect to database", 13, FEEDBACK_LABEL);
			FileLogger.getLogger().log(Level.FINE, e.getMessage());
			FileLogger.closeHandler();
		}
	}

}
