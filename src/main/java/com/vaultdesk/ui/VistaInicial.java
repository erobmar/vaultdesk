package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import javax.swing.*;
import java.util.Objects;

/**
 * Vista inicial del sistema, sin bóveda cargada
 *
 */
public class VistaInicial {

    private final ControladorPrincipal controladorPrincipal;

    public VistaInicial(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public Scene crearEscena() {

        BorderPane root = new BorderPane();

        MenuBar menu = new MenuBar();
        Menu menuArchivo = new Menu(GestorIdiomas.getText("menu.archivo")); // Archivo

        MenuItem itemAbrirBoveda = new MenuItem(GestorIdiomas.getText("menu.abrirboveda")); // Abrir bóveda...
        itemAbrirBoveda.setOnAction(e -> controladorPrincipal.abrirBoveda());

        MenuItem itemNuevaBoveda = new MenuItem(GestorIdiomas.getText("menu.nuevaboveda")); // Nueva Bóveda...
        itemNuevaBoveda.setOnAction(e -> controladorPrincipal.crearNuevaBoveda());

        MenuItem itemSalir = new MenuItem(GestorIdiomas.getText("menu.salir")); // "Salir"
        itemSalir.setOnAction(e -> controladorPrincipal.salirAplicacion());

        menuArchivo.getItems().addAll(itemAbrirBoveda, itemNuevaBoveda, itemSalir);

        menu.getMenus().add(menuArchivo);

        Image imagen = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/VaultDeskLogoFullSize.png")));
        ImageView logo = new ImageView(imagen);
        logo.setPreserveRatio(true);
        logo.setFitHeight(600);
        logo.setFitWidth(400);

        Label subtitulo = new Label(GestorIdiomas.getText("label.bienvenida")); // Gestor de credenciales local y seguro
        subtitulo.setFont(Font.font(24));

        VBox cajaVertical = new VBox(15, logo, subtitulo);
        cajaVertical.setAlignment(Pos.CENTER);

        root.setTop(menu);
        root.setCenter(cajaVertical);

        return new Scene(root, 640, 480);

    }


}
