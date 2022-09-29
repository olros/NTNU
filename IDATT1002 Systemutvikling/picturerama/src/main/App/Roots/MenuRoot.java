package Roots;

import Components.Authentication;
import Css.Css;
import Main.ApplicationManager;
import javafx.scene.control.Button;

/**
 * Class for the menu root
 */
public final class MenuRoot extends SceneRoot {

	private final Button UPLOAD_BUTTON = new Button("Upload");
	private final Button PHOTOS_BUTTON = new Button("Photos");
	private final Button ALBUMS_BUTTON = new Button("Albums");
	private final Button MAP_BUTTON = new Button("Map");
	private final Button LOG_OUT_BUTTON = new Button("Log out");
	private final Button DELETE_USER_BUTTON = new Button("Delete user");

	/**
	 * Instantiates a new Menu root.
	 */
	public MenuRoot() {
		super();
		this.setLayout();
	}

	/**
	 * Sets the layout of the menu. The setLayout() method from SceneRoot is overridden, but also
	 * called in the method in order to modify the method.
	 * Used in the constructor
	 */
	@Override
	void setLayout() {
		super.setLayout();
		super.setPageTitle("Menu");

		Css.setButton(340, 100, 40, UPLOAD_BUTTON, PHOTOS_BUTTON, ALBUMS_BUTTON, MAP_BUTTON, LOG_OUT_BUTTON, DELETE_USER_BUTTON);

		UPLOAD_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new UploadRoot()));
		PHOTOS_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new PhotosRoot()));
		ALBUMS_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new AlbumsRoot()));
		MAP_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new MapRoot()));
		LOG_OUT_BUTTON.setOnAction(e -> Authentication.logout());
		DELETE_USER_BUTTON.setOnAction(e -> ApplicationManager.setRoot(new DeleteUserRoot()));

		super.getGridPane().add(UPLOAD_BUTTON, 0, 0);
		super.getGridPane().add(PHOTOS_BUTTON, 1, 0);
		super.getGridPane().add(ALBUMS_BUTTON, 0, 1);
		super.getGridPane().add(MAP_BUTTON, 1, 1);
		super.getGridPane().add(LOG_OUT_BUTTON, 0, 2);
		super.getGridPane().add(DELETE_USER_BUTTON, 1, 2);
	}
}
