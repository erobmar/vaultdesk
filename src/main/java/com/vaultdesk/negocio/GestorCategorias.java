package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GestorCategorias {

    public static final int ID_CATEGORIA_OTROS = 1;

    public int crearCategoria(Connection conexion, Categoria categoria) throws SQLException {

        validarConexion(conexion);
        validarCategoriaCreacion(categoria);

        comprobarNombreCategoriaLibre(conexion, categoria.getNombre());

        int nuevoIdCategoria = obtenerSiguienteIdCategoria(conexion);

        String senteniaCreacion= """
                INSERT INTO categoria(
                    id_categoria,
                    nombre,
                    descripcion,
                    es_del_sistema
                )
                VALUES (?, ?, ?, ?)
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(senteniaCreacion)){

            sentencia.setInt(1, nuevoIdCategoria);
            sentencia.setString(2, categoria.getNombre().trim());
            sentencia.setString(3, normalizarTextoOpcional(categoria.getDescripcion()));
            sentencia.setInt(4, categoria.isDelSistema() ? 1 : 0);

            sentencia.executeUpdate();
        }

        categoria.setIdCategoria(nuevoIdCategoria);

        return nuevoIdCategoria;
    }

    public void editarCategoria(Connection conexion, Categoria categoria) throws SQLException{

        validarConexion(conexion);

        if(categoria == null){
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }

        if(categoria.getIdCategoria() <= 0){

            throw new IllegalArgumentException("Id de categoría no válido");
        }

        if(categoria.getIdCategoria() == ID_CATEGORIA_OTROS){

            throw new IllegalArgumentException("La categoría 'Otros' no puede ser editada");
        }

        if(categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()){

            throw new IllegalArgumentException("La categoría debe tener un nombre");
        }

        comprobarExisteCategoria(conexion, categoria.getIdCategoria());
        comprobarNombreSoloId(conexion, categoria.getNombre().trim(), categoria.getIdCategoria());

        String sentenciaEdicion = """
                UPDATE categoria
                SET nombre=?, descripcion = ?
                WHERE id_categoria = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaEdicion)){

            sentencia.setString(1, categoria.getNombre().trim());
            sentencia.setString(2, normalizarTextoOpcional(categoria.getDescripcion()));
            sentencia.setInt(3, categoria.getIdCategoria());

            int filas = sentencia.executeUpdate();

            if(filas == 0){
                throw new SQLException("No se puede actualizar la categoria");
            }
        }

    }

    public int reasignarCredencialesAOtros(Connection conexion, int idCategoriaOriginal) throws SQLException{

        validarConexion(conexion);

        if(idCategoriaOriginal <= 0){
            throw new IllegalArgumentException("El id de la categoría debe ser mayor de 1");
        }
        if(idCategoriaOriginal == ID_CATEGORIA_OTROS){
            throw new IllegalArgumentException("La categoría con id=1 ('Otros') no se puede reasignar a sí misma");
        }

        comprobarExisteCategoria(conexion, idCategoriaOriginal);
        comprobarExisteCategoria(conexion, ID_CATEGORIA_OTROS);

        String sentenciaReasignacion = """
                UPDATE credencial
                SET 
                id_categoria = ?
                WHERE id_categoria = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaReasignacion)){
            sentencia.setInt(1, ID_CATEGORIA_OTROS);
            sentencia.setInt(2, idCategoriaOriginal);

            return sentencia.executeUpdate();

        }



    }

    public void eliminarCategoria(Connection conexion, int idCategoria) throws SQLException{

        validarConexion(conexion);

        if(idCategoria <= ID_CATEGORIA_OTROS){
            throw new IllegalArgumentException("El id de categoría debe ser mayor de 1");
        }

        comprobarExisteCategoria(conexion, idCategoria);
        comprobarExisteCategoria(conexion, ID_CATEGORIA_OTROS);

        reasignarCredencialesAOtros(conexion, idCategoria);

        String sentenciaEliminación = """
                DELETE FROM categoria
                WHERE id_categoria = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaEliminación)){

            sentencia.setInt(1, idCategoria);

            int filasEliminadas = sentencia.executeUpdate();

            if(filasEliminadas == 0){
                throw new SQLException("Se produjo un error al eliminar la categoría");
            }
        }

    }

    private void validarConexion(Connection conexion) {
        if(conexion == null){
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }
    }


    /**
     * Valida los campos de una categoría antes de su creación
     * */
    private void validarCategoriaCreacion(Categoria categoria){
        if(categoria.getIdCategoria() == 1){

            throw new IllegalArgumentException("No se puede modificar/eliminar la categoría por defecto");
        }

        if(categoria == null){

            throw new IllegalArgumentException("La categoría no puede ser nula");
        }

        if(categoria.getNombre() == null){
            throw new IllegalArgumentException("El nombre de la categoría no puede ser nulo");
        }
    }


    /**
     * Comprueba que no exista ya una categoría con ese nombre
     * */
    private void comprobarNombreCategoriaLibre(Connection conexion, String nombre) throws SQLException{

        String sentenciaComprobacion= """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)){

            sentencia.setString(1, nombre);

            try(ResultSet resultado = sentencia.executeQuery()){

                if(resultado.next() && resultado.getInt(1) > 0){
                    throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                }

            }

        }



    }

    private int obtenerSiguienteIdCategoria(Connection conexion) throws SQLException{

        String sentenciaObtencion = """
                SELECT COALESCE(MAX(id_categoria), 0) + 1
                FROM categoria
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)){
            ResultSet resultado = sentencia.executeQuery();

            if(resultado.next()){
                return resultado.getInt(1);
            }
            throw new SQLException("No se puede calcular el siguiente idCategoria");
        }


    }

    private String normalizarTextoOpcional(String texto){

        if(texto == null){
            return null;
        }

        String textoLimpio = texto.trim();

        if(textoLimpio.isEmpty()){
            return null;
        }

        return textoLimpio;

    }

    private void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException{

        String sentenciaComprobacion = """
                SELECT COUNT(*)
                FROM categoria
                WHERE id_categoria = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)){

            sentencia.setInt(1, idCategoria);

            try(ResultSet setResultados = sentencia.executeQuery()){

                if(setResultados.next() && setResultados.getInt(1) == 0){
                    throw new IllegalArgumentException("La categoria no existe");
                }
            }
        }

    }

    /**
     * Comprueba que no existe otra categoria con ese nomnbre además de la de idCategoria
     * */
    private void comprobarNombreSoloId(Connection conexion, String nombre, int idCategoria) throws SQLException{

        String sentenciaComrprobacion= """
                SELECT COUNT(*)
                FROM categoria
                WHERE LOWER(nombre) = LOWER(?)
                AND id_categoria <> ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaComrprobacion)){

            sentencia.setString(1, nombre.trim());
            sentencia.setInt(2, idCategoria);

            try(ResultSet setResultado = sentencia.executeQuery()){

                if(setResultado.next() && setResultado.getInt(1) >0){

                    throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
                }
            }
        }


    }


    public List<Categoria> obtenerCategorias(Connection conexion) throws Exception{

        List<Categoria> listaCategorias = new ArrayList<>();

        validarConexion(conexion);

        String sentenciaObtencion = """
                SELECT id_categoria, nombre, descripcion, es_del_sistema
                FROM categoria
                ORDER BY es_del_sistema DESC, nombre ASC
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaObtencion)){

            ResultSet setResultados = sentencia.executeQuery();

            while(setResultados.next()){

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


    public record DatosNuevaCategoria(
            String nombre,
            String descripcion
    ){}

    public record DatosEdicionCategoria(
            int idCategoria,
            String nombre,
            String descripcion
    ){}

}
