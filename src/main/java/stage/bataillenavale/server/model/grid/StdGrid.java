package stage.bataillenavale.server.model.grid;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.geometry.Point2D;
import stage.bataillenavale.server.model.Ship.Ship;
import stage.bataillenavale.utils.Contract;
import stage.bataillenavale.utils.TileState;

public class StdGrid implements Grid{

	// ATTRIBUTS
	private final TileState[][] tilemap;
	private List<List<Point2D>> shipTiles;
	private final PropertyChangeSupport pcs;
	private final VetoableChangeSupport vcs;

	// CONSTRUCTEUR

	public StdGrid(Ship[] ships) {
		Contract.checkCondition(checkShipsNotNull(ships), "StdShip");
		// Initialisation de la tilemap avec EMPTY_TILE comme valeur par défaut
		tilemap = new TileState[DIM][DIM];
		for (TileState[] tileStates : tilemap) {
			Arrays.fill(tileStates, TileState.EMPTY_TILE);
		}
		// Initialisation de la liste de listes de points. Chaque liste
		// correspond à un bateau et contient les coordonnées de ses cases
		shipTiles = new LinkedList<>();
		for(Ship s : ships) {
			shipTiles.add(s.getTilesCoords());
			for(Point2D p : s.getTilesCoords()) {
				tilemap[(int) p.getX()][(int) p.getY()] = TileState.SHIP_TILE;
			}
		}
		pcs = new PropertyChangeSupport(this);
		vcs = new VetoableChangeSupport(this);
	}

	// REQUETES

	public TileState[][] getGridState() {
		// créer un nouveau tableau et l'initialise avec les valeurs de tilemap
		//	puis le renvoie
		int length = tilemap.length;
		TileState[][] r = new TileState[length][length];
		for(int i = 0; i < length; ++i) {
			System.arraycopy(tilemap[i], 0, r[i], 0, length);
		}
		return r;
	}

	public List<List<Point2D>> getShip() {
		List<List<Point2D>> newShipTiles = new LinkedList<>();
		for(List<Point2D> lp : shipTiles) {
			List<Point2D> l = new LinkedList<>();
			for(Point2D p : lp) {
				l.add(new Point2D(p.getX(), p.getY()));
			}
			newShipTiles.add(l);
		}
		return newShipTiles;
	}

	public int getNbShip() {
		return shipTiles.size();
	}

	public boolean isHit(int row, int col) {
		Contract.checkCondition(col >= 0 && col < DIM && row >= 0 && row <
				DIM, "isHit");
		return tilemap[row][col] == TileState.EMPTY_HIT_TILE ||
				tilemap[row][col] == TileState.SHIP_HIT_TILE;
	}

	public TileState stateOfTile(int row, int col) {
		return tilemap[row][col];
	}

	public PropertyChangeListener[] getPropertyChangeListeners(String pName) {
		Contract.checkCondition(pName != null,
				"ERR: StdGrid PropertyChangeListeners");
		return pcs.getPropertyChangeListeners();
	}

	public VetoableChangeListener[] getVetoableChangeListeners(String pName) {
		Contract.checkCondition(pName != null,
				"ERR: StdGrid VetoableChangeListeners");
		return vcs.getVetoableChangeListeners();
	}



	// COMMANDES

	public void hit(int row, int col) throws PropertyVetoException {
		TileState oldValue = tilemap[row][col];
		int test = (oldValue.getValue() + 1) % TileState.values().length;
		TileState newValue = TileState.getTileState((test));
		vcs.fireVetoableChange(Grid.PROP_GRID, oldValue, newValue);
		tilemap[row][col] = newValue;
		pcs.firePropertyChange(Grid.PROP_GRID, oldValue, newValue);
		if (newValue == TileState.SHIP_HIT_TILE) {
			// Création d'une copie de shipTiles ne contenant pas le Point passé
			//	en paramètre.
			int oldValue1 = shipTiles.size();
			List<List<Point2D>> newShipTiles = new LinkedList<>();
			for(List<Point2D> lp: shipTiles) {
				List<Point2D> l = new LinkedList<>();
				for(Point2D p: lp) {
					if(p.getX() != row || p.getY() != col) {
						l.add(p);
					}
				}
				if(!l.isEmpty()) {
					newShipTiles.add(l);
				}
			}
			shipTiles = newShipTiles;
			pcs.firePropertyChange(PROP_SHIP, oldValue1, shipTiles.size());
		}
	}

	public void addPropertyChangeListener(String pName,
										  PropertyChangeListener lnr) {
		Contract.checkCondition(pName != null && lnr != null,
				"ERR: StdGrid addPropertyChangeListener");
		pcs.addPropertyChangeListener(pName, lnr);
	}

	public void addVetoableChangeListener(String pName,
										  VetoableChangeListener lnr) {
		Contract.checkCondition(pName != null && lnr != null,
				"ERR: StdGrid addVetoableChangeListener");
		vcs.addVetoableChangeListener(pName, lnr);
	}

	public void removePropertyChangeListener(String pName,
											 PropertyChangeListener lnr) {
		Contract.checkCondition(pName != null && lnr != null,
				"ERR: StdGrid removePropertyChangeListener");
		pcs.removePropertyChangeListener(pName, lnr);
	}


	public void removeVetoableChangeListener(String pName,
											 VetoableChangeListener lnr) {
		Contract.checkCondition(pName != null && lnr != null,
				"ERR: StdGrid removeVetoableChangeListener");
		vcs.removeVetoableChangeListener(pName, lnr);

	}

	// OUTILS
	/**
	 * Vérifie qu'aucun élément du tableau de bateau ne vaut null.
	 */
	private boolean checkShipsNotNull(Ship[] ships) {
		boolean r = true;
		for(Ship s : ships) {
			if (s == null) {
				r = false;
				break;
			}
		}
		return r;
	}
}
