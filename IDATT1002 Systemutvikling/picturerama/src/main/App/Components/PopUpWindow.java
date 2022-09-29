package Components;

import Css.Css;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class that creates an universal popup window
 */
public final class PopUpWindow {

	private final Stage DIALOG_WINDOW;
	private final VBox DIALOG_VBOX;
	private final HBox DIALOG_HBOX;
	private final Text DIALOG_TEXT;

	/**
	 * Constructor that takes in the width and height of the popup window
	 *
	 * @param width  the width of the popup
	 * @param height the height of the popup
	 */
	public PopUpWindow(Stage owner, double width, double height) {
		DIALOG_WINDOW = new Stage();
		DIALOG_WINDOW.initOwner(owner);
		DIALOG_WINDOW.initModality(Modality.APPLICATION_MODAL);
		DIALOG_WINDOW.getIcons().add(new Image("file:src/main/App/Images/Logo.png"));
		DIALOG_WINDOW.setResizable(false);

		DIALOG_VBOX = new VBox();
		DIALOG_VBOX.setAlignment(Pos.CENTER);
		DIALOG_VBOX.setSpacing(10);

		DIALOG_TEXT = new Text();
		Css.setTextFont(17, DIALOG_TEXT);

		DIALOG_HBOX = new HBox();
		DIALOG_HBOX.setPadding(new Insets(10, 10, 10, 10));
		DIALOG_HBOX.setSpacing(10);

		DIALOG_VBOX.getChildren().addAll(DIALOG_TEXT, DIALOG_HBOX);
		Scene dialogScene = new Scene(DIALOG_VBOX, width, height);
		//Sets the escape key to run the closeProgram method
		dialogScene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ESCAPE) {
				DIALOG_WINDOW.close();
			}
		});

		DIALOG_WINDOW.setScene(dialogScene);
		DIALOG_WINDOW.show();
	}

	public Stage getDialogWindow() {
		return DIALOG_WINDOW;
	}

	public VBox getDialogVBox() {
		return DIALOG_VBOX;
	}

	public HBox getDialogHBox() {
		return DIALOG_HBOX;
	}

	public Text getDialogText() {
		return DIALOG_TEXT;
	}
}
