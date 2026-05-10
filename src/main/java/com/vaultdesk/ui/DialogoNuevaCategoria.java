package com.vaultdesk.ui;

import com.vaultdesk.negocio.GestorCategorias;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.util.function.Consumer;

public class DialogoNuevaCategoria {


    private final Stage owner;

    public DialogoNuevaCategoria(Stage owner) {
        this.owner = owner;
    }

    /**
     * Muestra el diálogo de creación de una nueva categoría
     *
     *
     */
    public void mostrar(Consumer<GestorCategorias.DatosNuevaCategoria> callback) {

        Stage dialogo = new Stage();
        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle(GestorIdiomas.getText("dialogo.nuevacategoria.title")); // "Nueva categoría"

        TextField campoNombre = new TextField(GestorIdiomas.getText("texto.nombre")); // "Nombre"
        TextArea campoDescripcion = new TextArea(GestorIdiomas.getText("texto.descripcion")); // "Descripción"

        campoDescripcion.setPrefRowCount(4);

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        Button botonAceptar = new Button(GestorIdiomas.getText("boton.aceptar")); // "Aceptar"
        Button botonCancelar = new Button(GestorIdiomas.getText("boton.cancelar")); // "Cancelar"

        botonAceptar.setOnAction(e -> {

            String nombreCategoria = campoNombre.getText() == null ? "" : campoNombre.getText().trim();
            String descripcionCategoria = campoDescripcion.getText() == null ? "" : campoDescripcion.getText().trim();

            if (nombreCategoria.isEmpty()) {
                etiquetaError.setText(GestorIdiomas.getText("label.error.sinnombre")); // "Debes indicar un nombre para la categoría"
                return;
            }

            dialogo.close();
            callback.accept(new GestorCategorias.DatosNuevaCategoria(nombreCategoria, descripcionCategoria));


        });

        botonCancelar.setOnAction(e -> {
            callback.accept(null);
            dialogo.close();
        });

        GridPane parrilla = new GridPane();
        parrilla.setHgap(10);
        parrilla.setVgap(10);
        parrilla.setPadding(new Insets(15));

        campoDescripcion.setPrefWidth(200);
        campoNombre.setPrefWidth(200);


        int fila = 0;

        parrilla.add(new Label(GestorIdiomas.getText("label.nombre")), 0, fila); // "Nombre"
        parrilla.add(campoNombre, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.descripcion")), 0, fila); // "Descripción"
        parrilla.add(campoDescripcion, 1, fila++);

        parrilla.add(etiquetaError, 0, fila, 2, 1);
        fila++;

        parrilla.add(botonAceptar, 0, fila);
        parrilla.add(botonCancelar, 1, fila);

        dialogo.setScene(new Scene(parrilla, 320, 240));
        dialogo.show();


    }


}
