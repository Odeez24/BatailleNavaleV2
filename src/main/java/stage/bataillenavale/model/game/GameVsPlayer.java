package stage.bataillenavale.model.game;

import javafx.geometry.Point2D;
import stage.bataillenavale.model.ship.Ship;
import stage.bataillenavale.network.BNClient;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;
import stage.bataillenavale.utils.utils;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * @inv <pre>
 *     server != null </pre>
 * @cons <pre>
 *     $ARG$ Ship[] playerShips, String key
 *     $PRE$ playerShips != null && key != null
 *     $POST$
 *     Crée un jeu contre un joueur avec les bateaux playerShips et la clé key
 *     </pre>
 * @throws IOException si un problème durant la communication avec le serveur
 * est survenue.
 * @throws NoSuchAlgorithmException si l'algorithme de cryptage n'est pas
 * disponible
 * @throws KeyManagementException si un problème avec la clé de cryptage est
 * survenue
 */
public class GameVsPlayer extends Game {
    // ATTRIBUTS CONSTANTES
    public static final String PROP_START_GAME = "start_game";
    private final String SEP_BETWEEN_SHIPS;
    private final String SEP_BETWEEN_COORDS;
    private final String SEP_BETWEEN_XY;
    private final String SEP;
    private final BNClient server;
    // ATTRIBUTS
    private boolean isFinished;

