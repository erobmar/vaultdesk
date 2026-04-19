package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.negocio.ExcepcionIntegridadBoveda;
import com.vaultdesk.negocio.GestorRutasAplicacion;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.ui.DialogoNuevaBoveda;
import com.vaultdesk.ui.DialogoPassword;
import com.vaultdesk.ui.VistaInicial;
import com.vaultdesk.ui.VistaPrincipal;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.IllformedLocaleException;

public class ControladorPrincipal {

    private final Stage primaryStage;
    private final GestorPersistencia gestorPersistencia;
    private final GestorBaseDatos gestorBaseDatos;

    private Boveda bovedaActual;

    private Connection conexionActual;
    private Path rutaBoveda;


    public ControladorPrincipal(Stage primaryStage){

        this.primaryStage = primaryStage;
        this.gestorPersistencia = new GestorPersistencia();
        this.gestorBaseDatos = new GestorBaseDatos();
    }

    public void mostrarVistaInicial(){

        VistaInicial vistaInicial = new VistaInicial(this);

        primaryStage.setTitle("VaultDesk");
        primaryStage.setScene(vistaInicial.crearEscena());
        primaryStage.show();
    }

    public void mostrarVistaPrincipal(){

        VistaPrincipal vistaPrincipal = new VistaPrincipal(this);

        String titulo = "VaultDesk";

        if(bovedaActual != null){
            titulo += " - " + bovedaActual.getNombre();
            if(bovedaActual.isModificadaSinGuardar()){
                titulo += " *";
            }
        }else if(rutaBoveda != null){
            titulo += " - " + rutaBoveda.getFileName();
        }

        primaryStage.setTitle(titulo);
        primaryStage.setScene(vistaPrincipal.crearEscena());
        primaryStage.show();
    }


    public void abrirBoveda(){

        GestorRutasAplicacion gestorRutasAplicacion = new GestorRutasAplicacion();

        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Abrir bóveda");
        selectorArchivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bovedas VaultDesk (*.vlt)", "*.vlt"));

        selectorArchivos.setInitialDirectory(gestorRutasAplicacion.obtenerDirectorioBovedas().toFile());


        File archivo = selectorArchivos.showOpenDialog(primaryStage);

        if(archivo == null){
            return;
        }

        Path ruta = archivo.toPath();

        DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);

