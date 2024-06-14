package stage.bataillenavale.model.ship;


import javafx.geometry.Point2D;

import java.util.List;

/**
 * @inv <pre>
 *		1 <= getLength() <= 5
 *		getPoint != null
 *		
 * @cons <pre>
 * 		$ARG$ 
 * 			int length, Point coords
 * 		$PRE$
 * 			length > 0 && length < 6 && coords != null
 *		$POST$
 *			getLength() == length
 *			getCoords() == coords
 *@cons <pre>
 * 		$ARG$ 
 * 			int length
 * 		$PRE$
 * 			length > 0 && length < 6
 *		$POST$
 *			getLength() == length
 */
public interface Ship  {
	
	// REQUETES
	
	/**
	 * La taille du bateau
	 */
	int getLength();
	
	/**
	 * 
	 * Coordonnées du bateau dans la grille de jeu
	 */
	List<Point2D> getTilesCoords();
	

	
	// COMMANDE
	
	
	/**
	 * @pre
	 * 		coords != null && for all i in coords i != null
	 * @post
	 * 		getTilesCoords() == coords
	 */
	void setTilesCoords(List<Point2D> tilesCoords);
	
	
}
