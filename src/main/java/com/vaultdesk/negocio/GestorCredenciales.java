package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;

import javax.xml.transform.Result;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public List<Credencial> buscarCredenciales(Connection conexion, int idBoveda, String textoBusqueda) throws SQLException {

        validarConexion(conexion);
        validarBoveda(idBoveda);

        comprobarExisteBoveda(conexion, idBoveda);

        String textoNormalizado = textoBusqueda == null ? "" : textoBusqueda.trim();

        if(textoNormalizado.isEmpty()){
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

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaBusqueda)){

            String patronBusqueda = "%" + textoNormalizado + "%";

            sentencia.setInt(1, idBoveda);
            sentencia.setString(2, patronBusqueda);
            sentencia.setString(3, patronBusqueda);
            sentencia.setString(4, patronBusqueda);
            sentencia.setString(5, patronBusqueda);

            try(ResultSet setResultados = sentencia.executeQuery()){

                while(setResultados.next()){

                    resultados.add(mapearCredencial(setResultados));
                }
            }

        }

        return resultados;

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

    private Credencial mapearCredencial(ResultSet setReultados) throws SQLException {

        Credencial credencial = new Credencial();

        credencial.setIdCredencial(setReultados.getInt("id_credencial"));
        credencial.setUrlIdentificador(setReultados.getString("url_identificador"));
        credencial.setUsername(setReultados.getString("username"));
        credencial.setPassword(setReultados.getString("password"));
        credencial.setDestacada((setReultados.getInt("destacada")) == 1);
        credencial.setAnotaciones(setReultados.getString("anotaciones"));
        credencial.setCaduca((setReultados.getInt("caduca"))==1);

        String fechaCaducidad = setReultados.getString("fecha_caducidad");
        if (fechaCaducidad != null){
            credencial.setFechaCaducidad(LocalDate.parse(fechaCaducidad));
        }

        int periodoCaducidad = setReultados.getInt("periodo_caducidad");
        if(!setReultados.wasNull()){
            credencial.setPeriodoCaducidad(periodoCaducidad);
        }

        String fechaUltimoUpdate = setReultados.getString("ultimo_update");
        if(fechaUltimoUpdate != null){
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

    public boolean estaCaducada(Credencial credencial, LocalDate fechaReferencia){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(fechaReferencia == null){

            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }

        // Si la credencial NO caduca por definición
        if(!credencial.isCaduca()){
            return false;
        }

        LocalDate fechaCaducidad = calcularFechaCaducidadReal(credencial);

        if(fechaCaducidad == null){
            return false;
        }

        return !fechaCaducidad.isAfter(fechaReferencia);

    }


    public boolean estaProximaCaducar (Credencial credencial, LocalDate fechaReferencia, int umbral){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(fechaReferencia == null){
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }

        if(umbral < 0){
            throw new IllegalArgumentException("El umbral de días no puede ser negativo");
        }

        if(!credencial.isCaduca()){
            return false;
        }

        LocalDate fechaCaducidad = calcularFechaCaducidadReal(credencial);


        if(fechaCaducidad == null){
            return false;
        }

        if(estaCaducada(credencial, fechaReferencia)){
            return false;
        }

        return !fechaCaducidad.isAfter(fechaReferencia.plusDays(umbral));

    }


    private LocalDate calcularFechaCaducidadReal(Credencial credencial){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(!credencial.isCaduca()){
            return null;
        }

        if(credencial.getFechaCaducidad() != null){
            return credencial.getFechaCaducidad();
        }

        // Si tiene un periodo de caducidad establecido
        if(credencial.getPeriodoCaducidad() > 0 && credencial.getFechaUltimoUpdate() != null){

            long dias = credencial.getPeriodoCaducidad() / 86400L; // Número de segundos en un día

            return credencial.getFechaUltimoUpdate().plusDays(dias);

        }

        return null;


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


    private List<Credencial> obtenerCredencialesDeBoveda(Connection conexion, int idBoveda) throws SQLException{

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
        try (PreparedStatement sentencia = conexion.prepareStatement(sentenciaConsulta)){

            sentencia.setInt(1, idBoveda);

            try(ResultSet setResultados = sentencia.executeQuery()){
                while (setResultados.next()){
                    credenciales.add(mapearCredencial(setResultados));
                }
            }


        }

        return credenciales;
    }

}
