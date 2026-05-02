package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.controlador.ControladorBovedas;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Clase encargada de gestionar las principales operaciones sobre bóvedas
 * <p>
 * Esta clase actúa como punto de entrada de la capa de dominio para realizar operaciones sobre bóvedas, incluyendo el
 * cambio de su contraseña maestra
 * </p>
 *
 */
public class GestorBovedas {

    /**
     * Cambia la contraseña maestra de una bóveda
     *
     * @param ruta           ruta donde está alojada la bóveda
     * @param passwordActual contraseña actual de la bóveda
     * @param nuevaPassword  nueva contraseña que se desea asignar a la bóveda
     * @throws Exception si ocurrió algún error durante el proceso
     *
     *
     */
    public void cambiarPasswordMaestra(Path ruta, char[] passwordActual, char[] nuevaPassword) throws Exception {

        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Connection conexion = null;

        // Se prueba primero si se puede abrir la bóveda con la contraseña actual. Esto evita que se guarde la bóveda
        // con una contraseña incorrecta
        try {
            conexion = gestorPersistencia.abrirBovedaEnMemoria(ruta, passwordActual);

            gestorPersistencia.guardarBovedaDesdeMemoria(ruta, nuevaPassword, conexion);
        } finally {
            if (conexion != null) {
                conexion.close();
            }
        }
    }

    /**
     * Inicializa la tabla 'boveda'
     *
     * @param conexion conexión activa con la base de datos
     * @param nombre   nombre que se dará a la base de datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorBovedas#insertarBovedaInicial(Connection, String)
     *
     */
    public void insertarBovedaInicial(Connection conexion, String nombre) throws Exception {

        String sentenciaInsercion = """
                INSERT INTO boveda (id_boveda, nombre, umbral_alerta, accesibilidad, id_idioma, id_tema_visual)
                VALUES (1, ?, 7, 0, 1, 1)
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsercion)) {
            sentencia.setString(1, nombre);
            sentencia.executeUpdate();
        }
    }

    /**
     * Carga una bóveda desde la base de datos
     *
     * @param conexion conexión actual con la base de datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorBovedas#cargarBovedaActualDesdeBD(Connection)
     *
     *
     */
    public Boveda cargarBovedaActualDesdeBD(Connection conexion) throws Exception {

        String sentenciaCarga = """
                SELECT
                    b.id_boveda,
                    b.nombre,
                    b.umbral_alerta,
                    b.accesibilidad,
                    b.id_idioma,
                    i.nombre AS nombre_idioma,
                    b.id_tema_visual,
                    t.nombre AS nombre_tema_visual
                FROM boveda b
                JOIN idioma i ON b.id_idioma = i.id_idioma
                JOIN tema_visual t ON b.id_tema_visual = t.id_tema_visual
                WHERE b.id_boveda = 1
                LIMIT 1
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaCarga)) {
            ResultSet setResultados = sentencia.executeQuery();

            if (setResultados.next()) {


                Boveda boveda = new Boveda();

                Idioma idioma = new Idioma();
                idioma.setIdIdioma(setResultados.getInt("id_idioma"));
                idioma.setNombre(setResultados.getString("nombre_idioma"));

                TemaVisual temaVisual = new TemaVisual();
                temaVisual.setIdTemaVisual(setResultados.getInt("id_tema_visual"));
                temaVisual.setNombre(setResultados.getString("nombre_tema_visual"));

                boveda.setIdBoveda(setResultados.getInt("id_boveda"));
                boveda.setNombre(setResultados.getString("nombre"));
                boveda.setUmbralAlerta(setResultados.getInt("umbral_alerta"));
                boveda.setAccesibilidad(setResultados.getInt("accesibilidad") == 1);
                boveda.setIdioma(idioma);
                boveda.setTemaVisual(temaVisual);
                boveda.setModificadaSinGuardar(false);

                return boveda;
            }

        }

        throw new IllegalArgumentException("No se encontró ningún registro para esa bóveda en la base de datos");

    }

}
