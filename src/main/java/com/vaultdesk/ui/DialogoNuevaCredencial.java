package com.vaultdesk.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class DialogoNuevaCredencial {

    private final Stage owner;

    public DialogoNuevaCredencial(Stage owner){
        this.owner = owner;
    }

    public void mostrar(Consumer<DatosNuevaCredencial> callback){

        Stage dialogo = new Stage();

        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Nueva credencial");

        TextField campoUrlIdentificador = new TextField();
        TextField campoUsername = new TextField();
        TextField campoPassword = new PasswordField();

        campoUrlIdentificador.setPromptText("URL o identificador");
        campoUsername.setPromptText("Username");
        campoPassword.setPromptText("Password");

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        Button botonAceptar = new Button("Aceptar");
        Button botonCancerlar  = new Button("Cancelar");

        botonAceptar.setOnAction(e->{
            String urlIdentificador = campoUrlIdentificador.getText() == null ? "" : campoUrlIdentificador.getText().trim();
            String username = campoUsername.getText() == null ? "" : campoUsername.getText().trim();
            String password = campoPassword.getText();

            if(urlIdentificador.isEmpty()){
                etiquetaError.setText("Debes indicar una URL o identificador de sistema");
                return;
            }

            if(username.isEmpty()){
                etiquetaError.setText("Debes indicar un nombre de usuario");
                return;
            }

            if(password == null){
                etiquetaError.setText("Debes indicar una contraseña");
                return;
            }

            callback.accept(new DatosNuevaCredencial(urlIdentificador, username, password, 1)); // Por defecto se adjudica a la categoría 'Otros'
            dialogo.close();
        });

        botonCancerlar.setOnAction(e->{
            callback.accept(null);
            dialogo.close();
        });

        // Presentando todo en la ventana de diálogo
        GridPane parrilla = new GridPane();
        parrilla.setHgap(10);
        parrilla.setVgap(10);
        parrilla.setPadding(new Insets(15));

        parrilla.add(new Label("URL/Identificador"), 0, 0);
        parrilla.add(campoUrlIdentificador, 1, 0);

        parrilla.add(new Label("Username"), 0, 1);
        parrilla.add(campoUsername, 1, 1);

        parrilla.add(new Label("Contraseña"), 0, 2);
        parrilla.add(campoPassword, 1, 2);

        parrilla.add(etiquetaError, 0, 3, 2, 1);
        parrilla.add(botonAceptar, 0,4);
        parrilla.add(botonCancerlar, 1, 4);

        dialogo.setScene(new Scene(parrilla, 420, 230));
        dialogo.show();
    }



    public record DatosNuevaCredencial(
            String urlIdentificador,
            String username,
            String password,
            int idCategoria
    ){}

}
