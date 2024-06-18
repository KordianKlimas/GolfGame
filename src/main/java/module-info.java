module com.kentwentyfour.project12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;

    opens com.kentwentyfour.project12 to javafx.fxml;
    exports com.kentwentyfour.project12;
    exports com.kentwentyfour.project12.presentation.controllers;
    opens com.kentwentyfour.project12.presentation.controllers to javafx.fxml;
    exports com.kentwentyfour.project12.presentation.controllers.alerts;
    opens com.kentwentyfour.project12.presentation.controllers.alerts to javafx.fxml;
}
//module com.kentwentyfour.project12 {
//        requires javafx.controls;
//        requires javafx.fxml;
//        requires javafx.graphics;
//
//        exports com.kentwentyfour.project12.GameObjects;
//        }