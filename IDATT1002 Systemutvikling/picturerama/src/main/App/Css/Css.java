package Css;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

/**
 * Css class used for styling different JavaFX elements
 */
public final class Css {

	private static final String BORDER_RADIUS = "15px";
	private static final String BORDER_WIDTH = "1px";
	private static final String BORDER_COLOR_LIGHT = "#DDDDDD";
	private static final String BOX_BACKGROUND_COLOR = "WHITE";
	private static final String FONT = "Montserrat";
	private static final String PANE_STYLE = "-fx-padding: 4;-fx-border-style: solid inside;-fx-border-width: " + BORDER_WIDTH + ";-fx-border-radius: " + BORDER_RADIUS + ";-fx-border-color: " + BORDER_COLOR_LIGHT + ";-fx-background-color: " + BOX_BACKGROUND_COLOR + ";-fx-background-radius: " + BORDER_RADIUS + ";";

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private Css() {
		throw new IllegalStateException("Can not make instance of utility class");
	}

	/**
	 * Styles panes
	 *
	 * @param panes an amount of panes
	 */
	public static void setPane(Pane... panes) {
		for (Pane pane : panes) {
			pane.setStyle(PANE_STYLE);
		}
	}

	/**
	 * Sets styling for the stack pane in the map scene
	 *
	 * @param stackPane a stack pane
	 */
	public static void setMapPane(StackPane stackPane) {
		stackPane.setStyle("-fx-border-style: solid inside;-fx-border-radius: " + BORDER_RADIUS + ";-fx-border-width: " + BORDER_WIDTH + ";-fx-border-color: " + BORDER_COLOR_LIGHT + ";");
	}

	/**
	 * Styles buttons and sets height, width and font size by given params
	 *
	 * @param width    the preferred width of buttons
	 * @param height   the preferred height of the buttons
	 * @param fontSize the font size of the text in the buttons
	 * @param buttons  an amount of buttons
	 */
	public static void setButton(int width, int height, int fontSize, Button... buttons) {
		for (Button button : buttons) {
			button.setFont(Font.font(FONT, fontSize));
			button.setPrefWidth(width);
			button.setPrefHeight(height);
			button.getStylesheets().add("file:src/main/App/Css/Button.css");
		}
	}

	/**
	 * Style buttons in the tag container class
	 *
	 * @param buttons an amount of buttons
	 */
	public static void setTagContainerButton(Button... buttons) {
		for (Button button : buttons) {
			button.setMaxSize(10, 10);
			button.getStylesheets().add("file:src/main/App/Css/TagContainer.css");
		}
	}

	/**
	 * Sets home button styling in the header
	 *
	 * @param button a button
	 */
	public static void setHomeButton(Button button) {
		button.getStylesheets().add("file:src/main/App/Css/HomeButton.css");
	}

	/**
	 * Sets style in image container class for a button and container
	 *
	 * @param button          a button
	 * @param container       a Hbox
	 * @param containerHeight height of button
	 * @param containerWidth  width of button
	 */
	public static void setContainer(Button button, HBox container, int containerHeight, int containerWidth) {
		container.setStyle(PANE_STYLE + "-fx-padding: 0; -fx-border-insets: 1; -fx-background-insets: 1");
		button.setAlignment(Pos.CENTER_LEFT);
		button.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
		button.setMinHeight(containerHeight);
		button.setMaxHeight(containerWidth);
		button.getStylesheets().add("file:src/main/App/Css/Container.css");
	}

	/**
	 * Styles textFields and sets height, width and font size by given params
	 *
	 * @param width      the preferred width of text fields
	 * @param height     the preferred height of text fields
	 * @param fontSize   the font size of the prompt text in text fields
	 * @param textFields an amount of text fields
	 */
	public static void setTextField(int width, int height, int fontSize, TextField... textFields) {
		for (TextField textField : textFields) {
			textField.getStylesheets().add("file:src/main/App/Css/TextField.css");
			textField.setPrefWidth(width);
			textField.setPrefHeight(height);
			textField.setFont(Font.font(FONT, fontSize));
		}
	}

	/**
	 * Sets styling for the scroll pane
	 *
	 * @param scrollPane a scroll pane
	 */
	public static void setScrollPane(ScrollPane scrollPane) {
		scrollPane.setStyle("-fx-background-color:transparent;");
	}

	/**
	 * Sets styling for labels
	 *
	 * @param fontSize the size of the font in the labels
	 * @param labels   An amount of Labels
	 */
	public static void setLabel(int fontSize, Label... labels) {
		for (Label label : labels) {
			label.setFont(Font.font(FONT, fontSize));
		}
	}

	/**
	 * Sets styling for feedback labels
	 *
	 * @param feedBackType FeedbackType Enum, the type of feedback (success or error)
	 * @param fontSize     the size of the font
	 * @param labels,      an amount of labels.
	 */
	public static void setFeedBackLabel(FeedbackType feedBackType, int fontSize, Label... labels) {
		for (Label label : labels) {
			label.setStyle("-fx-text-fill:" + feedBackType.getColor());
			label.setFont(Font.font(FONT, fontSize));
		}
	}

	/**
	 * Plays a fading transition on a feedback label
	 *
	 * @param feedBackType FeedbackType Enum, the type of feedback (success or error)
	 * @param labelMessage feedback message the label will show
	 * @param fontSize     the size of the font
	 * @param label        label to play the transition on
	 */
	public static void playFeedBackLabelTransition(FeedbackType feedBackType, String labelMessage, int fontSize, Label label) {
		setFeedBackLabel(feedBackType, fontSize, label);
		label.setText(labelMessage);
		FadeTransition ft = new FadeTransition(Duration.seconds(8), label);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.play();
	}

	/**
	 * Styles font of a text
	 *
	 * @param fontSize the size of the font
	 * @param texts    a number of texts
	 */
	public static void setTextFont(int fontSize, Text... texts) {
		for (Text text : texts) {
			text.setFont(Font.font(FONT, fontSize));
		}
	}

	/**
	 * Styles ProgressIndicators
	 *
	 * @param progressIndicators an amount of progressIndicators
	 */
	public static void setLoadingAnimation(ProgressIndicator... progressIndicators) {
		for (ProgressIndicator progressIndicator : progressIndicators) {
			progressIndicator.setStyle("-fx-progress-color: dimgrey; -fx-cursor: wait");
			progressIndicator.setMaxSize(30, 30);
			progressIndicator.setVisible(false);
		}
	}
}
