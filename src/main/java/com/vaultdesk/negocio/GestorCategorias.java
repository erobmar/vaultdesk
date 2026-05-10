package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar las principales operaciones sobre categorías dentro de una bóveda
 * <p>
 * Esta clase actúa como punto de entrada de la capa de dominio para realizar operaciones de creción, edición,
 * y eliminación de categorías. Parte de esta lógica es derivada a la clase auxiliar GestorValidacionCredenciales
 * </p>
 *
 */
public class GestorCategorias {

    public static final int ID_CATEGORIA_OTROS = 1;

    /**
     * Inserta la categoría pasada como parámetro en la base de datos
     *
     * @param conexion  conexión activa con la base de datos
     * @param categoria categoría que se crea
     * @return identificador de categoría creada
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public int crearCategoria(Connection conexion, Categoria categoria) throws SQLException {

        GestorValidacionCategorias gestorValidacionCategorias = new GestorValidacionCategorias(this);
        gestorValidacionCategorias.validarConexion(conexion);
        gestorValidacionCategorias.validarCategoriaCreacion(categoria);
        gestorValidacionCategorias.comprobarNombreCategoriaLibre(conexion, categoria.getNombre());


        int nuevoIdCategoria = obtenerSiguienteIdCategoria(conexion);

        String senteniaCreacion = """
                INSERT INTO categoria(
                    id_categoria,
                    nombre,
                    descripcion,
                    es_del_sistema
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(senteniaCreacion)) {

            sentencia.setInt(1, nuevoIdCategoria);
            sentencia.setString(2, categoria.getNombre().trim());
            sentencia.setString(3, normalizarTextoOpcional(categoria.getDescripcion()));
            sentencia.setInt(4, categoria.isDelSistema() ? 1 : 0);

            sentencia.executeUpdate();
        }

        categoria.setIdCategoria(nuevoIdCategoria);

        return nuevoIdCategoria;
    }

    /**
     * Actualiza la categoría pasada como parámetro en la base de datos
     *
     * @param conexion  conexión activa con la base de datos
     * @param categoria categoría que se actualiza
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public void editarCategoria(Connection conexion, Categoria categoria) throws SQLException {

        GestorValidacionCategorias gestorValidacionCategorias = new GestorValidacionCategorias(this);
        gestorValidacionCategorias.validarConexion(conexion);
        gestorValidacionCategorias.validarCategoriaEdicion(categoria);
        gestorValidacionCategorias.comprobarExisteCategoria(conexion, categoria.getIdCategoria());
        gestorValidacionCategorias.comprobarNombreSoloId(conexion, categoria.getNombre().trim(), categoria.getIdCategoria());

        String sentenciaEdicion = """
                UPDATE categoria
                SET nombre=?, descripcion = ?
                WHERE id_categoria = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaEdicion)) {

            sentencia.setString(1, categoria.getNombre().trim());
            sentencia.setString(2, normalizarTextoOpcional(categoria.getDescripcion()));
            sentencia.setInt(3, categoria.getIdCategoria());

            int filas = sentencia.executeUpdate();

            if (filas == 0) {
                throw new SQLException(GestorIdiomas.getText("excepcion.actualizarcategoria")); // "No se puede actualizar la categoria"
            }
        }

    }

    /**
     * Elimina la categoría pasada como parámetro de la base de datos
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se elimina
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public void eliminarCategoria(Connection conexion, int idCategoria) throws SQLException {

        GestorValidacionCategorias gestorValidacionCategorias = new GestorValidacionCategorias(this);
        gestorValidacionCategorias.validarConexion(conexion);
        gestorValidacionCategorias.validarCategoriaEliminacion(idCategoria);
        gestorValidacionCategorias.comprobarExisteCategoria(conexion, idCategoria);
        gestorValidacionCategorias.comprobarExisteCategoria(conexion, ID_CATEGORIA_OTROS);

        reasignarCredencialesAOtros(conexion, idCategoria);

        String sentenciaEliminación = """
                DELETE FROM categoria
                WHERE id_categoria = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaEliminación)) {

            sentencia.setInt(1, idCategoria);

            int filasEliminadas = sentencia.executeUpdate();

            if (filasEliminadas == 0) {
                throw new SQLException(GestorIdiomas.getText("excepcion.eliminarcategoria")); // "Se produjo un error al eliminar la categoría"
            }
        }

    }

    /**
     * Reasigna todas las credenciales asignadas a una categoría como 'Otros'. Necesario al eliminar una categoría para
     * evitar que queden credenciales huérfanas de categoría
     *
     * @param conexion            conexión activa con la base de datos
     * @param idCategoriaOriginal categoría en la que están las credenciales ANTES de la reasignación
     * @return número de credenciales afectadas por la reasignación
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public int reasignarCredencialesAOtros(Connection conexion, int idCategoriaOriginal) throws SQLException {

        GestorValidacionCategorias gestorValidacionCategorias = new GestorValidacionCategorias(this);
        gestorValidacionCategorias.validarConexion(conexion);
        gestorValidacionCategorias.validarCategoriaReasignacion(idCategoriaOriginal);
        gestorValidacionCategorias.comprobarExisteCategoria(conexion, idCategoriaOriginal);
        gestorValidacionCategorias.comprobarExisteCategoria(conexion, ID_CATEGORIA_OTROS);

        String sentenciaReasignacion = """
                UPDATE credencial
                SET 
                id_categoria = ?
                WHERE id_categoria = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaReasignacion)) {
            sentencia.setInt(1, ID_CATEGORIA_OTROS);
            sentencia.setInt(2, idCategoriaOriginal);

            return sentencia.executeUpdate();

        }


    }

    /**
     * Obtiene el listado de todas las categorías presentes en la base de datos
     *
     * @param conexion conexión activa con la base de datos
     * @return lista con todas las credenciales presentes en la base de datos
     * @throws Exception si la conexión con la base de datos presenta algún problema
     *
     */
    public List<Categoria> obtenerCategorias(Connection conexion) throws Exception {

        List<Categoria> listaCategorias = new ArrayList<>();
        GestorValidacionCategorias gestorValidacionCategorias = new GestorValidacionCategorias(this);

        gestorValidacionCategorias.validarConexion(conexion);

        String sentenciaObtencion = """
                SELECT id_categoria, nombre, descripcion, es_del_sistema
                FROM categoria
                ORDER BY es_del_sistema DESC, nombre ASC
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)) {

            ResultSet setResultados = sentencia.executeQuery();

            while (setResultados.next()) {

                Categoria categoria = new Categoria();

                categoria.setIdCategoria(setResultados.getInt("id_categoria"));
                categoria.setNombre(setResultados.getString("nombre"));
                categoria.setDescripcion(setResultados.getString("descripcion"));
                categoria.setEsDelSistema(setResultados.getInt("es_del_sistema") == 1);

                listaCategorias.add(categoria);
            }

        }
        return listaCategorias;
    }


    /**
     * Obtiene el identificador de la siguiente categoría que se insertaría en la base de datos
     *
     */
    private int obtenerSiguienteIdCategoria(Connection conexion) throws SQLException {

        String sentenciaObtencion = """
                SELECT COALESCE(MAX(id_categoria), 0) + 1
                FROM categoria
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)) {
            ResultSet resultado = sentencia.executeQuery();

            if (resultado.next()) {
                return resultado.getInt(1);
            }
            throw new SQLException(GestorIdiomas.getText("excepcion.siguienteid")); // "No se puede calcular el siguiente idCategoria"
        }


    }

    /**
     * Normaliza los campos de texto opcionales
     *
     */
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


    // Clases de record
    public record DatosNuevaCategoria(
            String nombre,
            String descripcion
    ) {
    }

    public record DatosEdicionCategoria(
            int idCategoria,
            String nombre,
            String descripcion
    ) {
    }

}
