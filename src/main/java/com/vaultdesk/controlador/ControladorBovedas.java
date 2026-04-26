package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;
import com.vaultdesk.negocio.ExcepcionIntegridadBoveda;
import com.vaultdesk.negocio.GestorRutasAplicacion;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.ui.DialogoNuevaBoveda;
import com.vaultdesk.ui.DialogoPassword;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class ControladorBovedas {

    private final ControladorPrincipal controladorPrincipal;

    private final GestorPersistencia gestorPersistencia;
    private final GestorBaseDatos gestorBaseDatos;


    public ControladorBovedas(ControladorPrincipal controladorPrincipal){

        this.gestorPersistencia = new GestorPersistencia();
        this.gestorBaseDatos = new GestorBaseDatos();

        this.controladorPrincipal = controladorPrincipal;
    }

    public void abrirBoveda(){

        Stage primaryStage = controladorPrincipal.getPrimaryStage();
        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();



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

        dialogoPassword.mostrar( "Introduzca la contraseña maestra de la bóveda",
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


                        controladorPrincipal.setConexionActual(conexion);
                        //conexionActual = conexion;

                        controladorPrincipal.setRutaBoveda(ruta);
                        //rutaBoveda = ruta;
                        controladorPrincipal.setBovedaActual(boveda);
                        //bovedaActual = boveda;

                        controladorPrincipal.mostrarVistaPrincipal();

                    }
                    catch (ExcepcionIntegridadBoveda e){
                        controladorPrincipal.mostrarMensajeError("No se pudo abrir la bóveda", e.getMessage());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        controladorPrincipal.mostrarMensajeError("Error al abrir la bóveda", e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                    finally {

                        Arrays.fill(passwordMaestra, '\0');
                    }
                }
        );
    }

    public void cerrarBoveda(){

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();


        if(!controladorPrincipal.resolverCambiosPendientes()){
            return;
        }
        try{
            if(conexionActual != null && !conexionActual.isClosed()){
                conexionActual.close();
            }

            controladorPrincipal.setConexionActual(null);
            //conexionActual = null;

            controladorPrincipal.setRutaBoveda(null);
            //rutaBoveda = null;

            controladorPrincipal.setBovedaActual(null);
            //bovedaActual = null;

            controladorPrincipal.mostrarVistaInicial();

        } catch (Exception e){

            controladorPrincipal.mostrarMensajeError("Error al cerrar la bóveda", e.getMessage());
        }
    }


    public boolean guardarBovedaInterna(char[] passwordMaestra){

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();
        Path rutaBoveda = controladorPrincipal.getRutaBoveda();

        if(conexionActual == null || rutaBoveda == null){
            controladorPrincipal.mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return false;
        }

        if(passwordMaestra == null || passwordMaestra.length == 0){
            return false;
        }

        try{
            gestorPersistencia.guardarBovedaDesdeMemoria(rutaBoveda, passwordMaestra, conexionActual);
            controladorPrincipal.mostrarMensajeInformacion("Guardar bóveda", "La bóveda se ha guardado correctamente");

            if(bovedaActual != null){
                bovedaActual.setModificadaSinGuardar(false);
                controladorPrincipal.actualizarTituloVentana();
            }

            return true;

        } catch (Exception e){

            e.printStackTrace();
            controladorPrincipal.mostrarMensajeError("Error al guardar la bóveda", e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }

    }

    public void guardarBoveda(){

        DialogoPassword dialogoPassword = new DialogoPassword(controladorPrincipal.getPrimaryStage());

        dialogoPassword.mostrar("Introduzca la contraseña maestra de la bóveda", passwordMaestra ->{

            try{
                guardarBovedaInterna(passwordMaestra);
            } finally {
                if(passwordMaestra != null){
                    Arrays.fill(passwordMaestra, '\0');
                }
            }


        });
    }

    public void crearNuevaBoveda(){

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();
        Path rutaBoveda = controladorPrincipal.getRutaBoveda();

        DialogoNuevaBoveda dialogo = new DialogoNuevaBoveda(controladorPrincipal.getPrimaryStage());

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

                File archivo = selector.showSaveDialog(controladorPrincipal.getPrimaryStage());

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

                controladorPrincipal.setConexionActual(conexion);
                controladorPrincipal.setRutaBoveda(ruta);
                controladorPrincipal.setBovedaActual(boveda);
                //conexionActual = conexion;
                //rutaBoveda = ruta;
                //bovedaActual = boveda;

                controladorPrincipal.mostrarVistaPrincipal();


            } catch (Exception e){

                e.printStackTrace();
                controladorPrincipal.mostrarMensajeError("Error al crear bóveda", e.getClass().getSimpleName() + ":" + e.getMessage());

            } finally {
                Arrays.fill(password, '\0');
            }
        });
    }



    private void insertarBovedaInicial(Connection conexion, String nombre) throws Exception{

        String sentenciaInsercion = """
                INSERT INTO boveda (id_boveda, nombre, umbral_alerta, accesibilidad, id_idioma, id_tema_visual)
                VALUES (1, ?, 7, 0, 1, 1)
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaInsercion)){
            sentencia.setString(1, nombre);
            sentencia.executeUpdate();
        }

    }



    public void cambiarPasswordMaestra() {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();
        Path rutaBoveda = controladorPrincipal.getRutaBoveda();

        if(rutaBoveda == null){
            controladorPrincipal.mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return;
        }

        DialogoPassword dialogoPasswordActual = new DialogoPassword(controladorPrincipal.getPrimaryStage());

        dialogoPasswordActual.mostrar("Introduzca la contraseña maestra actual", passwordActual ->{
            if(passwordActual == null || passwordActual.length == 0) {
                return;
            }

            DialogoPassword dialogoPasswordNueva = new DialogoPassword(controladorPrincipal.getPrimaryStage());
            dialogoPasswordNueva.mostrar("Introduzca la nueva contraseña maestra", passwordNueva ->{
                if(passwordNueva == null || passwordActual.length == 0){
                    controladorPrincipal.mostrarMensajeError("Error", "La contraseña no es válida");
                    Arrays.fill(passwordActual, '\0');
                    return;
                }
                try{
                    gestorPersistencia.cambiarPasswordMaestra(rutaBoveda, passwordActual, passwordNueva);
                    controladorPrincipal.mostrarMensajeInformacion("Cambio de contraseña maestra", "El cambio se ha realizado correctamente");
                } catch (Exception e){
                    e.printStackTrace();
                    controladorPrincipal.mostrarMensajeError("Error al cambiar la contraseña maestra",
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
                SELECT
                    b.id_boveda,
                    b.nombre,
                    b.umbral_alerta,
                    b.accesibilidad,
                    b.id_idioma,
                    i.nombre AS nombre_idioma,
                    b.id_tema_visual,
                    t.nombre AS nombre_tema_visual
                FROM boveda b
                JOIN idioma i ON b.id_idioma = i.id_idioma
                JOIN tema_visual t ON b.id_tema_visual = t.id_tema_visual
                WHERE b.id_boveda = 1
                LIMIT 1
                """;

        try(PreparedStatement sentencia = conexion.prepareStatement(sentenciaCarga)){
            ResultSet setResultados = sentencia.executeQuery();

            if(setResultados.next()){


                Boveda boveda = new Boveda();

                Idioma idioma = new Idioma();
                idioma.setIdIdioma(setResultados.getInt("id_idioma"));
                idioma.setNombre(setResultados.getString("nombre_idioma"));

                TemaVisual temaVisual = new TemaVisual();
                temaVisual.setIdTemaVisual(setResultados.getInt("id_tema_visual"));
                temaVisual.setNombre(setResultados.getString("nombre_tema_visual"));

                boveda.setIdBoveda(setResultados.getInt("id_boveda"));
                boveda.setNombre(setResultados.getString("nombre"));
                boveda.setUmbralAlerta(setResultados.getInt("umbral_alerta"));
                boveda.setAccesibilidad(setResultados.getInt("accesibilidad") == 1);
                boveda.setIdioma(idioma);
                boveda.setTemaVisual(temaVisual);
                boveda.setModificadaSinGuardar(false);

                return boveda;
            }

        }

        throw new IllegalArgumentException("No se encontró ningún registro para esa bóveda en la base de datos");

    }
}
