package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ControladorAjustes {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorAjustes(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public List<Idioma> obtenerIdiomas() throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();


        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay una conexión abierta");
        }

        String sentenciaConsulta = """
                SELECT id_idioma, nombre FROM idioma ORDER BY id_idioma
                """;

        List<Idioma> listaIdiomas = new ArrayList<>();

        try(PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaConsulta)){

            ResultSet setResultados = sentencia.executeQuery();

            while(setResultados.next()){

                Idioma idioma = new Idioma();
                idioma.setIdIdioma(setResultados.getInt("id_idioma"));
                idioma.setNombre(setResultados.getString("nombre"));

                listaIdiomas.add(idioma);
            }

        }

        return listaIdiomas;

    }

    public List<TemaVisual> obtenerTemasVisuales() throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión activa");
        }

        String sentenciaConsulta = """
                SELECT id_tema_visual, nombre
                FROM tema_visual
                ORDER BY id_tema_visual
                """;

        List<TemaVisual> listaTemasVisuales = new ArrayList<>();

        try(PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaConsulta)){

            ResultSet setResultados = sentencia.executeQuery();

            while (setResultados.next()){

                TemaVisual temaVisual = new TemaVisual();
                temaVisual.setIdTemaVisual(setResultados.getInt("id_tema_visual"));
                temaVisual.setNombre(setResultados.getString("nombre"));

                listaTemasVisuales.add(temaVisual);


            }

        }

        return listaTemasVisuales;
    }

    public void actualizarAjustesBoveda(
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual
    ) throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión abierta");
        }
        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }
        if(umbralAlerta < 0){
            throw new IllegalArgumentException("El umbral de alerta no puede ser menor que 0");
        }
        if(idioma == null){
            throw new IllegalStateException("Se debe seleccionar un idioma");
        }
        if(temaVisual == null){
            throw new IllegalStateException("Se debe seleccionar un tema visual");
        }

        String sentenciaActualizacion = """
                UPDATE boveda
                SET umbral_alerta = ?,
                    accesibilidad = ?,
                    id_idioma = ?,
                    id_tema_visual = ?
                WHERE id_boveda = ?
                """;

        try(PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaActualizacion)){

            sentencia.setInt(1, umbralAlerta);
            sentencia.setInt(2, accesibilidad ? 1 : 0);
            sentencia.setInt(3, idioma.getIdIdioma());
            sentencia.setInt(4, temaVisual.getIdTemaVisual());
            sentencia.setInt(5, bovedaActual.getIdBoveda());

            sentencia.executeUpdate();


        }

        bovedaActual.setUmbralAlerta(umbralAlerta);
        bovedaActual.setAccesibilidad(accesibilidad);
        bovedaActual.setTemaVisual(temaVisual);
        bovedaActual.setModificadaSinGuardar(true);

        controladorPrincipal.actualizarTituloVentana();

        controladorPrincipal.mostrarMensajeInformacion("Ajustes", "Los ajustes se han actualizado correctamente");


    }

}
