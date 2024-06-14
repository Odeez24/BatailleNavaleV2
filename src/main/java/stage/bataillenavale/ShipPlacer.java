package stage.bataillenavale;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stage.bataillenavale.gui.ShipPlacerController;

import java.io.IOException;

public class ShipPlacer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/ship-placer.fxml"));
        ShipPlacerController controller = new ShipPlacerController(primaryStage, true, "EASY");
        fxmlLoader.setController(controller);
        Scene primaryScene = new Scene(fxmlLoader.load());
        primaryStage.setTitle(stage.bataillenavale.Main.WINDOW_TITLE);
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
