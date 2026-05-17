package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Vista para la ventana principal del sistema, una vez se ha abierto una bóveda
 *
 */
public class VistaPrincipal {

    private final ControladorPrincipal controladorPrincipal;

    public VistaPrincipal(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public Scene crearEscena() {

        BorderPane root = new BorderPane();

        MenuBar menu = new MenuBar();


        // Menú Archivo...
        Menu menuArchivo = new Menu(GestorIdiomas.getText("menu.archivo")); // "Archivo"

        MenuItem itemCerrarBoveda = new MenuItem(GestorIdiomas.getText("menu.cerrarboveda")); // "Cerrar bóveda"
        itemCerrarBoveda.setOnAction(e -> controladorPrincipal.cerrarBoveda());

        MenuItem itemGuardarBoveda = new MenuItem(GestorIdiomas.getText("menu.guardarboveda")); // "Guardar bóveda..."
        itemGuardarBoveda.setOnAction(e -> controladorPrincipal.guardarBoveda());

        MenuItem itemExportarACsv = new MenuItem(GestorIdiomas.getText("menu.exportar")); // "Exportar credendiales a CSV..."
        itemExportarACsv.setOnAction(e -> exportarCredenciales(root));

        MenuItem itemSalir = new MenuItem(GestorIdiomas.getText("menu.salir")); // "Salir"
        itemSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());

        menuArchivo.getItems().addAll(
                itemCerrarBoveda,
                itemGuardarBoveda,
                new SeparatorMenuItem(),
                itemExportarACsv,
                new SeparatorMenuItem(),
                itemSalir);

        // Menú Bóveda
        Menu menuBoveda = new Menu(GestorIdiomas.getText("menu.boveda")); // "Bóveda"

        MenuItem itemCambiarPassword = new MenuItem(GestorIdiomas.getText("menu.cambiarpassword")); // "Cambiar contraseña maestra..."
        itemCambiarPassword.setOnAction(e -> controladorPrincipal.cambiarPasswordMaestra());

        menuBoveda.getItems().add(itemCambiarPassword);

        menu.getMenus().addAll(menuArchivo, menuBoveda);

        VistaCredenciales vistaCredenciales = new VistaCredenciales(controladorPrincipal);
        VistaCategorias vistaCategorias = new VistaCategorias(controladorPrincipal);
        VistaAlertas vistaAlertas = new VistaAlertas(controladorPrincipal);
        VistaGenerador vistaGenerador = new VistaGenerador(controladorPrincipal);
        VistaAjustes vistaAjustes = new VistaAjustes(controladorPrincipal);

        TabPane panelPestanas = new TabPane();

        Tab pestanaCredenciales = new Tab(GestorIdiomas.getText("menu.tabcredenciales"), vistaCredenciales.crearContenido()); // "Credenciales"
        Tab pestanaCategorias = new Tab(GestorIdiomas.getText("menu.tabcategorias"), vistaCategorias.crearContenido()); // "Categorias"
        Tab pestanaGenerador = new Tab(GestorIdiomas.getText("menu.generador"), vistaGenerador.crearContenido()); // "Generador"
        Tab pestanaAlertas = new Tab(GestorIdiomas.getText("menu.alertas"), vistaAlertas.crearContenido()); // "Alertas"
        Tab pestanaAjustes = new Tab(GestorIdiomas.getText("menu.ajustes"), vistaAjustes.crearContenido()); // "Ajustes"

        pestanaCredenciales.setClosable(false);
        pestanaCategorias.setClosable(false);
        pestanaGenerador.setClosable(false);
        pestanaAjustes.setClosable(false);
        pestanaAlertas.setClosable(false);

        panelPestanas.getTabs().addAll(
                pestanaCredenciales,
                pestanaCategorias,
                pestanaGenerador,
                pestanaAlertas,
                pestanaAjustes);

        panelPestanas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (actual == pestanaCredenciales) {
                pestanaCredenciales.setContent(new VistaCredenciales(controladorPrincipal).crearContenido());
            }

            if (actual == pestanaAlertas) {
                VistaAlertas nuevaVistaAlertas = new VistaAlertas(controladorPrincipal);
                pestanaAlertas.setContent(nuevaVistaAlertas.crearContenido());
            }
        });

        root.setTop(menu);
        root.setCenter(panelPestanas);

        return new Scene(root, 1050, 800);


    }

    private void exportarCredenciales(BorderPane root) {
        boolean confirmado = controladorPrincipal.confirmarExportacion();

        if (!confirmado) {
            return;
        }

        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle(GestorIdiomas.getText("selectorarchivos.exportar.title")); // "Exportar credenciales a CSV"
        selectorArchivos.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(GestorIdiomas.getText("selectorarchivos.exportar.filter"), "*.csv") // "Archivos CSV"
        );
        selectorArchivos.setInitialFileName("credenciales.csv");

        File archivoSalida = selectorArchivos.showSaveDialog(root.getScene().getWindow());

        if (archivoSalida == null) {
            return;
        }

        try {
            controladorPrincipal.exportarACsv(archivoSalida.toPath());

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(GestorIdiomas.getText("alerta.exportacioncompletada.title")); // "Exportación completada"
            alerta.setHeaderText(null);
            alerta.setContentText(GestorIdiomas.getText("alerta.exportacioncompletada.content")); // "Se ha completado la exportación correctamente"
            alerta.showAndWait();
        } catch (Exception e) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle(GestorIdiomas.getText("alerta.errorexportacion.title")); // "Error en la exportación"
            alerta.setHeaderText(GestorIdiomas.getText("alerta.errorexportacion.header")); // "No se ha podido realizar la exportación"
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();

        }

    }

}
