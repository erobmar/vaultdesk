package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;
import com.vaultdesk.negocio.GestorAjustes;

import java.sql.Connection;
import java.util.List;

/**
 * Clase auxiliar encargada de las operaciones sobre ajustes de una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas a consulta y cambio de
 * ajustes de la bóveda
 * </p>
 *
 */
public class ControladorAjustes {

    private final ControladorPrincipal controladorPrincipal;
    private final GestorAjustes gestorAjustes;

    public ControladorAjustes(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        this.gestorAjustes = new GestorAjustes();
    }

    /**
     * Obtiene la lista de idiomas disponibles en el sistema
     *
     * @return lista de idiomas
     * @throws Exception si encuentra cualquier problema durante el proceso
     * @see ControladorPrincipal#obtenerIdiomas()
     * @see GestorAjustes#obtenerIdiomas(Connection)
     *
     *
     */
    public List<Idioma> obtenerIdiomas() throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        return gestorAjustes.obtenerIdiomas(conexionActual);

    }

    /**
     * Obtiene la lista de temas visuales disponibles en el sistema
     *
     * @return lista de temas visuales
     * @throws Exception si encuentra cualquier problema durante el proceso
     * @see ControladorPrincipal#obtenerTemasVisuales()
     * @see GestorAjustes#obtenerTemasVisuales(Connection)
     *
     *
     */
    public List<TemaVisual> obtenerTemasVisuales() throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        return gestorAjustes.obtenerTemasVisuales(conexionActual);

    }

    /**
     * Actualiza los ajustes de una bóveda a los valores dados
     *
     * @param umbralAlerta  umbral en días de alerta para caducidad de credenciales
     * @param accesibilidad indica si las opciones de accesibilidad están activadas o no
     * @param idioma        idioma para la bóveda
     * @param temaVisual    tema visual en el que se mostrará la aplicación
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#actualizarAjustesBoveda(int, boolean, Idioma, TemaVisual)
     * @see GestorAjustes#actualizarAjustesBoveda(int, boolean, Idioma, TemaVisual, Connection, Boveda)
     *
     *
     */
    public void actualizarAjustesBoveda(
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual
    ) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        gestorAjustes.actualizarAjustesBoveda(umbralAlerta, accesibilidad, idioma, temaVisual, conexionActual, bovedaActual);

        controladorPrincipal.actualizarTituloVentana();

        controladorPrincipal.mostrarMensajeInformacion("Ajustes", "Los ajustes se han actualizado correctamente");


    }

}
