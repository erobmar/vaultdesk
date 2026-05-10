package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase encargada de gestionar las operaciones de validación asociadas a operaciones con categorías dentro de una bóveda
 * <p>
 * Esta clase auxilixar actúa como apoyo para la clase GestorCategorias, tomando algunas de sus responsabilidades,
 * como la validación de conexiones, credenciales, y los nombres de nuevas categorías para evitar duplicados,
 * así como la comprobación de existencia de estas en la base de datos
 * </p>
 *
 */
public class GestorValidacionCategorias {


    private final GestorCategorias gestorCategorias;

    public GestorValidacionCategorias(GestorCategorias gestorCategorias) {
        this.gestorCategorias = gestorCategorias;
    }

    /**
     * Valida que una conexión no sea nula
     *
     * @param conexion conexión que se quiere validar
     *
     */
    public void validarConexion(Connection conexion) {
        if (conexion == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.conexionnula")); // "La conexión no puede ser nula"
        }
    }


    /**
     * Valida los campos de una categoría antes de su creación
     *
     * @param categoria categoría que se va a crear
     *
     */
    public void validarCategoriaCreacion(Categoria categoria) {
        if (categoria.getIdCategoria() == 1) {

            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categoriadefault")); // "No se puede modificar/eliminar la categoría por defecto"
        }

        if (categoria == null) {

            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categorianula")); // "La categoría no puede ser nula"
        }

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categoriasinnombre")); // "El nombre de la categoría no puede ser nulo"
        }
    }

    /**
     * Valida los campos de una categoría antes de su edición
     *
     * @param categoria categoría que se va a editar
     *
     */
    public void validarCategoriaEdicion(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categorianula")); // "La categoría no puede ser nula"
        }

        if (categoria.getIdCategoria() <= 0) {

            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.idcategoria")); // "Id de categoría no válido"
        }

        if (categoria.getIdCategoria() == 1) {

            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.editarotros")); // "La categoría 'Otros' no puede ser editada"
        }

        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categoriasinnombre")); // "La categoría debe tener un nombre"
        }

    }

    /**
     * Comprueba si la categoría que se va a eliminar es la categoría por defecto del sistema
     *
     * @param idCategoria identeificador de la categoría que se va a eliminar
     *
     */
    public void validarCategoriaEliminacion(int idCategoria) {
        if (idCategoria <= 1) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.idcategorianegativo")); // "El id de categoría debe ser mayor de 1"
        }
    }

    /**
     * Valida los identificadores de categoría que se van a reasignar a la categoría por defecto del sistema
     *
     * @param idCategoriaOriginal identificador de categoría que se va a reasignar
     *
     */
    public void validarCategoriaReasignacion(int idCategoriaOriginal) {

        if (idCategoriaOriginal <= 0) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.idcategorianegativa")); // "El id de la categoría debe ser mayor de 1"
        }
        if (idCategoriaOriginal == 1) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.reasignarotros")); // "La categoría con id=1 ('Otros') no se puede reasignar a sí misma"
        }

    }

    /**
     * Comprueba que no exista ya una categoría con ese nombre en la base de datos
     *
     * @param conexion conexión activa con la base de datos
     * @param nombre   nombre de la categoría que se va a comprobar
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     */
    public void comprobarNombreCategoriaLibre(Connection conexion, String nombre) throws SQLException {

        String sentenciaComprobacion = """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)) {

            sentencia.setString(1, nombre);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next() && resultado.getInt(1) > 0) {
                    throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.nombrecategoriaexiste"));  // "Ya existe una categoría con ese nombre"
                }

            }

        }


    }

    /**
     * Comprueba que existe una categoría en la base de datos con el identificador de categoría pasado como parámetro
     *
     * @param conexion    conexión activa con la base de datos
     * @param idCategoria identificador de la categoría que se quiere comprobar
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     *
     */
    public void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException {

        String sentenciaComprobacion = """
                SELECT COUNT(*)
                FROM categoria
                WHERE id_categoria = ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)) {

            sentencia.setInt(1, idCategoria);

            try (ResultSet setResultados = sentencia.executeQuery()) {

                if (setResultados.next() && setResultados.getInt(1) == 0) {
                    throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categorianoexiste")); // "La categoria no existe"
                }
            }
        }

    }

    /**
     * Comprueba si existe ese nombre con otro identificador (para edición de credenciales)
     *
     * @param conexion    conexión activa con la base de datos
     * @param nombre      nombre de la categoría que se quiere comprobar
     * @param idCategoria identificador de la categoría que se quiere comprobar
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     *
     */
    public void comprobarNombreSoloId(Connection conexion, String nombre, int idCategoria) throws SQLException {

        String sentenciaComrprobacion = """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                AND id_categoria <> ?
                """;

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaComrprobacion)) {

            sentencia.setString(1, nombre.trim());
            sentencia.setInt(2, idCategoria);

            try (ResultSet setResultado = sentencia.executeQuery()) {

                if (setResultado.next() && setResultado.getInt(1) > 0) {

                    throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.categoriayaexiste")); // "Ya existe otra categoría con ese nombre"
                }
            }
        }


    }

}
