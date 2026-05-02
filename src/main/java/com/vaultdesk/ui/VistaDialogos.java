package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 * Vista para centralizar los diálogos
 *
 *
 */
public class VistaDialogos {

    private final ControladorPrincipal controladorPrincipal;

    public VistaDialogos(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public void mostrarMensajeError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void mostrarMensajeInformacion(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public ButtonType mostrarConfirmacionCierre() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar bóveda");
        alert.setHeaderText("Hay cambios sin guardar");
        alert.setContentText("¿Quieres guardar los cambios antes de cerrar la bóveda?");

        ButtonType botonGuardar = new ButtonType("Guardar");
        ButtonType botonNoGuardar = new ButtonType("No Guardar");
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonGuardar, botonNoGuardar, botonCancelar);

        return alert.showAndWait().orElse(botonCancelar);

    }


}
