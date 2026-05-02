package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.negocio.GestorCategorias;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Vista para la pestaña 'Categorías'
 *
 */
public class VistaCategorias {

    private final ControladorPrincipal controladorPrincipal;

    private TableView<Categoria> tablaCategorias;

    private BorderPane root;

    public VistaCategorias(ControladorPrincipal controladorPrincipal) {

        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido() {

        root = new BorderPane();

        tablaCategorias = new TableView<>();

        TableColumn<Categoria, Number> columnaIdCategoria = new TableColumn<>("ID");
        TableColumn<Categoria, String> columnaNombre = new TableColumn<>("Nombre");
        TableColumn<Categoria, String> columnaDescripcion = new TableColumn<>("Descripción");
        TableColumn<Categoria, String> columnaEsDelSistema = new TableColumn<>("Sistema");

        columnaIdCategoria.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getIdCategoria()));

        columnaNombre.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getNombre()));

        columnaDescripcion.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getDescripcion() == null ? "" : datos.getValue().getDescripcion()));

        columnaEsDelSistema.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().isDelSistema() ? "Sí" : "No"));


        tablaCategorias.getColumns().addAll(
                columnaIdCategoria,
                columnaNombre,
                columnaDescripcion,
                columnaEsDelSistema
        );

        columnaIdCategoria.setPrefWidth(30);
        columnaNombre.setPrefWidth(150);
        columnaDescripcion.setPrefWidth(300);
        columnaEsDelSistema.setPrefWidth(30);

        Button botonNuevaCategoria = new Button("Nueva...");
        Button botonEditarCategoria = new Button("Editar...");
        Button botonEliminarCategoria = new Button("Eliminar");

        botonNuevaCategoria.setOnAction(e -> {

            DialogoNuevaCategoria dialogoNuevaCategoria = new DialogoNuevaCategoria((Stage) root.getScene().getWindow());

            dialogoNuevaCategoria.mostrar(datos -> {
                if (datos == null) {
                    return;
                }
                try {
                    controladorPrincipal.crearCategoria(datos.nombre(), datos.descripcion());
                    refrescarTabla(tablaCategorias);
                } catch (Exception ex) {

                    ex.printStackTrace();
                }
            });

        });

        botonEditarCategoria.setOnAction(e -> {

            Categoria seleccionada = tablaCategorias.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }
            if (seleccionada.getIdCategoria() == GestorCategorias.ID_CATEGORIA_OTROS || seleccionada.isDelSistema()) {
                return;
            }

            DialogoEditarCategoria dialogoEditarCategoria = new DialogoEditarCategoria((Stage) root.getScene().getWindow());

            dialogoEditarCategoria.mostrar(seleccionada, datos -> {

                if (datos == null) {
                    return;
                }
                try {
                    controladorPrincipal.editarCategoria(datos.idCategoria(), datos.nombre(), datos.descripcion());
                    refrescarTabla(tablaCategorias);
                    tablaCategorias.getSelectionModel().clearSelection();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

            });


        });

        botonEliminarCategoria.setOnAction(e -> {

            Categoria seleccionada = tablaCategorias.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }
            if (seleccionada.getIdCategoria() == GestorCategorias.ID_CATEGORIA_OTROS || seleccionada.isDelSistema()) {
                return;
            }

            boolean confirmado = controladorPrincipal.confirmarEliminacionCategoria(seleccionada);

            if (!confirmado) {
                return;
            }
            try {
                controladorPrincipal.eliminarCategoria(seleccionada);
                refrescarTabla(tablaCategorias);
                tablaCategorias.getSelectionModel().clearSelection();

            } catch (Exception exce) {
                exce.printStackTrace();
            }
        });

        HBox barraSuperior = new HBox(10, botonNuevaCategoria, botonEditarCategoria, botonEliminarCategoria);

        try {
            List<Categoria> categorias = controladorPrincipal.obtenerCategorias();
            tablaCategorias.setItems(FXCollections.observableArrayList(categorias));
        } catch (Exception e) {
            root.setTop(barraSuperior);
            root.setCenter(new Label("Error al cargar las categorías" + e.getMessage()));
            return root;
        }

        root.setTop(barraSuperior);
        root.setCenter(tablaCategorias);
        return root;


    }

    /**
     * Refresca el contenido de la tabla para reflejar los últimos cambios
     *
     */
    private void refrescarTabla(TableView<Categoria> tablaCategorias) throws Exception {
        List<Categoria> categoriasActualizadas = controladorPrincipal.obtenerCategorias();
        tablaCategorias.setItems(FXCollections.observableArrayList(categoriasActualizadas));
    }

}
