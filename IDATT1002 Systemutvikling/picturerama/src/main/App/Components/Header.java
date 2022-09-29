package Components;

import Css.Css;
import Main.ApplicationManager;
import Roots.LoginRoot;
import Roots.MenuRoot;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Class that creates the header of the application
 */
public final class Header {

	private static final Image LOGO = new Image("file:src/main/App/Images/Logo.png", 80, 80, true, true);
	private static final ImageView LOGO_VIEW = new ImageView(LOGO);
	private static final Label PICTURERAMA = new Label("Picturerama");
	private static final Image HOME_ICON = new Image("file:src/main/App/Images/HomeIcon.png");
	private static final Button HOME_BUTTON = new Button("", new ImageView(HOME_ICON));
	private static final HBox HBOX = new HBox();
	private final Label PAGE_TITLE = new Label();

	/**
	 * Constructor that sets up the header of the application
	 */
	public Header() {
		// If user is logged in, make home button redirect to menu, if not to login
		if (UserInfo.getUser() != null) {
			HOME_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new MenuRoot()));
		} else {
			HOME_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new LoginRoot()));
		}
		HBOX.getChildren().clear();
		this.setHBox();
	}

	public static HBox getHBox() {
		return HBOX;
	}

	public void setPageTitle(String newTitle) {
		this.PAGE_TITLE.setText("- " + newTitle);
	}

	/**
	 * Sets padding and alignment for the HBox
	 */
	private void setHBox() {
		Region spacing = new Region();
		HBox.setHgrow(spacing, Priority.ALWAYS);

		HBOX.setSpacing(10.0D);
		HBOX.setAlignment(Pos.CENTER);
		HBOX.getChildren().add(0, LOGO_VIEW);
		HBOX.getChildren().add(PICTURERAMA);
		HBOX.getChildren().add(PAGE_TITLE);
		HBOX.getChildren().add(spacing);
		HBOX.getChildren().add(HOME_BUTTON);
		HBOX.setMaxWidth(700.0D);
		HBOX.setPrefHeight(100.0D);

		PICTURERAMA.setFont(Font.font("Montserrat", FontWeight.BOLD, 40.0D));
		PAGE_TITLE.setFont(Font.font("Montserrat", FontWeight.NORMAL, 40.0D));

		Css.setHomeButton(HOME_BUTTON);
		Css.setPane(HBOX);
	}
}
