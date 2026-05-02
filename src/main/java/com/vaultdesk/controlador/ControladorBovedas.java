package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.negocio.ExcepcionIntegridadBoveda;
import com.vaultdesk.negocio.GestorBovedas;
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
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Clase auxiliar encargada de las operaciones sobre bóvedas
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas apertura, cierre y
 * guardado de bóvedas, así como inicialización y cambio de contraseña maestra de las mismas.
 * </p>
 *
 */
public class ControladorBovedas {

    private final ControladorPrincipal controladorPrincipal;

    private final GestorPersistencia gestorPersistencia;
    private final GestorBaseDatos gestorBaseDatos;
    private final GestorBovedas gestorBovedas;


    public ControladorBovedas(ControladorPrincipal controladorPrincipal) {

        this.gestorPersistencia = new GestorPersistencia();
        this.gestorBaseDatos = new GestorBaseDatos();
        this.gestorBovedas = new GestorBovedas();

        this.controladorPrincipal = controladorPrincipal;
    }

    /**
     * Abre una bóveda y la carga en memoria
     *
     * @see DialogoPassword#mostrar(String, Consumer)
     * @see GestorPersistencia#abrirBovedaEnMemoria(Path, char[])
     *
     *
     */
    public void abrirBoveda() {

        Stage primaryStage = controladorPrincipal.getPrimaryStage();
        Connection conexionActual = controladorPrincipal.getConexionActual();

        GestorRutasAplicacion gestorRutasAplicacion = new GestorRutasAplicacion();

        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Abrir bóveda");
        selectorArchivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bovedas VaultDesk (*.vlt)", "*.vlt"));

        selectorArchivos.setInitialDirectory(gestorRutasAplicacion.obtenerDirectorioBovedas().toFile());

        File archivo = selectorArchivos.showOpenDialog(primaryStage);

        if (archivo == null) {
            return;
        }

        Path ruta = archivo.toPath();

        DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);

        dialogoPassword.mostrar("Introduzca la contraseña maestra de la bóveda",
                passwordMaestra -> {

                    if (passwordMaestra == null || passwordMaestra.length == 0) {
                        return;
                    }

                    try {
                        Connection conexion = gestorPersistencia.abrirBovedaEnMemoria(ruta, passwordMaestra);

                        Boveda boveda = cargarBovedaActualDesdeBD(conexion);


                        if (conexionActual != null && !conexionActual.isClosed()) {
                            conexionActual.close();
                        }

                        controladorPrincipal.setConexionActual(conexion);
                        controladorPrincipal.setRutaBoveda(ruta);
                        controladorPrincipal.setBovedaActual(boveda);
                        controladorPrincipal.mostrarVistaPrincipal();

                    } catch (ExcepcionIntegridadBoveda e) {
                        controladorPrincipal.mostrarMensajeError("No se pudo abrir la bóveda", e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                        controladorPrincipal.mostrarMensajeError("Error al abrir la bóveda", e.getClass().getSimpleName() + ": " + e.getMessage());
                    } finally {

                        Arrays.fill(passwordMaestra, '\0');
                    }
                }
        );
    }

    /**
     * Cierra una bóveda tras comprobar si se han guardado los cambios más recientes
     *
     * @see ControladorPrincipal#resolverCambiosPendientes()
     *
     */
    public void cerrarBoveda() {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        if (!controladorPrincipal.resolverCambiosPendientes()) {
            return;
        }
        try {
            if (conexionActual != null && !conexionActual.isClosed()) {
                conexionActual.close();
            }

            controladorPrincipal.setConexionActual(null);
            controladorPrincipal.setRutaBoveda(null);
            controladorPrincipal.setBovedaActual(null);
            controladorPrincipal.mostrarVistaInicial();

        } catch (Exception e) {

            controladorPrincipal.mostrarMensajeError("Error al cerrar la bóveda", e.getMessage());
        }
    }

