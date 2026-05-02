package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar las operaciones validación asociadas a operaciones con credenciales dentro de una bóveda
 * <p>
 * Esta clase auxilixar actúa como apoyo para la clase GestorCredenciales, tomando algunas de sus responsabilidades,
 * como la validación de conexiones, bóvedas y credenciales, así como la comprobación de existencia de estas
 * </p>
 *
 */
public class GestorValidacionCredenciales {

    private final GestorCredenciales gestorCredenciales;


    public GestorValidacionCredenciales(GestorCredenciales gestorCredenciales) {
        this.gestorCredenciales = gestorCredenciales;
    }

    /**
     * Valida que una conexión no sea nula
     *
     * @param conexion conexión que se quiere validar
     * @see GestorCredenciales#validarConexion(Connection)
     *
     */
    public void validarConexion(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }
    }

    /**
     * Valida que el identificador de la bóveda sea correcta
     *
     * @param idBoveda identificador de la bóveda que se quiere validar
     * @see GestorCredenciales#validarBoveda(int)
     *
     */
    public void validarBoveda(int idBoveda) {
        if (idBoveda < 1) {
            throw new IllegalArgumentException("El id de la bóveda es incorrecto");
        }
    }

    /**
     * Valida que una credencial sea correcta
     *
     * @param credencial credencial que se quiere validar
     * @see GestorCredenciales#validarCredencial(Credencial)
     *
     */
    public void validarCredencial(Credencial credencial) {
        if (credencial == null) {
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }
        if (credencial.getUrlIdentificador() == null || credencial.getUrlIdentificador().isEmpty()) {
            throw new IllegalArgumentException("El campo 'URL/Identificador' no puede estar vacío");
        }
        if (credencial.getUsername() == null || credencial.getUsername().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Nombre de usuario' no puede estar vacío");
        }
        if (credencial.getPassword() == null || credencial.getPassword().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Contraseña' no puede estar vacío");
        }
        if (credencial.getIdCredencial() < 0) {
            throw new IllegalArgumentException("El id de credencial no es válido");
        }
    }

    /**
     * Comprueba si una bóveda existe en la base de datos
     *
     * @param conexion conexión activa con la base de datos
     * @param idBoveda identificador de la bóveda que se quiere comprobar
     * @see GestorCredenciales#comprobarExisteBoveda(Connection, int)
     *
     */
    public void comprobarExisteBoveda(Connection conexion, int idBoveda) throws SQLException {

        String sentenciaComprobacion = "SELECT COUNT (*) from boveda WHERE id_boveda = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)) {

            sentencia.setInt(1, idBoveda);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next() && resultado.getInt(1) == 0) {
                    throw new IllegalArgumentException("La bóveda indicada no existe");
                }
            }
        }
    }

    /**
     * Comprueba si una categoría existe en la base de datos
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se quiere comprobar
     *
     */
    public void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException {

        String sentenciaComprobacion = "SELECT COUNT(*) FROM categoria WHERE id_categoria = ?";

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)) {

            sentencia.setInt(1, idCategoria);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next() && resultado.getInt(1) == 0) {
                    throw new IllegalArgumentException("La categoría indicada no existe");
                }
            }
        }
    }

    /**
     * Comprueba si una credencial existe dentro de una bóveda en la base de datos
     *
     * @param conexion     conexión activa con la base de datos
     * @param idCredencial identificador de la credencial que se quiere comprobar
     * @param idBoveda     identificador de la bóveda que se quiere comprobar
     *
     */
    public void comprobarExisteCredencialEnBoveda(Connection conexion, int idCredencial, int idBoveda) throws SQLException {

        String sentenciaComprobacion = """
                SELECT COUNT(*) FROM credencial
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try (PreparedStatement sentecia = conexion.prepareStatement(sentenciaComprobacion)) {

            sentecia.setInt(1, idCredencial);
            sentecia.setInt(2, idBoveda);

            try (ResultSet resultado = sentecia.executeQuery()) {

                if (resultado.next() && resultado.getInt(1) == 0) {
                    throw new IllegalArgumentException("La credencial indicada no existe en la bóveda");
                }
            }


        }

    }

}
