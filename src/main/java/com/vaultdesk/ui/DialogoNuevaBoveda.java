package com.vaultdesk.ui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Consumer;


public class DialogoNuevaBoveda {

    private final Stage owner;

    public DialogoNuevaBoveda(Stage owner){
        this.owner = owner;
    }

    public void mostrar(Consumer<DatosNuevaBoveda> callback){

        Dialog<DatosNuevaBoveda> dialogo = new Dialog<>();
        dialogo.initOwner(owner);
        dialogo.setTitle("Nueva bóveda");
        dialogo.setHeaderText("Crear nueva bóveda");

        TextField campoNombre = new TextField();
        PasswordField campoPassword = new PasswordField();
        PasswordField confirmarPassword = new PasswordField();

        campoNombre.setPromptText("Nombre de la bóveda");
        campoPassword.setPromptText("Contraseña maestra");
        confirmarPassword.setPromptText("Confirmar contreseña");

        Label etiquetaNombre = new Label("Nombre:");
        Label etiquetaPassword = new Label("Contraseña:");
        Label etiquetaConfirmarPassowrd = new Label("Confirmar:");


        VBox contenido = new VBox(10,
                etiquetaNombre, campoNombre,
                etiquetaPassword, campoPassword,
                etiquetaConfirmarPassowrd, confirmarPassword);

        dialogo.getDialogPane().setContent(contenido);

        ButtonType botonAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogo.getDialogPane().getButtonTypes().addAll(botonAceptar, botonCancelar);

        dialogo.setResultConverter(
                botonPulsado -> {
                    if(botonPulsado == botonAceptar){
                        String nombre = campoNombre.getText();
                        String password = campoPassword.getText();
                        String confirmar = confirmarPassword.getText();

                        if(nombre == null || nombre.isBlank()){
                            return null;
                        }
                        if(password == null || password.isEmpty()){
                            return null;
                        }
                        if(!password.equals(confirmar)){
                            return null;
                        }

                        return new DatosNuevaBoveda(nombre, password.toCharArray());


                    }
                    return null;

                });

        Optional<DatosNuevaBoveda> resultado = dialogo.showAndWait();

        callback.accept(resultado.orElse(null));

        /*
        TextInputDialog dialogoNombre = new TextInputDialog();

        dialogoNombre.initOwner(owner);

        dialogoNombre.setTitle("Nueva bóveda");
        dialogoNombre.setHeaderText("Nombre de la bóveda");

        Optional<String> resultadoNombre = dialogoNombre.showAndWait();

        if(resultadoNombre.isEmpty()){
            callback.accept(null);
            return;
        }

        DialogoPassword dialogoPassword = new DialogoPassword(owner);

        dialogoPassword.mostrar(password -> {

            if(password == null || password.length == 0){
                callback.accept(null);
                return;
            }
            callback.accept(new DatosNuevaBoveda(resultadoNombre.get(), password));

        });
        */

    }

    public record DatosNuevaBoveda(String nombre, char[] password) {}

}
