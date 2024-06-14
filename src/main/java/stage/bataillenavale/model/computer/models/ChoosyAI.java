package stage.bataillenavale.model.computer.models;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Random;

public class ChoosyAI extends AIModel {

    public static final int PROBABILITY_NUM = 6;
    public static final int PROBABILITY_DENOM = 10;
    /***
     * Fonctionnement de l'IA : liste l'ensemble des points qui n'ont pas encore été touchés,
     * puis choisit soit points à proximié des coordonées réussis, mais dans de rares cas, peut
     * aussi se tromper et choisir un point aléatoire.
     * @return
     * Point
     */
    @Override
    public Point2D chooseCoordinates() {
        ArrayList<Point2D> possibleTargets = getAllEmptyTiles();
        ArrayList<Point2D> priorityTargets = getAllNearbyTiles();
        Point2D ret;
        Random rand = new Random();
        if (priorityTargets.isEmpty() || rand.nextInt() % PROBABILITY_DENOM > PROBABILITY_NUM) {
            ret = possibleTargets.get(rand.nextInt(possibleTargets.size()));
        } else {
            ret = priorityTargets.get(rand.nextInt(priorityTargets.size()));
        }
        return ret;
    }
}
