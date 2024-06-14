package stage.bataillenavale.model.ship;

import javafx.geometry.Point2D;
import stage.bataillenavale.utils.Contract;

import java.util.LinkedList;
import java.util.List;



public class StdShip implements Ship {
	
	// ATTRIBUTS
	private final int length;
	private List<Point2D> tilesCoords;
	
	// CONSTRUCTEUR
	
	public StdShip(int len) {
		Contract.checkCondition(len > 0 && len < 6,
				"Error on StdShip: attributs not right");
		length = len;
		tilesCoords = new LinkedList<Point2D>();
	}
	
	
	// REQUETES
	
	public List<Point2D> getTilesCoords() {
		return tilesCoords;
	}

	public int getLength() {
		return length;
	}

	
	// COMMANDES
	
	public void setTilesCoords(List<Point2D> points) {
		Contract.checkCondition(checkTilesPoint(points));
		tilesCoords = points;
	}
	

	
	// OUTILS
	
	private boolean checkTilesPoint(List<Point2D> points) {
		boolean r = true;
		if(points == null) {
			return false;
		}
		for(Point2D p : points) {
            if (p == null) {
                r = false;
                break;
            }
		}
		return r;
	}
}
