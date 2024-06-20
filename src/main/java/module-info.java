module com.kentwentyfour.project12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.kentwentyfour.project12 to javafx.fxml;
    exports com.kentwentyfour.project12;
    exports com.kentwentyfour.project12.presentation.controllers;
    opens com.kentwentyfour.project12.presentation.controllers to javafx.fxml;
    exports com.kentwentyfour.project12.presentation.controllers.maps;
    opens com.kentwentyfour.project12.presentation.controllers.maps to javafx.fxml;
    exports com.kentwentyfour.project12.gameobjects;
    opens com.kentwentyfour.project12.gameobjects to javafx.fxml;
    exports com.kentwentyfour.project12.gameobjects.matrixmapobjects.obstacles;
    exports com.kentwentyfour.project12.gameobjects.movableobjects;
}
