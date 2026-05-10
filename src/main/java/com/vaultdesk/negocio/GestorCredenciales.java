package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.ui.AlertaCaducidad;

import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;


/**
 * Clase encargada de gestionar las principales operaciones sobre credenciales dentro de una bóveda
 * <p>
 * Esta clase actúa como punto de entrada de la capa de dominio para realizar operaciones de creción, edición,
 * eliminación de credenciales y consulta y exportación de listados de credenciales. Parte de esta lógica es
 * derivada a las clases auxiliares GestorCaducidadCredenciales, GestorConsultasCredenciales y
 * GestorExportaciónCredenciales
 * </p>
 *
 */
public class GestorCredenciales {

    private static final int ID_CATEGORIA_OTROS = 1;
    private final GestorCaducidadCredenciales gestorCaducidadCredenciales = new GestorCaducidadCredenciales(this);
    private final GestorConsultasCredenciales gestorConsultasCredenciales = new GestorConsultasCredenciales(this);
    private final GestorExportacionCredenciales gestorExportacionCredenciales = new GestorExportacionCredenciales(this);
    private final GestorValidacionCredenciales gestorValidacionCredenciales = new GestorValidacionCredenciales(this);


    // Métodos relativos a consultas de credenciales -> Redirigidos a GestorConsultasCredenciales

    /**
     * Busca en la bóveda las credenciales que coincidan en sus campos url/identificador, username, anotaciones o
     * categoría coincidan con el parámetro textoBusqueda
     *
     * @param conexion      conexión activa con la base de datos
     * @param idBoveda      identificador de la bóveda en la que se busca
     * @param textoBusqueda criterio de búsqueda
     * @return Lista de credenciales que cumplen el criterio de búsqueda
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorConsultasCredenciales#buscarCredenciales(Connection, int, String)
     *
     */
    public List<Credencial> buscarCredenciales(Connection conexion, int idBoveda, String textoBusqueda) throws SQLException {

        return gestorConsultasCredenciales.buscarCredenciales(conexion, idBoveda, textoBusqueda);
    }

    /**
     * Busca en la bóveda las credenciales que pertenecen a la categoría pasada como parámetro
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se busca
     * @return Lista de credenciales que pertenecen a la categoría
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorConsultasCredenciales#obtenerCredencialesPorCategoria(Connection, int)
     *
     */
    public List<Credencial> obtenerCredencialesPorCategoria(Connection conexion, int idCategoria) throws Exception {

        return gestorConsultasCredenciales.obtenerCredencialesPorCategoria(conexion, idCategoria);
    }

    /**
     * Busca en la bóveda las credenciales destacadas
     *
     * @param conexion conexión activa con la base de datos
     * @param idBoveda identificador de la bóveda en la que se busca
     * @return Lista de credenciales destacadas
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorConsultasCredenciales#obtenerCredencialesDestacadas(Connection, int)
     *
     */
    public List<Credencial> obtenerCredencialesDestacadas(Connection conexion, int idBoveda) throws Exception {

        return gestorConsultasCredenciales.obtenerCredencialesDestacadas(conexion, idBoveda);
    }


    // Métodos relativos a caducidad de credenciales -> Redirigidos a GestorCaducidadCredenciales

    /**
     * Obtiene la lista de alertas por caducidad de credenciales
     *
     * @param listaCredenciales lista de todas las credenciales de la bóveda
     * @param bovedaActual      bóveda en la que se está trabajando
     * @return lista de alertas por caducidad de credenciales
     * @see GestorCaducidadCredenciales#obtenerAlertasCaducidad(List, Boveda)
     *
     */
    public List<AlertaCaducidad> obtenerAlertasCaducidad(List<Credencial> listaCredenciales, Boveda bovedaActual) {
        return gestorCaducidadCredenciales.obtenerAlertasCaducidad(listaCredenciales, bovedaActual);
    }

    // Métodos relativos a exportación de credenciales -> Redirigidos a GestorExportacionCredenciales

    /**
     * Exporta un listado con todas las credenciales de la bóveda a un archivo CSV
     *
     * @param credenciales lista de todas las credenciales de la bóveda
     * @param rutaDestino  directorio en el que se creará el archivo CSV
     * @see GestorExportacionCredenciales#exportarCredencialesCSV(List, Path)
     *
     */
    public void exportarCredencialesCSV(List<Credencial> credenciales, Path rutaDestino) throws Exception {

        gestorExportacionCredenciales.exportarCredencialesCSV(credenciales, rutaDestino);
    }

    // Métodos de validación -> Redirigidos a GestorValidacionCredenciales

