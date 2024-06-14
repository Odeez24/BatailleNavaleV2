package stage.bataillenavale.server;

import javafx.geometry.Point2D;
import stage.bataillenavale.server.model.GamePanel;
import stage.bataillenavale.server.model.Ship.Ship;
import stage.bataillenavale.server.model.Ship.StdShip;
import stage.bataillenavale.server.model.UtilServer;
import stage.bataillenavale.server.model.grid.Grid;
import stage.bataillenavale.utils.TileState;

import javax.net.ssl.SSLSocket;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe qui gère une partie entre deux joueurs
 *
 * @inv <pre>
 *     gamePanelPlayer1 != null
 *     gamePanelPlayer2 != null
 *     player1Reader != null
 *     player2Reader != null
 *     player1Writer != null
 *     player2Writer != null
 *     playerSocket1 != null
 *     playerSocket2 != null </pre>
 * @cons <pre>
 *     $ARGS$ SSLSocket playerSocket1, SSLSocket playerSocket2
 *     $PRE$
 *     $POST$
 *          Crée les écouteurs et écrivains pour chaque joueur puis initialise
 *          les grilles de jeu des deux joueurs avec les bateaux qui sont
 *          envoyés par chaque joueur.
 * @throws
 *      IOException si une erreur survient lors de la création des
 */
public class ServerGame {
    // ATTRIBUTS
    private final SSLSocket playerSocket1;
    private final SSLSocket playerSocket2;
    private final BufferedReader player1Reader;
    private final BufferedReader player2Reader;
    private final PrintWriter player1Writer;
    private final PrintWriter player2Writer;
    private final GamePanel gamePanelPlayer1;
    private final GamePanel gamePanelPlayer2;

    // CONSTRUCTEUR
    public ServerGame(SSLSocket playerSocket1, SSLSocket playerSocket2)
            throws IOException {
        this.playerSocket1 = playerSocket1;
        this.playerSocket2 = playerSocket2;
        player1Reader = new BufferedReader(new InputStreamReader(
                playerSocket1.getInputStream()));
        player2Reader = new BufferedReader(new InputStreamReader(
                playerSocket2.getInputStream()));
        player1Writer = new PrintWriter(new OutputStreamWriter(
                playerSocket1.getOutputStream()));
        player2Writer = new PrintWriter(new OutputStreamWriter(
                playerSocket2.getOutputStream()));
        String ShipPlayer1 = UtilServer.ContentInMessage(
                player1Reader.readLine());
        String ShipPlayer2 = UtilServer.ContentInMessage(
                player2Reader.readLine());
        gamePanelPlayer1 = new GamePanel(getListShip(ShipPlayer1));
        gamePanelPlayer2 = new GamePanel(getListShip(ShipPlayer2));
        gamePanelPlayer2.SetRoundPlayer(true);
        // Initialisation des VetoableChangeListener
        setVetoChangeListener();
    }

    // COMMANDES

    /**
     * Démarre la partie entre les deux joueurs, s'occupe de la gestion du
     * tour par tour entre les deux joueurs. Jusqu'à ce qu'un des deux
     * joueurs perdent.
     *
     * @throws IOException si une erreur survient lors de la lecture ou de
     *                     l'écriture des données.
     */
    public void startGame() throws IOException {
        while (!gamePanelPlayer1.playerLose() && !gamePanelPlayer2.playerLose()) {
            if (gamePanelPlayer1.isRoundPlayer()) {
                UtilServer.sendData(player1Writer, "S" +
                        UtilServer.SEP + "PLAY");
                UtilServer.sendData(player2Writer, "S" +
                        UtilServer.SEP + "WAIT");
                OneTurn(player1Writer, player2Writer, player1Reader,
                        gamePanelPlayer1, gamePanelPlayer2);
            } else {
                UtilServer.sendData(player2Writer, "S" +
                        UtilServer.SEP + "PLAY");
                UtilServer.sendData(player1Writer, "S" +
                        UtilServer.SEP + "WAIT");
                OneTurn(player2Writer, player1Writer, player2Reader,
                        gamePanelPlayer2, gamePanelPlayer1);
            }
        }
        if (gamePanelPlayer1.playerLose()) {
            UtilServer.sendData(player1Writer, "S" +
                    UtilServer.SEP + "LOSE");
            UtilServer.sendData(player2Writer, "S" +
                    UtilServer.SEP + "WIN");
        } else {
            UtilServer.sendData(player1Writer, "S" +
                    UtilServer.SEP + "WIN");
            UtilServer.sendData(player2Writer, "S" +
                    UtilServer.SEP + "LOSE");
        }
        endGame();
    }

    // OUTILS

