package stage.bataillenavale.model.computer;

import javafx.geometry.Point2D;
import stage.bataillenavale.model.computer.models.AIModel;
import stage.bataillenavale.model.game.GamePanel;
import stage.bataillenavale.model.grid.StdGrid;
import stage.bataillenavale.utils.TileState;

import java.beans.PropertyVetoException;

/**
 * Représente un joueur IA
 * @inv <pre
 *      panel != null
 *      model != null </pre>
 * @cons <pre>
 *     $ARG$ AIModel model
 *     $POST$ Un nouveau joueur IA est créé avec le modèle model </pre>
 */
public class AIPlayer {
    // ATTRIBUTS
    private final GamePanel panel;
    private final AIModel model;

    public AIPlayer(AIModel model) {
        ShipPlacer placer = new ShipPlacer(StdGrid.DIM);
        placer.placeShips();
        this.panel = new GamePanel(placer.getList());
        this.model = model;
        this.model.setEnemyGrid(this.panel.getEnemyGrid());
    }

    // REQUÊTES
    /**
     * Renvoie le panel du joueur
     */
    public GamePanel getPanel() {
        return this.panel;
    }

    /**
     * Renvoie une coordonnée choisie par l'IA
     */
    public Point2D chooseCoords() {
        return model.chooseCoordinates();
    }

    // COMMANDES

    /**
     * Renvoie l'état de la case touchée par l'IA
     * @throws PropertyVetoException si la case est déjà touchée
     */
    public TileState getHit(int row, int col) throws PropertyVetoException {
        panel.ennemyHitPlayer(row, col);
        return panel.getPlayerGrid().stateOfTile(row, col);
    }
    /**
     * Attend la réponse de l'adversaire et met à jour le panel du joueur IA
     */
    public void awaitResponse(int row, int col, TileState state) {
        panel.playerHitEnnemy(row, col, state);
    }

}