    /**
     * Guarda una bóveda utilizando la contraseña proporcionada
     *
     * @param passwordMaestra contraseña maestra de la bóveda
     * @return true si se guardó correctamente, false en caso contrario
     *
     *
     */
    public boolean guardarBovedaInterna(char[] passwordMaestra) {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();
        Path rutaBoveda = controladorPrincipal.getRutaBoveda();

        if (conexionActual == null || rutaBoveda == null) {
            controladorPrincipal.mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return false;
        }

        if (passwordMaestra == null || passwordMaestra.length == 0) {
            return false;
        }

        try {
            gestorPersistencia.guardarBovedaDesdeMemoria(rutaBoveda, passwordMaestra, conexionActual);
            controladorPrincipal.mostrarMensajeInformacion("Guardar bóveda", "La bóveda se ha guardado correctamente");

            if (bovedaActual != null) {
                bovedaActual.setModificadaSinGuardar(false);
                controladorPrincipal.actualizarTituloVentana();
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            controladorPrincipal.mostrarMensajeError("Error al guardar la bóveda", e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }

    }

    /**
     * Gestiona el proceso de guardado de una bóveda, incluyendo la solicitud y validación de la contraseña maestra
     *
     * @see ControladorPrincipal#guardarBoveda()
     * @see ControladorBovedas#guardarBovedaInterna(char[])
     *
     *
     */
    public void guardarBoveda() {

        DialogoPassword dialogoPassword = new DialogoPassword(controladorPrincipal.getPrimaryStage());

        dialogoPassword.mostrar("Introduzca la contraseña maestra de la bóveda", passwordMaestra -> {

            try {
                guardarBovedaInterna(passwordMaestra);
            } finally {
                if (passwordMaestra != null) {
                    Arrays.fill(passwordMaestra, '\0');
                }
            }
        });
    }

    /**
     * Crea una nueva bóveda y la inicializa
     *
     * @see ControladorPrincipal#crearNuevaBoveda()
     * @see GestorBaseDatos#crearBaseDatosEnMemoria()
     *
     */
    public void crearNuevaBoveda() {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        DialogoNuevaBoveda dialogo = new DialogoNuevaBoveda(controladorPrincipal.getPrimaryStage());

        dialogo.mostrar(datos -> {

            if (datos == null) {
                return;
            }
            char[] password = datos.password();
            try {

                GestorRutasAplicacion gestorRutasAplicacion = new GestorRutasAplicacion();

                FileChooser selector = new FileChooser();
                selector.setTitle("Guardar bóveda");
                selector.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Bóvedas (*.vlt)", "*.vlt")
                );
                selector.setInitialDirectory(gestorRutasAplicacion.obtenerDirectorioBovedas().toFile());

                selector.setInitialFileName(datos.nombre() + ".vlt");

                File archivo = selector.showSaveDialog(controladorPrincipal.getPrimaryStage());

                if (archivo == null) {
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
                if (conexionActual != null && !conexionActual.isClosed()) {
                    conexionActual.close();
                }

                controladorPrincipal.setConexionActual(conexion);
                controladorPrincipal.setRutaBoveda(ruta);
                controladorPrincipal.setBovedaActual(boveda);
                controladorPrincipal.mostrarVistaPrincipal();


            } catch (Exception e) {

                e.printStackTrace();
                controladorPrincipal.mostrarMensajeError("Error al crear bóveda", e.getClass().getSimpleName() + ":" + e.getMessage());

            } finally {
                Arrays.fill(password, '\0');
            }
        });
    }


    /**
     * Inicializa la tabla 'boveda'
     *
     * @param conexion conexión activa con la base de datos
     * @param nombre   nombre que se dará a la base de datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see GestorBovedas#insertarBovedaInicial(Connection, String)
     *
     */
    public void insertarBovedaInicial(Connection conexion, String nombre) throws Exception {

        gestorBovedas.insertarBovedaInicial(conexion, nombre);
    }


    /**
     * Gestiona la modificaciónd de la contraseña maestra de una bóveda
     *
     * @see ControladorPrincipal#cambiarPasswordMaestra()
     *
     */
    public void cambiarPasswordMaestra() {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();
        Path rutaBoveda = controladorPrincipal.getRutaBoveda();

        if (rutaBoveda == null) {
            controladorPrincipal.mostrarMensajeError("Error", "No hay ninguna bóveda abierta");
            return;
        }

        DialogoPassword dialogoPasswordActual = new DialogoPassword(controladorPrincipal.getPrimaryStage());

        dialogoPasswordActual.mostrar("Introduzca la contraseña maestra actual", passwordActual -> {
            if (passwordActual == null || passwordActual.length == 0) {
                return;
            }

            DialogoPassword dialogoPasswordNueva = new DialogoPassword(controladorPrincipal.getPrimaryStage());
            dialogoPasswordNueva.mostrar("Introduzca la nueva contraseña maestra", passwordNueva -> {
                if (passwordNueva == null || passwordActual.length == 0) {
                    controladorPrincipal.mostrarMensajeError("Error", "La contraseña no es válida");
                    Arrays.fill(passwordActual, '\0');
                    return;
                }
                try {
                    gestorBovedas.cambiarPasswordMaestra(rutaBoveda, passwordActual, passwordNueva);
                    controladorPrincipal.mostrarMensajeInformacion("Cambio de contraseña maestra", "El cambio se ha realizado correctamente");
                } catch (Exception e) {
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

    /**
     * Carga una bóveda desde la base de datos
     *
     * @param conexion conexión actual con la base de datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorBovedas#cargarBovedaActualDesdeBD(Connection)
     *
     *
     */
    public Boveda cargarBovedaActualDesdeBD(Connection conexion) throws Exception {

        return gestorBovedas.cargarBovedaActualDesdeBD(conexion);
    }
}