    /**
     * Centraliza las validaciones de conexión, bóveda y credencial
     *
     * @param conexion   conexión activa con la base de datos
     * @param idBoveda   identificador de la bóveda con la que se está trabajando
     * @param credencial credencial sobre la que se va a realizar la operación
     *
     */
    public void validarOperacion(Connection conexion, int idBoveda, Credencial credencial) {
        validarConexion(conexion);
        validarBoveda(idBoveda);
        validarCredencial(credencial);
    }

    /**
     * Valida la conexión con la base de datos antes de realizar una operación
     *
     * @param conexion conexión activa con la base de datos
     * @see GestorValidacionCredenciales#validarConexion(Connection)
     *
     */
    public void validarConexion(Connection conexion) {

        gestorValidacionCredenciales.validarConexion(conexion);
    }

    /**
     * Valida que la bóveda es correcta antes de realizar una operación
     *
     * @param idBoveda identificador de la bóveda con la que se está trabajando
     * @see GestorValidacionCredenciales#validarBoveda(int)
     *
     */
    public void validarBoveda(int idBoveda) {

        gestorValidacionCredenciales.validarBoveda(idBoveda);
    }

    /**
     * Valida la credencial antes de realizar una operación con ella
     *
     * @param credencial credencial sobre la que se va a realizar la operación
     * @see GestorValidacionCredenciales#validarCredencial(Credencial)
     *
     */
    public void validarCredencial(Credencial credencial) {

        gestorValidacionCredenciales.validarCredencial(credencial);
    }

    /**
     * Comprueba si existe la bóveda indicada en la base de datos
     *
     * @param conexion conexión activa con la base de datos
     * @param idBoveda identificador de la bóveda con la que se está trabajando
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorValidacionCredenciales#comprobarExisteBoveda(Connection, int)
     *
     */
    public void comprobarExisteBoveda(Connection conexion, int idBoveda) throws SQLException {

        gestorValidacionCredenciales.comprobarExisteBoveda(conexion, idBoveda);
    }

    /**
     * Comprueba si existe la categoría indicada en la base de datos
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se quiere comprobar
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorValidacionCredenciales#comprobarExisteCategoria(Connection, int)
     *
     */
    private void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException {

        gestorValidacionCredenciales.comprobarExisteCategoria(conexion, idCategoria);
    }

    /**
     * Comprueba si existe la credencial indicada en la base de datos
     *
     * @param conexion     conexión activa con la base de datos
     * @param idCredencial credencial que se quiere comprobar
     * @param idBoveda     identificador de la bóveda con la que se está trabajando
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorValidacionCredenciales#comprobarExisteCredencialEnBoveda(Connection, int, int)
     *
     */
    private void comprobarExisteCredencialEnBoveda(Connection conexion, int idCredencial, int idBoveda) throws SQLException {

        gestorValidacionCredenciales.comprobarExisteCredencialEnBoveda(conexion, idCredencial, idBoveda);
    }


    // Métodos de actualización de credenciales -> Redirigidos a GestorActualizacionCredenciales

