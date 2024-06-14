package stage.bataillenavale.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import stage.bataillenavale.Main;
import stage.bataillenavale.model.game.Game;
import stage.bataillenavale.model.game.GamePanel;
import stage.bataillenavale.model.game.GameVsPlayer;
import stage.bataillenavale.model.grid.GraphicGrid;
import stage.bataillenavale.model.grid.Grid;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static stage.bataillenavale.Main.DisplayErrorAlert;
import static stage.bataillenavale.Main.DisplayInformationAlert;

/**
 * Contrôleur de l'interface graphique du jeu en cours.
 *
 * @inv <pre>
 *     game != null
 *     primaryStage != null
 *     enemyGraphicGrid != null
 *     playerGraphicGrid != null </pre>
 * @cons <pre>
 *     $ARGS$ Stage primaryStage, Game game
 *     $PRE$ primaryStage != null && game != null
 *     $POST$ </pre>
 * Crée un contrôleur pour l'interface de jeu InGameAppli
 */
public class InGameAppliController implements Initializable {
    // ATTRIBUTS STATIQUES
    private static final int TileSize = 50;
    // ATTRIBUTS
    private final Game game;
    private final Thread t;
    private final MediaPlayer SunkSound;
    private final MediaPlayer HitSound;
    private final MediaPlayer MissSound;
    // ATTRIBUTS FXML
    @FXML
    GridPane enemyGrid;
    @FXML
    AnchorPane Pane;
    @FXML
    GridPane playerGrid;
    @FXML
    Label NbShipsEnnemy;
    @FXML
    Label NbShipsPlayer;
    @FXML
    Label Time;
    private Stage primaryStage;
    private Stage waitingStage;
    private GraphicGrid enemyGraphicGrid;
    private GraphicGrid playerGraphicGrid;

    public InGameAppliController(Stage primaryStage, Game game) {
        Contract.checkCondition(primaryStage != null && game != null,
                "InGameAppliController: constructor, argument is null");
        this.primaryStage = primaryStage;
        this.game = game;
        SunkSound = new MediaPlayer(new Media(Objects.requireNonNull(
                Main.class.getResource("son/SunkSound.wav")).toString()));
        SunkSound.setVolume(0.05);
        SunkSound.setStopTime(Duration.seconds(4));
        HitSound = new MediaPlayer(new Media(Objects.requireNonNull(
                Main.class.getResource("son/HitSound.wav")).toString()));
        HitSound.setVolume(0.05);
        HitSound.setStopTime(Duration.seconds(2));
        MissSound = new MediaPlayer(new Media(Objects.requireNonNull(
                Main.class.getResource("son/MissSound.wav")).toString()));
        MissSound.setVolume(0.2);
        MissSound.setStopTime(Duration.seconds(2));
        SunkSound.setOnEndOfMedia(() -> {
            SunkSound.stop();
            SunkSound.seek(Duration.ZERO);
        });
        HitSound.setOnEndOfMedia(() -> {
            HitSound.stop();
            HitSound.seek(Duration.ZERO);
        });
        MissSound.setOnEndOfMedia(() -> {
            MissSound.stop();
            MissSound.seek(Duration.ZERO);
        });
        t = new Thread(() -> {
            try {
                game.run();
            } catch (IOException e) {
                DisplayErrorAlert("Error during the game.",
                        "An error occurred during the game.");
            }
        });
        t.start();
    }

    // COMMANDES

