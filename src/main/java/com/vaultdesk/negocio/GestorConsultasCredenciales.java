package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar las operaciones sobre consultas de credenciales dentro de una bóveda
 * <p>
 * Esta clase auxilixar actúa como apoyo para la clase GestorCredenciales, tomando algunas de sus responsabilidades,
 * como la obtención del listado de credenciales caducadas o próximas a caducar y el cálculo de la fecha de caducidad
 * efectiva de las credenciales
 * </p>
 *
 */
public class GestorConsultasCredenciales {

    private final GestorCredenciales gestorCredenciales;

    public GestorConsultasCredenciales(GestorCredenciales gestorCredenciales) {
        this.gestorCredenciales = gestorCredenciales;
    }

    /**
     * Busca en la bóveda las credenciales que coincidan en sus campos url/identificador, username, anotaciones o
     * categoría coincidan con el parámetro textoBusqueda
     *
     * @param conexion      conexión activa con la base de datos
     * @param idBoveda      identificador de la bóveda en la que se busca
     * @param textoBusqueda criterio de búsqueda
     * @return Lista de credenciales que cumplen el criterio de búsqueda
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorCredenciales#buscarCredenciales(Connection, int, String)
     *
     */
    public List<Credencial> buscarCredenciales(Connection conexion, int idBoveda, String textoBusqueda) throws SQLException {

        gestorCredenciales.validarConexion(conexion);
        gestorCredenciales.validarBoveda(idBoveda);

        gestorCredenciales.comprobarExisteBoveda(conexion, idBoveda);

        String textoNormalizado = textoBusqueda == null ? "" : textoBusqueda.trim();

        if (textoNormalizado.isEmpty()) {
            return obtenerCredencialesDeBoveda(conexion, idBoveda);
        }


        String sentenciaBusqueda = """
                SELECT
                    c.id_credencial,
                    c.url_identificador,
                    c.username,
                    c.password,
                    c.destacada,
                    c.anotaciones,
                    c.caduca,
                    c.fecha_caducidad,
                    c.periodo_caducidad,
                    c.ultimo_update,
                    c.req_longitud,
                    c.req_mayusculas,
                    c.req_minusculas,
                    c.req_digitos,
                    c.req_especiales,
                    c.id_categoria,
                    cat.nombre AS nombre_categoria,
                    cat.descripcion AS descripcion_categoria,
                    cat.es_del_sistema
                FROM credencial c
                JOIN categoria cat ON c.id_categoria = cat.id_categoria
                WHERE id_boveda = ?
                AND(
                    LOWER(c.url_identificador) LIKE ?
                    OR LOWER(c.username) LIKE ?
                    OR LOWER(COALESCE(c.anotaciones, '')) LIKE ?
                    OR LOWER(cat.nombre) LIKE ?
                )
                ORDER BY c.username
                """;

        List<Credencial> resultados = new ArrayList<>();

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaBusqueda)) {

            String patronBusqueda = "%" + textoNormalizado + "%";

            sentencia.setInt(1, idBoveda);
            sentencia.setString(2, patronBusqueda);
            sentencia.setString(3, patronBusqueda);
            sentencia.setString(4, patronBusqueda);
            sentencia.setString(5, patronBusqueda);

            try (ResultSet setResultados = sentencia.executeQuery()) {

                while (setResultados.next()) {

                    resultados.add(mapearCredencial(setResultados));
                }
            }

        }

        return resultados;

    }

    /**
     * Busca en la bóveda las credenciales que pertenecen a la categoría pasada como parámetro
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se busca
     * @return Lista de credenciales que pertenecen a la categoría
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorCredenciales#obtenerCredencialesPorCategoria(Connection, int)
     *
     */
    public List<Credencial> obtenerCredencialesPorCategoria(Connection conexion, int idCategoria) throws Exception {

        String sentenciaObtencion = """
                SELECT
                    c.id_credencial,
                    c.url_identificador,
                    c.username,
                    c.password,
                    c.destacada,
                    c.anotaciones,
                    c.caduca,
                    c.fecha_caducidad,
                    c.periodo_caducidad,
                    c.ultimo_update,
                    c.req_longitud,
                    c.req_mayusculas,
                    c.req_minusculas,
                    c.req_digitos,
                    c.req_especiales,
                    c.id_categoria,
                    cat.nombre AS nombre_categoria,
                    cat.descripcion AS descripcion_categoria,
                    cat.es_del_sistema
                FROM credencial c
                JOIN categoria cat ON c.id_categoria = cat.id_categoria
                WHERE c.id_categoria = ?
                ORDER BY c.username
                """;

        List<Credencial> listaCredencialesFiltrada = new ArrayList<>();

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)) {

            sentencia.setInt(1, idCategoria);

            ResultSet setResultados = sentencia.executeQuery();

            while (setResultados.next()) {
                listaCredencialesFiltrada.add(mapearCredencial(setResultados));
            }

        }


        return listaCredencialesFiltrada;
    }

    /**
     * Busca en la bóveda las credenciales destacadas
     *
     * @param conexion conexión activa con la base de datos
     * @param idBoveda identificador de la bóveda en la que se busca
     * @return Lista de credenciales destacadas
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorCredenciales#obtenerCredencialesDestacadas(Connection, int)
     *
     */
    public List<Credencial> obtenerCredencialesDestacadas(Connection conexion, int idBoveda) throws Exception {

        gestorCredenciales.validarConexion(conexion);
        gestorCredenciales.validarBoveda(idBoveda);

        String sentenciaObtencion = """
                SELECT
                    c.id_credencial,
                    c.url_identificador,
                    c.username,
                    c.password,
                    c.destacada,
                    c.anotaciones,
                    c.caduca,
                    c.fecha_caducidad,
                    c.periodo_caducidad,
                    c.ultimo_update,
                    c.req_longitud,
                    c.req_mayusculas,
                    c.req_minusculas,
                    c.req_digitos,
                    c.req_especiales,
                    c.id_categoria,
                    cat.nombre AS nombre_categoria,
                    cat.descripcion AS descripcion_categoria,
                    cat.es_del_sistema
                FROM credencial c
                JOIN categoria cat ON c.id_categoria = cat.id_categoria
                WHERE c.id_boveda = ? 
                    AND c.destacada = 1
                ORDER BY c.username
                """;

        List<Credencial> listaDestacadas = new ArrayList<>();

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)) {
            sentencia.setInt(1, idBoveda);

            try (ResultSet setResultados = sentencia.executeQuery()) {
                while (setResultados.next()) {
                    listaDestacadas.add(mapearCredencial(setResultados));
                }
            }

        }

        return listaDestacadas;

    }

    private Credencial mapearCredencial(ResultSet setReultados) throws SQLException {

        Credencial credencial = new Credencial();

        credencial.setIdCredencial(setReultados.getInt("id_credencial"));
        credencial.setUrlIdentificador(setReultados.getString("url_identificador"));
        credencial.setUsername(setReultados.getString("username"));
        credencial.setPassword(setReultados.getString("password"));
        credencial.setDestacada((setReultados.getInt("destacada")) == 1);
        credencial.setAnotaciones(setReultados.getString("anotaciones"));
        credencial.setCaduca((setReultados.getInt("caduca")) == 1);

        String fechaCaducidad = setReultados.getString("fecha_caducidad");
        if (fechaCaducidad != null) {
            credencial.setFechaCaducidad(LocalDate.parse(fechaCaducidad));
        }

        int periodoCaducidad = setReultados.getInt("periodo_caducidad");
        if (!setReultados.wasNull()) {
            credencial.setPeriodoCaducidad(periodoCaducidad);
        }

        String fechaUltimoUpdate = setReultados.getString("ultimo_update");
        if (fechaUltimoUpdate != null) {
            credencial.setFechaUltimoUpdate(LocalDate.parse(fechaUltimoUpdate));
        }

        credencial.setReqLongitud(setReultados.getInt("req_longitud"));
        credencial.setReqMayusculas(setReultados.getInt("req_mayusculas"));
        credencial.setReqMinusculas(setReultados.getInt("req_minusculas"));
        credencial.setReqDigitos(setReultados.getInt("req_digitos"));
        credencial.setReqEspeciales(setReultados.getInt("req_especiales"));

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(setReultados.getInt("id_categoria"));
        categoria.setNombre(setReultados.getString("nombre_categoria"));
        categoria.setDescripcion(setReultados.getString("descripcion_categoria"));
        categoria.setEsDelSistema(setReultados.getInt("es_del_sistema") == 1);

        credencial.setCategoria(categoria);

        return credencial;
    }

    private List<Credencial> obtenerCredencialesDeBoveda(Connection conexion, int idBoveda) throws SQLException {

        String sentenciaConsulta = """
                SELECT
                    c.id_credencial,
                    c.url_identificador,
                    c.username,
                    c.password,
                    c.destacada,
                    c.anotaciones,
                    c.caduca,
                    c.fecha_caducidad,
                    c.periodo_caducidad,
                    c.ultimo_update,
                    c.req_longitud,
                    c.req_mayusculas,
                    c.req_minusculas,
                    c.req_digitos,
                    c.req_especiales,
                    c.id_categoria,
                    cat.nombre AS nombre_categoria,
                    cat.descripcion AS descripcion_categoria,
                    cat.es_del_sistema
                FROM credencial c
                JOIN categoria cat ON c.id_categoria = cat.id_categoria
                WHERE c.id_boveda = ?
                ORDER BY c.username
                """;

        List<Credencial> credenciales = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaConsulta)) {

            sentencia.setInt(1, idBoveda);

            try (ResultSet setResultados = sentencia.executeQuery()) {
                while (setResultados.next()) {
                    credenciales.add(mapearCredencial(setResultados));
                }
            }


        }

        return credenciales;
    }

}