    /**
     * Inserta la credencial dada en la base de datos
     *
     * @param credencial credencial que se inserta en la base de datos
     * @param idBoveda   identificador de la bóveda con la que se está trabajando
     * @param conexion   conexión activa con la base de datos
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public int crearCredencial(Credencial credencial, int idBoveda, Connection conexion) throws Exception {

        validarOperacion(conexion, idBoveda, credencial);

        comprobarExisteBoveda(conexion, idBoveda);

        int idCategoria = obtenerIdCategoria(credencial.getCategoria());
        comprobarExisteCategoria(conexion, idCategoria);

        int nuevoIdCredencial = obtenerSiguienteIdCredencial(conexion);

        String sentenciaInsercion = """
                INSERT INTO credencial(
                    id_credencial,
                    url_identificador,
                    username,
                    password,
                    destacada,
                    anotaciones,
                    caduca,
                    fecha_caducidad,
                    periodo_caducidad,
                    ultimo_update,
                    req_longitud,
                    req_mayusculas,
                    req_minusculas,
                    req_digitos,
                    req_especiales,
                    id_boveda,
                    id_categoria
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsercion)) {

            sentencia.setInt(1, nuevoIdCredencial);
            sentencia.setString(2, credencial.getUrlIdentificador().trim());
            sentencia.setString(3, credencial.getUsername().trim());
            sentencia.setString(4, credencial.getPassword()); // OJO - No lleva trim() porque puede contener espacios
            sentencia.setInt(5, credencial.isDestacada() ? 1 : 0);
            sentencia.setString(6, normalizarTextoOpcional(credencial.getAnotaciones()));
            sentencia.setInt(7, credencial.isCaduca() ? 1 : 0);

            if (credencial.getFechaCaducidad() == null) {
                sentencia.setNull(8, Types.VARCHAR);
            } else {
                sentencia.setString(8, credencial.getFechaCaducidad().toString());
            }

            if (credencial.getPeriodoCaducidad() <= 0) {
                sentencia.setNull(9, Types.INTEGER);
            } else {
                sentencia.setInt(9, credencial.getPeriodoCaducidad());
            }

            if (credencial.getFechaUltimoUpdate() == null) {
                sentencia.setNull(10, Types.VARCHAR);
            } else {
                sentencia.setString(10, credencial.getFechaUltimoUpdate().toString());
            }

            if (credencial.getReqLongitud() <= 0) {
                sentencia.setNull(11, Types.INTEGER);
            } else {
                sentencia.setInt(11, credencial.getReqLongitud());
            }

            if (credencial.getReqMayusculas() <= 0) {
                sentencia.setNull(12, Types.INTEGER);
            } else {
                sentencia.setInt(12, credencial.getReqMayusculas());
            }

            if (credencial.getReqMinusculas() <= 0) {
                sentencia.setNull(13, Types.INTEGER);
            } else {
                sentencia.setInt(13, credencial.getReqMinusculas());
            }

            if (credencial.getReqDigitos() <= 0) {
                sentencia.setNull(14, Types.INTEGER);
            } else {
                sentencia.setInt(14, credencial.getReqDigitos());
            }

            if (credencial.getReqEspeciales() <= 0) {
                sentencia.setNull(15, Types.INTEGER);
            } else {
                sentencia.setInt(15, credencial.getReqEspeciales());
            }

            sentencia.setInt(16, idBoveda);
            sentencia.setInt(17, idCategoria);

            sentencia.executeUpdate();

        }

        credencial.setIdCredencial(nuevoIdCredencial);
        return nuevoIdCredencial;
    }

    /**
     * Actualiza los datos de una credencial en la base de datos
     *
     * @param conexion   conexión activa con la base de datos
     * @param boveda     bóveda en la que se está trabajando
     * @param credencial credencial que se actualiza en la base de datos
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public void editarCredencial(Connection conexion, Boveda boveda, Credencial credencial) throws SQLException {

        validarOperacion(conexion, boveda.getIdBoveda(), credencial);

        comprobarExisteCredencialEnBoveda(conexion, credencial.getIdCredencial(), boveda.getIdBoveda());

        int idCategoria = 1; // Categoría por defecto

        if (credencial.getCategoria() != null && credencial.getCategoria().getIdCategoria() > 0) {
            idCategoria = credencial.getCategoria().getIdCategoria();
        }

        String sentenciaActualizacion = """
                UPDATE credencial
                SET
                    url_identificador = ?,
                    username = ?,
                    password = ?,
                    destacada = ?,
                    anotaciones = ?,
                    caduca = ?,
                    ultimo_update = ?,
                    fecha_caducidad = ?,
                    periodo_caducidad = ?,
                    req_longitud = ?,
                    req_mayusculas = ?,
                    req_minusculas = ?,
                    req_digitos = ?,
                    req_especiales = ?,
                    id_categoria = ?
                WHERE id_credencial = ?
                AND id_boveda = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)) {

            sentencia.setString(1, credencial.getUrlIdentificador());
            sentencia.setString(2, credencial.getUsername());
            sentencia.setString(3, credencial.getPassword());
            sentencia.setInt(4, credencial.isDestacada() ? 1 : 0);
            sentencia.setString(5, credencial.getAnotaciones());
            sentencia.setInt(6, credencial.isCaduca() ? 1 : 0);

            if (credencial.getFechaUltimoUpdate() != null) {
                sentencia.setString(7, credencial.getFechaUltimoUpdate().toString());
            } else {
                sentencia.setNull(7, Types.VARCHAR);
            }

            if (credencial.getFechaCaducidad() != null) {
                sentencia.setString(8, credencial.getFechaCaducidad().toString());
            } else {
                sentencia.setNull(8, Types.VARCHAR);
            }

            if (credencial.getPeriodoCaducidad() > 0) {
                sentencia.setInt(9, credencial.getPeriodoCaducidad());

            } else {
                sentencia.setNull(9, Types.INTEGER);
            }

            if (credencial.getReqLongitud() > 0) {
                sentencia.setInt(10, credencial.getReqLongitud());
            } else {
                sentencia.setNull(10, Types.INTEGER);
            }

            if (credencial.getReqMayusculas() > 0) {
                sentencia.setInt(11, credencial.getReqMayusculas());
            } else {
                sentencia.setNull(11, Types.INTEGER);
            }
            if (credencial.getReqMinusculas() > 0) {
                sentencia.setInt(12, credencial.getReqMinusculas());
            } else {
                sentencia.setNull(12, Types.INTEGER);
            }
            if (credencial.getReqDigitos() > 0) {
                sentencia.setInt(13, credencial.getReqDigitos());
            } else {
                sentencia.setNull(13, Types.INTEGER);
            }
            if (credencial.getReqEspeciales() > 0) {
                sentencia.setInt(14, credencial.getReqEspeciales());
            } else {
                sentencia.setNull(14, Types.INTEGER);
            }

            sentencia.setInt(15, idCategoria);
            sentencia.setInt(16, credencial.getIdCredencial());
            sentencia.setInt(17, boveda.getIdBoveda());

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException(GestorIdiomas.getText("excepcion.actualizarcredencial")); // "No se pudo actualizar la credencial"
            }


        }


    }

    /**
     * Elimina los datos de una credencial de la base de datos
     *
     * @param conexion   conexión activa con la base de datos
     * @param idBoveda   identificador de la bóveda en la que se está trabajando
     * @param credencial credencial que se elimina de la base de datos
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public void eliminarCredencial(Connection conexion, int idBoveda, Credencial credencial) throws SQLException {

        int idCredencial = credencial.getIdCredencial();

        validarOperacion(conexion, idBoveda, credencial);

        comprobarExisteBoveda(conexion, idBoveda);
        comprobarExisteCredencialEnBoveda(conexion, idCredencial, idBoveda);

        String sentenciaEliminacion = """
                DELETE FROM credencial
                WHERE id_credencial = ?
                AND id_boveda = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaEliminacion)) {

            sentencia.setInt(1, idCredencial);
            sentencia.setInt(2, idBoveda);

            int filasEliminadas = sentencia.executeUpdate();

            if (filasEliminadas == 0) {

                throw new SQLException(GestorIdiomas.getText("excepcion.eliminarcredencial")); // "No se pudo eliminar la credencial"
            }
        }


    }

    /**
     * Actualiza la contraseña de una credencial en la base de datos
     *
     * @param conexion   conexión activa con la base de datos
     * @param idBoveda   identificador de la bóveda en la que se está trabajando
     * @param credencial credencial que se actualiza en la base de datos
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public String actualizarPassword(Connection conexion, int idBoveda, Credencial credencial) throws SQLException {

        validarOperacion(conexion, idBoveda, credencial);

        comprobarExisteCredencialEnBoveda(conexion, credencial.getIdCredencial(), idBoveda);

        GestorPasswords gestorPasswords = new GestorPasswords();

        String nuevoPassword = gestorPasswords.generarPassword(credencial);
        LocalDate fechaActualizacion = LocalDate.now();

        String sentenciaActualizacion = """
                UPDATE credencial
                SET password = ?, ultimo_update = ?
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)) {

            sentencia.setString(1, nuevoPassword);
            sentencia.setString(2, fechaActualizacion.toString());
            sentencia.setInt(3, credencial.getIdCredencial());
            sentencia.setInt(4, idBoveda);

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {
                throw new SQLException(GestorIdiomas.getText("excepcion.actualizarpassword")); // "No se pudo actualizar el password de la credencial"
            }

            credencial.setPassword(nuevoPassword);
            credencial.setFechaUltimoUpdate(fechaActualizacion);

            return nuevoPassword;

        }

    }

    /**
     * Actualiza el campo 'destacada' de una credencial en la base de datos
     *
     * @param conexion     conexión activa con la base de datos
     * @param idCredencial identificador de la credencial que se actualiza
     * @param nuevoValor   true si es destacada, false en caso contrario
     * @throws Exception si la conexión con la base de datos presenta algún problema
     *
     */
    public void actualizarDestacada(Connection conexion, int idCredencial, boolean nuevoValor) throws Exception {

        validarConexion(conexion);

        String sentenciaActualizacion = """
                UPDATE credencial
                SET destacada = ?
                WHERE id_credencial = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)) {

            sentencia.setInt(1, nuevoValor ? 1 : 0);
            sentencia.setInt(2, idCredencial);

            sentencia.executeUpdate();
        }
    }


    // Otros métodos auxiliares

    private int obtenerIdCategoria(Categoria categoria) {

        if (categoria == null || categoria.getIdCategoria() <= 0) {
            return ID_CATEGORIA_OTROS;
        }
        return categoria.getIdCategoria();

    }

    private int obtenerSiguienteIdCredencial(Connection conexion) throws SQLException {

        String sentenciaSiguienteCredencial = "SELECT COALESCE(MAX(id_credencial), 0) + 1 FROM credencial";

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaSiguienteCredencial)) {

            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return resultado.getInt(1);
            }

        }
        throw new SQLException(GestorIdiomas.getText("excepcion.idcredencial")); // "No se pudo obtener el id de la credencial"
    }

    private String normalizarTextoOpcional(String texto) {

        if (texto == null) {
            return null;
        }

        String textoLimpio = texto.trim();

        if (textoLimpio.isEmpty()) {
            return null;
        }

        return textoLimpio;
    }

}
