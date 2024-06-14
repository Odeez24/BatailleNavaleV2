package stage.bataillenavale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import stage.bataillenavale.gui.StartMenuApplicationController;

import java.io.IOException;

public class Main extends Application {

    // CONSTANTS
    public static final String WINDOW_TITLE = "BattleShip Game v2.0";

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/start-menu.fxml"));
        StartMenuApplicationController controller = new StartMenuApplicationController(primaryStage);
        fxmlLoader.setController(controller);
        Scene PrimaryScene = new Scene(fxmlLoader.load());
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(PrimaryScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void DisplayErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void DisplayInformationAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
