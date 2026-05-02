package com.vaultdesk;

import com.vaultdesk.controlador.ControladorPrincipal;
import javafx.application.Application;
import javafx.stage.Stage;

public class VaultDeskApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        ControladorPrincipal controladorPrincipal = new ControladorPrincipal(primaryStage);

        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            controladorPrincipal.salirAplicacion();
        });

        controladorPrincipal.mostrarVistaInicial();
    }

    public static void main(String[] args) {
        launch(args);
    }
}