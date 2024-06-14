package stage.bataillenavale.model.grid;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import stage.bataillenavale.Main;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

/**
 * @inv <pre>
 *     getTileSize() > 0
 *     getInterneButton() != null </pre>
 * @cons <pre>
 *      $ARGS$ GridPane gridPane: la grille dans laquelle on va ajouter les
 *              éléments
 *             int tileSize: taille en pixel d'une case de la grille
 *             boolean TileIsButton: true si on veut des boutons, false si on
 *                  veut des AnchorPane comme noeuds dans la grille.
 *      $PRE$
 *          gridPane != null && tileSize > 0
 *      $POST$
 *          Crée une grille de taille 10x10 avec des boutons ou des AnchorPane
 */
public class GraphicGrid extends Node {
    // ATTRIBUTS STATIQUES
    public static final int GRID_SIZE = 10;

    // ATTRIBUTS
    private final AnchorPane[][] gridNoButton;
    private final Button[][] gridButton;
    private final int tileSize;
    private final boolean TileIsButton;

    // CONSTRUCTEUR
    public GraphicGrid(GridPane gridPane, int tileSize, boolean TileIsButton) {
        Contract.checkCondition(gridPane != null && tileSize > 0,
                "Error on GraphicGrid: attributs not right");
        gridNoButton = new AnchorPane[GRID_SIZE][GRID_SIZE];
        gridButton = new Button[GRID_SIZE][GRID_SIZE];
        this.TileIsButton = TileIsButton;
        this.tileSize = tileSize;
        Image image = new Image(Main.class.getResource(
                "image/water.jpg").toString(), tileSize, tileSize,
                false, false);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (TileIsButton) {
                    gridButton[i][j] = new Button();
                    gridButton[i][j].setBackground(new Background(new
                            BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
                    gridButton[i][j].setPrefHeight(tileSize);
                    gridButton[i][j].setPrefWidth(tileSize);
                    gridButton[i][j].setBorder(new Border(new BorderStroke(
                            Color.BLACK, BorderStrokeStyle.SOLID,
                            CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    gridPane.add(gridButton[i][j], i, j);
                } else {
                    gridNoButton[i][j] = new AnchorPane();
                    gridNoButton[i][j].setMaxHeight(tileSize);
                    gridNoButton[i][j].setMaxWidth(tileSize);
                    gridNoButton[i][j].setMinHeight(tileSize);
                    gridNoButton[i][j].setMinWidth(tileSize);
                    gridNoButton[i][j].setPrefHeight(tileSize);
                    gridNoButton[i][j].setPrefWidth(tileSize);
                    gridNoButton[i][j].setBackground(new Background(new
                            BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.DEFAULT,
                            BackgroundSize.DEFAULT)));
                    //pane.getChildren().addAll(water[i][j]);
                    gridNoButton[i][j].setBorder(new Border(new BorderStroke(
                            Color.BLACK, BorderStrokeStyle.SOLID,
                            CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    gridPane.add(gridNoButton[i][j], i, j);
                    //gridPane.add(water[i][j], i, j);
                }
            }
        }
    }

    // REQUÊTES

    /**
     * Retourne la taille d'une case de la grille
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * Retourne la grille de boutons
     */
    public Button[][] getInterneButton() {
        return gridButton;
    }

    // COMMANDES

    /**
     * Met à jour la grille avec les nouvelles valeurs selon une matrice de
     * TileState
     *
     * @pre <pre>
     *     tab != null && tab.length == GRID_SIZE && tab[0].length == GRID_SIZE
     * </pre>
     */
    public void updateGrid(TileState[][] tab) {
        Contract.checkCondition(tab != null && tab.length == GRID_SIZE &&
                tab[0].length == GRID_SIZE, "Error on GraphicGrid: tab not right");
        Image water = new Image(Main.class.getResource(
                "image/water.jpg").toString(), tileSize, tileSize,
                false, false);
        Image waterSplash = new Image(Main.class.getResource(
                "image/watersplash.jpg").toString(), tileSize, tileSize,
                false, false);
        Image waterExplosion = new Image(Main.class.getResource(
                "image/waterexplosion.jpg").toString(), tileSize, tileSize,
                false, false);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (tab[i][j] == TileState.EMPTY_TILE) {
                    setImage(water, i, j);
                } else if (tab[i][j] == TileState.EMPTY_HIT_TILE) {
                    setImage(waterSplash, i, j);
                } else if (tab[i][j] == TileState.SHIP_HIT_TILE) {
                    setImage(waterExplosion, i, j);
                }
            }
        }
    }

    // OUTIL

    /**
     * Met à jour l'image de la case de coordonnées i, j semon si c'est une
     * grille de boutons ou non
     */
    private void setImage(Image image, int i, int j) {
        if (TileIsButton) {
            gridButton[i][j].setBackground(new Background(new BackgroundImage(
                    image, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));
        } else {
            gridNoButton[i][j].setBackground(new Background(new BackgroundImage(
                    image, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT)));
        }
    }
}
