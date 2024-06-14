package stage.bataillenavale.server.model.grid;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.List;

import javafx.geometry.Point2D;
import stage.bataillenavale.utils.TileState;

/**
 * @inv <pre>
 * 		getGridState() != null </pre>
 * @cons <pre>
 * 		$ARGS$ int dim 
 * 		$PRE$  dim > 0 && dim < MAX_DIM
 *     	$POST$
 * 			crée une grille de taille dim avec SHIPNB bateau </pre>
 * @cons <pre>
 * 		$ARGS$ int dim, int nbship
 * 		$PRE$  dim > 0 && dim < MAX_DIM && nbship > 0 && nbship < MAX_SHIPNB
 *     	$POST$
 * 			crée une grille de taille dim avec nbship bateau </pre>
 * @cons <pre>
 * 		$ARG$
 * 		$PRE$
 * 		$POST$
 * 			Crée une grille de taille DIM avec SHIPNB bateau
 */
public interface Grid {
	
	//ATTRIBUT STATIQUE
	int SHIPNB = 5;
	int DIM = 10;
	String PROP_GRID = "grid";
	String PROP_SHIP = "ship";
	
	//REQUETE
	
	/**
	 *  Renvoie la grille
	 */
	TileState[][] getGridState();
	
	/**
	 * Renvoie la liste des parties des bateaux non touchés
	 */
	List<List<Point2D>> getShip();
	
	/**
	 * Renvoie le nombre de bateau non totalement détruit;
	 */
	int getNbShip();
	
	/**
	 * Renvoie si la haque élément
	 * 	de cette classe possède un champ évolutions qui contient la valeur vers
	 * 	laquelle un état peut évoluer.case de colonne col et de ligne line a déjà était touché
	 * @pre <pre>
	 * 		col > 0 && col <= DIM 
	 * 		line > 0 && col <= DIM </pre>
	 */
	boolean isHit(int row, int col);
	
	/**
	 * Renvoie l'état de la case de colonne col et de ligne line
	 * @pre <pre>
	 * 		col > 0 && col <= DIM
	 * 		line > 0 && col <= DIM </pre>
	 */
	TileState stateOfTile(int row, int col);
    
    /**
     * Tous les observateurs de changement de valeur de la propriété pName.
     */
    VetoableChangeListener[] getVetoableChangeListeners(String pName);
	
	// COMMANDE
	/**
	 * Passe la case de colonne col et ligne line à touché(si accepté) et 
	 * 		notifié
	 * @pre <pre>
	 * 		col > 0 && col <= DIM 
	 * 		line > 0 && col <= DIM </pre>
	 * @post <pre>
	 * 		La case est mit à la valeur de EMPTY_HIT_TILE ou SHIP_HIT_TILE </pre>
	 * @throws
     *     PropertyVetoException la case ne respecte pas la condition
	 */
	void hit(int row, int col) throws PropertyVetoException;


    /**
     * Ajoute un VCL pour la propriété pName.
     * Ne fait rien si lnr a déjà été ajouté.
     * @pre <pre>
     *     pName != null && lnr != null </pre>
     * @post <pre>
     *     lnr a été ajouté à la liste des écouteurs de la propriété pName
     * </pre>
     */
    void addVetoableChangeListener(String pName, VetoableChangeListener lnr);
	
	
}