    /**
     * Gère un tour de jeu entre les deux joueurs.
     *
     * @param TurnPlayerWriter      l'écrivain du joueur qui joue
     * @param TargetPlayerWriter    l'écrivain du joueur qui subit l'attaque
     * @param TurnPlayerReader      le lecteur du joueur qui joue
     * @param TurnPlayerGamePanel   le GamePanel du joueur qui joue
     * @param TargetPlayerGamePanel le GamePanel du joueur qui subit l'attaque
     * @throws IOException si une erreur survient lors de la lecture ou de
     *                     l'écriture des données.
     */
    private void OneTurn(PrintWriter TurnPlayerWriter, PrintWriter
            TargetPlayerWriter, BufferedReader
                                 TurnPlayerReader, GamePanel TurnPlayerGamePanel,
                         GamePanel TargetPlayerGamePanel) throws IOException {
        String message = TurnPlayerReader.readLine();
        while (UtilServer.checkAskSeparator(TurnPlayerWriter, message)) {
            message = TurnPlayerReader.readLine();
        }
        System.out.println("TurnPlayer " + message);
        String coord = UtilServer.ContentInMessage(message);
        if (coord.equals("CHANGE_TURN")) {
            TurnPlayerGamePanel.SetRoundPlayer(false);
            TargetPlayerGamePanel.SetRoundPlayer(true);
            return;
        }
        Matcher m = Pattern.compile("[0-9]-[0-9]").matcher(coord);
        if (!m.matches()) {
            UtilServer.sendData(TurnPlayerWriter, "S:ERR:BADARGS");
        }
        StringTokenizer st = new StringTokenizer(coord, "-");
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        TileState state;
        int currentnbship = TargetPlayerGamePanel.getPlayerGrid().getNbShip();
        try {
            state = TargetPlayerGamePanel.ennemyHitPlayer(x, y);
        } catch (PropertyVetoException e) {
            UtilServer.sendData(TurnPlayerWriter, "S" +
                    UtilServer.SEP + "CANT_HIT");
            startGame();
            OneTurn(TurnPlayerWriter, TargetPlayerWriter, TurnPlayerReader,
                    TurnPlayerGamePanel, TargetPlayerGamePanel);
            return;
        }
        String s;
        if (state == TileState.SHIP_HIT_TILE) {
            if (currentnbship !=
                    TargetPlayerGamePanel.getPlayerGrid().getNbShip()) {
                s = "S" + UtilServer.SEP + "SUNK" + UtilServer.SEP + x + "-" + y;
            } else {
                s = "S" + UtilServer.SEP + "HIT" + UtilServer.SEP + x + "-" + y;
            }
        } else {
            s = "S" + UtilServer.SEP + "MISS" + UtilServer.SEP + x + "-" + y;
        }
        System.out.println("TurnPlayer " + s);
        UtilServer.sendData(TurnPlayerWriter, s);
        System.out.println("TargetPlayer S " + UtilServer.SEP + x + "-" + y);
        UtilServer.sendData(TargetPlayerWriter, "S" +
                UtilServer.SEP + x + "-" + y);
        TurnPlayerGamePanel.playerHitEnnemy(x, y, state);
        TurnPlayerGamePanel.SetRoundPlayer(false);
        TargetPlayerGamePanel.SetRoundPlayer(true);
    }

    /**
     * Ferme les flux et les sockets des deux joueurs.
     */
    private void endGame() {
        try {
            player1Reader.close();
            player2Reader.close();
            player1Writer.close();
            player2Writer.close();
            playerSocket1.close();
            playerSocket2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Renvoie un tableau de bateaux à partir d'une chaîne de caractères
     *
     * @param ShipPlayer la chaîne de caractères qui contient les bateaux
     */
    private Ship[] getListShip(String ShipPlayer) {
        StringTokenizer st = new StringTokenizer(ShipPlayer,
                UtilServer.SEP_BETWEEN_SHIPS);
        Ship[] ships = new Ship[st.countTokens()];
        for (int i = 0; i < ships.length; i++) {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(),
                    UtilServer.SEP_BETWEEN_COORDS);
            Ship ship = new StdShip(st2.countTokens());
            List<Point2D> coords = new LinkedList<>();
            while (st2.hasMoreTokens()) {
                StringTokenizer st3 = new StringTokenizer(st2.nextToken(),
                        UtilServer.SEP_BETWEEN_XY);
                int x = Integer.parseInt(st3.nextToken());
                int y = Integer.parseInt(st3.nextToken());
                coords.add(new Point2D(x, y));
            }
            ship.setTilesCoords(coords);
            ships[i] = ship;
        }
        return ships;
    }

    /**
     * Ajoute l'écouteur à veto défini par la méthode
     * createVetoableChangeListener() à la grille du joueur1 et du joueur2
     */
    private void setVetoChangeListener() {
        gamePanelPlayer1.getPlayerGrid().addVetoableChangeListener(
                Grid.PROP_GRID, createVetoableChangeListener());
        gamePanelPlayer2.getPlayerGrid().addVetoableChangeListener(
                Grid.PROP_GRID, createVetoableChangeListener());

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
