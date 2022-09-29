package Components;

import Css.Css;
import Main.ApplicationManager;
import javafx.scene.control.Button;

/**
 * Utility class that is used to create confirmations boxes
 */
public final class ConfirmationBox {

	private static boolean answer;

	/**
	 * Private constructor to hinder the creation of the utility class
	 */
	private ConfirmationBox() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Creates the confirmation box and displays it
	 *
	 * @param width   the width of the confirmation box, if less than 200 it is set to 200
	 * @param height  the height of the confirmation box, if less than 100 it is set to 100
	 * @param title   the title of the conformation box
	 * @param message the message that the confirmation box is going to display
	 * @return boolean answer of the confirmation box
	 */
	public static boolean display(int width, int height, String title, String message) {
		// Makes sure the confirmation box size is at least 200 x 100
		if (width < 200) width = 200;
		if (height < 100) height = 100;

		PopUpWindow dialogWindow = new PopUpWindow(ApplicationManager.getStage(), width, height);
		dialogWindow.getDialogWindow().close();

		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		yesButton.setOnAction(s -> {
			answer = true;
			dialogWindow.getDialogWindow().close();
		});
		noButton.setCancelButton(true);
		noButton.setOnAction(s -> {
			answer = false;
			dialogWindow.getDialogWindow().close();
		});

		Css.setButton(width - 100, 30, 15, yesButton, noButton);

		dialogWindow.getDialogWindow().setTitle(title);
		dialogWindow.getDialogText().setText(message);
		dialogWindow.getDialogVBox().getChildren().addAll(yesButton, noButton);

		dialogWindow.getDialogWindow().showAndWait();

		return answer;
	}
}
