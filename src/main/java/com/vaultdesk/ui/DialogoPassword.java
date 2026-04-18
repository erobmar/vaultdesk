package com.vaultdesk.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Consumer;

public class DialogoPassword {

        private final Stage escena;

        public DialogoPassword(Stage escena){
            this.escena = escena;
        }

        public void mostrar(Consumer<char[]> callback){

            Dialog<char[]> dialogo = new Dialog<>();

            dialogo.initOwner(escena);
            dialogo.initModality(Modality.APPLICATION_MODAL);
            dialogo.setTitle("Contraseña maestra");
            dialogo.setHeaderText("Introduce la contraseña maestra de la bóveda");

            PasswordField password = new PasswordField();
            password.setPromptText("Contraseña maestra");

            VBox cajavertical = new VBox(10, new Label("Contraseña"), password);
            dialogo.getDialogPane().setContent(cajavertical);

            ButtonType botonAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialogo.getDialogPane().getButtonTypes().addAll(botonAceptar, botonCancelar);

            dialogo.setResultConverter(
                    buttonType -> {
                        if(buttonType == botonAceptar){
                            return password.getText().toCharArray();
                        }
                        return null;
                    }
            );

            dialogo.setOnHidden(e -> callback.accept(dialogo.getResult()));

            dialogo.show();

        }


}
