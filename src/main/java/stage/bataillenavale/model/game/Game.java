package stage.bataillenavale.model.game;

import stage.bataillenavale.model.ship.Ship;
import stage.bataillenavale.utils.Contract;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.io.IOException;

/**
 * @inv <pre>
 *     getPlayer() != null
 *     getTime() >= 0 </pre>
 * @cons <pre>
 *     $ARG$ Ship[] playerShips
 *     $PRE$ playerShips != null
 *     $POST$
 *          getPlayer() == new GamePanel(playerShips)
 */
public abstract class Game {
    public static final String PROP_LOSE_GAME = "lose_game";
    public static final String PROP_WIN_GAME = "win_game";
    public static final String PROP_TIMER = "timer";
    // ATTRIBUTS STATIQUES
    protected final int SLEEP_DURATION = 1000;
    // ATTRIBUTS
    protected final GamePanel Player;
    public int TURN_TIME;
    protected int time;
    protected PropertyChangeSupport pcs;

    // CONSTRUCTEUR
    public Game(Ship[] playerShips) {
        Contract.checkCondition(playerShips != null,
                "ERR: Game constructor");
        for (Ship ship : playerShips) {
            Contract.checkCondition(ship != null,
                    "ERR: Game constructor");
        }
        Player = new GamePanel(playerShips);
        pcs = new PropertyChangeSupport(this);
    }

    // REQUÊTES

    /**
     * Renvoie le GamePanel du joueur
     */
    public GamePanel getPlayer() {
        return Player;
    }

    /**
     * Renvoie le temps restant
     */
    public int getTime() {
        return time;
    }

    // COMMANDES

    /**
     * Ajoute un PCL pour la propriété pName.
     * Ne fais rien si lnr a déjà été ajouté.
     *
     * @pre <pre>
     *     pName != null && lnr != null </pre>
     * @post <pre>
     *     lnr a été ajouté à la liste des écouteurs de la propriété pName.
     * </pre>
     */
    public void addPropertyChangeListener(String pName,
                                          PropertyChangeListener lnr) {
        Contract.checkCondition(pName != null && lnr != null,
                "ERR: Game addPropertyChangeListener");
        pcs.addPropertyChangeListener(pName, lnr);
    }


    /**
     * Touche la case adverse de ligne row et colonne col. Met à jour dans son
     * GamePanel les données sur son adversaire.
     *
     * @pre <pre>
     * 		0 <= row && row <= Grid.DIM
     * 		0 <= col && col <= Grid.DIM <\pre>
     * @post <pre>
     * 		getPanel().getgetEnemyGrid()[row][col] == TileState.EMPTY_HIT_TILE
     * 		|| getPanel().getgetEnemyGrid()[row][col] == TileState.SHIP_HIT_TILE
     * 		!isRoundPlayer()
     * @throws PropertyVetoException si la case ne peut pas être touché selon la
     * 			condition mise dans la méthode createVetoableChangeListener().
     * @throws IOException si un problème durant la communication avec le
     * serveur
     * 			est survenue.
     */
    public void hit(int row, int col) throws IOException,
            PropertyVetoException {

    }

    /**
     * Se met en écoute que l'adversaire lui envoie les coordonnées de la case
     * qu'il touche.
     *
     * @post <pre>
     * 		getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.EMPTY_HIT_TILE
     * 		|| getPanel().getPlayerGrid().stateOfTile(row, col) ==
     * 			TileState.SHIP_HIT_TILE
     * 		isRoundPlayer()
     * @throws IOException si un problème durant la communication avec le
     *  serveur est survenue.
     */
    public void getHit() throws IOException {
    }

    /**
     * Tant que la partie n'est pas finie gère le timer d'un tour.
     *
     * @throws IOException si un problème durant la communication avec le
     *                     serveur est survenue.
     */
    public void run() throws IOException {

    }
}
