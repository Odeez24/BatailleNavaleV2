package stage.bataillenavale.model.computer.models;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class StupidAI extends AIModel {
    /***
     * Fonctionnement de l'IA : liste l'ensemble des points qui n'ont pas encore été touchés,
     * puis en choisit un au hasard.
     * @return
     * Point
     */
    @Override
    public Point2D chooseCoordinates() {
        ArrayList<Point2D> possibleTargets = getAllEmptyTiles();
        return possibleTargets.get(new Random().nextInt(possibleTargets.size()));
    }
}
