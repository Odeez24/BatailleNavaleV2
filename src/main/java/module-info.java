module stage.bataillenavale {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.media;

    opens stage.bataillenavale to javafx.fxml, javafx.graphics;
    exports stage.bataillenavale;
    opens stage.bataillenavale.model.ship to javafx.graphics;
    exports stage.bataillenavale.gui;
    opens stage.bataillenavale.gui to javafx.fxml, javafx.graphics;
    exports stage.bataillenavale.model.game;
    exports stage.bataillenavale.model.ship;
    exports stage.bataillenavale.utils;
    exports stage.bataillenavale.model.grid;
}