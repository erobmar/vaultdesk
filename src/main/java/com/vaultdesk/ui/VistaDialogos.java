package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.negocio.GestorIdiomas;
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
        alert.setTitle(GestorIdiomas.getText("alerta.cerrarboveda.title")); // "Cerrar bóveda"
        alert.setHeaderText(GestorIdiomas.getText("alerta.cerrarboveda.header")); // "Hay cambios sin guardar"
        alert.setContentText(GestorIdiomas.getText("alerta.cerrarboveda.content")); // "¿Quieres guardar los cambios antes de cerrar la bóveda?"

        ButtonType botonGuardar = new ButtonType(GestorIdiomas.getText("boton.guardar")); // "Guardar"
        ButtonType botonNoGuardar = new ButtonType(GestorIdiomas.getText("boton.noguardar")); // "No Guardar"
        ButtonType botonCancelar = new ButtonType(GestorIdiomas.getText("boton.cancelar"), ButtonBar.ButtonData.CANCEL_CLOSE); // "Cancelar"

        alert.getButtonTypes().setAll(botonGuardar, botonNoGuardar, botonCancelar);

        return alert.showAndWait().orElse(botonCancelar);

    }


}
