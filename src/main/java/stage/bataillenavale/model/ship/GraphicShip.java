package stage.bataillenavale.model.ship;

import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import stage.bataillenavale.Main;
import stage.bataillenavale.model.grid.GraphicGrid;
import stage.bataillenavale.utils.Contract;

/**
 * @inv <pre>
 *     getImageView() != null
 *     getModel() != null </pre>
 * @cons <pre>
 *     $ARGS$ GraphicGrid pane: la grille dans laquelle le bateau se déplacera.
 *            int size: taille du bateau
 *            int pSize: Taille en pixel d'une case de la grille
 *            int StarPositionX, int StarPositionY: Position de départ de la vue
 *                  par rapport au conteneur dans lequel
 *            est le bateau.
 *     $PRE$ pane != null && size >= MIN_SHIP_SIZE && size <= MAX_SHIP_SIZE
 *              && pSize > 0 && StarPositionX >= 0 && StarPositionY >= 0
 *     $POST$
 *         Crée un bateau de taille size avec une image correspondante en
 *              fonction de la taille du bateau
 */
public class GraphicShip {
    // ATTRIBUTS CONSTANTES
    public static final int MIN_SHIP_SIZE = 2;
    public static final int MAX_SHIP_SIZE = 5;

    // ATTRIBUTS
    private final ImageView imageView;
    private final Ship model;
    private final int startPositionX;
    private final int startPositionY;
    private boolean isHorizontal;
    private boolean isSelected;
    private int width;
    private int height;
    private boolean isPlaced;
    // ATTRIBUTS DÉPLACEMENT DE LA VUE
    private double mouseAnchorX;
    private double mouseAnchorY;

    // CONSTRUCTEUR
    public GraphicShip(GraphicGrid pane, int size, int pSize,
                       int StarPositionX, int StarPositionY) {
        Contract.checkCondition(pane != null && size >= MIN_SHIP_SIZE
                        && size <= MAX_SHIP_SIZE && pSize > 0
                        && StarPositionX >= 0 && StarPositionY >= 0,
                "Error on GraphicShip: attributs not right");
        model = new StdShip(size);
        this.width = size * pSize;
        this.height = pSize;
        isHorizontal = true;
        imageView = new ImageView(Main.class.getResource(
                "image/ship" + size + ".png").toString());
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        this.startPositionX = StarPositionX;
        this.startPositionY = StarPositionY;
        imageView.setLayoutX(StarPositionX);
        imageView.setLayoutY(StarPositionY);
        mouseAnchorX = StarPositionX;
        mouseAnchorY = StarPositionY;
    }

    // Requête

    /**
     * Retourne l'image du bateau
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Retourne le modèle de bateau associé à la vue
     */
    public Ship getModel() {
        return model;
    }

    /**
     * Retourne si le bateau est horizontal
     */
    public boolean isHorizontal() {
        return isHorizontal;
    }

    /**
     * Retourne si le bateau est sélectionné
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * @post isSelected() == selected
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * Retourne si le bateau est placé
     */
    public boolean isPlaced() {
        return isPlaced;
    }

    /**
     * @post isPlaced() == placed
     */
    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }

    /**
     * Retourne la largeur du bateau
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retourne la hauteur du bateau
     */
    public int getHeight() {
        return height;
    }

    /**
     * Retourne la position sur l'axe X de départ
     */
    public int getStartPositionX() {
        return startPositionX;
    }

    /**
     * Retourne la position sur l'axe Y de départ
     */
    public int getStartPositionY() {
        return startPositionY;
    }

    // COMMANDES

    /**
     * Retourne la position de la souris sur l'axe X
     */
    public double getMouseAnchorX() {
        return mouseAnchorX;
    }

    /**
     * @post getMouseAnchorX() == x
     */
    public void setMouseAnchorX(double x) {
        mouseAnchorX = x;
    }

    /**
     * Retourne la position de la souris sur l'axe Y
     */
    public double getMouseAnchorY() {
        return mouseAnchorY;
    }

    /**
     * @post getMouseAnchorY() == y
     */
    public void setMouseAnchorY(double y) {
        mouseAnchorY = y;
    }

    /**
     * Applique une rotation de 90° au bateau
     *
     * @post isHorizontal() == !isHorizontal()
     * getWidth() == getHeight()
     * getHeight() == getWidth()
     */
    public void rotate() {
        Rotate rotate;
        if (isHorizontal) {
            rotate = new Rotate(90, height, 0);
        } else {
            rotate = new Rotate(-90, width, 0);
        }
        imageView.getTransforms().add(rotate);
        isHorizontal = !isHorizontal;
        int tmp = width;
        width = height;
        height = tmp;

    }
}
