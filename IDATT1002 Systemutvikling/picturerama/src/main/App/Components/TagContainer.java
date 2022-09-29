package Components;

import Css.Css;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Class that is used to display tags of photos
 */
public final class TagContainer {

	private final Label TAG;
	private final Button DELETE_TAG_BUTTON;
	private final HBox CONTAINER;

	/**
	 * Constructor that creates a tag container, containing a label with the tag, a button and the container itself
	 *
	 * @param tag is the tag that is in the tag container
	 */
	public TagContainer(String tag) {
		this.TAG = new Label(tag);
		this.DELETE_TAG_BUTTON = new Button("", new ImageView(new Image("file:src/main/App/Images/Close.png")));
		this.CONTAINER = new HBox();
		this.setLayout();
	}

	/**
	 * Sets up the layout of the container
	 */
	private void setLayout() {
		CONTAINER.getChildren().addAll(this.TAG, DELETE_TAG_BUTTON);
		Css.setPane(CONTAINER);
		CONTAINER.setAlignment(Pos.CENTER);
		Css.setTagContainerButton(DELETE_TAG_BUTTON);
	}

	Button getDeleteTagButton() {
		return DELETE_TAG_BUTTON;
	}

	public HBox getContainer() {
		return CONTAINER;
	}

	public String getTagAsString() {
		return this.TAG.getText();
	}
}
