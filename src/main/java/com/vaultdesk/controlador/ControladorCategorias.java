package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.negocio.GestorCategorias;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Clase auxiliar encargada de las operaciones sobre categorías en una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas creación, edición y
 * eliminación de categorías, así como la obtención de listados de las mismas
 * </p>
 *
 */
public class ControladorCategorias {

    private final ControladorPrincipal controladorPrincipal;

    private final GestorCategorias gestorCategorias;

    public ControladorCategorias(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;

        this.gestorCategorias = new GestorCategorias();
    }

    /**
     * Obtiene una lista de todas las categorías de una bóveda
     *
     * @return lista de categorías de la bóveda
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorPrincipal#obtenerCategorias()
     * @see GestorCategorias#obtenerCategorias(Connection)
     *
     *
     */
    public List<Categoria> obtenerCategorias() throws SQLException {

        Connection conexionActual = controladorPrincipal.getConexionActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No hay ninguna conexión activa"
        }

        try {
            return gestorCategorias.obtenerCategorias(conexionActual);
        } catch (Exception e) {
            throw new RuntimeException(GestorIdiomas.getText("excepcion.obtencioncategorias"), e); // "Error al obtener categorías"
        }

    }

    /**
     * Crea una nueva categoría con el nombre y descripción pasados como parámetros
     *
     * @param nombre      nombre para la nueva categoría
     * @param descripcion descripción de la nueva categoría
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorPrincipal#crearCategoria(String, String)
     * @see GestorCategorias#crearCategoria(Connection, Categoria)
     *
     *
     */
    public void crearCategoria(String nombre, String descripcion) throws SQLException {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No hay ninguna conexión activa"
        }
        if (bovedaActual == null) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.boveda")); // "No hay ninguna bóveda abierta"
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEsDelSistema(false);

        gestorCategorias.crearCategoria(conexionActual, categoria);
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    /**
     * Actualiza una categoría con el nombre y descripción pasados como parámetros
     *
     * @param idCategoria identificador de la categoría que se quiere modificar
     * @param nombre      nuevo nombre para la categoría
     * @param descripcion nueva descripción de la categoría
     * @throws SQLException si encuentra algún problema con la base de datos durante el proceso
     * @see ControladorPrincipal#editarCategoria(int, String, String)
     * @see GestorCategorias#editarCategoria(Connection, Categoria)
     *
     *
     */
    public void editarCategoria(int idCategoria, String nombre, String descripcion) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No hay ninguna conexión abierta"
        }
        if (bovedaActual == null) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.boveda")); // "No hay ninguna bóveda abierta"
        }

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(idCategoria);
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);

        gestorCategorias.editarCategoria(conexionActual, categoria);
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();

    }

    /**
     * Elimina una categoría dada de la bóveda
     *
     * @param categoria categoría que se va a eliminar
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#eliminarCategoria(Categoria)
     * @see GestorCategorias#eliminarCategoria(Connection, int)
     *
     *
     */
    public void eliminarCategoria(Categoria categoria) throws Exception {

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if (conexionActual == null || conexionActual.isClosed()) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.conexion")); // "No hay ninguna conexión activa"
        }
        if (bovedaActual == null) {
            throw new IllegalStateException(GestorIdiomas.getText("excepcion.boveda")); // "No hay ninguna bóveda abierta"
        }

        gestorCategorias.eliminarCategoria(conexionActual, categoria.getIdCategoria());
        bovedaActual.setModificadaSinGuardar(true);
        controladorPrincipal.actualizarTituloVentana();
    }

    /**
     * Muestra un diálogo para confirmar la eliminación de la categoría dada
     *
     * @param categoria categoría  que se va a eliminar
     * @return true si se confirma la eliminación, false en caso contrario
     * @see ControladorPrincipal#confirmarEliminacionCategoria(Categoria)
     *
     *
     */
    public boolean confirmarEliminacionCategoria(Categoria categoria) {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(GestorIdiomas.getText("alerta.eliminarcategoria.title")); // "Eliminar categoría"
        alerta.setHeaderText(GestorIdiomas.getText("alerta.eliminarcategoria.header") + " " + categoria.getNombre()); // "Va a eliminar la categoría "
        alerta.setContentText(GestorIdiomas.getText("alerta.eliminarcategoria.content")); // "Las credenciales asignadas se reasignarán a 'Otros'. ¿Desea continuar?"

        Optional<ButtonType> respuesta = alerta.showAndWait();

        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
    }


}
