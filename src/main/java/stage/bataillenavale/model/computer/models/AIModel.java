package stage.bataillenavale.model.computer.models;

import javafx.geometry.Point2D;
import stage.bataillenavale.utils.TileState;

import java.util.ArrayList;
/**
 * Modèle de l'IA
 * @cons <pre>
 *     $ARGS$ TileState[][] enemyGrid
 *     $PRE$
 *     $POST$ enemy = enemyGrid
 *     </pre>
 * @cons <pre>
 *     $ARGS$
 *     $PRE$
 *     $POST$ enemy = null
 */
public abstract class AIModel {
    // ATTRIBUTS
    protected TileState[][] enemy;

    public AIModel() {
        this(null);
    }

    public AIModel(TileState[][] enemyGrid) {
        this.enemy = enemyGrid;
    }

    // REQUÊTES
    /**
     * Choisit les coordonnées de la prochaine attaque
     */
    public abstract Point2D chooseCoordinates();

    /**
     * Renvoie la liste des cases vides
     */
    protected ArrayList<Point2D> getAllEmptyTiles() {
        ArrayList<Point2D> possibleTargets = new ArrayList<>();
        for (int x = 0; x < enemy.length; x += 1) {
            for (int y = 0; y < enemy[x].length; y += 1) {
                if (enemy[x][y] == TileState.EMPTY_TILE) {
                    possibleTargets.add(new Point2D(x, y));
                }
            }
        }

        return possibleTargets;
    }

    /**
     * Renvoie la liste des cases adjacentes à une case touchée
     */
    protected ArrayList<Point2D> getAllNearbyTiles() {
        ArrayList<Point2D> priorityTargets = new ArrayList<>();
        for (int x = 0; x < enemy.length; x += 1) {
            for (int y = 0; y < enemy[x].length; y += 1) {
                if (enemy[x][y] == TileState.SHIP_HIT_TILE) {
                    Point2D[] poss = new Point2D[]{
                            new Point2D(x + 1, y),
                            new Point2D(x - 1, y),
                            new Point2D(x, y + 1),
                            new Point2D(x, y - 1),
                    };
                    for (Point2D p : poss) {
                        if (checkIfPositionValid(p)
                                && enemy[(int) p.getX()][(int) p.getY()] == TileState.EMPTY_TILE) {
                            priorityTargets.add(p);
                        }
                    }
                }
            }
        }
        return priorityTargets;
    }

    /**
     * Vérifie si la position est valide
     */
    protected boolean checkIfPositionValid(int x, int y) {
        int sz = enemy.length;
        return x >= 0 && x < sz && y >= 0 && y < sz;
    }

    /**
     * Vérifie si la position est valide
     */
    protected boolean checkIfPositionValid(Point2D p) {
        assert (p != null);
        return checkIfPositionValid((int) p.getX(), (int) p.getY());
    }

    // COMMANDE

    /**
     * Modifie la grille de l'ennemi
     */
    public void setEnemyGrid(TileState[][] grid) {
        assert grid != null;
        enemy = grid;
    }
}