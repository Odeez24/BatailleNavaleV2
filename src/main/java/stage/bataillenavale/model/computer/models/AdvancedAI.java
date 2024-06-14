package stage.bataillenavale.model.computer.models;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Random;

public class AdvancedAI extends AIModel {
    /***
     * Fonctionnement de l'IA : liste l'ensemble des points qui n'ont pas encore été touchés,
     * puis choisit en priorité les points à proximité des coordonées réussis.
     * @return
     * Point
     */
    @Override
    public Point2D chooseCoordinates() {
        ArrayList<Point2D> priorityTargets = getAllNearbyTiles();
        ArrayList<Point2D> possibleTargets = getAllEmptyTiles();
        Point2D ret;
        Random rand = new Random();
        if (priorityTargets.isEmpty()) {
            ret = possibleTargets.get(rand.nextInt(possibleTargets.size()));
        } else {
            ret = priorityTargets.get(rand.nextInt(priorityTargets.size()));
        }
        return ret;
    }
}
