package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorCredenciales;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase auxiliar encargada de las operaciones sobre credenciales en una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas a la creación, edición y
 * eliminación de credenciales, así como la obtención de listados de las mismas
 * </p>
 *
 */
public class ControladorCredenciales {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorCredenciales(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }


    public List<Credencial> obtenerCredenciales() throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        List<Credencial> credenciales = new ArrayList<>();

        String sentenciaConsulta = """
                SELECT
                     c.id_credencial,
                     c.url_identificador,
                     c.username,
                     c.password,
                     c.destacada,
                     c.anotaciones,
                     c.caduca,
                     c.ultimo_update,
                     c.fecha_caducidad,
                     c.periodo_caducidad,
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

        try (PreparedStatement sentencia = conexionActual.prepareStatement(sentenciaConsulta)) {

            sentencia.setInt(1, bovedaActual.getIdBoveda());

            try (ResultSet setResultados = sentencia.executeQuery()) {
                while (setResultados.next()) {

                    Credencial credencial = new Credencial();

                    credencial.setIdCredencial(setResultados.getInt("id_credencial"));
                    credencial.setUrlIdentificador(setResultados.getString("url_identificador"));
                    credencial.setUsername(setResultados.getString("username"));
                    credencial.setPassword(setResultados.getString("password"));
                    credencial.setDestacada(setResultados.getInt("destacada") == 1);
                    credencial.setAnotaciones(setResultados.getString("anotaciones"));
                    credencial.setCaduca(setResultados.getInt("caduca") == 1);

                    // El resto de campos son opcionales, hay que comprobarlos individualmente

                    // Comprobación de fecha de actualización
                    String ultimoUpdate = setResultados.getString("ultimo_update");

                    if (ultimoUpdate != null) {

                        credencial.setFechaUltimoUpdate(LocalDate.parse(ultimoUpdate));
                    }

                    // Comprobación de fecha de caducidad
                    String fechaCaducidad = setResultados.getString("fecha_caducidad");

                    if (fechaCaducidad != null) {

                        credencial.setFechaCaducidad(LocalDate.parse(fechaCaducidad));
                    }

                    // Comprobación de periodo de caducidad
                    int periodoCaducidad = setResultados.getInt("periodo_caducidad");

                    if (!setResultados.wasNull()) {

                        credencial.setPeriodoCaducidad(periodoCaducidad);
                    }

                    // Comprobación de requisito de longitud
                    int reqLongitud = setResultados.getInt("req_longitud");

                    if (!setResultados.wasNull()) {

                        credencial.setReqLongitud(reqLongitud);
                    }

                    // Comprobación de requisito de mayúsculas
                    int reqMayusculas = setResultados.getInt("req_mayusculas");

                    if (!setResultados.wasNull()) {

                        credencial.setReqMayusculas(reqMayusculas);
                    }

                    // Comprobación de requisito de minúsculas
                    int reqMinusculas = setResultados.getInt("req_minusculas");

                    if (!setResultados.wasNull()) {

                        credencial.setReqMinusculas(reqMinusculas);
                    }

                    // Comprobación de requisito de dígitos
                    int reqDigitos = setResultados.getInt("req_digitos");

                    if (!setResultados.wasNull()) {

                        credencial.setReqDigitos(reqDigitos);
                    }

                    // Comprobación de requisito de caracteres espciales
                    int reqEspeciales = setResultados.getInt("req_especiales");

                    if (!setResultados.wasNull()) {

                        credencial.setReqEspeciales(reqEspeciales);
                    }

                    // Comprobación para categoría
                    Categoria categoria = new Categoria();
                    categoria.setIdCategoria(setResultados.getInt("id_categoria"));
                    categoria.setNombre(setResultados.getString("nombre_categoria"));
                    categoria.setDescripcion(setResultados.getString("descripcion_categoria"));
                    categoria.setEsDelSistema(setResultados.getInt("es_del_sistema") == 1);
                    credencial.setCategoria(categoria);

                    credenciales.add(credencial);

                }
            }

            return credenciales;
        }


    }

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
     * @see ControladorPrincipal#crearCredencial(String, String, String, boolean, String, boolean, String, int, int, int, int, int, int, int)
     * @see GestorCredenciales#crearCredencial(Credencial, int, Connection)
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

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay una bóveda abierta");
        }

        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda activa");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();


        Credencial credencial = new Credencial();


        credencial.setUrlIdentificador(urlIdentificador);
        credencial.setUsername(username);
        credencial.setPassword(password);
        credencial.setDestacada(destacada);
        credencial.setAnotaciones(anotaciones == null || anotaciones.isBlank() ? null : anotaciones.trim());
        credencial.setCaduca(caduca);

        if (caduca && fechaCaducidad != null && !fechaCaducidad.isBlank()) {
            credencial.setFechaCaducidad(LocalDate.parse(fechaCaducidad.trim()));
        } else {
            credencial.setFechaCaducidad(null);
        }

        credencial.setPeriodoCaducidad(Math.max(periodoCaducidad, 0));

        if (caduca && periodoCaducidad > 0) {
            credencial.setFechaUltimoUpdate(LocalDate.now());
        } else {
            credencial.setFechaUltimoUpdate(null);
        }

        credencial.setReqLongitud(Math.max(reqLongitud, 0));
        credencial.setReqMayusculas(Math.max(reqMayusculas, 0));
        credencial.setReqMinusculas(Math.max(reqMinusculas, 0));
        credencial.setReqDigitos(Math.max(reqDigitos, 0));
        credencial.setReqEspeciales(Math.max(reqEspeciales, 0));

        Categoria categoria = new Categoria();

        categoria.setIdCategoria(idCategoria);
        credencial.setCategoria(categoria);

        gestorCredenciales.crearCredencial(credencial, bovedaActual.getIdBoveda(), conexionActual);

        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

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
     * @see ControladorPrincipal#editarCredencial(int, String, String, String, boolean, String, boolean, String, int, int, int, int, int, int, int)
     * @see GestorCredenciales#editarCredencial(Connection, Boveda, Credencial)
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

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }
        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda activa");
        }

        Credencial credencial = new Credencial();

        credencial.setIdCredencial(idCredencial);
        credencial.setUrlIdentificador(urlIdentificador);
        credencial.setUsername(username);
        credencial.setPassword(password);
        credencial.setDestacada(destacada);
        credencial.setAnotaciones(anotaciones == null || anotaciones.isBlank() ? null : anotaciones.trim());
        credencial.setCaduca(caduca);

        if (caduca && fechaCaducidad != null && !fechaCaducidad.isBlank()) {
            credencial.setFechaCaducidad(LocalDate.parse(fechaCaducidad.trim()));
        } else {
            credencial.setFechaCaducidad(null);
        }

        credencial.setPeriodoCaducidad(periodoCaducidad);

        if (caduca && periodoCaducidad > 0) {
            credencial.setFechaUltimoUpdate(LocalDate.now());
        } else {
            credencial.setFechaUltimoUpdate(null);
        }

        credencial.setReqLongitud(Math.max(reqLongitud, 0));
        credencial.setReqMayusculas(Math.max(reqMayusculas, 0));
        credencial.setReqMinusculas(Math.max(reqMinusculas, 0));
        credencial.setReqDigitos(Math.max(reqDigitos, 0));
        credencial.setReqEspeciales(Math.max(reqEspeciales, 0));

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        credencial.setCategoria(categoria);

        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        gestorCredenciales.editarCredencial(conexionActual, bovedaActual, credencial);

        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();
    }

    /**
     * Elimina una credencial
     *
     * @param credencial la credencial a eliminar
     * @throws Exception si encuentra algún error durante el proceso
     * @see ControladorPrincipal#eliminarCredencial(Credencial)
     * @see GestorCredenciales#eliminarCredencial(Connection, int, Credencial)
     *
     *
     */
    public void eliminarCredencial(Credencial credencial) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No existe ninguna bóveda abierta");
        }

        if (bovedaActual == null) {
            throw new IllegalStateException("No existe ninguna bóveda abierta");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();

        gestorCredenciales.eliminarCredencial(conexionActual, bovedaActual.getIdBoveda(), credencial);

        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    /**
     * Muestra un diálogo para confirmar la eliminación de una credencial
     *
     * @return true si la credencial se puede eliminar, false en caso contrario
     * @see ControladorPrincipal#confirmarEliminacionCredencial()
     *
     */
    public boolean confirmarEliminacionCredencial() {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Eliminar credencial");
        alerta.setHeaderText("Vas a eliminar una credencial");
        alerta.setContentText("¿Estás seguro de que quieres continuar?");

        Optional<ButtonType> resultado = alerta.showAndWait();

        return resultado.isPresent() && resultado.get() == ButtonType.OK;

    }

    /**
     * Obtiene un listado de todas las credenciales de una bóveda que pertenecen a una categoría dada
     *
     * @param idCategoria identificador de la categoría por la que se va a filtrar
     * @return lista de credenciales pertenecientes a la categoría dada
     * @throws Exception si encuentra algún problema duratne el proceso
     * @see ControladorPrincipal#obtenerCredencialesPorCategoria(int)
     * @see GestorCredenciales#obtenerCredencialesPorCategoria(Connection, int)
     *
     *
     */
    public List<Credencial> obtenerCredencialesPorCategoria(int idCategoria) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna conexión activa");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        return gestorCredenciales.obtenerCredencialesPorCategoria(conexionActual, idCategoria);
    }

    /**
     * Cambia el estado de 'destacada' de una credencial dada
     *
     * @param credencial credencial a la que se desea cambiar el estado de 'destacada'
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#toggleDestacada(Credencial)
     * @see GestorCredenciales#actualizarDestacada(Connection, int, boolean)
     *
     *
     */
    public void toggleDestacada(Credencial credencial) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay una conexión activa");
        }

        boolean nuevoValor = !credencial.isDestacada();

        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        gestorCredenciales.actualizarDestacada(conexionActual, credencial.getIdCredencial(), nuevoValor);

        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    /**
     * Obtiene un listado de las credenciales de una bóveda marcadas como 'destacada'
     *
     * @return lista de credenciales destacadas
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#obtenerCredencialesDestacadas()
     * @see GestorCredenciales#obtenerCredencialesDestacadas(Connection, int)
     *
     */
    public List<Credencial> obtenerCredencialesDestacadas() throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda abierta");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();

        return gestorCredenciales.obtenerCredencialesDestacadas(conexionActual, bovedaActual.getIdBoveda());

    }

    /**
     * Obtiene un listado de las credenciales de una bóveda que cumplen un determinado criterio de búsqueda
     *
     * @param textoBusqueda criterio de búsqueda
     * @return lista de credenciale que cumplen el criterio
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#buscarCredencial(String)
     * @see GestorCredenciales#buscarCredenciales(Connection, int, String)
     *
     *
     */
    public List<Credencial> buscarCredencial(String textoBusqueda) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if (bovedaActual == null) {
            throw new IllegalStateException("No hay ninguna bóveda activa");
        }

        GestorCredenciales gestorCredenciales = new GestorCredenciales();

        return gestorCredenciales.buscarCredenciales(conexionActual, bovedaActual.getIdBoveda(), textoBusqueda);


    }

}
