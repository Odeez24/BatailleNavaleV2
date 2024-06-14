package stage.bataillenavale.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import stage.bataillenavale.Main;
import stage.bataillenavale.utils.Contract;

import java.io.IOException;
import java.util.Random;

/**
 * Contrôleur de l'interface graphique du menu de démarrage du jeu.
 * @inv <pre>
 *     primaryStage != null </pre>
 * @cons <pre>
 *     $ARGS$ Stage primaryStage
 *     $PRE$ primaryStage != null
 *     $POST$
 */
public class StartMenuApplicationController {
    // FXML ATTRIBUTS
    @FXML
    private Label seed;
    @FXML
    private ToggleGroup PlayAgainst;
    @FXML
    private TextField keyField;
    @FXML
    private ComboBox<String> chooseDifficulty;
    // ATTRIBUTS
    Stage primaryStage;
    // CONSTRUCTEUR
    public StartMenuApplicationController(Stage primaryStage) {
        Contract.checkCondition(primaryStage != null,
                "Invalid primaryStage");
        this.primaryStage = primaryStage;
    }

    // COMMANDES

    /**
     * Applique des changements à l'interface graphique avant son affichage.
     */
    public void initialize() {
        String randomId = createRandomId();
        seed.setText(randomId);
        keyField.setPrefColumnCount(6);
        chooseDifficulty.getItems().addAll("EASY", "MEDIUM", "HARD");
        chooseDifficulty.setPlaceholder(new Label("Difficulty"));
        chooseDifficulty.setDisable(true);
    }

    /**
     * Lance le jeu en fonction des paramètres choisis par l'utilisateur.
     */
    @FXML
    protected void onStartButtonClick() {
        ShipPlacerController controller;
        RadioButton selectedRadioButton = (RadioButton) PlayAgainst.getSelectedToggle();
        String toggleGroupValue = selectedRadioButton.getText();
        String aux;
        if (toggleGroupValue.equals("Player")) {
            String key = getKey();
            aux = key;
            if (key == null) {
                return;
            }
            controller = new ShipPlacerController(primaryStage, true,
                    key);

        } else {
            String difficulty = chooseDifficulty.getValue();
            aux = difficulty;
            if (difficulty == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid difficulty");
                alert.setContentText("You must choose a difficulty.");
                alert.showAndWait();
                return;
            }
            controller = new ShipPlacerController(primaryStage,
                    false, difficulty);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(
                "fxml/ship-placer.fxml"));
        fxmlLoader.setController(controller);
        Scene primaryScene;
        try {
            primaryScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error during loading the game.");
            alert.setContentText("An error occurred while loading the game." +
                    "\n Please try again or restart the game.");
            alert.showAndWait();
            throw new RuntimeException(e);
        }
        primaryStage.setTitle(Main.WINDOW_TITLE + " : " + aux);
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    // OUTILS
    /**
     * Génère un nombre entre 100000 et 999999 de manière aléatoire.
     */
    static String createRandomId() {
        StringBuilder str = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; ++i) {
            str.append(rand.nextInt(9));
        }
        return str.toString();
    }

    /**
     * Récupère la clé entrée par l'utilisateur dans le TextField de
     *  l'interface.
     */
    private String getKey() {
        String key;
        if (keyField.getText().isEmpty()) {
            key = seed.getText();
        } else {
            key = keyField.getText();
        }
        // ATTRIBUTS
        String PATTERN = "[0-9]{6}";
        if (!key.matches(PATTERN)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid key");
            alert.setContentText("The key must be a 6-digit number.");
            alert.showAndWait();
            return null;
        }
        return key;
    }

    /**
     * Désactive le champ de texte de la clé et active le choix de la
     *  difficulté.
     */
    @FXML
    protected void onIARadio() {
        keyField.setDisable(true);
        chooseDifficulty.setDisable(false);
    }

    /**
     * Désactive le choix de la difficulté et active le champ de texte de
     *  la clé.
     */
    @FXML
    protected void onPlayerRadio() {
        keyField.setDisable(false);
        chooseDifficulty.setDisable(true);
    }
}
