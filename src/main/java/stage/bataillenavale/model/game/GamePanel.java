package stage.bataillenavale.model.game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;

import stage.bataillenavale.model.grid.Grid;
import stage.bataillenavale.model.grid.StdGrid;
import stage.bataillenavale.model.ship.Ship;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

/**
 * Gestion d'un partie locale du point de vue d'un joueur
 * @inv <pre>
 * 		getPlayerGrid() != null
 * 		getEnemyGrid() != null
 * 		
 * 		for (int i = 0; i < getEnemyGrid().length(); ++i){
 * 			for (int j = 0; j < getEnemyGrid().length(); ++j){
 * 				getEnemyGrid()[i][j] != TileState.SHIP_TILE;
 * 			}
 * 		} </pre>
 * @cons <pre>
 * 		$ARGS$ Ship[] playerShips
 * 		$PRE$
 *     		playerShips != null
 * 		$POST$
 *     		getPlayerGrid().nbShip() == playerShips.length
 *     		for (int i = 0; i < getEnemyGrid().length(); ++i){
 * 				for (int j = 0; j < getEnemyGrid().length(); ++j){
 * 					getEnemyGrid()[i][j] != TileState.EMPTY_TILE;
 * 				}
 * 			} </pre>
 */
public class GamePanel {
	
	// ATTRIBUTS STATIQUES
	public static final String PROP_ENEMY_GRID = "enemy_grid";
	public static final String PROP_ENEMY_SHIP = "enemy_ship";
	public static final String PROP_ROUND_PLAYER = "Round_Player";
	
	// ATTRIBUTS
	
	private final Grid playerGrid;
	private final TileState[][] enemyGrid;
	private boolean isRoundPlayer;
	private int nbEnemyShip;
	private final PropertyChangeSupport pcs;
	
	// CONSTRUCTEUR
	public GamePanel(Ship[] playerShips) {
		Contract.checkCondition(playerShips != null, "Game");
		playerGrid = new StdGrid(playerShips);
		// Initialisation de la tilemap avec EMPTY_TILE comme valeur par défaut.
		enemyGrid = new TileState[Grid.DIM][Grid.DIM];
		for (int i = 0; i < enemyGrid.length; ++i) {
			for (int j = 0; j < enemyGrid.length; ++j) {
				enemyGrid[i][j] = TileState.EMPTY_TILE;
			}
		}
		pcs = new PropertyChangeSupport(this);
        nbEnemyShip = playerShips.length;
	}

	// REQUETES
	
	/**
	 * Retourne la grille du joueur actuelle
	 */
	public Grid getPlayerGrid() {
		return playerGrid;
	}
	/**
	 * Retourne le tableau de tableau représentant la vision du joueur sur 
	 * 		la grille de l'adversaire
	 */
	public TileState[][] getEnemyGrid() {
		return enemyGrid;
	}
	
	/**
	 * Retourne si le joueur à perdu la partie
	 */
	public boolean playerLose() {
		return playerGrid.getNbShip() == 0;
	}
	
	/**
	 * Retourne si le tour actuelle est celui du joueur
	 */
	public boolean isRoundPlayer() {
		return isRoundPlayer;
	}
    
	/**
	 * Retourne le nombre de bateau enemie restant
	 */
	public int getNbEnemyShip() {
		return nbEnemyShip;
	}

	
	// COMMANDES
    
	/**
	 * Met la case de line row et colonne col de la grille de l'adversaire à 
	 * 		value
	 * @pre <pre>
	 * 		0 <= row <= Grid.DIM 
	 * 		&& 0 <= col <= Grid.DIM </pre>
	 * @post <pre>
	 * 		enemyGrid()[row][col] == value </pre>
	 */
	public void playerHitEnnemy (int row, int col, TileState value) {
		Contract.checkCondition(0 <= row && row <= Grid.DIM
								&& 0 <= col && col <= Grid.DIM, 
								"GamePanel: Coord not valid");
		TileState oldValue = enemyGrid[row][col];
		enemyGrid[row][col] = value;
		pcs.firePropertyChange(GamePanel.PROP_ENEMY_GRID, oldValue, value);
	}
	
	/**
	 * L'adversaire touche la grille du joueur à la case de line row et colonne 
	 * 		col
	 * Retourne la valeur de l'état de la case touché
	 * @pre <pre>
	 * 		0 <= row <= Grid.DIM 
	 * 		&& 0 <= col <= Grid.DIM </pre>
	 * @post <pre>
	 * 		getPlayerGrid().stateOfTile(col, row) == TileState.EMPTY_HIT_TILE
	 * 		|| getPlayerGrid().stateOfTile(col, row) == TileState.SHIP_HIT_TILE
	 * @throws
     *     PropertyVetoException si la case ne peut pas être touché.
	 */
	public TileState ennemyHitPlayer (int row, int col) 
			throws PropertyVetoException {
		Contract.checkCondition(0 <= row && row <= Grid.DIM
				&& 0 <= col && col <= Grid.DIM, 
				"GamePanel: Coord not valid");
		
		playerGrid.hit(row, col);
		return playerGrid.stateOfTile(row, col);
	}
	
	/**
	 * @post <pre>
	 * 		isROundPlayer() == value</pre>
	 */
	public void SetRoundPlayer(boolean value) {
		boolean oldValue = isRoundPlayer;
		isRoundPlayer = value;
		pcs.firePropertyChange(PROP_ROUND_PLAYER, oldValue, value);
	}
	
	/**
	 * @pre <pre>
	 * 		nbShip > 0 && nbShip < getEnemyShip()<\pre>
	 * 
	 * @post <pre>
	 * 		getEnemyShip() == nbShip<\pre> 
	 */
	public void setEnemyShip(int nbShip) {
		int oldValue = nbEnemyShip;
		nbEnemyShip = nbShip;
		pcs.firePropertyChange(PROP_ENEMY_SHIP, oldValue, nbShip);
	}
    
    /**
     * Ajoute un PCL  pour la propriété pName.
     * Ne fait rien si lnr a déjà été ajouté.
     */
    public void addPropertyChangeListener(String pName, 
    		PropertyChangeListener lnr) {
    	pcs.addPropertyChangeListener(pName, lnr);
    }
}
