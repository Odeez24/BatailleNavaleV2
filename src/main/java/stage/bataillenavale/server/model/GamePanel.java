package stage.bataillenavale.server.model;


import stage.bataillenavale.server.model.Ship.Ship;
import stage.bataillenavale.server.model.grid.Grid;
import stage.bataillenavale.server.model.grid.StdGrid;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

import java.beans.PropertyVetoException;


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

	
	// ATTRIBUTS
	
	private Grid playerGrid;
	private boolean isRoundPlayer;
	private TileState[][] enemyGrid;
	
	// CONSTRUCTEUR
	public GamePanel(Ship[] playerShips) {
		Contract.checkCondition(playerShips != null, "Game");
		playerGrid = new StdGrid(playerShips);
		enemyGrid = new TileState[Grid.DIM][Grid.DIM];
		for (int i = 0; i < Grid.DIM; i++) {
			for (int j = 0; j < Grid.DIM; j++) {
				enemyGrid[i][j] = TileState.EMPTY_TILE;
			}
		}

	}
	
	// REQUETES
	
	/**
	 * Retourne la grille du joueur actuelle
	 */
	public Grid getPlayerGrid() {
		return playerGrid;
	}

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
	}
}
