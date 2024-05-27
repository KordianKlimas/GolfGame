//package com.kentwentyfour.project12.GameObjects;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
//
//public class SuperClass extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//
//        MapManager mapManager = new MapManager("sin( ( x - y ) / 7 ) + 0.5");
//        mapManager.generateTerrainData();
//        Pane mapPane = mapManager.createMap();
//        Scene scene = new Scene(mapPane, 700, 700);
//
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}