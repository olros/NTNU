package Roots;

import Components.Header;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Class that has the basic structure of each root used in the application
 * All other root classes extends this class
 */
public abstract class SceneRoot {

	private Header header = new Header();
	private BorderPane borderPane = new BorderPane();
	private GridPane gridPane = new GridPane();

	/**
	 * SceneRoot constructor, used by all its subclasses
	 */
	SceneRoot() {
		this.setGridPane();
	}

	void setPageTitle(String title) {
		header.setPageTitle(title);
	}

	public BorderPane getBorderPane() {
		return borderPane;
	}

	GridPane getGridPane() {
		return gridPane;
	}

	/**
	 * SetGridPane void, sets padding and alignment for the grid pane
	 */
	void setGridPane() {
		gridPane.setPadding(new Insets(10, 10, 10, 10));
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setAlignment(Pos.CENTER);
	}

	/**
	 * Sets the basic layout of the application
	 */
	void setLayout() {
		BorderPane.setAlignment(Header.getHBox(), Pos.CENTER);
		BorderPane.setMargin(Header.getHBox(), new Insets(10.0D, 10.0D, 10.0D, 10.0D));
		borderPane.setTop(Header.getHBox());
		borderPane.setCenter(gridPane);
	}
}
