package com.vaultdesk.negocio;

import com.vaultdesk.controlador.ControladorAjustes;
import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar las principales operaciones sobre ajustes de la bóveda
 * <p>
 * Esta clase recibe la responsabilidad de obtener listas de idiomas y temas visuales, así como actualizar los ajustes
 * de una bóveda
 * </p>
 *
 */
public class GestorAjustes {

    /**
     * Obtiene la lista de idiomas disponibles en el sistema
     *
     * @param conexionActual conexión activa con la base de datos
     * @return lista de idiomas
     * @throws Exception si encuentra cualquier problema durante el proceso
     * @see ControladorAjustes#obtenerIdiomas()
     *
     *
     */
    public List<Idioma> obtenerIdiomas(Connection conexionActual) throws Exception {


        validarConexion(conexionActual);

        String sentenciaConsulta = """
                SELECT id_idioma, nombre FROM idioma ORDER BY id_idioma
                """;

        List<Idioma> listaIdiomas = new ArrayList<>();

        try (PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaConsulta)) {

            ResultSet setResultados = sentencia.executeQuery();

            while (setResultados.next()) {

                Idioma idioma = new Idioma();
                idioma.setIdIdioma(setResultados.getInt("id_idioma"));
                idioma.setNombre(setResultados.getString("nombre"));

                listaIdiomas.add(idioma);
            }

        }

        return listaIdiomas;

    }

    /**
     * Obtiene la lista de temas visuales disponibles en el sistema
     *
     * @param conexionActual conexión activa con la base de datos
     * @return lista de temas visuales
     * @throws Exception si encuentra cualquier problema durante el proceso
     * @see ControladorAjustes#obtenerTemasVisuales()
     *
     *
     */
    public List<TemaVisual> obtenerTemasVisuales(Connection conexionActual) throws Exception {

        validarConexion(conexionActual);

        String sentenciaConsulta = """
                SELECT id_tema_visual, nombre
                FROM tema_visual
                ORDER BY id_tema_visual
                """;

        List<TemaVisual> listaTemasVisuales = new ArrayList<>();

        try (PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaConsulta)) {

            ResultSet setResultados = sentencia.executeQuery();

            while (setResultados.next()) {

                TemaVisual temaVisual = new TemaVisual();
                temaVisual.setIdTemaVisual(setResultados.getInt("id_tema_visual"));
                temaVisual.setNombre(setResultados.getString("nombre"));

                listaTemasVisuales.add(temaVisual);


            }

        }

        return listaTemasVisuales;
    }

    /**
     * Actualiza los ajustes de una bóveda a los valores dados
     *
     * @param umbralAlerta   umbral en días de alerta para caducidad de credenciales
     * @param accesibilidad  indica si las opciones de accesibilidad están activadas o no
     * @param idioma         idioma para la bóveda
     * @param temaVisual     tema visual en el que se mostrará la aplicación
     * @param conexionActual conexión activa con la base de datos
     * @param bovedaActual   bóveda sobre la que se está trabajando
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorAjustes#actualizarAjustesBoveda(int, boolean, Idioma, TemaVisual)
     *
     *
     */
    public void actualizarAjustesBoveda(
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual,
            Connection conexionActual,
            Boveda bovedaActual
    ) throws Exception {

        validarConexion(conexionActual);

        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }
        if (umbralAlerta < 0) {
            throw new IllegalArgumentException("El umbral de alerta no puede ser menor que 0");
        }
        if (idioma == null) {
            throw new IllegalStateException("Se debe seleccionar un idioma");
        }
        if (temaVisual == null) {
            throw new IllegalStateException("Se debe seleccionar un tema visual");
        }

        String sentenciaActualizacion = """
                UPDATE boveda
                SET umbral_alerta = ?,
                    accesibilidad = ?,
                    id_idioma = ?,
                    id_tema_visual = ?
                WHERE id_boveda = ?
                """;

        try (PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaActualizacion)) {

            sentencia.setInt(1, umbralAlerta);
            sentencia.setInt(2, accesibilidad ? 1 : 0);
            sentencia.setInt(3, idioma.getIdIdioma());
            sentencia.setInt(4, temaVisual.getIdTemaVisual());
            sentencia.setInt(5, bovedaActual.getIdBoveda());

            sentencia.executeUpdate();


        }

        bovedaActual.setUmbralAlerta(umbralAlerta);
        bovedaActual.setAccesibilidad(accesibilidad);
        bovedaActual.setIdioma(idioma);
        bovedaActual.setTemaVisual(temaVisual);
        bovedaActual.setModificadaSinGuardar(true);

        GestorIdiomas.cambiarIdioma(IdiomaEnum.getCodigoDesdeId(idioma.getIdIdioma()).getCodigo());


    }

    /**
     * Valida la conexión antes de continuar
     *
     */
    private void validarConexion(Connection conexionActual) throws SQLException {
        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No hay ninguna conexión activa"
        }
    }

}
