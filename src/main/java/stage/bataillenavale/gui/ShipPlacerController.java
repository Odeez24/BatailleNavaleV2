package stage.bataillenavale.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import stage.bataillenavale.Main;
import stage.bataillenavale.model.game.Game;
import stage.bataillenavale.model.game.GameVsIA;
import stage.bataillenavale.model.game.GameVsPlayer;
import stage.bataillenavale.model.grid.GraphicGrid;
import stage.bataillenavale.model.ship.GraphicShip;
import stage.bataillenavale.model.ship.Ship;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static stage.bataillenavale.Main.DisplayErrorAlert;
import static stage.bataillenavale.Main.DisplayInformationAlert;

/**
 * Contrôleur de l'interface graphique du placement des bateaux.
 *
 * @inv <pre>
 *     ships != null
 *     placedShips != null
 *     graphicGrid != null
 *     primaryStage != null </pre>
 * @cons <pre>
 *     $ARGS$ Stage primaryStage, boolean isMultiplayer, String aux
 *     $PRE$
 *     $POST$
 */
public class ShipPlacerController implements Initializable {
    // ATTRIBUTS STATIQUES
    private static final int SHIP_NB = 5;
    private final int BASE_POSITION = 64;
    private final int BASE_Y_GAP = 110;
    private final Stage primaryStage;
    private final boolean isMultiplayer;
    private final String aux;
    // ATTRIBUTS FXML
    @FXML
    private AnchorPane Pane;
    @FXML
    private FlowPane flowPane;
    @FXML
    private Button finishButton;
    @FXML
    private GridPane BoardGrid;
    @FXML
    private VBox VboxLabel;
    // ATTRIBUTS
    private List<GraphicShip> ships;
    private List<Ship> placedShips;
    private GraphicGrid graphicGrid;

    public ShipPlacerController(Stage primaryStage, boolean isMultiplayer,
                                String aux) {
        this.primaryStage = primaryStage;
        this.isMultiplayer = isMultiplayer;
        this.aux = aux;
    }

    // COMMANDES

    /**
     * Applique des changements à l'interface graphique avant son affichage.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        graphicGrid = new GraphicGrid(BoardGrid, 64, false);
        placedShips = new LinkedList<>();
        //Adds ship to each cell in grid
        ships = new LinkedList<>();
        ships.add(new GraphicShip(graphicGrid, 5, 64, BASE_POSITION,
                BASE_POSITION));
        ships.add(new GraphicShip(graphicGrid, 4, 64, BASE_POSITION,
                BASE_POSITION + BASE_Y_GAP));
        ships.add(new GraphicShip(graphicGrid, 3, 64, BASE_POSITION,
                BASE_POSITION + 2 * BASE_Y_GAP));
        ships.add(new GraphicShip(graphicGrid, 3, 64, BASE_POSITION,
                BASE_POSITION + 3 * BASE_Y_GAP));
        ships.add(new GraphicShip(graphicGrid, 2, 64, BASE_POSITION,
                BASE_POSITION + 4 * BASE_Y_GAP));

        for (int i = 0; i < SHIP_NB; i++) {
            Pane.getChildren().add(ships.get(i).getImageView());
        }
        addController();
    }

    /**
     * Gère l'événement de clic sur le bouton de fin de placement des bateaux.
     */
    @FXML
    public void onClickFinishButton() {
        if (placedShips.size() != SHIP_NB) {
            DisplayInformationAlert("Invalid placement",
                    "All ships must be placed.");
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(
                "fxml/in-game-panel.fxml"));
        Game game;
        if (!isMultiplayer) {
            game = new GameVsIA(placedShips.toArray(new Ship[0]), aux);
        } else {
            try {
                game = new GameVsPlayer(placedShips.toArray(new Ship[0]), aux);
            } catch (Exception e) {
                e.printStackTrace();
                DisplayErrorAlert("Connection error",
                        "An error occurred while connecting to the " +
                                "server. Please try again.");
                primaryStage.close();
                return;
            }
        }
        InGameAppliController controller = new InGameAppliController(
                primaryStage, game);
        fxmlLoader.setController(controller);
        Scene primaryScene;
        try {
            primaryScene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            DisplayErrorAlert("Interface error",
                    "An error occurred while rendering the panel. " +
                            "Please try again.");
            primaryStage.close();
            return;
        }
        primaryStage.setTitle(Main.WINDOW_TITLE + " : " + aux);
        primaryStage.setScene(primaryScene);
        primaryStage.setResizable(false);
        if (isMultiplayer && game.getPlayer().isRoundPlayer()) {
            primaryStage.hide();
        }


    }

