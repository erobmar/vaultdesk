package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class VistaPrincipal {

    private final ControladorPrincipal controladorPrincipal;

    public VistaPrincipal(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public Scene crearEscena(){

        BorderPane root = new BorderPane();

        MenuBar menu = new MenuBar();


        // Menú Archivo...
        Menu menuArchivo = new Menu("Archivo");

        MenuItem itemCerrarBoveda = new MenuItem("Cerrar bóveda");
        itemCerrarBoveda.setOnAction(e -> controladorPrincipal.cerrarBoveda());

        MenuItem itemGuardarBoveda = new MenuItem("Guardar bóveda...");
        itemGuardarBoveda.setOnAction(e-> controladorPrincipal.guardarBoveda());

        MenuItem itemExportarACsv = new MenuItem("Exportar credendiales a CSV...");
        itemExportarACsv.setOnAction(e-> exportarCredenciales(root));

        MenuItem itemSalir = new MenuItem("Salir");
        itemSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());

        menuArchivo.getItems().addAll(
                itemCerrarBoveda,
                itemGuardarBoveda,
                new SeparatorMenuItem(),
                itemExportarACsv,
                new SeparatorMenuItem(),
                itemSalir);

        // Menú Bóveda
        Menu menuBoveda = new Menu("Bóveda");

        MenuItem itemCambiarPassword = new MenuItem("Cambiar contraseña maestra...");
        itemCambiarPassword.setOnAction(e -> controladorPrincipal.cambiarPasswordMaestra());

        menuBoveda.getItems().add(itemCambiarPassword);

        menu.getMenus().addAll(menuArchivo, menuBoveda);

        VistaCredenciales vistaCredenciales = new VistaCredenciales(controladorPrincipal);
        VistaCategorias vistaCategorias = new VistaCategorias(controladorPrincipal);
        VistaAlertas vistaAlertas = new VistaAlertas(controladorPrincipal);
        VistaGenerador vistaGenerador = new VistaGenerador(controladorPrincipal);
        VistaAjustes vistaAjustes = new VistaAjustes(controladorPrincipal);

        TabPane panelPestanas = new TabPane();

        Tab pestanaCredenciales = new Tab("Credenciales", vistaCredenciales.crearContenido());
        Tab pestanaCategorias = new Tab("Categorias", vistaCategorias.crearContenido());
        Tab pestanaGenerador = new Tab("Generador", vistaGenerador.crearContenido());
        Tab pestanaAlertas = new Tab("Alertas" , vistaAlertas.crearContenido());
        Tab pestanaAjustes = new Tab("Ajustes", vistaAjustes.crearContenido());

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

        panelPestanas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) ->{
            if(actual == pestanaAlertas){
                VistaAlertas nuevaVistaAlertas = new VistaAlertas(controladorPrincipal);
                pestanaAlertas.setContent(nuevaVistaAlertas.crearContenido());
            }
        });

        root.setTop(menu);
        root.setCenter(panelPestanas);

        return new Scene(root,1000, 1000);


    }

    private void exportarCredenciales(BorderPane root){
        boolean confirmado = controladorPrincipal.confirmarExportacion();

        if(!confirmado){
            return;
        }

        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Exportar credenciales a CSV");
        selectorArchivos.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV", "*.csv")
        );
        selectorArchivos.setInitialFileName("credenciales.csv");

        File archivoSalida = selectorArchivos.showSaveDialog(root.getScene().getWindow());

        if(archivoSalida == null){
            return;
        }

        try{
            controladorPrincipal.exportarACsv(archivoSalida.toPath());

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Exportación completada");
            alerta.setHeaderText(null);
            alerta.setContentText("Se ha completado la exportación correctamente");
            alerta.showAndWait();
        } catch (Exception e){

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error en la exportación");
            alerta.setHeaderText("No se ha podido realizar la exportación");
            alerta.setContentText(e.getMessage());
            alerta.showAndWait();

        }

    }

}
