package com.vaultdesk.ui;

import com.vaultdesk.negocio.GestorIdiomas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Consumer;


public class DialogoNuevaBoveda {

    private final Stage owner;

    public DialogoNuevaBoveda(Stage owner) {
        this.owner = owner;
    }

    /**
     * Muestra el diálogo de creación de una nueva Bóveda
     *
     *
     */
    public void mostrar(Consumer<DatosNuevaBoveda> callback) {

        Dialog<DatosNuevaBoveda> dialogo = new Dialog<>();
        dialogo.initOwner(owner);
        dialogo.setTitle(GestorIdiomas.getText("dialogo.nuevaboveda.title")); // "Nueva bóveda"
        dialogo.setHeaderText(GestorIdiomas.getText("dialogo.nuevaboveda.header")); // "Crear nueva bóveda"

        TextField campoNombre = new TextField();
        PasswordField campoPassword = new PasswordField();
        PasswordField confirmarPassword = new PasswordField();

        campoNombre.setPromptText(GestorIdiomas.getText("prompt.nombrebiveda")); // "Nombre de la bóveda"
        campoPassword.setPromptText(GestorIdiomas.getText("prompt.passwordmaestra")); // "Contraseña maestra"
        confirmarPassword.setPromptText(GestorIdiomas.getText("prompt.confirmarpassword")); // "Confirmar contreseña"

        Label etiquetaNombre = new Label(GestorIdiomas.getText("label.nombre")); // "Nombre:"
        Label etiquetaPassword = new Label(GestorIdiomas.getText("label.passwordmaestra")); // "Contraseña:"
        Label etiquetaConfirmarPassowrd = new Label(GestorIdiomas.getText("label.confirmar")); // "Confirmar:"


        VBox contenido = new VBox(10,
                etiquetaNombre, campoNombre,
                etiquetaPassword, campoPassword,
                etiquetaConfirmarPassowrd, confirmarPassword);

        dialogo.getDialogPane().setContent(contenido);

        ButtonType botonAceptar = new ButtonType(GestorIdiomas.getText("boton.aceptar"), ButtonBar.ButtonData.OK_DONE); // "Aceptar"
        ButtonType botonCancelar = new ButtonType(GestorIdiomas.getText("boton.cancelar"), ButtonBar.ButtonData.CANCEL_CLOSE); // "Cancelar"

        dialogo.getDialogPane().getButtonTypes().addAll(botonAceptar, botonCancelar);

        dialogo.setResultConverter(
                botonPulsado -> {
                    if (botonPulsado == botonAceptar) {
                        String nombre = campoNombre.getText();
                        String password = campoPassword.getText();
                        String confirmar = confirmarPassword.getText();

                        if (nombre == null || nombre.isBlank()) {
                            return null;
                        }
                        if (password == null || password.isEmpty()) {
                            return null;
                        }
                        if (!password.equals(confirmar)) {
                            return null;
                        }

                        return new DatosNuevaBoveda(nombre, password.toCharArray());


                    }
                    return null;

                });

        Optional<DatosNuevaBoveda> resultado = dialogo.showAndWait();

        callback.accept(resultado.orElse(null));


    }

    public record DatosNuevaBoveda(String nombre, char[] password) {
    }

}
