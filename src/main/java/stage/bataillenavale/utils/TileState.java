package stage.bataillenavale.utils;

/**
 * Les différents état dans lequel une case de la grille peut être :
 * <ul>
 *   <li> EMPTY_TILE: La case n'as pas de bateau sur elle et n'est pas touché ;</li>
 *   <li> EMPTY_HIT_TILE: La case n'as pas de bateau sur elle et elle est touché ;</li>
 *   <li> SHIP_TILE: La case as un bateau sur elle et n'est pas touché ;</li>
 *   <li> SHIP_HIT_TILE: La case as un de bateau sur elle et elle est touché.</li>
 * </ul>
 */
public enum TileState {
	EMPTY_TILE(0), 
	EMPTY_HIT_TILE(1), 
	SHIP_TILE(2), 
	SHIP_HIT_TILE(3);
	
	// ATTRIBUT
	private final int value;
	
	// CONSTRUCTEUR
	TileState(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static TileState getTileState(int value) {
		return TileState.values()[value];
	}
	
	
}
