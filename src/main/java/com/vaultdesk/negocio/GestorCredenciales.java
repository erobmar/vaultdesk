package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;

import javax.xml.transform.Result;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GestorCredenciales {

    private static final int ID_CATEGORIA_OTROS = 1;
    private final GestorCaducidadCredenciales gestorCaducidadCredenciales = new GestorCaducidadCredenciales(this);
    private final GestorConsultasCredenciales gestorConsultasCredenciales = new GestorConsultasCredenciales(this);
    private final GestorExportacionCredenciales gestorExportacionCredenciales = new GestorExportacionCredenciales(this);
    private final GestorValidacionCredenciales gestorValidacionCredenciales = new GestorValidacionCredenciales(this);


    // Métodos relativos a consultas de credenciales -> Redirigidos a GestorConsultasCredenciales

    public List<Credencial> buscarCredenciales(Connection conexion, int idBoveda, String textoBusqueda) throws SQLException {

        return gestorConsultasCredenciales.buscarCredenciales(conexion, idBoveda,textoBusqueda);
    }

    public List<Credencial> obtenerCredencialesPorCategoria(Connection conexion, int idCategoria) throws Exception{

        return gestorConsultasCredenciales.obtenerCredencialesPorCategoria(conexion, idCategoria);
        }

    public List<Credencial> obtenerCredencialesDestacadas(Connection conexion, int idBoveda) throws  Exception{

        return gestorConsultasCredenciales.obtenerCredencialesDestacadas(conexion, idBoveda);
    }


    // Métodos relativos a caducidad de credenciales -> Redirigidos a GestorCaducidadCredenciales

    public boolean estaCaducada(Credencial credencial, LocalDate fechaReferencia){

        return gestorCaducidadCredenciales.estaCaducada(credencial, fechaReferencia);
    }

    public boolean estaProximaCaducar (Credencial credencial, LocalDate fechaReferencia, int umbral){

        return gestorCaducidadCredenciales.estaProximaCaducar(credencial, fechaReferencia, umbral);
    }


    // Métodos relativos a exportación de credenciales -> Redirigidos a GestorExportacionCredenciales

    public void exportarCredencialesCSV(List<Credencial> credenciales, Path rutaDestino) throws Exception{

        gestorExportacionCredenciales.exportarCredencialesCSV(credenciales, rutaDestino);
    }

    // Métodos de validación -> Redirigidos a GestorValidacionCredenciales

    public void validarConexion(Connection conexion){

        gestorValidacionCredenciales.validarConexion(conexion);
    }

    public void validarBoveda(int idBoveda){

        gestorValidacionCredenciales.validarBoveda(idBoveda);
    }


    public void validarCredencial(Credencial credencial){

        gestorValidacionCredenciales.validarCredencial(credencial);
    }

    public void comprobarExisteBoveda(Connection conexion, int idBoveda) throws SQLException {

        gestorValidacionCredenciales.comprobarExisteBoveda(conexion, idBoveda);
    }

    private void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException{

        gestorValidacionCredenciales.comprobarExisteCategoria(conexion, idCategoria);
    }

    private void comprobarExisteCredencialEnBoveda(Connection conexion, int idCredencial, int idBoveda) throws SQLException{

        gestorValidacionCredenciales.comprobarExisteCredencialEnBoveda(conexion, idCredencial, idBoveda);
    }


    // Métodos de actualización de credenciales -> Redirigidos a GestorActualizacionCredenciales

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

    public void cambiarEstadoDestacada(Connection conexion,
                                       int idCredencial,
                                       int idBoveda,
                                       boolean destacada) throws SQLException{

        validarConexion(conexion);

        if(idCredencial <= 0){
            throw new IllegalArgumentException("Id de credencial no válida");
        }
        if(idBoveda <= 0){
            throw new IllegalArgumentException("Id de bóveda no válida");
        }

        comprobarExisteCredencialEnBoveda(conexion,idCredencial, idBoveda);

        String sentenciaCambio = """
                UPDATE credencial
                SET destacada = ?
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaCambio)) {

            sentencia.setInt(1, destacada ? 1 : 0);
            sentencia.setInt(2, idCredencial);
            sentencia.setInt(3, idBoveda);

            sentencia.executeUpdate();

        }
    }

    public String actualizarPassword(Connection conexion, int idBoveda, Credencial credencial) throws SQLException{

        validarConexion(conexion);
        validarBoveda(idBoveda);

        if(conexion == null){
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }

        if(credencial.getIdCredencial() <= 0){
            throw new IllegalArgumentException("El id de credencial no es válido");
        }

        comprobarExisteCredencialEnBoveda(conexion, credencial.getIdCredencial(), idBoveda);

        GestorPasswords gestorPasswords = new GestorPasswords();

        String nuevoPassword = gestorPasswords.generarPassword(credencial);
        LocalDate fechaActualizacion = LocalDate.now();

        String sentenciaActualizacion = """
                UPDATE credencial
                SET password = ?, ultimo_update = ?
                WHERE id_credencial = ? AND id_boveda = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)){

            sentencia.setString(1, nuevoPassword);
            sentencia.setString(2, fechaActualizacion.toString());
            sentencia.setInt(3, credencial.getIdCredencial());
            sentencia.setInt(4, idBoveda);

            int filasActualizadas = sentencia.executeUpdate();

            if(filasActualizadas == 0){
                throw new SQLException("No se pudo actualizar el password de la credencial");
            }

            credencial.setPassword(nuevoPassword);
            credencial.setFechaUltimoUpdate(fechaActualizacion);

            return nuevoPassword;

        }

    }

    public void actualizarCredencial(Connection conexion, Boveda boveda, Credencial credencial) throws SQLException{

        validarConexion(conexion);
        validarBoveda(boveda.getIdBoveda());
        validarCredencial(credencial);

        if(credencial.getIdCredencial() <= 0){
            throw new IllegalArgumentException("El id de credencial no es válido");
        }

        comprobarExisteCredencialEnBoveda(conexion, credencial.getIdCredencial(), boveda.getIdBoveda());

        int idCategoria = 1; // Categoría por defecto

        if(credencial.getCategoria() != null && credencial.getCategoria().getIdCategoria() > 0){
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

        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)){

            sentencia.setString(1, credencial.getUrlIdentificador());
            sentencia.setString(2, credencial.getUsername());
            sentencia.setString(3, credencial.getPassword());
            sentencia.setInt(4, credencial.isDestacada() ? 1:0);
            sentencia.setString(5, credencial.getAnotaciones());
            sentencia.setInt(6 , credencial.isCaduca() ? 1:0);

            if(credencial.getFechaUltimoUpdate() != null){
                sentencia.setString(7, credencial.getFechaUltimoUpdate().toString() );
            } else {
                sentencia.setNull(7, Types.VARCHAR);
            }

            if(credencial.getFechaCaducidad() != null){
                sentencia.setString(8 , credencial.getFechaCaducidad().toString());
            } else {
                sentencia.setNull(8, Types.VARCHAR);
            }

            if(credencial.getPeriodoCaducidad() >0 ){
                sentencia.setInt(9, credencial.getPeriodoCaducidad());

            } else {
                sentencia.setNull(9, Types.INTEGER);
            }

            if(credencial.getReqLongitud() > 0){
                sentencia.setInt(10, credencial.getReqLongitud());
            } else {
                sentencia.setNull(10, Types.INTEGER);
            }

            if(credencial.getReqMayusculas() > 0){
                sentencia.setInt(11, credencial.getReqMayusculas());
            } else {
                sentencia.setNull(11, Types.INTEGER);
            }
            if(credencial.getReqMinusculas() > 0){
                sentencia.setInt(12, credencial.getReqMinusculas());
            } else {
                sentencia.setNull(12, Types.INTEGER);
            }
            if(credencial.getReqDigitos() > 0){
                sentencia.setInt(13, credencial.getReqDigitos());
            } else {
                sentencia.setNull(13, Types.INTEGER);
            }
            if(credencial.getReqEspeciales() > 0){
                sentencia.setInt(14, credencial.getReqEspeciales());
            } else {
                sentencia.setNull(14, Types.INTEGER);
            }

            sentencia.setInt(15, idCategoria);
            sentencia.setInt(16, credencial.getIdCredencial());
            sentencia.setInt(17, boveda.getIdBoveda());

            int filasActualizadas = sentencia.executeUpdate();

            if(filasActualizadas == 0){
                throw new SQLException("No se pudo actualizar la credencial");
            }




        }






    }

    public void actualizarDestacada(Connection conexion, int idCredencial, boolean nuevoValor) throws Exception{

        validarConexion(conexion);

        String sentenciaActualizacion = """
                UPDATE credencial
                SET destacada = ?
                WHERE id_credencial = ?
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaActualizacion)){

            sentencia.setInt(1, nuevoValor ? 1 : 0);
            sentencia.setInt(2, idCredencial);

            sentencia.executeUpdate();
        }
    }


    // Otros métodos auxiliares

    private int obtenerIdCategoria(Categoria categoria){

        if(categoria == null || categoria.getIdCategoria() <= 0){
            return ID_CATEGORIA_OTROS;
        }
        return categoria.getIdCategoria();

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

}
