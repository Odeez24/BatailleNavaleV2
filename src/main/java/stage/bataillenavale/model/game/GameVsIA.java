package stage.bataillenavale.model.game;

import javafx.geometry.Point2D;
import stage.bataillenavale.model.computer.AIPlayer;
import stage.bataillenavale.model.computer.models.AIModel;
import stage.bataillenavale.model.computer.models.AdvancedAI;
import stage.bataillenavale.model.computer.models.ChoosyAI;
import stage.bataillenavale.model.computer.models.StupidAI;
import stage.bataillenavale.model.ship.Ship;
import stage.bataillenavale.server.model.grid.Grid;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

/**
 * @inv <pre>
 *     getAI() != null </pre>
 * @cons <pre>
 *     $ARG$ Ship[] playerShips, String difficulty
 *     $PRE$ playerShips != null && difficulty != null
 *     $POST$
 *          Crée un jeu contre l'IA avec les bateaux playerShips et la
 *              difficulté difficulty
 */
public class GameVsIA extends Game {
    // ATTRIBUT
    private final AIPlayer AI;

    // CONSTRUCTEUR
    public GameVsIA(Ship[] playerShips, String difficulty) {
        super(playerShips);
        Contract.checkCondition(difficulty != null,
                "ERR: GameVsIA constructor");
        AIModel aiModel = switch (difficulty) {
            case "EASY" -> new StupidAI();
            case "MEDIUM" -> new ChoosyAI();
            case "HARD" -> new AdvancedAI();
            default -> null;
            // Création du model de l'IA
        };
        this.AI = new AIPlayer(aiModel);
        AI.getPanel().SetRoundPlayer(false);
        Player.SetRoundPlayer(true);
        TURN_TIME = 120;
        // Initialisation des VetoableChangeListener
        setVetoChangeListener();
    }

    // COMMANDES

    /**
     * Se met en écoute que l'adversaire lui envoie les coordonnées de la case
     * qu'il touche. Puis passe son tour
     *
     * @post <pre>
     * 		getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.EMPTY_HIT_TILE
     * 		|| getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.SHIP_HIT_TILE
     * 		isRoundPlayer()
     * @throws PropertyVetoException si la case ne peut pas être touché selon la
     * 			condition mise dans la méthode createVetoableChangeListener().
     */
    @Override
    public void hit(int row, int col) throws PropertyVetoException {
        Contract.checkCondition(row >= 0 && row < Grid.DIM &&
                col >= 0 && col < Grid.DIM, "ERR: GameVsIA.hit");
        // Récupération nombre de bateaux ennemis avant le tir
        int nbShip = AI.getPanel().getPlayerGrid().getNbShip();
        TileState state = AI.getHit(row, col);
        if (AI.getPanel().getPlayerGrid().getNbShip() != nbShip) {
            Player.setEnemyShip(Player.getNbEnemyShip() - 1);
        }
        Player.playerHitEnnemy(row, col, state);
        Player.SetRoundPlayer(false);
        AI.getPanel().SetRoundPlayer(true);
    }

    /**
     * Se met en écoute que l'adversaire lui envoie les coordonnées de la case
     * qu'il touche. Puis prend le tour
     *
     * @post <pre>
     * 		getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.EMPTY_HIT_TILE
     * 		|| getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.SHIP_HIT_TILE
     * 		isRoundPlayer()
     */
    @Override
    public void getHit() {
        int row = 0;
        int col = 0;
        TileState val = null;
        while (val == null) {
            // Demande à l'IA de choisir les coordonées où elle tire.
            Point2D lastHit = AI.chooseCoords();
            row = (int) lastHit.getX();
            col = (int) lastHit.getY();
            try {
                // Touche la case choisie par l'IA.
                val = Player.ennemyHitPlayer(row, col);
            } catch (PropertyVetoException e) {
                throw new RuntimeException(e);
            }
        }
        AI.awaitResponse(row, col, val);
        // Prend le tour.
        Player.SetRoundPlayer(true);
        AI.getPanel().SetRoundPlayer(false);
    }

    /**
     * Tant que la partie n'est pas finie gère le timer d'un tour.
     */
    @Override
    public void run() {
        time = TURN_TIME;
        // Vérification fin de partie
        while (!Player.playerLose() && !AI.getPanel().playerLose()) {
            if (!Player.isRoundPlayer()) {
                try {
                    Thread.sleep(2 * SLEEP_DURATION);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // Tour adverse
                time = TURN_TIME;
                pcs.firePropertyChange(Game.PROP_TIMER, null, time);
                getHit();
            } else {
                // Tour du joueur
                time -= 1;
                if (time == 0) {
                    Player.SetRoundPlayer(false);
                    AI.getPanel().SetRoundPlayer(true);
                }
            }
            try {
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pcs.firePropertyChange(Game.PROP_TIMER, null, time);
        }
        if (Player.playerLose()) {
            pcs.firePropertyChange(Game.PROP_LOSE_GAME, null,
                    null);
        } else {
            pcs.firePropertyChange(Game.PROP_WIN_GAME, null,
                    null);
        }
    }

    // OUTILS

    /**
     * Ajoute l'écouteur à veto défini par la méthode
     * createVetoableChangeListener() à la grille du joueur1 et du joueur2
     */
    private void setVetoChangeListener() {
        Player.getPlayerGrid().addVetoableChangeListener(Grid.PROP_GRID,
                createVetoableChangeListener());
        AI.getPanel().getPlayerGrid().addVetoableChangeListener(Grid.PROP_GRID,
                createVetoableChangeListener());

    }

    /**
     * Renvoie un écouteur à veto pour la grille de joueur lorsque l'on touche
     * une case de la grille.
     */
    private VetoableChangeListener createVetoableChangeListener() {
        return new VetoableChangeListener() {
            @Override
            public void vetoableChange(PropertyChangeEvent evt)
                    throws PropertyVetoException {
                if (accept(evt)) {
                    throw new PropertyVetoException("Case déjà touché", evt);
                }
            }

            private boolean accept(PropertyChangeEvent evt) {
                TileState state = (TileState) evt.getOldValue();
                return state == TileState.EMPTY_HIT_TILE ||
                        state == TileState.SHIP_HIT_TILE;
            }
        };
    }
}
