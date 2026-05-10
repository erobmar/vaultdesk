package com.vaultdesk.controlador;

import com.vaultdesk.dominio.*;
import com.vaultdesk.negocio.*;
import com.vaultdesk.persistencia.GestorPersistencia;
import com.vaultdesk.ui.*;

import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Objects.*;

/**
 * Clase de controlador principal del sistema
 * <p>
 * Esta clase centraliza las operaciones con el sistema. Toma información de entrada del usuario a través de las vistas
 * y redirige el flujo de trabajo hacia el controlador o gestor adecuado para cada operación.
 * </p>
 *
 */
public class ControladorPrincipal {

    private final Stage primaryStage;

    private final GestorPersistencia gestorPersistencia;

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


    public ControladorPrincipal(Stage primaryStage) {

        this.primaryStage = primaryStage;

        this.gestorPersistencia = new GestorPersistencia();

        this.controladorCategorias = new ControladorCategorias(this);
        this.controladorCredenciales = new ControladorCredenciales(this);
        this.controladorBovedas = new ControladorBovedas(this);
        this.controladorAlertas = new ControladorAlertas(this);
        this.controladorPasswords = new ControladorPasswords(this);
        this.controladorExportacion = new ControladorExportacion(this);
        this.controladorAjustes = new ControladorAjustes(this);

        this.vistaDialogos = new VistaDialogos(this);
    }

    /**
     * Comprueba si existen cambios sin guardar en la bóveda y cierra la aplicación
     *
     *
     */
    public void salirAplicacion() {

        if (!resolverCambiosPendientes()) {
            return;
        }
        try {
            if (conexionActual != null && !conexionActual.isClosed()) {
                conexionActual.close();
            }
        } catch (Exception e) {

            mostrarMensajeError("Error al cerrar la conexión", e.getMessage());
            return;
        }

        primaryStage.close();

    }

