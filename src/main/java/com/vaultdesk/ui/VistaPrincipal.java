package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

public class VistaPrincipal {

    private final ControladorPrincipal controladorPrincipal;

    public VistaPrincipal(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public Scene crearEscena(){

        BorderPane root = new BorderPane();

        MenuBar menu = new MenuBar();

        Menu menuArchivo = new Menu("Archivo");

        MenuItem itemCerrarBoveda = new MenuItem("Cerrar bóveda...");
        itemCerrarBoveda.setOnAction(e -> controladorPrincipal.cerrarBoveda());

        MenuItem itemGuardarBoveda = new MenuItem("Guardar bóveda...");
        itemGuardarBoveda.setOnAction(e-> controladorPrincipal.guardarBoveda());

        MenuItem itemSalir = new MenuItem("Salir");
        itemSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());

        menuArchivo.getItems().addAll(itemCerrarBoveda, itemGuardarBoveda, itemSalir);
        menu.getMenus().add(menuArchivo);

        TabPane panelPestanas = new TabPane();

        Tab pestanaCredenciales = new Tab("Credenciales", new Label("Contenido de Credenciales"));
        Tab pestanaCategorias = new Tab("Categorias", new Label("Contenido de Categorias"));
        Tab pestanaGenerador = new Tab("Generador", new Label("Contenido de Generador de Contraseñas"));
        Tab pestanaAjustes = new Tab("Ajustss", new Label("Contenido de Ajustes del Sistema"));

        pestanaCredenciales.setClosable(false);
        pestanaCategorias.setClosable(false);
        pestanaGenerador.setClosable(false);
        pestanaAjustes.setClosable(false);

        panelPestanas.getTabs().addAll(pestanaCredenciales, pestanaCategorias, pestanaGenerador, pestanaAjustes);

        root.setTop(menu);
        root.setCenter(panelPestanas);

        return new Scene(root,800, 600);


    }



}