    /**
     * Applique des changements à l'interface graphique avant son affichage.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        enemyGraphicGrid = new GraphicGrid(enemyGrid, TileSize,
                true);
        playerGraphicGrid = new GraphicGrid(playerGrid, TileSize,
                false);
        ModifyStateButton();
        NbShipsEnnemy.setText("Enemy ship remaining : " +
                game.getPlayer().getNbEnemyShip());
        NbShipsPlayer.setText("Your ship remaining : " +
                game.getPlayer().getPlayerGrid().getNbShip());
        Time.setText("Time remaining : " + game.TURN_TIME);
        addController();
        addPlayerShip();
        if (game instanceof GameVsPlayer) {
            if (game.getPlayer().isRoundPlayer()) {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(
                        "fxml/waiting-screen.fxml"));
                Scene PrimaryScene = null;
                try {
                    PrimaryScene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    DisplayErrorAlert("Error during the game.",
                            "An error occurred during loading the game.");
                    primaryStage.close();
                }
                waitingStage = new Stage();
                waitingStage.setTitle(Main.WINDOW_TITLE);
                waitingStage.setScene(PrimaryScene);
                waitingStage.setResizable(false);
                waitingStage.show();
            }
        }
    }

    // OUTILS

    /**
     * Ajoute les contrôleurs pour les éléments de l'interface graphique
     */
    private void addController() {
        for (int i = 0; i < enemyGrid.getRowCount(); ++i) {
            for (int j = 0; j < enemyGrid.getColumnCount(); ++j) {
                int finalI = i;
                int finalJ = j;
                enemyGraphicGrid.getInterneButton()[i][j].setOnAction(e -> {
                    try {
                        game.hit(finalI, finalJ);
                    } catch (IOException exception) {
                        DisplayErrorAlert("Error during the game.",
                                "An error occurred during the game.");
                        primaryStage.close();
                    } catch (PropertyVetoException propertyVetoException) {
                        DisplayInformationAlert(
                                "You can't hit this tile.",
                                "You can't hit the same tile twice");
                    }
                });
            }
        }
        if (game instanceof GameVsPlayer) {
            game.addPropertyChangeListener(GameVsPlayer.PROP_START_GAME, evt ->
                    Platform.runLater(() -> {
                        if (game.getPlayer().isRoundPlayer()) {

                            waitingStage.close();
                            primaryStage.show();
                        }
                    }));
        }

        game.addPropertyChangeListener(Game.PROP_TIMER, evt ->
                Platform.runLater(() ->
                        Time.setText("Time remaining : " + game.getTime())
                ));

        game.addPropertyChangeListener(Game.PROP_LOSE_GAME, evt ->
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Bataille Navale");
                    alert.setHeaderText("Game Over !!!");
                    alert.setContentText("You lose the game.");
                    newGame(alert);
                }));

        game.addPropertyChangeListener(Game.PROP_WIN_GAME, evt ->
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Bataille Navale");
                    alert.setHeaderText("You win !!!");
                    alert.setContentText("You win the game.");

                    newGame(alert);
                }));

        game.getPlayer().addPropertyChangeListener(GamePanel.PROP_ROUND_PLAYER,
                evt -> ModifyStateButton());

        game.getPlayer().addPropertyChangeListener(GamePanel.PROP_ENEMY_SHIP,
                evt -> Platform.runLater(() -> {
                    NbShipsEnnemy.setText("Enemy ship remaining : " +
                            evt.getNewValue());
                    SunkSound.play();
                }));

        game.getPlayer().addPropertyChangeListener(GamePanel.PROP_ENEMY_GRID,
                evt -> Platform.runLater(() -> {
                    enemyGraphicGrid.updateGrid(
                            game.getPlayer().getEnemyGrid());
                    if (evt.getNewValue() == TileState.SHIP_HIT_TILE) {
                        HitSound.play();
                    } else {
                        MissSound.play();
                    }

                }));

        game.getPlayer().getPlayerGrid().addPropertyChangeListener(
                Grid.PROP_SHIP, evt ->
                        Platform.runLater(() -> {
                            NbShipsPlayer.setText("Your ship remaining : " +
                                    game.getPlayer().getPlayerGrid().getNbShip());
                            SunkSound.play();
                        }));


        game.getPlayer().getPlayerGrid().addPropertyChangeListener(
                Grid.PROP_GRID, evt ->
                        Platform.runLater(() -> {
                            playerGraphicGrid.updateGrid(
                                    game.getPlayer().getPlayerGrid().getGridState());
                            if (evt.getNewValue() == TileState.SHIP_HIT_TILE) {
                                HitSound.play();
                            } else {
                                MissSound.play();
                            }
                        }));


    }

    /**
     * Ajoute la représentation des bateaux du joueur à l'interface graphique
     */
    private void addPlayerShip() {
        List<List<Point2D>> ships = game.getPlayer().getPlayerGrid().getShips();
        for (List<Point2D> ship : ships) {
            Image image = new Image(Objects.requireNonNull(
                    Main.class.getResourceAsStream(
                            "image/ship" + ship.size() + ".png")));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(TileSize * ship.size());
            imageView.setFitHeight(TileSize);
            imageView.setOpacity(0.65);
            int x = ((int) ship.get(0).getX() * TileSize);
            int y = ((int) ship.get(0).getY() * TileSize);
            if (ship.get(0).getX() == ship.get(1).getX()) {
                imageView.getTransforms().add(new Rotate(90, 0, 0));
                x += TileSize;
            }
            Point2D point = Pane.localToParent(x, y);
            imageView.setLayoutX(point.getX());
            imageView.setLayoutY(point.getY());
            Pane.getChildren().add(imageView);
        }
    }

    /**
     * Crée une nouvelle partie ou ferme l'application en fonction de la
     * réponse de l'utilisateur.
     */
    private void newGame(Alert alert) {
        ButtonType restart = new ButtonType("Restart",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType quit = new ButtonType("Quit",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().addAll(restart, quit);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == quit) {
                primaryStage.close();
            } else {
                primaryStage.close();
                primaryStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(
                        "fxml/start-menu.fxml"));
                StartMenuApplicationController controller =
                        new StartMenuApplicationController(primaryStage);
                fxmlLoader.setController(controller);
                Scene PrimaryScene = null;
                try {
                    PrimaryScene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    DisplayErrorAlert("Error during the game.",
                            "An error occurred during loading the game.");
                    primaryStage.close();
                }
                primaryStage.setTitle(Main.WINDOW_TITLE);
                primaryStage.setScene(PrimaryScene);
                primaryStage.setResizable(false);
                primaryStage.show();
            }
        }
        try {
            t.join();
        } catch (InterruptedException e) {
            DisplayErrorAlert("Error during the game.",
                    "An error occurred during end of the game.");
            primaryStage.close();
        }
    }

    /**
     * Modifie l'état des boutons de l'interface graphique en fonction de si
     * c'est le tour du joueur ou non.
     */
    private void ModifyStateButton() {
        if (game.getPlayer().isRoundPlayer()) {
            Platform.runLater(() -> {
                for (int i = 0; i < enemyGrid.getRowCount(); ++i) {
                    for (int j = 0; j < enemyGrid.getColumnCount(); ++j) {
                        enemyGraphicGrid.getInterneButton()[i][j].setDisable(false);
                    }
                }
            });
        } else {
            Platform.runLater(() -> {
                for (int i = 0; i < enemyGrid.getRowCount(); ++i) {
                    for (int j = 0; j < enemyGrid.getColumnCount(); ++j) {
                        enemyGraphicGrid.getInterneButton()[i][j].setDisable(true);
                    }
                }
            });
        }
    }
}