    /**
     * Informa al usuario de que existen cambios sin guardar antes de cerrar la bóveda o aplicación y resuelve en
     * función de la respuesta del usuario
     *
     * @return true si se puede continuar cerrando la bóvada o aplicación, false en caso contrario
     * @see ControladorPrincipal#mostrarConfirmacionCierre()
     * @see ControladorPrincipal#guardarBovedaInterna(char[])
     * @see ControladorPrincipal#mostrarMensajeError(String, String)
     * @see DialogoPassword#mostrarYEsperar()
     * @see GestorPersistencia#abrirBovedaEnMemoria(Path, char[])
     *
     *
     */
    public boolean resolverCambiosPendientes() {
        if (bovedaActual == null || !bovedaActual.isModificadaSinGuardar()) {
            return true;
        }

        ButtonType decision = mostrarConfirmacionCierre();

        if (decision.getText().equals(GestorIdiomas.getText("boton.cancelar"))) { // "Cancelar"
            return false;
        }
        if (decision.getText().equals(GestorIdiomas.getText("boton.noguardar"))) { // "No Guardar"
            return true;
        }
        if (decision.getText().equals(GestorIdiomas.getText("boton.guardar"))) { // "Guardar"

            DialogoPassword dialogoPassword = new DialogoPassword(primaryStage);
            char[] passwordMaestra = dialogoPassword.mostrarYEsperar();

            try {
                try {
                    gestorPersistencia.abrirBovedaEnMemoria(rutaBoveda, passwordMaestra);
                } catch (Exception e) {
                    mostrarMensajeError(
                            GestorIdiomas.getText("error.password.title"),
                            GestorIdiomas.getText("error.password.content")); // "Contraseña incorrecta", "No se han guardado los cambios"
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

    public Connection getConexionActual() {

        return conexionActual;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public Path getRutaBoveda() {

        return rutaBoveda;
    }

    public void setConexionActual(Connection conexionActual) {
        this.conexionActual = conexionActual;
    }

    public void setRutaBoveda(Path rutaBoveda) {
        this.rutaBoveda = rutaBoveda;
    }

    public void setBovedaActual(Boveda bovedaActual) {
        this.bovedaActual = bovedaActual;
    }

    public Boveda getBovedaActual() {
        return bovedaActual;
    }


    // Operaciones sobre bóvedas -> Métodos fachada que redirigen a ControladorBovedas

    /**
     * Abre un archivo de bóveda y lo carga en memoria
     *
     * @see ControladorBovedas#abrirBoveda()
     *
     */
    public void abrirBoveda() {

        controladorBovedas.abrirBoveda();
    }

    /**
     * Cierra un archivo de bóveda
     *
     * @see ControladorBovedas#cerrarBoveda()
     *
     */
    public void cerrarBoveda() {

        controladorBovedas.cerrarBoveda();
    }

    /**
     * Guarda una bóveda utilizando la contraseña proporcionada
     *
     * @param passwordMaestra contraseña maestra de la bóveda
     * @return true si se guardó correctamente, false en caso contrario
     * @see ControladorBovedas#guardarBovedaInterna(char[])
     *
     *
     */
    private boolean guardarBovedaInterna(char[] passwordMaestra) {

        return controladorBovedas.guardarBovedaInterna(passwordMaestra);
    }

    /**
     * Gestiona el proceso de guardado de una bóveda, incluyendo la solicitud y validación de la contraseña maestra
     *
     * @see ControladorBovedas#guardarBoveda()
     *
     */
    public void guardarBoveda() {

        controladorBovedas.guardarBoveda();
    }

    /**
     * Crea una nueva bóveda y la inicializa
     *
     * @see ControladorBovedas#crearNuevaBoveda()
     *
     */
    public void crearNuevaBoveda() {

        controladorBovedas.crearNuevaBoveda();
    }

    /**
     * Gestiona la modificaciónd de la contraseña maestra de una bóveda
     *
     * @see ControladorBovedas#cambiarPasswordMaestra()
     *
     */
    public void cambiarPasswordMaestra() {

        controladorBovedas.cambiarPasswordMaestra();
    }


    // Operaciones sobre visualización

    /**
     * Construye y muestra la vista inicial de la aplicación
     *
     *
     */
    public void mostrarVistaInicial() {

        VistaInicial vistaInicial = new VistaInicial(this);

        primaryStage.setTitle("VaultDesk");
        primaryStage.setScene(vistaInicial.crearEscena());
        primaryStage.show();
    }

    /**
     * Construye y muestra la vista principal de la aplicación
     *
     * @see ControladorPrincipal#actualizarTituloVentana()
     *
     */
    public void mostrarVistaPrincipal() {

        VistaPrincipal vistaPrincipal = new VistaPrincipal(this);


        primaryStage.setScene(vistaPrincipal.crearEscena());
        actualizarTituloVentana();
        primaryStage.show();
    }

    /**
     * Actualiza el título de la ventana para mostrar el archivo de bóveda abierto y el indicador de cambios sin guardar
     *
     *
     */
    public void actualizarTituloVentana() {

        String titulo = "VaultDesk";

        if (bovedaActual != null) {
            titulo += " - " + bovedaActual.getNombre();

            if (bovedaActual.isModificadaSinGuardar()) {
                titulo += " (*)";
            }
        } else if (rutaBoveda != null) {

            titulo += " - " + rutaBoveda.getFileName();
        }

        if(bovedaActual.isAccesibilidad())
        {
            primaryStage.getScene().getStylesheets().add(getClass().getResource("/css/accesibilidad.css").toExternalForm());
        } else {
            primaryStage.getScene().getStylesheets().remove(getClass().getResource("/css/accesibilidad.css").toExternalForm());
        }

        primaryStage.setTitle(titulo);


    }


    // Operaciones con diálogos -> métodos fachada que llaman a los métodos correspondientes de VistaDialogos

    /**
     * Muestra un mensaje de error personalizado en pantalla
     *
     * @param titulo  título de la ventana de diálogo que se mostrará
     * @param mensaje mensaje con información adicional sobre el error
     * @see VistaDialogos#mostrarMensajeError(String, String)
     *
     *
     */
    public void mostrarMensajeError(String titulo, String mensaje) {

        vistaDialogos.mostrarMensajeError(titulo, mensaje);
    }

    /**
     * Muestra un mensaje de información personalizado en pantalla
     *
     * @param titulo  título de la ventana de información que se mostrará
     * @param mensaje mensaje con la información que se desea mostrar al usuario
     * @see VistaDialogos#mostrarMensajeInformacion(String, String)
     *
     */
    public void mostrarMensajeInformacion(String titulo, String mensaje) {

        vistaDialogos.mostrarMensajeInformacion(titulo, mensaje);
    }

    /**
     * Muestra un diálogo para confirmar si se desea cerrar la aplicacióm
     *
     * @return botón pulsado por el usuario
     * @see VistaDialogos#mostrarConfirmacionCierre()
     *
     */
    private ButtonType mostrarConfirmacionCierre() {

        return vistaDialogos.mostrarConfirmacionCierre();
    }


    // Operaciones sobre credenciales -> Métodos fachada que redirigen a ControladorCredenciales

    /**
     * Crea una credencial con los parámetros recibidos
     *
     * @param urlIdentificador URL o identificador de la credencial
     * @param username         nombre de usuario
     * @param password         contraseña
     * @param destacada        true si es una credencial destacada, false en caso contrario
     * @param anotaciones      observaciones sobre la credencial
     * @param caduca           true si la contraseña caduca, false en caso contrario
     * @param fechaCaducidad   fecha de caducidad de la contraseña
     * @param periodoCaducidad periodo en días tras el cual la contraseña caduca
     * @param reqLongitud      número mínimo de caracteres de la contraseña
     * @param reqMayusculas    número mínimo de mayúsculas de la contraseña
     * @param reqMinusculas    número mínimo de minúsculas de la contraseña
     * @param reqDigitos       número mínimo de dígitos en la contraseña
     * @param reqEspeciales    número mínimo de carateres especiales de la contraseña
     * @param idCategoria      identificador de la categoría a la que pertenece la credencial
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#crearCredencial(String, String, String, boolean, String, boolean, String, int, int, int, int, int, int, int)
     *
     *
     */
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
    ) throws Exception {

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

    /**
     * Actualiza una credencial con los parámetros indicados
     *
     * @param idCredencial     identificador de la credencial que se quiere actualizar
     * @param urlIdentificador URL o identificador de la credencial
     * @param username         nombre de usuario
     * @param password         contraseña
     * @param destacada        true si es una credencial destacada, false en caso contrario
     * @param anotaciones      observaciones sobre la credencial
     * @param caduca           true si la contraseña caduca, false en caso contrario
     * @param fechaCaducidad   fecha de caducidad de la contraseña
     * @param periodoCaducidad periodo en días tras el cual la contraseña caduca
     * @param reqLongitud      número mínimo de caracteres de la contraseña
     * @param reqMayusculas    número mínimo de mayúsculas de la contraseña
     * @param reqMinusculas    número mínimo de minúsculas de la contraseña
     * @param reqDigitos       número mínimo de dígitos en la contraseña
     * @param reqEspeciales    número mínimo de carateres especiales de la contraseña
     * @param idCategoria      identificador de la categoría a la que pertenece la credencial
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#editarCredencial(int, String, String, String, boolean, String, boolean, String, int, int, int, int, int, int, int)
     *
     *
     */
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
    ) throws Exception {

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

    /**
     * Elimina una credencial
     *
     * @param credencial la credencial a eliminar
     * @throws Exception si encuentra algún error durante el proceso
     * @see ControladorCredenciales#eliminarCredencial(Credencial)
     *
     *
     */
    public void eliminarCredencial(Credencial credencial) throws Exception {

        controladorCredenciales.eliminarCredencial(credencial);
    }

    /**
     * Muestra un diálogo para confirmar la eliminación de una credencial
     *
     * @return true si la credencial se puede eliminar, false en caso contrario
     * @see ControladorCredenciales#confirmarEliminacionCredencial()
     *
     */
    public boolean confirmarEliminacionCredencial() {

        return controladorCredenciales.confirmarEliminacionCredencial();
    }

    /**
     * Obtiene un listado de todas las credenciales de la bóveda
     *
     * @return lista de credenciales de la bóveda
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#obtenerCredenciales()
     *
     */
    public List<Credencial> obtenerCredenciales() throws Exception {

        return controladorCredenciales.obtenerCredenciales();
    }

    /**
     * Obtiene un listado de las credenciales de una bóveda que cumplen un determinado criterio de búsqueda
     *
     * @param textoBusqueda criterio de búsqueda
     * @return lista de credenciale que cumplen el criterio
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#buscarCredencial(String)
     *
     *
     */
    public List<Credencial> buscarCredencial(String textoBusqueda) throws Exception {

        return controladorCredenciales.buscarCredencial(textoBusqueda);
    }

    /**
     * Obtiene un listado de todas las credenciales de una bóveda que pertenecen a una categoría dada
     *
     * @param idCategoria identificador de la categoría por la que se va a filtrar
     * @return lista de credenciales pertenecientes a la categoría dada
     * @throws Exception si encuentra algún problema duratne el proceso
     * @see ControladorCredenciales#obtenerCredencialesPorCategoria(int)
     *
     *
     */
    public List<Credencial> obtenerCredencialesPorCategoria(int idCategoria) throws Exception {

        return controladorCredenciales.obtenerCredencialesPorCategoria(idCategoria);
    }

    /**
     * Cambia el estado de 'destacada' de una credencial dada
     *
     * @param credencial credencial a la que se desea cambiar el estado de 'destacada'
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#toggleDestacada(Credencial)
     *
     *
     */
    public void toggleDestacada(Credencial credencial) throws Exception {

        controladorCredenciales.toggleDestacada(credencial);
    }

    /**
     * Obtiene un listado de las credenciales de una bóveda marcadas como 'destacada'
     *
     * @return lista de credenciales destacadas
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCredenciales#obtenerCredencialesDestacadas()
     *
     */
    public List<Credencial> obtenerCredencialesDestacadas() throws Exception {

        return controladorCredenciales.obtenerCredencialesDestacadas();
    }


    // Operaciones sobre categorías -> Métodos fachada que redirigen a ControladorCategorias

    /**
     * Obtiene una lista de todas las categorías de una bóveda
     *
     * @return lista de categorías de la bóveda
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorCategorias#obtenerCategorias()
     *
     *
     */
    public List<Categoria> obtenerCategorias() throws SQLException {

        return controladorCategorias.obtenerCategorias();
    }

    /**
     * Crea una nueva categoría con el nombre y descripción pasados como parámetros
     *
     * @param nombre      nombre para la nueva categoría
     * @param descripcion descripción de la nueva categoría
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorCategorias#crearCategoria(String, String)
     *
     *
     */
    public void crearCategoria(String nombre, String descripcion) throws SQLException {

        controladorCategorias.crearCategoria(nombre, descripcion);
    }

    /**
     * Actualiza una categoría con el nombre y descripción pasados como parámetros
     *
     * @param idCategoria identificador de la categoría que se quiere modificar
     * @param nombre      nuevo nombre para la categoría
     * @param descripcion nueva descripción de la categoría
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorCategorias#editarCategoria(int, String, String)
     *
     *
     */
    public void editarCategoria(int idCategoria, String nombre, String descripcion) throws Exception {

        controladorCategorias.editarCategoria(idCategoria, nombre, descripcion);
    }

    /**
     * Elimina una categoría dada de la bóveda
     *
     * @param categoria categoría que se va a eliminar
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorCategorias#eliminarCategoria(Categoria)
     *
     *
     */
    public void eliminarCategoria(Categoria categoria) throws Exception {

        controladorCategorias.eliminarCategoria(categoria);
    }

    /**
     * Muestra un diálogo para confirmar la eliminación de la categoría dada
     *
     * @param categoria categoría  que se va a eliminar
     * @return true si se confirma la eliminación, false en caso contrario
     * @see ControladorCategorias#confirmarEliminacionCategoria(Categoria)
     *
     *
     */
    public boolean confirmarEliminacionCategoria(Categoria categoria) {

        return controladorCategorias.confirmarEliminacionCategoria(categoria);
    }


    // Operaciones sobre alertas -> Métodos fachada que redirigen a ControladorAlertas

    /**
     * Obtiene el listado de alertas de credenciales caducadas o próximas a caducar en la bóveda
     *
     * @return listado de alerta de caducidad
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorAlertas#obtenerAlertasCaducidad()
     *
     *
     */
    public List<AlertaCaducidad> obtenerAlertasCaducidad() throws Exception {

        return controladorAlertas.obtenerAlertasCaducidad();
    }


    // Operaciones de exportación a CSV -> Métodos fachada que redirigen a ControladorExportacion

    /**
     * Exporta el listado completo de credenciales de la bóveda a un archivo CSV
     *
     * @param rutaCsv ruta donde serán exportados los datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorExportacion#exportarACsv(Path)
     *
     *
     */
    public void exportarACsv(Path rutaCsv) throws Exception {

        controladorExportacion.exportarACsv(rutaCsv);
    }

    /**
     * Solicita al usaurio confirmación para la exportación de credenciales a un archivo CSV
     *
     * @return true si el usuario confirma la exportación, false en caso contrario
     * @see ControladorExportacion#confirmarExportacion()
     *
     *
     */
    public boolean confirmarExportacion() {

        return controladorExportacion.confirmarExportacion();
    }


    // Operaciones sobre ajustes -> Métodos fachada que redirigen a ControladorAjustes

    /**
     * Obtiene la lista de idiomas disponibles en el sistema
     *
     * @return lista de idiomas
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorAjustes#obtenerIdiomas()
     *
     *
     */
    public List<Idioma> obtenerIdiomas() throws Exception {

        return controladorAjustes.obtenerIdiomas();

    }

    /**
     * Obtiene la lista de temas visuales disponibles en el sistema
     *
     * @return lista de temas visuales
     * @throws Exception si encuentra cualquier problema durante el proceso
     * @see ControladorAjustes#obtenerTemasVisuales()
     *
     *
     */
    public List<TemaVisual> obtenerTemasVisuales() throws Exception {

        return controladorAjustes.obtenerTemasVisuales();
    }

    /**
     * Actualiza los ajustes de una bóveda a los valores dados
     *
     * @param umbralAlerta  umbral en días de alerta para caducidad de credenciales
     * @param accesibilidad indica si las opciones de accesibilidad están activadas o no
     * @param idioma        idioma para la bóveda
     * @param temaVisual    tema visual en el que se mostrará la aplicación
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorAjustes#actualizarAjustesBoveda(int, boolean, Idioma, TemaVisual)
     *
     *
     */
    public void actualizarAjustesBoveda(
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual
    ) throws Exception {

        controladorAjustes.actualizarAjustesBoveda(
                umbralAlerta,
                accesibilidad,
                idioma,
                temaVisual
        );
    }


    // Operaciones sobre contraseñas -> Métodos fachada que redirigen a ControladorPasswords

    /**
     * Genera una contraseña con los requisitos dados
     *
     * @param longitud   longitud mínima de la contraseña
     * @param mayusculas número mínimo de mayúsculas de la contraseña
     * @param minusculas número mínimo de minúsculas de la contraseña
     * @param digitos    número mínimo de dígitos en la contraseña
     * @param especiales número mínimo de caracteres especiales de la contraseña
     * @return contraseña generada
     * @see ControladorPasswords#generarPassword(int, int, int, int, int)
     *
     *
     */
    public String generarPassword(int longitud, int mayusculas, int minusculas, int digitos, int especiales) {

        return controladorPasswords.generarPassword(longitud, mayusculas, minusculas, digitos, especiales);

    }

    /**
     * Genera una contraseña para actualizar una credencial
     *
     * @param credencial credencial que se actualiza
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPasswords#actualizarPasswordCredencial(Credencial)
     *
     *
     */
    public void actualizarPasswordCredencial(Credencial credencial) throws Exception {

        controladorPasswords.actualizarPasswordCredencial(credencial);

    }

    /**
     * Muestra un diálogo de confirmación para la actualizaciópn de contraseñas
     *
     * @return true si se confirma la actualización, false en caso contrario
     * @see ControladorPasswords#confirmarActualizacionPassword()
     *
     *
     */
    public boolean confirmarActualizacionPassword() {

        return controladorPasswords.confirmarActualizacionPassword();
    }

}
