package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.negocio.GestorCategorias;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ControladorCategorias {

    private final ControladorPrincipal controladorPrincipal;

    private final GestorCategorias gestorCategorias;

    public ControladorCategorias(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal=controladorPrincipal;

        this.gestorCategorias = new GestorCategorias();
    }


    public List<Categoria> obtenerCategorias() throws SQLException {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        if(conexionActual==null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión activa");
        }

        try {
            return gestorCategorias.obtenerCategorias(conexionActual);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener categorías", e);
        }

    }

    public void crearCategoria(String nombre, String descripcion) throws SQLException {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEsDelSistema(false);

        gestorCategorias.crearCategoria(conexionActual, categoria);
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    public void editarCategoria(int idCategoria, String nombre, String descripcion) throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión abierta");
        }
        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);

        gestorCategorias.editarCategoria(conexionActual, categoria);
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    public void eliminarCategoria(Categoria categoria) throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        gestorCategorias.eliminarCategoria(conexionActual, categoria.getIdCategoria());
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();
    }

    public boolean confirmarEliminacionCategoria(Categoria categoria){

        Alert alerta  = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar categoría");
        alerta.setHeaderText("Va a eliminar la categoría " + categoria.getNombre());
        alerta.setContentText("Las credenciales asignadas se reasignarán a 'Otros'. ¿Desea continuar?");

        Optional<ButtonType> respuesta = alerta.showAndWait();

        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }




}
