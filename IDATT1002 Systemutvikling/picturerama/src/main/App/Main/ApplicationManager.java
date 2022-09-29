package Main;

import Components.ConfirmationBox;
import Database.Hibernate;
import Roots.LoginRoot;
import Roots.SceneRoot;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Class that initializes the stage and scene of the application
 * Changes the roots of the scene
 */
public final class ApplicationManager {

	private static final double WIDTH = 900.0;
	private static final double HEIGHT = 600.0;
	private static Stage stage;
	private static Scene scene;

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private ApplicationManager() {
		throw new IllegalStateException("Can not make instance of utility class");
	}

	/**
	 * Initializes the stage
	 * Can only be used once, when the stage and scene is still null
	 *
	 * @param primaryStage is the stage used as the main stage through the whole application
	 */
	public static void initialize(Stage primaryStage) {
		if (stage == null && scene == null) {
			stage = primaryStage;
			stage.setOnCloseRequest(s -> {
				//Hinders the program from closing
				s.consume();
				//Runs our close program instead
				closeProgram();
			});
			stage.getIcons().add(new Image("file:src/main/App/Images/Logo.png"));
			stage.setTitle("Picturerama");

			//Sets the minimum size of the stage to hinder disoriented content
			stage.setMinWidth(WIDTH);
			stage.setMinHeight(HEIGHT);

			SceneRoot root = new LoginRoot();
			scene = new Scene(root.getBorderPane(), WIDTH, HEIGHT);

			//Sets the escape key to run the closeProgram method
			scene.setOnKeyPressed(e -> {
				if (e.getCode() == KeyCode.ESCAPE) {
					closeProgram();
				}
			});

			stage.setScene(scene);
			stage.show();
		}
	}

	/**
	 * Gets the main stage of the application
	 *
	 * @return stage, the main stage of the application
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * Sets the root of the scene
	 * Uses the border pane of an object of a SceneRoot class, since the border pane is the outer layer
	 *
	 * @param root an object of a subclass of the SceneRoot class
	 */
	public static void setRoot(SceneRoot root) {
		scene.setRoot(root.getBorderPane());
	}

	/**
	 * Gives a confirmation box with the choices of exiting or not.
	 * If the user exits the program the connection to the database will be closed
	 */
	private static void closeProgram() {
		boolean close = ConfirmationBox.display(250, 150, "Exit", "Are you sure you want to exit?");
		if (close) {
			stage.close();
			Hibernate.getEm().clear();
			Hibernate.getEntityManagerFactory().close();
		}
	}
}