        dialogoPassword.mostrar(
                passwordMaestra -> {

                    if(passwordMaestra == null || passwordMaestra.length == 0){
                        return;
                    }

                    try{
                        Connection conexion = gestorPersistencia.abrirBovedaEnMemoria(ruta, passwordMaestra);

                        Boveda boveda = cargarBovedaActualDesdeBD(conexion);

                        if(conexionActual != null && !conexionActual.isClosed()){
                            conexionActual.close();
                        }

                    conexionActual = conexion;
                    rutaBoveda = ruta;
                    bovedaActual = boveda;

                    mostrarVistaPrincipal();

                    }
                catch (ExcepcionIntegridadBoveda e){
                    mostrarMensajeError("No se pudo abrir la bóveda", e.getMessage());
                }
                catch (Exception e){
                    e.printStackTrace();
                    mostrarMensajeError("Error al abrir la bóveda", e.getClass().getSimpleName() + ": " + e.getMessage());
                }
                finally {

                        Arrays.fill(passwordMaestra, '\0');
                }
            }
        );
    }

    public void salirAplicacion(){

        try{
            if(conexionActual != null && !conexionActual.isClosed()){
                conexionActual.close();
            }
        } catch (Exception ignorado){
        }
        primaryStage.close();
    }

    public Connection getConexionActual(){
        return conexionActual;
    }

    public Path getRutaBoveda(){
        return rutaBoveda;
    }

    private void mostrarMensajeError(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void cerrarBoveda(){

        try{

            if(bovedaActual != null && bovedaActual.isModificadaSinGuardar()){

                ButtonType decision = mostrarConfirmacionCierre();

                if("Cancelar".equals(decision.getText())){
                    return;
                }
                if("Guardar".equals(decision.getText())){

                    guardarBoveda();
                    if(bovedaActual != null && bovedaActual.isModificadaSinGuardar()){
                        return;
                    }
                }
            }


            if(conexionActual == null && !conexionActual.isClosed()){
                conexionActual.close();
            }
        } catch (Exception e){
            e.printStackTrace();
            mostrarMensajeError("Error al cerrar la bóveda",
                    e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        }
        conexionActual = null;
        rutaBoveda = null;
        bovedaActual = null;

        mostrarVistaInicial();
    }

    public void guardarBoveda(){

        if(conexionActual == null || rutaBoveda == null){
            mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return;
        }

        DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);

        dialogoPassword.mostrar(passwordMaestra ->{

            if(passwordMaestra == null || passwordMaestra.length == 0){
                return;
            }
            try{

                gestorPersistencia.guardarBovedaDesdeMemoria(rutaBoveda, passwordMaestra, conexionActual);
                mostrarMensajeInformacion("Guardar bóveda", "La bóveda se ha guardado correctamente");

                if(bovedaActual != null){
                    bovedaActual.setModificadaSinGuardar(false);
                }

            } catch (Exception e){
                e.printStackTrace();
                mostrarMensajeError("Error al guardar la bóveda",
                        e.getClass().getSimpleName() + ": " + e.getMessage());
            } finally {
                Arrays.fill(passwordMaestra, '\0');
            }
        });
    }

    private void mostrarMensajeInformacion(String titulo, String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void crearNuevaBoveda(){

        DialogoNuevaBoveda dialogo = new DialogoNuevaBoveda(primaryStage);

        dialogo.mostrar(datos ->{

            if(datos == null){
                return;
            }
            char[] password = datos.password();
            try{

                GestorRutasAplicacion gestorRutasAplicacion = new GestorRutasAplicacion();

                FileChooser selector = new FileChooser();
                selector.setTitle("Guardar bóveda");
                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Bóvedas (*.vlt)", "*.vlt")
                );
                selector.setInitialDirectory(gestorRutasAplicacion.obtenerDirectorioBovedas().toFile());

                selector.setInitialFileName(datos.nombre() + ".vlt");

                File archivo = selector.showSaveDialog(primaryStage);

                if(archivo == null){
                    return;
                }

                Path ruta = archivo.toPath();

                // 1 - Crear BD en memoria
                Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria();

                // 2 - Insertar registro inicial
                insertarBovedaInicial(conexion, datos.nombre());

                // 3 - Cargar los datos a la bóveda
                Boveda boveda = cargarBovedaActualDesdeBD(conexion);

                // 4 - Guardar bóveda cifrada
                gestorPersistencia.guardarBovedaDesdeMemoria(ruta, password, conexion);

                // 5 - Cerrar anterior (si existe)
                if(conexionActual != null && !conexionActual.isClosed()){
                    conexionActual.close();
                }

                conexionActual = conexion;
                rutaBoveda = ruta;
                bovedaActual = boveda;

                mostrarVistaPrincipal();


            } catch (Exception e){

                e.printStackTrace();
                mostrarMensajeError("Error al crear bóveda", e.getClass().getSimpleName() + ":" + e.getMessage());

            } finally {
                Arrays.fill(password, '\0');
            }
        });
    }

    private void insertarBovedaInicial(Connection conexion, String nombre) throws Exception{

        String sentenciaInsercion = """
                INSERT INTO boveda (id_boveda, nombre, umbral_alerta, accesibilidad, id_idioma, id_tema_visual)
                VALUES (1, ?, 30, 0, 1, 1)
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsercion)){
            sentencia.setString(1, nombre);
            sentencia.executeUpdate();
        }

    }


    public void cambiarPasswordMaestra() {

        if(rutaBoveda == null){
            mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return;
        }

        DialogoPassword dialogoPasswordActual = new DialogoPassword(primaryStage);

        dialogoPasswordActual.mostrar(passwordActual ->{
            if(passwordActual == null || passwordActual.length == 0) {
                return;
            }

            DialogoPassword dialogoPasswordNueva = new DialogoPassword(primaryStage);
            dialogoPasswordNueva.mostrar(passwordNueva ->{
                if(passwordNueva == null || passwordActual.length == 0){
                    mostrarMensajeError("Error", "La contraseña no es válida");
                    Arrays.fill(passwordActual, '\0');
                    return;
                }
                try{
                    gestorPersistencia.cambiarPasswordMaestra(rutaBoveda, passwordActual, passwordNueva);
                    mostrarMensajeInformacion("Cambio de contraseña maestra", "El cambio se ha realizado correctamente");
                } catch (Exception e){
                    e.printStackTrace();
                    mostrarMensajeError("Error al cambiar la contraseña maestra",
                            e.getClass().getSimpleName() + ": " + e.getMessage());

                } finally {
                    Arrays.fill(passwordActual, '\0');
                    Arrays.fill(passwordNueva, '\0');
                }

            });
        });

    }

    public Boveda cargarBovedaActualDesdeBD(Connection conexion) throws Exception{

        String sentenciaCarga = """
                SELECT id_boveda, nombre, umbral_alerta, accesibilidad
                FROM boveda
                LIMIT 1
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaCarga)){
            ResultSet setResultados = sentencia.executeQuery();

            if(setResultados.next()){
                Boveda boveda = new Boveda();

                boveda.setIdBoveda(setResultados.getInt("id_boveda"));
                boveda.setNombre(setResultados.getString("nombre"));
                boveda.setUmbralAlerta(setResultados.getInt("umbral_alerta"));
                boveda.setAccesibilidad(setResultados.getInt("accesibilidad") == 1);
                boveda.setModificadaSinGuardar(false);

                return boveda;
            }

        }

        throw new IllegalArgumentException("No se encontró ningún registro para esa bóveda en la base de datos");

    }

    public Boveda getBovedaActual(){
        return bovedaActual;
    }

    private ButtonType mostrarConfirmacionCierre(){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar bóveda");
        alert.setHeaderText("Hay cambios sin guardar");
        alert.setContentText("¿Quieres guardar los cambios antes de cerrar la bóveda?");

        ButtonType botonGuardar = new ButtonType("Guardar");
        ButtonType botonNoGuardar = new ButtonType("No Guardar");
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(botonGuardar, botonNoGuardar, botonCancelar);

        return alert.showAndWait().orElse(botonCancelar);

    }

}
