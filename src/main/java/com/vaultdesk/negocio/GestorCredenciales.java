package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;

import javax.xml.transform.Result;
import java.sql.*;

public class GestorCredenciales {

    private static final int ID_CATEGORIA_OTROS = 1;

    public int crearCredencial(Credencial credencial, int idBoveda, Connection conexion) throws Exception{

        validarConexion(conexion);
        validarBoveda(idBoveda);
        validarCredencial(credencial);

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

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsercion)){

            sentencia.setInt(1, nuevoIdCredencial);
            sentencia.setString(2, credencial.getUrlIdentificador().trim());
            sentencia.setString(3, credencial.getUsername().trim());
            sentencia.setString(4, credencial.getPassword()); // OJO - No lleva trim() porque puede contener espacios
            sentencia.setInt(5, credencial.isDestacada() ? 1 : 0);
            sentencia.setString(6, normalizarTextoOpcional(credencial.getAnotaciones()));
            sentencia.setInt(7, credencial.isCaduca() ? 1 : 0);

            if(credencial.getFechaCaducidad() == null){
                sentencia.setNull(8, Types.VARCHAR);
            } else {
                sentencia.setString(8, credencial.getFechaCaducidad().toString());
            }

            if(credencial.getPeriodoCaducidad() <= 0){
                sentencia.setNull(9, Types.INTEGER);
            } else {
                sentencia.setInt(9, credencial.getPeriodoCaducidad());
            }

            if(credencial.getFechaUltimoUpdate() == null){
                sentencia.setNull(10, Types.VARCHAR);
            } else {
                sentencia.setString(10, credencial.getFechaUltimoUpdate().toString());
            }

            if(credencial.getReqLongitud() <= 0){
                sentencia.setNull(11, Types.INTEGER);
            } else {
                sentencia.setInt(11, credencial.getReqLongitud());
            }

            if(credencial.getReqMayusculas() <= 0){
                sentencia.setNull(12, Types.INTEGER);
            } else {
                sentencia.setInt(12, credencial.getReqMayusculas());
            }

            if(credencial.getReqMinusculas() <= 0){
                sentencia.setNull(13, Types.INTEGER);
            } else {
                sentencia.setInt(13, credencial.getReqMinusculas());
            }

            if(credencial.getReqDigitos() <= 0){
                sentencia.setNull(14, Types.INTEGER);
            } else {
                sentencia.setInt(14, credencial.getReqDigitos());
            }

            if(credencial.getReqEspeciales() <= 0){
                sentencia.setNull(15, Types.INTEGER);
            } else {
                sentencia.setInt(15, credencial.getReqEspeciales());
            }

            sentencia.setInt(16, idBoveda);
            sentencia.setInt(17, idCategoria);

            sentencia.executeUpdate();

        }

        credencial.setIdCredencial(nuevoIdCredencial);
        return  nuevoIdCredencial;
    }

    public void editarCredencial(Connection conexion, int idBoveda, Credencial credencial) throws SQLException{

        validarConexion(conexion);
        validarBoveda(idBoveda);
        validarCredencial(credencial);

        if(credencial.getIdCredencial() <= 0){

            throw new IllegalArgumentException("Id de credencial no válida");
        }

        comprobarExisteBoveda(conexion, idBoveda);

        int idCategoria = obtenerIdCategoria(credencial.getCategoria());
        comprobarExisteCategoria(conexion, idCategoria);
        comprobarExisteCredencialEnBoveda(conexion, credencial.getIdCredencial(), idBoveda);

        String sentenciaActualizacion = """
                UPDATE credencial SET
                    url_identificador = ?,
                    username = ?,
                    password = ?,
                    destacada = ?,
                    anotaciones = ?,
                    caduca = ?,
                    fecha_caducidad = ?,
                    periodo_caducidad = ?,
                    ultimo_update = ?,
                    req_longitud = ?,
                    req_mayusculas = ?,
                    req_minusculas = ?,
                    req_digitos = ?,
                    req_especiales = ?,
                    id_categoria = ?
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)){
            sentencia.setString(1, credencial.getUrlIdentificador().trim());
            sentencia.setString(2, credencial.getUsername().trim());
            sentencia.setString(3, credencial.getPassword());
            sentencia.setInt(4, credencial.isDestacada() ? 1 : 0);
            sentencia.setString(5, normalizarTextoOpcional(credencial.getAnotaciones()));
            sentencia.setInt(6, credencial.isCaduca() ? 1 : 0);

            if(credencial.getFechaCaducidad() == null){
                sentencia.setNull(7, Types.VARCHAR);
            } else {
                sentencia.setString(7, credencial.getFechaCaducidad().toString());
            }

            if(credencial.getPeriodoCaducidad() <= 0){
                sentencia.setNull(8, Types.INTEGER);
            } else {
                sentencia.setInt(8, credencial.getPeriodoCaducidad());
            }

            if(credencial.getFechaUltimoUpdate() == null){
                sentencia.setNull(9, Types.VARCHAR);
            } else {
                sentencia.setString(9, credencial.getFechaUltimoUpdate().toString());
            }

            if(credencial.getReqLongitud() <= 0){
                sentencia.setNull(10, Types.INTEGER);
            } else {
                sentencia.setInt(10, credencial.getReqLongitud());
            }

            if(credencial.getReqMayusculas() <= 0){
                sentencia.setNull(11, Types.INTEGER);
            } else {
                sentencia.setInt(11, credencial.getReqMayusculas());
            }

            if(credencial.getReqMinusculas() <= 0){
                sentencia.setNull(12, Types.INTEGER);
            } else {
                sentencia.setInt(12, credencial.getReqMinusculas());
            }

            if(credencial.getReqDigitos() <= 0){
                sentencia.setNull(13, Types.INTEGER);
            } else {
                sentencia.setInt(13, credencial.getReqDigitos());
            }

            if(credencial.getReqEspeciales() <= 0){
                sentencia.setNull(14, Types.INTEGER);
            } else {
                sentencia.setInt(14, credencial.getReqEspeciales());
            }

            sentencia.setInt(15, idCategoria);
            sentencia.setInt(16, credencial.getIdCredencial());
            sentencia.setInt(17, idBoveda);

            int filasActualizadas = sentencia.executeUpdate();

            if(filasActualizadas == 0){
                throw new SQLException("No se pudo actualizar la credencial");
            }


        }

    }

    public void eliminarCredencial(Connection conexion, int idBoveda, Credencial credencial) throws SQLException{

        int idCrecencial = credencial.getIdCredencial();

        validarConexion(conexion);
        validarBoveda(idBoveda);

        if(idCrecencial <= 0){
            throw new IllegalArgumentException("El id de credencial no es válido");
        }

        comprobarExisteBoveda(conexion, idBoveda);
        comprobarExisteCredencialEnBoveda(conexion, idCrecencial, idBoveda);

        String sentenciaEliminacion= """
                DELETE FROM credencial
                WHERE id_credencial = ?
                AND id_boveda = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaEliminacion)){

            sentencia.setInt(1, idCrecencial);
            sentencia.setInt(2, idBoveda);

            int filasEliminadas = sentencia.executeUpdate();

            if(filasEliminadas == 0){

                throw new SQLException("No se pudo eliminar la credencial");
            }
        }




    }

    private void validarConexion(Connection conexion){
        if(conexion == null){
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }
    }

    private void validarBoveda(int idBoveda){
        if(idBoveda < 1){
            throw new IllegalArgumentException("El id de la bóveda es incorrecto");
        }
    }

    private void validarCredencial(Credencial credencial){
        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }
        if(credencial.getUrlIdentificador() == null || credencial.getUrlIdentificador().isEmpty()){
            throw new IllegalArgumentException("El campo 'URL/Identificador' no puede estar vacío");
        }
        if(credencial.getUsername() == null || credencial.getUsername().isEmpty()){
            throw new IllegalArgumentException("El campo 'Nombre de usuario' no puede estar vacío");
        }
        if(credencial.getPassword() == null || credencial.getPassword().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Contraseña' no puede estar vacío");
        }
    }

    private void comprobarExisteBoveda(Connection conexion, int idBoveda) throws SQLException {

        String sentenciaComprobacion = "SELECT COUNT (*) from boveda WHERE id_boveda = ?";

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)){

            sentencia.setInt(1, idBoveda);

            try(ResultSet resultado  = sentencia.executeQuery()) {
                if (resultado.next() && resultado.getInt(1) == 0) {
                    throw new IllegalArgumentException("La bóveda indicada no existe");
                }
            }
        }
    }

    private int obtenerIdCategoria(Categoria categoria){

        if(categoria == null || categoria.getIdCategoria() <= 0){
            return ID_CATEGORIA_OTROS;
        }
        return categoria.getIdCategoria();

    }

    private void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException{

        String sentenciaComprobacion = "SELECT COUNT(*) FROM categoria WHERE id_categoria = ?";

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaComprobacion)){

            sentencia.setInt(1, idCategoria);

            try(ResultSet resultado = sentencia.executeQuery()){

                if(resultado.next() && resultado.getInt(1)==0){
                    throw new IllegalArgumentException("La categoría indicada no existe");
                }
            }
        }
    }

    private int obtenerSiguienteIdCredencial(Connection conexion) throws SQLException{

        String sentenciaSiguienteCredencial = "SELECT COALESCE(MAX(id_credencial), 0) + 1 FROM credencial";

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaSiguienteCredencial)){

            ResultSet resultado = sentencia.executeQuery();

            if(resultado.next()){
                return resultado.getInt(1);
            }

        }
        throw new SQLException("No se pudo obtener el id de la credencial");
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

    private void comprobarExisteCredencialEnBoveda(Connection conexion, int idCredencial, int idBoveda) throws SQLException{

        String sentenciaComprobacion = """
                SELECT COUNT(*) FROM credencial
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try(PreparedStatement sentecia = conexion.prepareStatement(sentenciaComprobacion)){

            sentecia.setInt(1 , idCredencial);
            sentecia.setInt(2, idBoveda);

            try(ResultSet resultado = sentecia.executeQuery()){

                if(resultado.next() && resultado.getInt(1) == 0){
                    throw new IllegalArgumentException("La credencial indicada no existe en la bóveda");
                }
            }


        }

    }

}
