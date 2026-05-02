package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorCredenciales;
import com.vaultdesk.ui.AlertaCaducidad;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase auxiliar encargada de las operaciones sobre alertas de caducidad de una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas a alertas de caducidad
 * de credenciales de la bóveda
 * </p>
 *
 */
public class ControladorAlertas {

    private final ControladorPrincipal controladorPrincipal;
    private final GestorCredenciales gestorCredenciales = new GestorCredenciales();

    public ControladorAlertas(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    /**
     * Obtiene el listado de alertas de credenciales caducadas o próximas a caducar en la bóveda
     *
     * @return listado de alerta de caducidad
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#obtenerAlertasCaducidad()
     * @see GestorCredenciales#obtenerAlertasCaducidad(List, Boveda)
     *
     *
     */
    public List<AlertaCaducidad> obtenerAlertasCaducidad() throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda activa");
        }

        List<Credencial> listaCredenciales = controladorPrincipal.obtenerCredenciales();


        List<AlertaCaducidad> listaAlertas = new ArrayList<>();

        listaAlertas = gestorCredenciales.obtenerAlertasCaducidad(listaCredenciales, bovedaActual);

        return listaAlertas;
    }

}