    // OUTILS

    /**
     * Ajoute les contrôleurs pour les éléments de l'interface graphique.
     */
    private void addController() {
        flowPane.setOnMouseReleased(mouseEvent -> {
            for (int i = 0; i < SHIP_NB; ++i) {
                GraphicShip ship = ships.get(i);
                if (ship.isSelected()) {
                    if (ship.isPlaced()) {
                        if (isOverLapping(ship) || isBadLocationOnGrid(ship)) {
                            if (!ship.isHorizontal()) {
                                ship.rotate();
                            }
                            ship.getImageView().setLayoutX(ship.
                                    getStartPositionX());
                            ship.getImageView().setLayoutY(ship.
                                    getStartPositionY());
                            ship.setPlaced(false);
                            placedShips.remove(ship.getModel());
                        } else {
                            placedShips.add(ship.getModel());
                        }
                    } else {
                        placedShips.remove(ship.getModel());
                    }
                }
                ship.setSelected(false);
            }
        });

        flowPane.setOnMousePressed(mouseEvent -> {
            for (int i = 0; i < SHIP_NB; ++i) {
                GraphicShip ship = ships.get(i);
                if (ship.isSelected()) {
                    placedShips.remove(ship.getModel());
                }
            }
        });

        flowPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().toString().equals("R")) {
                for (int i = 0; i < SHIP_NB; ++i) {
                    GraphicShip ship = ships.get(i);
                    if (ship.isSelected()) {
                        ship.rotate();
                    }
                }
            }
        });
        addShipController();
    }

    /**
     * Ajoute les contrôleurs pour les bateaux
     */
    private void addShipController() {
        int StartXGrid = AnchorPane.getLeftAnchor(BoardGrid).intValue();
        int StartYGrid = (int) VboxLabel.getPrefHeight();
        int EndXGrid = StartXGrid + GraphicGrid.GRID_SIZE * graphicGrid.
                getTileSize();
        int EndYGrid = StartYGrid + GraphicGrid.GRID_SIZE * graphicGrid.
                getTileSize();
        for (int j = 0; j < SHIP_NB; j++) {
            GraphicShip ship = ships.get(j);
            ImageView imageView = ship.getImageView();
            imageView.setOnMousePressed(mouseEvent -> {
                ship.setSelected(true);
                if (mouseEvent.getSceneX() < StartXGrid) {
                    ship.setMouseAnchorX(mouseEvent.getX());
                    ship.setMouseAnchorY(mouseEvent.getY() + StartYGrid);
                }
            });

            imageView.setOnMouseDragged(mouseEvent -> {
                if (mouseEvent.getSceneX() > 0 && mouseEvent.getSceneY() > 0
                        && mouseEvent.getSceneY() <= EndYGrid) {
                    double mouseHeight;
                    double mouseWidth;
                    if (ship.isHorizontal()) {
                        mouseHeight = mouseEvent.getSceneY() + ship.getHeight();
                    } else {
                        mouseHeight = mouseEvent.getSceneY() + ship.getHeight()
                                - ship.getWidth();
                    }
                    mouseWidth = mouseEvent.getSceneX() + ship.getWidth();
                    if (mouseEvent.getSceneX() >= StartXGrid && mouseWidth <=
                            EndXGrid && mouseEvent.getSceneY() >= StartYGrid &&
                            mouseHeight <= EndYGrid) {
                        double mouseAnchorX = mouseEvent.getSceneX();
                        double mouseAnchorY = mouseEvent.getSceneY() -
                                StartYGrid;

                        int x = ((int) mouseAnchorX / graphicGrid.getTileSize())
                                * graphicGrid.getTileSize();
                        int y = ((int) mouseAnchorY / graphicGrid.getTileSize())
                                * graphicGrid.getTileSize();
                        if (!ship.isHorizontal() && mouseAnchorY <=
                                graphicGrid.getTileSize()) {
                            imageView.setLayoutX(x + 16);
                        } else {
                            imageView.setLayoutX(x + 16);
                            imageView.setLayoutY(y);
                        }
                    } else {
                        if (mouseEvent.getSceneX() < StartXGrid &&
                                mouseEvent.getSceneY() > StartYGrid &&
                                mouseHeight < EndYGrid) {
                            imageView.setLayoutX(mouseEvent.getSceneX() -
                                    ship.getMouseAnchorX());
                            imageView.setLayoutY(mouseEvent.getSceneY() -
                                    ship.getMouseAnchorY());
                        } else if (mouseEvent.getSceneX() > StartXGrid) {
                            if (mouseWidth > EndXGrid && mouseHeight < EndYGrid
                                    && mouseEvent.getSceneY() >
                                    graphicGrid.getTileSize()) {
                                double mouseAnchorY = mouseEvent.getSceneY() -
                                        StartYGrid;
                                int y = (int) (mouseAnchorY /
                                        graphicGrid.getTileSize()) *
                                        graphicGrid.getTileSize();
                                imageView.setLayoutY(y);
                            } else if (mouseEvent.getSceneY() > StartYGrid &&
                                    mouseWidth < EndXGrid && mouseHeight >=
                                    EndYGrid) {
                                double mouseAnchorX = mouseEvent.getSceneX();
                                int x = (int) (mouseAnchorX /
                                        graphicGrid.getTileSize()) *
                                        graphicGrid.getTileSize();
                                imageView.setLayoutX(x + 16);
                            }
                        }
                    }
                    mouseEvent.consume();
                }
            });

            imageView.setOnMouseReleased(mouseEvent -> {
                if (imageView.getLayoutX() >= StartXGrid &&
                        imageView.getLayoutY() >= 0 &&
                        imageView.getLayoutY() <= EndYGrid) {
                    ship.setPlaced(true);
                    LinkedList<Point2D> coords = new LinkedList<>();
                    for (int i = 0; i < ship.getModel().getLength(); ++i) {
                        int x;
                        int y;
                        if (ship.isHorizontal()) {
                            x = (int) ((imageView.getLayoutX() - StartXGrid) /
                                    graphicGrid.getTileSize()) + i;
                            y = (int) (imageView.getLayoutY() /
                                    graphicGrid.getTileSize());
                        } else {
                            x = (int) ((imageView.getLayoutX() - StartXGrid) /
                                    graphicGrid.getTileSize());
                            y = (int) (imageView.getLayoutY() /
                                    graphicGrid.getTileSize()) + i - 1;
                        }
                        coords.add(new Point2D(x, y));
                    }
                    ship.getModel().setTilesCoords(coords);
                } else {
                    ship.setPlaced(false);
                }

            });
        }
    }

    /**
     * Vérifie si un bateau est en collision avec un autre bateau parmi ceux
     * déjà placé.
     */
    private boolean isOverLapping(GraphicShip ship) {
        for (Point2D point : ship.getModel().getTilesCoords()) {
            for (Ship placedShip : placedShips) {
                if (placedShip.getTilesCoords().contains(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si un bateau est en dehors de la grille de jeu.
     */
    private boolean isBadLocationOnGrid(GraphicShip ship) {
        for (Point2D point : ship.getModel().getTilesCoords()) {
            if (point.getX() < 0 || point.getX() >= GraphicGrid.GRID_SIZE ||
                    point.getY() < 0 || point.getY() >= GraphicGrid.GRID_SIZE) {
                return true;
            }
        }
        return false;
    }

}
