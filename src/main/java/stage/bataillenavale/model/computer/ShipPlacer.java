package stage.bataillenavale.model.computer;

import javafx.geometry.Point2D;
import stage.bataillenavale.model.ship.Ship;
import stage.bataillenavale.model.ship.StdShip;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ShipPlacer {
    // ATTRIBUTS
    private final List<Ship> list;
    private final boolean[][] dummy;
    private final int dim;

    public ShipPlacer(int dim) {
        this.dim = dim;
        this.list = generateShips();
        this.dummy = new boolean[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                dummy[i][j] = false;
            }
        }
    }

    public static LinkedList<Ship> generateShips() {
        LinkedList<Ship> list = new LinkedList<>();
        list.add(new StdShip(5));
        list.add(new StdShip(4));
        list.add(new StdShip(3));
        list.add(new StdShip(3));
        list.add(new StdShip(2));
        return list;
    }
    
    public static LinkedList<Ship> generateTestShips(){
    	LinkedList<Ship> list = new LinkedList<>();
    	list.add(new StdShip(2));
        return list;
    }

    public Ship[] getList() {
        return list.toArray(new Ship[0]);
    }

    public void placeShips() {
        Random rd = new Random();
        for (Ship ship : list) {
            boolean found = false;
            while (!found) {
                found = placeShip(ship, rd.nextInt(dim), rd.nextInt(dim), 
                		rd.nextBoolean());
            }
        }
    }

    private boolean checkPointPosition(Point2D p) {
        return p.getX() >= 0
                && p.getX() < dim
                && p.getY() >= 0
                && p.getY() < dim
                && !dummy[(int) p.getX()][(int) p.getY()];
    }

    private boolean placeShip(Ship s, int x, int y, boolean o) {
        List<Point2D> pot = new ArrayList<>();
        for (int i = 0; i < s.getLength(); i += 1) {
            Point2D p = new Point2D(
                    o ? x + i : x,
                    !o ? y + i : y);
            if (!checkPointPosition(p)) {
                return false;
            }
            dummy[(int) p.getX()][(int) p.getY()] = true;
            pot.add(p);
        }

        s.setTilesCoords(pot);
        return true;
    }
}
