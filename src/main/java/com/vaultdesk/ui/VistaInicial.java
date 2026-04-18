package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.swing.*;

public class VistaInicial {

    private final ControladorPrincipal controladorPrincipal;

    public VistaInicial(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public Scene crearEscena(){

        BorderPane root = new BorderPane();

        MenuBar menu = new MenuBar();
        Menu menuArchivo = new Menu("Archivo");

        MenuItem itemAbrirBoveda = new MenuItem("Abrir Bóveda...");
        itemAbrirBoveda.setOnAction(e -> controladorPrincipal.abrirBoveda());

        MenuItem itemSalir = new MenuItem("Salir");
        itemSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());

        menuArchivo.getItems().addAll(itemAbrirBoveda, itemSalir);

        menu.getMenus().add(menuArchivo);

        Label logo = new Label("VaultDesk");
        logo.setFont(Font.font(40));

        Label subtitulo = new Label("Gestor de credenciales local y seguro");

        VBox cajaVertical = new VBox(15, logo, subtitulo);
        cajaVertical.setAlignment(Pos.CENTER);

        root.setTop(menu);
        root.setCenter(cajaVertical);

        return new Scene(root, 640, 480);

    }


}
