package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GestorValidacionCredenciales {

    private final GestorCredenciales gestorCredenciales;


    public GestorValidacionCredenciales(GestorCredenciales gestorCredenciales){
        this.gestorCredenciales = gestorCredenciales;
    }


    public void validarConexion(Connection conexion){
        if(conexion == null){
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }
    }

    public void validarBoveda(int idBoveda){
        if(idBoveda < 1){
            throw new IllegalArgumentException("El id de la bóveda es incorrecto");
        }
    }

    public void validarCredencial(Credencial credencial){
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

    public void comprobarExisteBoveda(Connection conexion, int idBoveda) throws SQLException {

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

    public void comprobarExisteCategoria(Connection conexion, int idCategoria) throws SQLException{

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

    public void comprobarExisteCredencialEnBoveda(Connection conexion, int idCredencial, int idBoveda) throws SQLException{

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
