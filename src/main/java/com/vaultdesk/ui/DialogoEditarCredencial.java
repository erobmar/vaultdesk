package com.vaultdesk.ui;

import com.vaultdesk.dominio.Credencial;
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

public class DialogoEditarCredencial {

    private final Stage owner;

    public DialogoEditarCredencial(Stage owner){
        this.owner = owner;
    }

    public void mostrar(Credencial credencial, Consumer<DatosEdicionCredencial> callback){

        Stage dialogo = new Stage();
        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Editar credencial");

        TextField campoUrl = new TextField(credencial.getUrlIdentificador());
        TextField campoUsername = new TextField(credencial.getUsername());
        PasswordField campoPassword = new PasswordField();
        campoPassword.setText(credencial.getPassword());
        //campoPassword.setVisible(false);

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red");

        Button botonAceptar = new Button("Aceptar");
        Button botonCancelar = new Button("Cancelar");

        botonAceptar.setOnAction(e-> {

            String url = campoUrl.getText() == null ? "" : campoUrl.getText().trim();
            String username = campoPassword.getText() == null ? "" : campoUsername.getText().trim();
            String password = campoPassword.getText();

            if(url.isEmpty()){
                etiquetaError.setText("Debes especificar una URL o Identificador de sistema");
                return;
            }
            if(username.isEmpty()){
                etiquetaError.setText("Debes especificar un nombre de usuario");
                return;
            }
            if(password == null || password.isEmpty()){
                etiquetaError.setText("Debes indicar una contraseña");
                return;
            }

            dialogo.close();
            callback.accept(new DatosEdicionCredencial(credencial.getIdCredencial(), url, username, password, 1));


        });

        botonCancelar.setOnAction(e->{
            callback.accept(null);
            return;
        });

        GridPane parrilla = new GridPane();
        parrilla.setVgap(10);
        parrilla.setHgap(10);
        parrilla.setPadding(new Insets(15));

        parrilla.add(new Label("URL/Identificado"), 0 , 0);
        parrilla.add(campoUrl, 1, 0);

        parrilla.add(new Label("Username"), 0, 1);
        parrilla.add(campoUsername, 1,1 );

        parrilla.add(new Label("Password"), 0, 2);
        parrilla.add(campoPassword, 1,2);

        parrilla.add(etiquetaError, 0,3,2,1);
        parrilla.add(botonAceptar, 0, 4);
        parrilla.add(botonCancelar, 1,4);

        dialogo.setScene(new Scene(parrilla, 420,230));
        dialogo.show();

    }


    public record DatosEdicionCredencial(
            int idCredencial,
            String urlIdentificador,
            String username,
            String password,
            int idCategoria
    ){ }

}
