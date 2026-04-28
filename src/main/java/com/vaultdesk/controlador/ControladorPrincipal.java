package com.vaultdesk.controlador;

import com.vaultdesk.dominio.*;
import com.vaultdesk.negocio.*;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.ui.*;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.security.PublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class ControladorPrincipal {

    private final Stage primaryStage;
    private final GestorPersistencia gestorPersistencia;
    private final GestorBaseDatos gestorBaseDatos;
    //private final GestorCategorias gestorCategorias;

    private final ControladorCategorias controladorCategorias;
    private final ControladorCredenciales controladorCredenciales;
    private final ControladorBovedas controladorBovedas;
    private final ControladorAlertas controladorAlertas;
    private final ControladorPasswords controladorPasswords;
    private final ControladorExportacion controladorExportacion;
    private final ControladorAjustes controladorAjustes;

    private final VistaDialogos vistaDialogos;

    private Boveda bovedaActual;

    private Connection conexionActual;
    private Path rutaBoveda;


    public ControladorPrincipal(Stage primaryStage){

        this.primaryStage = primaryStage;
        this.gestorPersistencia = new GestorPersistencia();
        this.gestorBaseDatos = new GestorBaseDatos();
        //this.gestorCategorias = new GestorCategorias();

        this.controladorCategorias = new ControladorCategorias(this);
        this.controladorCredenciales = new ControladorCredenciales(this);
        this.controladorBovedas = new ControladorBovedas(this);
        this.controladorAlertas = new ControladorAlertas(this);
        this.controladorPasswords = new ControladorPasswords(this);
        this.controladorExportacion = new ControladorExportacion(this);
        this.controladorAjustes = new ControladorAjustes(this);

        this.vistaDialogos = new VistaDialogos(this);
    }


    public void salirAplicacion(){

        if(!resolverCambiosPendientes()){
            return;
        }
        try{
            if(conexionActual != null && !conexionActual.isClosed()){
                conexionActual.close();
            }
        } catch (Exception e){

            mostrarMensajeError("Error al cerrar la conexión", e.getMessage());
            return;
        }

        primaryStage.close();

    }

    public boolean resolverCambiosPendientes(){
        if(bovedaActual == null || !bovedaActual.isModificadaSinGuardar()){
            return true;
        }

        ButtonType decision = mostrarConfirmacionCierre();

        if(decision.getText().equals("Cancelar")){
            return false;
        }
        if(decision.getText().equals("No Guardar")){
            return true;
        }
        if(decision.getText().equals("Guardar")){

            DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);
            char[] passwordMaestra = dialogoPassword.mostrarYEsperar();

            try{
                try{
                    gestorPersistencia.abrirBovedaEnMemoria(rutaBoveda, passwordMaestra);
                } catch (Exception e){
                    mostrarMensajeError("Contraseña incorrecta", "No se han guardado los cambios");
                    return false;
                }
                return guardarBovedaInterna(passwordMaestra);
            } finally {
                Arrays.fill(passwordMaestra, '\0');
            }

        }
        return false;
    }


    // Métodos getter/setter

    public Connection getConexionActual(){

        return conexionActual;
    }

    public Stage getPrimaryStage(){
        return this.primaryStage;
    }

    public Path getRutaBoveda(){

        return rutaBoveda;
    }

    public void setConexionActual(Connection conexionActual){
        this.conexionActual = conexionActual;
    }

    public void setRutaBoveda(Path rutaBoveda){
        this.rutaBoveda = rutaBoveda;
    }

    public void setBovedaActual(Boveda bovedaActual){
        this.bovedaActual = bovedaActual;
    }

    public Boveda getBovedaActual(){
        return bovedaActual;
    }



    // Operaciones sobre bóvedas -> Métodos fachada que redirigen a ControladorBovedas

    public void abrirBoveda(){

        controladorBovedas.abrirBoveda();
    }

    public void cerrarBoveda(){

        controladorBovedas.cerrarBoveda();
    }

    private boolean guardarBovedaInterna(char[] passwordMaestra){

        return controladorBovedas.guardarBovedaInterna(passwordMaestra);
    }

    public void guardarBoveda(){

        controladorBovedas.guardarBoveda();
    }

    public void crearNuevaBoveda(){

        controladorBovedas.crearNuevaBoveda();
    }

    public void cambiarPasswordMaestra() {

        controladorBovedas.cambiarPasswordMaestra();
    }



    // Operaciones sobre visualización

    public void mostrarVistaInicial(){

        VistaInicial vistaInicial = new VistaInicial(this);

        primaryStage.setTitle("VaultDesk");
        primaryStage.setScene(vistaInicial.crearEscena());
        primaryStage.show();
    }

    public void mostrarVistaPrincipal(){

        VistaPrincipal vistaPrincipal = new VistaPrincipal(this);


        primaryStage.setScene(vistaPrincipal.crearEscena());
        actualizarTituloVentana();
        primaryStage.show();
    }

    public void actualizarTituloVentana(){

        String titulo = "VaultDesk";

        if(bovedaActual != null) {
            titulo += " - " + bovedaActual.getNombre();

            if (bovedaActual.isModificadaSinGuardar()) {
                titulo += " (*)";
            }
        } else if(rutaBoveda != null){

            titulo += " - " + rutaBoveda.getFileName();
        }

        primaryStage.setTitle(titulo);


    }


    // Operaciones con diálogos -> métodos fachada que llaman a los métodos correspondientes de VistaDialogos

    public void mostrarMensajeError(String titulo, String mensaje){

        vistaDialogos.mostrarMensajeError(titulo, mensaje);
    }

    public void mostrarMensajeInformacion(String titulo, String mensaje) {

        vistaDialogos.mostrarMensajeInformacion(titulo,mensaje);
    }

    private ButtonType mostrarConfirmacionCierre(){

        return vistaDialogos.mostrarConfirmacionCierre();
    }



    // Operaciones sobre credenciales -> Métodos fachada que redirigen a ControladorCredenciales

    public void crearCredencial(
            String urlIdentificador,
            String username,
            String password,
            boolean destacada,
            String anotaciones,
            boolean caduca,
            String fechaCaducidad,
            int periodoCaducidad,
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales,
            int idCategoria
            ) throws Exception{

        controladorCredenciales.crearCredencial(
                urlIdentificador,
                username,
                password,
                destacada,
                anotaciones,
                caduca,
                fechaCaducidad,
                periodoCaducidad,
                reqLongitud,
                reqMayusculas,
                reqMinusculas,
                reqDigitos,
                reqEspeciales,
                idCategoria
        );
    }

    public void editarCredencial(
            int idCredencial,
            String urlIdentificador,
            String username,
            String password,
            boolean destacada,
            String anotaciones,
            boolean caduca,
            String fechaCaducidad,
            int periodoCaducidad,
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales,
            int idCategoria
    ) throws Exception{

        controladorCredenciales.editarCredencial(
                idCredencial,
                urlIdentificador,
                username,
                password,
                destacada,
                anotaciones,
                caduca,
                fechaCaducidad,
                periodoCaducidad,
                reqLongitud,
                reqMayusculas,
                reqMinusculas,
                reqDigitos,
                reqEspeciales,
                idCategoria
        );
    }

    public void eliminarCredencial(Credencial credencial) throws Exception{

        controladorCredenciales.eliminarCredencial(credencial);
    }

    public boolean confirmarEliminacionCredencial(){

        return controladorCredenciales.confirmarEliminacionCredencial();
    }

    public List<Credencial> obtenerCredenciales() throws Exception{

        return controladorCredenciales.obtenerCredenciales();
    }

    public List<Credencial> buscarCredencial(String textoBusqueda) throws Exception{

        return controladorCredenciales.buscarCredencial(textoBusqueda);
    }

    public List<Credencial> obtenerCredencialesPorCategoria(int idCategoria) throws Exception{

        return controladorCredenciales.obtenerCredencialesPorCategoria(idCategoria);
    }

    public void toggleDestacada(Credencial credencial) throws Exception{

        controladorCredenciales.toggleDestacada(credencial);
    }

    public List<Credencial> obtenerCredencialesDestacadas() throws Exception {

        return controladorCredenciales.obtenerCredencialesDestacadas();
    }


    // Operaciones sobre categorías -> Métodos fachada que redirigen a ControladorCategorias

    public List<Categoria> obtenerCategorias() throws SQLException {

        return controladorCategorias.obtenerCategorias();
    }

    public void crearCategoria(String nombre, String descripcion) throws SQLException {

        controladorCategorias.crearCategoria(nombre, descripcion);
    }

    public void editarCategoria(int idCategoria, String nombre, String descripcion) throws Exception{

        controladorCategorias.editarCategoria(idCategoria, nombre, descripcion);
    }

    public void eliminarCategoria(Categoria categoria) throws Exception{

        controladorCategorias.eliminarCategoria(categoria);
    }

    public boolean confirmarEliminacionCategoria(Categoria categoria){

        return controladorCategorias.confirmarEliminacionCategoria(categoria);
    }



    // Operaciones sobre alertas -> Métodos fachada que redirigen a ControladorAlertas

    public List<AlertaCaducidad> obtenerAlertasCaducidad() throws Exception{

        return controladorAlertas.obtenerAlertasCaducidad();
    }


    // Operaciones de exportación a CSV -> Métodos fachada que redirigen a ControladorExportacion

    public void exportarACsv(Path rutaCsv) throws Exception{

        controladorExportacion.exportarACsv(rutaCsv);
    }

    public boolean confirmarExportacion(){

        return controladorExportacion.confirmarExportacion();
    }


    // Operaciones sobre ajustes -> Métodos fachada que redirigen a ControladorAjustes

    public List<Idioma> obtenerIdiomas() throws Exception{

        return controladorAjustes.obtenerIdiomas();

    }

    public List<TemaVisual> obtenerTemasVisuales() throws Exception{

        return controladorAjustes.obtenerTemasVisuales();
    }

    public void actualizarAjustesBoveda(
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual
    ) throws Exception{

        controladorAjustes.actualizarAjustesBoveda(
                umbralAlerta,
                accesibilidad,
                idioma,
                temaVisual
        );
    }


    // Operaciones sobre contraseñas -> Métodos fachada que redirigen a ControladorPasswords

    public String generarPassword(int longitud, int mayusculas, int minusculas, int digitos, int especiales){

        return controladorPasswords.generarPassword(longitud,mayusculas,minusculas,digitos,especiales);

    }

    public void actualizarPasswordCredencial(Credencial credencial) throws  Exception{

        controladorPasswords.actualizarPasswordCredencial(credencial);

    }

    public boolean confirmarActualizacionPassword() {

        return controladorPasswords.confirmarActualizacionPassword();
    }

}
