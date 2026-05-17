package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * Vista para la pestaña 'Generador'
 *
 */
public class VistaGenerador {

    private final ControladorPrincipal controladorPrincipal;

    public VistaGenerador(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido() {

        BorderPane root = new BorderPane();

        GridPane parrilla = new GridPane();
        parrilla.setHgap(10);
        parrilla.setVgap(10);
        parrilla.setPadding(new Insets(15));

        TextField campoLogintud = new TextField();
        campoLogintud.setPromptText(GestorIdiomas.getText("prompt.longitud")); // "Longitud"
        TextField campoMayusculas = new TextField();
        campoMayusculas.setPromptText(GestorIdiomas.getText("prompt.mayusculas")); // "Mínimo mayúsculas"
        TextField campoMinusculas = new TextField();
        campoMinusculas.setPromptText(GestorIdiomas.getText("prompt.minusculas")); // "Mínimo minúsculas"
        TextField campoDigitos = new TextField();
        campoDigitos.setPromptText(GestorIdiomas.getText("prompt.digitos")); // "Mínimo dígitos"
        TextField campoEspeciales = new TextField();
        campoEspeciales.setPromptText(GestorIdiomas.getText("prompt.especiales")); // "Caracteres especiales"
        TextField campoResultado = new TextField();
        campoResultado.setEditable(false);

        Button botonGenerar = new Button(GestorIdiomas.getText("boton.generar")); // "Generar contraseña"
        Button botonCopiar = new Button(GestorIdiomas.getText("boton.copiarportapapeles")); // "Copiar al portapapeles"

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        botonGenerar.setOnAction(e -> {
            try {
                int longitud = parse(campoLogintud.getText());
                int mayusculas = parse(campoMayusculas.getText());
                int minusculas = parse(campoMinusculas.getText());
                int digitos = parse(campoDigitos.getText());
                int especiales = parse(campoEspeciales.getText());

                String password = controladorPrincipal.generarPassword(longitud, mayusculas, minusculas, digitos, especiales);

                campoResultado.setText(password);
                etiquetaError.setText("");

            } catch (Exception ex) {
                etiquetaError.setText("Error: " + ex.getMessage());
            }
        });

        botonCopiar.setOnAction(e -> {
            if (!campoResultado.getText().isEmpty()) {
                ClipboardContent contenido = new ClipboardContent();
                contenido.putString(campoResultado.getText());
                Clipboard.getSystemClipboard().setContent(contenido);
            }
        });

        int fila = 0;

        parrilla.add(new Label(GestorIdiomas.getText("label.longitudminima")), 0, fila); // "Longitud mínima"
        parrilla.add(campoLogintud, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minimomayusculas")), 0, fila); // "Mínimo de mayúsculas"
        parrilla.add(campoMayusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minimominusculas")), 0, fila); // "Mínimo de minúsculas"
        parrilla.add(campoMinusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minimodigitos")), 0, fila); // "Mínimo de dígitos"
        parrilla.add(campoDigitos, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minimoespeciales")), 0, fila); // "Caracteres especiales"
        parrilla.add(campoEspeciales, 1, fila++);

        parrilla.add(botonGenerar, 0, fila);
        parrilla.add(botonCopiar, 1, fila++);

        parrilla.add(campoResultado, 0, fila++);
        parrilla.add(etiquetaError, 0, fila, 2, 1);

        root.setCenter(parrilla);

        return root;
    }

    public int parse(String campo) {

        if (campo == null || campo.isBlank()) {
            return 0;
        }
        return Integer.parseInt(campo);
    }

}
