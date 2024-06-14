package stage.bataillenavale.GraphicGrid;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import stage.bataillenavale.Main;
import stage.bataillenavale.utils.TileState;

public class GraphicGrid extends Node{
    
    public static final int GRID_SIZE = 10;

    private final AnchorPane[][] gridnobutton;
    private final Button[][] gridbutton;
    private final int tileSize;
    private final boolean TileIsButton;

    public GraphicGrid(GridPane gridPane, int tileSize, boolean TileIsButton) {
        gridnobutton = new AnchorPane[GRID_SIZE][GRID_SIZE];
        gridbutton = new Button[GRID_SIZE][GRID_SIZE];
        this.TileIsButton = TileIsButton;
        this.tileSize = tileSize;
        Image image = new Image(Main.class.getResource("image/water.jpg").toString(), tileSize, tileSize, false, false);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (TileIsButton) {
                    gridbutton[i][j] = new Button();
                    gridbutton[i][j].setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
                    gridbutton[i][j].setPrefHeight(tileSize);
                    gridbutton[i][j].setPrefWidth(tileSize);
                    gridbutton[i][j].setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    gridPane.add(gridbutton[i][j], i, j);
                } else {
                    gridnobutton[i][j] = new AnchorPane();
                    gridnobutton[i][j].setMaxHeight(tileSize);
                    gridnobutton[i][j].setMaxWidth(tileSize);
                    gridnobutton[i][j].setMinHeight(tileSize);
                    gridnobutton[i][j].setMinWidth(tileSize);
                    gridnobutton[i][j].setPrefHeight(tileSize);
                    gridnobutton[i][j].setPrefWidth(tileSize);
                    gridnobutton[i][j].setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
                    //pane.getChildren().addAll(water[i][j]);
                    gridnobutton[i][j].setBorder(new Border(new BorderStroke(javafx.scene.paint.Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                    gridPane.add(gridnobutton[i][j], i, j);
                    //gridPane.add(water[i][j], i, j);
                }
            }
        }

    }

    public int getTileSize() {
        return tileSize;
    }

    public Button[][] getInterneButton() {
        return gridbutton;
    }

    public void updateGrid(TileState [][] tab) {
        Image water = new Image(Main.class.getResource("image/water.jpg").toString(), tileSize, tileSize, false, false);
        Image  waterSplash = new Image(Main.class.getResource("image/watersplash.jpg").toString(), tileSize, tileSize, false, false);
        Image waterExplosion = new Image(Main.class.getResource("image/waterexplosion.jpg").toString(), tileSize, tileSize, false, false);
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

    private void setImage (Image image, int i, int j) {
        if (TileIsButton) {
            gridbutton[i][j].setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        } else {
            gridnobutton[i][j].setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        }
    }
}
