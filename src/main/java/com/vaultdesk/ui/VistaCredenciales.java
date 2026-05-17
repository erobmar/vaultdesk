package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * Vista para la pestaña 'Credenciales'
 *
 */
public class VistaCredenciales {

    private final ControladorPrincipal controladorPrincipal;

    private TableView<Credencial> tablaCredenciales;

    private BorderPane root;

    private Integer idCredencialVisible = null;

    public VistaCredenciales(ControladorPrincipal controladorPrincipal) {

        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido() {

        root = new BorderPane();

        tablaCredenciales = new TableView<>();

        TableColumn<Credencial, Number> columnaID = new TableColumn<>(GestorIdiomas.getText("tablecolumn.id")); // "ID"
        TableColumn<Credencial, String> columnaUsername = new TableColumn<>(GestorIdiomas.getText("tablecolumn.username")); // "Username"
        TableColumn<Credencial, String> columnaUrlIdentificador = new TableColumn<>(GestorIdiomas.getText("tablecolumn.url")); // "URL/Identificador"
        TableColumn<Credencial, String> columnaPassword = new TableColumn<>(GestorIdiomas.getText("tablecolumn.password")); // "Contraseña"
        TableColumn<Credencial, String> columnaDestacada = new TableColumn<>(GestorIdiomas.getText("tablecolumn.destacada")); // "Destacada"
        TableColumn<Credencial, String> columnaAnotaciones = new TableColumn<>(GestorIdiomas.getText("tablecolumn.anotaciones")); // "Anotaciones"
        TableColumn<Credencial, String> columnaCaduca = new TableColumn<>(GestorIdiomas.getText("tablecolumn.caduca")); // "Caduca"
        TableColumn<Credencial, String> columnaFechaCaducidad = new TableColumn<>(GestorIdiomas.getText("tablecolumn.fechacaducidad")); // "Fecha de caducidad"
        TableColumn<Credencial, String> columnaPeriodoCaducidad = new TableColumn<>(GestorIdiomas.getText("tablecolumn.periodocaducidad")); // "Periodo caducidad (en días)"
        TableColumn<Credencial, String> columnaReqLongitud = new TableColumn<>(GestorIdiomas.getText("tablecolumn.longitud")); // "Longitud requerida"
        TableColumn<Credencial, String> columnaReqMayusculas = new TableColumn<>(GestorIdiomas.getText("tablecolumn.mayusculas")); // "Mayúsculas requeridas"
        TableColumn<Credencial, String> columnaReqMinusculas = new TableColumn<>(GestorIdiomas.getText("tablecolumn.minusculas")); // "Minúsculasas requeridas"
        TableColumn<Credencial, String> columnaReqDigitos = new TableColumn<>(GestorIdiomas.getText("tablecolumn.digitos")); // "Dígitos requeridos"
        TableColumn<Credencial, String> columnaReqEspeciales = new TableColumn<>(GestorIdiomas.getText("tablecolumn.especiales")); // "Caracteres especiales requeridos"
        TableColumn<Credencial, String> columnaCategoria = new TableColumn<>(GestorIdiomas.getText("tablecolumn.categoria")); // "Categoría"


        columnaID.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getIdCredencial())
        );
        columnaUsername.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUsername())
        );
        columnaUrlIdentificador.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUrlIdentificador())
        );
        columnaPassword.setCellValueFactory(datos ->

                new ReadOnlyObjectWrapper<>(
                        idCredencialVisible != null
                                && datos.getValue().getIdCredencial() == idCredencialVisible
                                ? datos.getValue().getPassword()
                                : ocultarPassword(datos.getValue().getPassword())
                )
        );
        columnaDestacada.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().isDestacada() ? "Sí" : "No")
        );
        columnaCaduca.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().isCaduca() ? "Sí" : "No")
        );
        columnaFechaCaducidad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getFechaCaducidad() == null ? "" : datos.getValue().getFechaCaducidad().toString()
                )
        );
        columnaPeriodoCaducidad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getPeriodoCaducidad() <= 0 ? "" : String.valueOf(datos.getValue().getPeriodoCaducidad())
                )
        );
        columnaReqLongitud.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqLongitud() <= 0 ? "" : String.valueOf(datos.getValue().getReqLongitud())
                )
        );
        columnaReqMayusculas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqMayusculas() <= 0 ? "" : String.valueOf(datos.getValue().getReqMayusculas())
                )
        );
        columnaReqMinusculas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqMinusculas() <= 0 ? "" : String.valueOf(datos.getValue().getReqMinusculas())
                )
        );
        columnaReqDigitos.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqDigitos() <= 0 ? "" : String.valueOf(datos.getValue().getReqDigitos())
                )
        );
        columnaReqEspeciales.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqEspeciales() <= 0 ? "" : String.valueOf(datos.getValue().getReqEspeciales())
                )
        );
        columnaAnotaciones.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getAnotaciones() == null ? "" : datos.getValue().getAnotaciones()
                ));

        columnaCategoria.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getCategoria() == null ? "" : datos.getValue().getCategoria().getNombre()
                ));

        tablaCredenciales.getColumns().addAll(
                columnaID,
                columnaUrlIdentificador,
                columnaCategoria,
                columnaUsername,
                columnaPassword,
                columnaDestacada,
                columnaCaduca,
                columnaFechaCaducidad,
                columnaPeriodoCaducidad,
                columnaAnotaciones
        );

        columnaID.setPrefWidth(20);
        columnaUrlIdentificador.setPrefWidth(150);
        columnaCategoria.setPrefWidth(100);
        columnaUsername.setPrefWidth(150);
        columnaPassword.setPrefWidth(150);
        columnaDestacada.setPrefWidth(50);
        columnaAnotaciones.setPrefWidth(185);
        columnaCaduca.setPrefWidth(50);
        columnaFechaCaducidad.setPrefWidth(80);
        columnaPeriodoCaducidad.setPrefWidth(50);

        // Sección para botón de 'Nueva credencial' - "Editar credencial"
        Button botonNuevaCredencial = new Button(GestorIdiomas.getText("boton.nueva")); // "Nueva..."
        Button botonEditarCredencial = new Button(GestorIdiomas.getText("boton.editar")); // "Editar..."
        Button botonEliminarCredencial = new Button(GestorIdiomas.getText("boton.eliminar")); // "Eliminar"
        Button botonTogglePassword = new Button(GestorIdiomas.getText("boton.togglepassowrd")); // "Mostrar/Ocultar password"
        Button botonCopiarPassword = new Button(GestorIdiomas.getText("boton.copiarpassword")); // "Copiar password"
        Button botonActualizarPassword = new Button(GestorIdiomas.getText("boton.actualizarpassword")); // "Actualizar password"
        Button botonToggleDestacada = new Button(GestorIdiomas.getText("boton.toggledestacada")); // "Marca/Descmarca destacada"
        Button botonSoloDestacadas = new Button(GestorIdiomas.getText("boton.solodestacadas")); // "Solo destacadas"


        botonCopiarPassword.setDisable(true);

        // Sección de búsqueda
        TextField campoBusqueda = new TextField(GestorIdiomas.getText("texto.buscar")); // "Buscar credenciales..."

        ComboBox<Categoria> comboBoxFiltroCategorias = new ComboBox<>();
        comboBoxFiltroCategorias.setPromptText(GestorIdiomas.getText("prompt.filtrar")); // "Filtrar por categoría"

        // Poblar el ComboBox de categorías
        try {
            List<Categoria> listaCategorias = controladorPrincipal.obtenerCategorias();
            comboBoxFiltroCategorias.setItems(FXCollections.observableArrayList(listaCategorias));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Button botonFiltrar = new Button(GestorIdiomas.getText("boton.filtrar")); // "Filtrar"
        Button botonLimpiarFiltro = new Button(GestorIdiomas.getText("boton.limpiarfiltro")); // "Limpiar filtro"

        Button botonBuscar = new Button(GestorIdiomas.getText("boton.buscar")); // "Buscar"
        Button botonLimpiarBusqueda = new Button(GestorIdiomas.getText("boton.limpiarbusqueda")); // "Limpiar búsqueda"


        botonNuevaCredencial.setOnAction(e -> {

            DialogoNuevaCredencial dialogo = new DialogoNuevaCredencial((Stage) root.getScene().getWindow());

            try {

                List<Categoria> listaCategorias = controladorPrincipal.obtenerCategorias();

                dialogo.mostrar(listaCategorias, datos -> {

                    if (datos == null) {
                        return;
                    }
                    try {


                        controladorPrincipal.crearCredencial(
                                datos.urlIdentificador(),
                                datos.username(),
                                datos.password(),
                                datos.destacada(),
                                datos.anotaciones(),
                                datos.caduca(),
                                datos.fechaCaducidad(),
                                datos.periodoCaducidad(),
                                datos.reqLongitud(),
                                datos.reqMayusculas(),
                                datos.reqMinusculas(),
                                datos.reqDigitos(),
                                datos.reqEspeciales(),
                                datos.idCategoria()
                        );

                        refrescarTabla(tablaCredenciales);
                        botonCopiarPassword.setDisable(true);

                    } catch (Exception excepcion) {
                        excepcion.printStackTrace();
                    }
                });
            } catch (Exception es) {

                es.printStackTrace();

            }
        });

        botonEditarCredencial.setOnAction(e -> {
            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }

            DialogoEditarCredencial dialogoEditarCredencial = new DialogoEditarCredencial((Stage) root.getScene().getWindow());

            try {

                List<Categoria> listaCategorias = controladorPrincipal.obtenerCategorias();

                dialogoEditarCredencial.mostrar(seleccionada, listaCategorias, datos -> {

                    if (datos == null) {
                        return;
                    }
                    try {
                        controladorPrincipal.editarCredencial(
                                datos.idCredencial(),
                                datos.urlIdentificador(),
                                datos.username(),
                                datos.password(),
                                datos.destacada(),
                                datos.anotaciones(),
                                datos.caduca(),
                                datos.fechaCaducidad(),
                                datos.periodoCaducidad(),
                                datos.reqLongitud(),
                                datos.reqMayusculas(),
                                datos.reqMinusculas(),
                                datos.reqDigitos(),
                                datos.reqEspeciales(),
                                datos.idCategoria()
                        );

                        refrescarTabla(tablaCredenciales);
                        botonCopiarPassword.setDisable(!sePuedeCopiar(seleccionada));
                    } catch (Exception exc) {

                        exc.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        botonEliminarCredencial.setOnAction(e -> {

            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {

                return;
            }

            boolean confirmado = controladorPrincipal.confirmarEliminacionCredencial();

            if (!confirmado) {
                return;
            }

            try {
                controladorPrincipal.eliminarCredencial(seleccionada);

                botonCopiarPassword.setDisable(!sePuedeCopiar(seleccionada));
                idCredencialVisible = null;
                refrescarTabla(tablaCredenciales);


            } catch (Exception ex) {

                ex.printStackTrace();
                throw new RuntimeException(ex);
            }


        });

        botonBuscar.setOnAction(e -> {
            try {
                List<Credencial> listaBusqueda = controladorPrincipal.buscarCredencial(campoBusqueda.getText());
                tablaCredenciales.setItems(FXCollections.observableArrayList(listaBusqueda));
            } catch (Exception exce) {
                exce.printStackTrace();
            }
        });

        botonLimpiarBusqueda.setOnAction(e -> {
            try {
                campoBusqueda.clear();
                comboBoxFiltroCategorias.getSelectionModel().clearSelection();
                idCredencialVisible = null;
                refrescarTabla(tablaCredenciales);
                botonCopiarPassword.setDisable(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        campoBusqueda.setOnAction(e -> botonBuscar.fire());

        botonTogglePassword.setOnAction(e -> {

            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }
            if (idCredencialVisible != null && idCredencialVisible == seleccionada.getIdCredencial()) {
                idCredencialVisible = null;
            } else {
                idCredencialVisible = seleccionada.getIdCredencial();
            }

            botonCopiarPassword.setDisable(!sePuedeCopiar(seleccionada));

            try {
                tablaCredenciales.refresh();
                botonCopiarPassword.setDisable(!sePuedeCopiar(seleccionada));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        botonCopiarPassword.setOnAction(e -> {
            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (!sePuedeCopiar(seleccionada)) {
                return;
            }


            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(seleccionada.getPassword());


            Clipboard portapapeles = Clipboard.getSystemClipboard();
            portapapeles.setContent(contenido);


            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(GestorIdiomas.getText("alerta.passwordcopiada.title")); // "Contraseña copiada"
            alerta.setHeaderText(null);
            alerta.setContentText(GestorIdiomas.getText("alerta.passwordcopiada.content")); // "La contraseña se ha copiado al portapapeles"
            alerta.showAndWait();

        });

        botonActualizarPassword.setOnAction(e -> {
            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }

            boolean confirmado = controladorPrincipal.confirmarActualizacionPassword();

            if (!confirmado) {
                return;
            }
            try {
                controladorPrincipal.actualizarPasswordCredencial(seleccionada);
                refrescarTabla(tablaCredenciales);
                tablaCredenciales.refresh();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        botonFiltrar.setOnAction(e -> {

            Categoria seleccionFiltro = comboBoxFiltroCategorias.getValue();

            if (seleccionFiltro == null) {
                return;
            }
            try {
                List<Credencial> credencialesFiltradas = controladorPrincipal.obtenerCredencialesPorCategoria(seleccionFiltro.getIdCategoria());
                tablaCredenciales.setItems(FXCollections.observableArrayList(credencialesFiltradas));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        botonLimpiarFiltro.setOnAction(e -> {
            try {
                comboBoxFiltroCategorias.getSelectionModel().clearSelection();
                refrescarTabla(tablaCredenciales);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        botonToggleDestacada.setOnAction(e -> {

            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if (seleccionada == null) {
                return;
            }
            try {
                controladorPrincipal.toggleDestacada(seleccionada);
                refrescarTabla(tablaCredenciales);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });

        botonSoloDestacadas.setOnAction(e -> {
            try {
                List<Credencial> destacadas = controladorPrincipal.obtenerCredencialesDestacadas();
                tablaCredenciales.setItems(FXCollections.observableArrayList(destacadas));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        HBox controlesOperacion = new HBox(
                10,
                botonNuevaCredencial,
                botonEditarCredencial,
                botonEliminarCredencial,
                botonTogglePassword,
                botonCopiarPassword,
                botonActualizarPassword,
                botonToggleDestacada
        );

        HBox controlesVisualizacion = new HBox(
                10,
                campoBusqueda,
                botonBuscar,
                botonLimpiarBusqueda,
                comboBoxFiltroCategorias,
                botonFiltrar,
                botonLimpiarFiltro,
                botonSoloDestacadas
        );


        VBox controles = new VBox(
                2, controlesOperacion, controlesVisualizacion
        );


        try {
            List<Credencial> credenciales = controladorPrincipal.obtenerCredenciales();
            tablaCredenciales.setItems(FXCollections.observableArrayList(credenciales));
        } catch (Exception e) {
            root.setTop(controles);
            root.setCenter(new Label(GestorIdiomas.getText("label.errorcargarcredenciales") + e.getMessage())); // "Error al cargar las credenciales"
            return root;
        }

        root.setTop(controles);
        root.setCenter(tablaCredenciales);

        return root;
    }

    /**
     * Refresca la tabla para reflejar los últimos cambios
     *
     */
    private void refrescarTabla(TableView<Credencial> tablaCredenciales) throws Exception {
        List<Credencial> credencialesActualizadas = controladorPrincipal.obtenerCredenciales();
        tablaCredenciales.setItems(FXCollections.observableArrayList(credencialesActualizadas));
    }

    /**
     * Oculta la contraseña remplazándola por asteriscos
     *
     */
    private String ocultarPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(password.length());
    }

    /**
     * Indica si la contraseña está visible para poder ser copiada al portapapeles
     *
     */
    private boolean sePuedeCopiar(Credencial credencial) {
        return credencial != null && idCredencialVisible != null && credencial.getIdCredencial() == idCredencialVisible;
    }

}
