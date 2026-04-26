package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorCredenciales;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class ControladorExportacion {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorExportacion(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public void exportarACsv(Path rutaCsv) throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No existe ninguna conexión activa");
        }

        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        List<Credencial> listaCredenciales = controladorPrincipal.obtenerCredenciales();

        if(listaCredenciales.isEmpty()){
            throw new IllegalStateException("No hay credenciales para exportar");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        gestorCredenciales.exportarCredencialesCSV(listaCredenciales, rutaCsv);

    }

    public boolean confirmarExportacion(){

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Exportar credenciales");
        alerta.setHeaderText("Vas a exportar tus credenciales en formato visible");
        alerta.setContentText("¿Deseas continuar?");

        alerta.getDialogPane().setMinWidth(450);

        Optional<ButtonType> respuesta = alerta.showAndWait();

        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

}
