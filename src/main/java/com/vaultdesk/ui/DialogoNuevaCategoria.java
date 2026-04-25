package com.vaultdesk.ui;

import com.vaultdesk.negocio.GestorCategorias;
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

    public DialogoNuevaCategoria(Stage owner){
        this.owner = owner;
    }

    public void mostrar(Consumer<GestorCategorias.DatosNuevaCategoria> callback){

        Stage dialogo = new Stage();
        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Nueva categoría");

        TextField campoNombre = new TextField("Nombre");
        TextArea campoDescripcion = new TextArea("Descripción");

        campoDescripcion.setPrefRowCount(4);

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        Button botonAceptar = new Button("Aceptar");
        Button botonCancelar = new Button("Cancelar");

        botonAceptar.setOnAction(e ->{

            String nombreCategoria = campoNombre.getText() == null ? "" : campoNombre.getText().trim();
            String descripcionCategoria = campoDescripcion.getText() == null ? "": campoDescripcion.getText().trim();

            if(nombreCategoria.isEmpty()){
                etiquetaError.setText("Debes indicar un nombre para la categoría");
                return;
            }

            dialogo.close();
            callback.accept(new GestorCategorias.DatosNuevaCategoria(nombreCategoria, descripcionCategoria));



        });

        botonCancelar.setOnAction(e->{
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

        parrilla.add(new Label("Nombre"), 0, fila);
        parrilla.add(campoNombre, 1, fila++);

        parrilla.add(new Label("Descripción"), 0, fila);
        parrilla.add(campoDescripcion, 1, fila++);

        parrilla.add(etiquetaError, 0, fila, 2, 1);
        fila++;

        parrilla.add(botonAceptar, 0, fila);
        parrilla.add(botonCancelar, 1, fila);

        dialogo.setScene(new Scene(parrilla, 320, 240));
        dialogo.show();


    }



}
