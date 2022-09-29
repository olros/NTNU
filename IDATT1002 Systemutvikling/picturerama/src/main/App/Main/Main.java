package Main;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class where the project is ran from
 */
public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		ApplicationManager.initialize(stage);
	}
}
