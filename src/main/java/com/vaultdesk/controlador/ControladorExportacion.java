package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorCredenciales;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.nio.file.Path;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Clase auxiliar encargada de las operaciones sobre exportación de credenciales de una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas a exportación de
 * credenciales de una bóveda a un archivo CSV
 * </p>
 *
 */
public class ControladorExportacion {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorExportacion(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    /**
     * Exporta el listado completo de credenciales de la bóveda a un archivo CSV
     *
     * @param rutaCsv ruta donde serán exportados los datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#exportarACsv(Path)
     * @see GestorCredenciales#exportarCredencialesCSV(List, Path)
     *
     *
     */
    public void exportarACsv(Path rutaCsv) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No existe ninguna conexión activa"
        }

        if (bovedaActual == null) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.boveda")); // "No hay ninguna bóveda abierta"
        }

        List<Credencial> listaCredenciales = controladorPrincipal.obtenerCredenciales();

        if (listaCredenciales.isEmpty()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.sincredenciales")); // "No hay credenciales para exportar"
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        gestorCredenciales.exportarCredencialesCSV(listaCredenciales, rutaCsv);

    }

    /**
     * Solicita al usaurio confirmación para la exportación de credenciales a un archivo CSV
     *
     * @return true si el usuario confirma la exportación, false en caso contrario
     * @see ControladorPrincipal#confirmarExportacion()
     *
     */
    public boolean confirmarExportacion() {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(GestorIdiomas.getText("alerta.exportar.title")); // "Exportar credenciales"
        alerta.setHeaderText(GestorIdiomas.getText("alerta.exportar.header")); // "Vas a exportar tus credenciales en formato visible"
        alerta.setContentText(GestorIdiomas.getText("alerta.exportar.content")); // "¿Deseas continuar?"

        alerta.getDialogPane().setMinWidth(450);

        Optional<ButtonType> respuesta = alerta.showAndWait();

        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }

}