    // CONSTRUCTEUR
    public GameVsPlayer(Ship[] playerShips, String key)
            throws IOException, NoSuchAlgorithmException,
            KeyManagementException {
        super(playerShips);
        Contract.checkCondition(key != null,
                "ERR: GameVsPlayer constructor");
        server = new BNClient();
        server.sendData("SEP");
        SEP = server.getData();
        server.sendData("SEP_BETWEEN_SHIPS");
        SEP_BETWEEN_SHIPS = server.getData();
        server.sendData("SEP_BETWEEN_COORDS");
        SEP_BETWEEN_COORDS = server.getData();
        server.sendData("SEP_BETWEEN_XY");
        SEP_BETWEEN_XY = server.getData();
        server.sendData("TURN_TIME");
        TURN_TIME = Integer.parseInt(server.getData());
        server.sendData("INIT:" + key);
        String s = server.getData();
        StringTokenizer st = new StringTokenizer(s, SEP);
        if (!st.nextToken().equals("S")) {
            throw new IOException("Error in the connection");
        }
        String rep = st.nextToken();
        if (rep.equals("ERR")) {
            throw utils.checkServerError(st.nextToken());
        } else if (rep.equals("PENDING")) {
            getPlayer().SetRoundPlayer(true);
            //server.getData();
        }
        server.sendData("SEND" + SEP + ShipToString(playerShips));
        isFinished = false;
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
     * @throws IOException si un problème durant la communication avec le serveur
     * 			est survenue.
     */
    @Override
    public void hit(int row, int col) throws IOException, PropertyVetoException {
        server.sendData("SEND" + SEP + row + "-" + col);
        String s = server.getData();
        StringTokenizer st = new StringTokenizer(s, SEP);
        if (!st.nextToken().equals("S")) {
            throw new IOException("Error in the connection");
        }
        String rep = st.nextToken();
        if (rep.equals("ERR")) {
            throw utils.checkServerError(st.nextToken());
        }
        TileState state = TileState.EMPTY_TILE;
        switch (rep) {
            case "HIT":
                state = TileState.SHIP_HIT_TILE;
                break;
            case "SUNK":
                state = TileState.SHIP_HIT_TILE;
                Player.setEnemyShip(Player.getNbEnemyShip() - 1);
                break;
            case "MISS":
                state = TileState.EMPTY_HIT_TILE;
                break;
            case "CANT_HIT":
                throw new PropertyVetoException("Can't hit", null);
            case "PLAY":
                Player.SetRoundPlayer(true);
                return;
        }
        StringTokenizer str = new StringTokenizer(st.nextToken(), SEP_BETWEEN_XY);
        row = Integer.parseInt(str.nextToken());
        col = Integer.parseInt(str.nextToken());
        Player.playerHitEnnemy(row, col, state);
        Player.SetRoundPlayer(false);
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
     * @throws IOException si un problème durant la communication avec le serveur
     * 			est survenue.
     */
    @Override
    public void getHit() throws IOException {
        StringTokenizer st = new StringTokenizer(server.getData(), SEP);
        if (!st.nextToken().equals("S")) {
            throw new IOException("Error in the connection");
        }
        String rep = st.nextToken();
        if (rep.equals("ERR")) {
            throw utils.checkServerError(st.nextToken());
        }
        if (rep.equals("CHANGE_TURN")) {
            Player.SetRoundPlayer(true);
            return;
        }
        StringTokenizer st1 = new StringTokenizer(rep, SEP_BETWEEN_XY);
        int row = Integer.parseInt(st1.nextToken());
        int col = Integer.parseInt(st1.nextToken());
        try {
            Player.ennemyHitPlayer(row, col);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        Player.SetRoundPlayer(true);
    }

    /**
     * Tant que la partie n'est pas finie gère le timer d'un tour ainsi que
     * si c'est le tour du jour et la fin de partie en écoutant le serveur.
     *
     * @throws IOException si un problème durant la communication avec le serveur
     *                     est survenue.
     */
    @Override
    public void run() throws IOException {
        if (Player.isRoundPlayer()) {
            server.getData();
            pcs.firePropertyChange(PROP_START_GAME, null, null);
        }
        time = TURN_TIME;
        while (!isFinished) {
            StringTokenizer st = new StringTokenizer(server.getData(), SEP);
            if (!st.nextToken().equals("S")) {
                throw new IOException("Error in the connection");
            }
            String rep = st.nextToken();
            if (rep.equals("ERR")) {
                throw utils.checkServerError(st.nextToken());
            }
            switch (rep) {
                case "LOSE":
                    isFinished = true;
                    pcs.firePropertyChange(PROP_LOSE_GAME, null,
                            null);
                    return;
                case "WIN":
                    isFinished = true;
                    pcs.firePropertyChange(PROP_WIN_GAME, null,
                            null);
                    return;
                case "PLAY":
                    Player.SetRoundPlayer(true);
                    break;
                case "WAIT":
                    Player.SetRoundPlayer(false);
                    break;
            }
            if (!Player.isRoundPlayer()) {
                time = TURN_TIME;
                pcs.firePropertyChange(PROP_TIMER, null, time);
                getHit();
            } else {
                while (Player.isRoundPlayer()) {
                    time -= 1;
                    pcs.firePropertyChange(PROP_TIMER, null, time);
                    if (time == 0) {
                        server.sendData("SEND" + SEP + "CHANGE_TURN");
                        Player.SetRoundPlayer(false);
                    } else {
                        try {
                            Thread.sleep(SLEEP_DURATION);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        server.terminateConnection();
    }

    // OUTILS

    /**
     * Transforme les coordonnées des bateaux en une chaine de caractère pour
     * les envoyer au serveur. En utilisant les séparateurs définis par le
     * serveur lors de l'initialisation.
     *
     * @pre <pre>
     *      ships != null </pre>
     * @post <pre>
     *      retourne une chaine de caractère contenant les coordonnées des
     *      bateaux de ships séparés par les séparateurs définis par le
     *      serveur. </pre>
     */
    private String ShipToString(Ship[] ships) {
        Contract.checkCondition(ships != null,
                "ERR: ShipToString");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ships.length; i++) {
            Ship ship = ships[i];
            LinkedList<Point2D> coords = (LinkedList<Point2D>)
                    ship.getTilesCoords();
            for (int j = 0; j < coords.size(); j++) {
                sb.append((int) coords.get(j).getX())
                        .append(SEP_BETWEEN_XY).append((int)
                                coords.get(j).getY());
                if (j != coords.size() - 1) {
                    sb.append(SEP_BETWEEN_COORDS);
                }
            }
            if (i != ships.length - 1) {
                sb.append(SEP_BETWEEN_SHIPS);
            }
        }
        return sb.toString();
    }


}
