package com.vaultdesk.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class DialogoPassword {

    private final Stage escena;

    public DialogoPassword(Stage escena) {
        this.escena = escena;
    }

    /**
     * Muestra el diálogo para solicitar al usuario su contraseña maestra
     *
     *
     */
    public void mostrar(String mensaje, Consumer<char[]> callback) {

        Dialog<char[]> dialogo = new Dialog<>();

        dialogo.initOwner(escena);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Contraseña maestra");
        dialogo.setHeaderText(mensaje);

        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña maestra");

        VBox cajavertical = new VBox(10, new Label("Contraseña"), password);
        dialogo.getDialogPane().setContent(cajavertical);

        ButtonType botonAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogo.getDialogPane().getButtonTypes().addAll(botonAceptar, botonCancelar);

        dialogo.setResultConverter(
                buttonType -> {
                    if (buttonType == botonAceptar) {
                        return password.getText().toCharArray();
                    }
                    return null;
                }
        );

        dialogo.setOnHidden(e -> callback.accept(dialogo.getResult()));

        dialogo.show();

    }

    /**
     * Muestra el diálogo para solicitar al usuario su contraseña maestra y espera respuesta del usuario
     *
     *
     */
    public char[] mostrarYEsperar() {

        final char[][] resultado = new char[1][];

        Stage dialogo = new Stage();
        dialogo.initOwner(escena);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Contraseña maestra");

        PasswordField campoPassword = new PasswordField();

        Button botonAceptar = new Button("Aceptar");
        Button botonCancelar = new Button("Cancelar");

        botonAceptar.setOnAction(e -> {
            resultado[0] = campoPassword.getText().toCharArray();
            dialogo.close();
        });

        botonCancelar.setOnAction(e -> {
            resultado[0] = null;
            dialogo.close();
        });

        HBox botones = new HBox(10, botonAceptar, botonCancelar);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
                new Label("Introduce la contraseña maestra"),
                campoPassword,
                botones
        );

        dialogo.setScene(new Scene(root, 350, 150));
        dialogo.showAndWait();

        return resultado[0];


    }

}
