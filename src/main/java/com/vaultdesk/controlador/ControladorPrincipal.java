package com.vaultdesk.controlador;

import com.vaultdesk.negocio.ExcepcionIntegridadBoveda;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.ui.DialogoPassword;
import com.vaultdesk.ui.VistaInicial;
import com.vaultdesk.ui.VistaPrincipal;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;

public class ControladorPrincipal {

    private final Stage primaryStage;
    private final GestorPersistencia gestorPersistencia;

    private Connection conexionActual;
    private Path rutaBoveda;


    public ControladorPrincipal(Stage primaryStage){

        this.primaryStage = primaryStage;
        this.gestorPersistencia = new GestorPersistencia();
    }

    public void mostrarVistaInicial(){

        VistaInicial vistaInicial = new VistaInicial(this);

        primaryStage.setTitle("VaultDesk");
        primaryStage.setScene(vistaInicial.crearEscena());
        primaryStage.show();
    }

    public void mostrarVistaPrincipal(){

        VistaPrincipal vistaPrincipal = new VistaPrincipal(this);
        primaryStage.setTitle("VaultDesk - Bóveda abierta");
        primaryStage.setScene(vistaPrincipal.crearEscena());
        primaryStage.show();
    }


    public void abrirBoveda(){

        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Abrir bóveda");
        selectorArchivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bovedas VaultDesk (*.vlt)", "*.vlt"));

        File archivo = selectorArchivos.showOpenDialog(primaryStage);

        if(archivo == null){
            return;
        }

        Path ruta = archivo.toPath();

        DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);

        //char[] passwordMaestra = dialogoPassword.mostrar();

        dialogoPassword.mostrar(
                passwordMaestra -> {

                    if(passwordMaestra == null || passwordMaestra.length == 0){
                        return;
                    }



                    try{
                        Connection conexion = gestorPersistencia.abrirBovedaEnMemoria(ruta, passwordMaestra);

                        if(conexionActual == null || conexionActual.isClosed()){
                            conexionActual.close();
                        }

                    conexionActual = conexion;
                    rutaBoveda = ruta;

                    mostrarVistaPrincipal();

                    }
                catch (ExcepcionIntegridadBoveda e){
                    mostrarMensajeError("No se pudo abrir la bóveda", e.getMessage());
                }
                catch (Exception e){
                    mostrarMensajeError("Error", "Se produjo un error al abrir la bóveda");
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

    public void cerrarBoveda(){}

    public void guardarBoveda(){}
}
