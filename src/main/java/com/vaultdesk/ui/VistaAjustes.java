package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Idioma;
import com.vaultdesk.dominio.TemaVisual;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import java.security.cert.Extension;
import java.util.List;

public class VistaAjustes {

    private final ControladorPrincipal controladorPrincipal;

    public VistaAjustes(ControladorPrincipal controladorPrincipal){

        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido(){

        BorderPane root = new BorderPane();

        TextField campoUmbralAlerta = new TextField();

        CheckBox checkBoxAccesibilidad = new CheckBox("Activar opciones de accesibilidad");

        ComboBox<Idioma> comboBoxIdiomas = new ComboBox<>();
        ComboBox<TemaVisual> comboBoxTemasVisuales = new ComboBox<>();

        Button botonGuardar = new Button("Guardar");
        Button botonCancelar = new Button("Cancelar");

        Label etiquetaError = new Label();

        etiquetaError.setStyle("-fx-text-fill: red;");

        try{

            List<Idioma> listaIdiomas = controladorPrincipal.obtenerIdiomas();
            List<TemaVisual> listaTemasVisuales = controladorPrincipal.obtenerTemasVisuales();

            comboBoxIdiomas.setItems(FXCollections.observableArrayList(listaIdiomas));
            comboBoxTemasVisuales.setItems(FXCollections.observableArrayList(listaTemasVisuales));

            cargarValoresActuales(campoUmbralAlerta, checkBoxAccesibilidad, comboBoxIdiomas, comboBoxTemasVisuales);

        } catch (Exception e){
            root.setCenter(new Label("Error al cargar ajustes" + e.getMessage()));
            return root;
        }

        botonGuardar.setOnAction(e->{
            try{
                int umbral = Integer.parseInt(campoUmbralAlerta.getText().trim());

                controladorPrincipal.actualizarAjustesBoveda(
                        umbral,
                        checkBoxAccesibilidad.isSelected(),
                        comboBoxIdiomas.getValue(),
                        comboBoxTemasVisuales.getValue()
                );

                etiquetaError.setText("");

            } catch (NumberFormatException ex){
                etiquetaError.setText("El umbral de alerta debe ser un número entero");
            } catch (Exception ex){
                etiquetaError.setText("Error: " + ex.getMessage());
            }
        });

        botonCancelar.setOnAction(e->{
            cargarValoresActuales(campoUmbralAlerta, checkBoxAccesibilidad, comboBoxIdiomas, comboBoxTemasVisuales);
            etiquetaError.setText("");
        });

        GridPane parrilla = new GridPane();
        parrilla.setPadding(new Insets(15));
        parrilla.setVgap(10);
        parrilla.setHgap(10);

        int fila = 0;

        parrilla.add(new Label("Umbral de alerta (días)"), 0, fila);
        parrilla.add(campoUmbralAlerta, 1, fila++);

        parrilla.add(new Label("Accesibilidad"), 0, fila);
        parrilla.add(checkBoxAccesibilidad, 1, fila++);

        parrilla.add(new Label("Idioma"), 0, fila);
        parrilla.add(comboBoxIdiomas, 1, fila++);

        parrilla.add(new Label("Tema visual"), 0, fila);
        parrilla.add(comboBoxTemasVisuales, 1, fila++);

        parrilla.add(botonGuardar, 0, fila);
        parrilla.add(botonCancelar, 1, fila++);
        parrilla.add(etiquetaError, 0, fila,2 ,1);

        root.setCenter(parrilla);


        return root;

    }


    private void cargarValoresActuales(
            TextField campoUmbralAlerta,
            CheckBox checkBoxAccesibilidad,
            ComboBox<Idioma> comboBoxIdiomas,
            ComboBox<TemaVisual> comboBoxTemasVisuales
    ){

        Boveda boveda = controladorPrincipal.getBovedaActual();

        if(boveda == null){
            return;
        }

        campoUmbralAlerta.setText(String.valueOf(boveda.getUmbralAlerta()));
        checkBoxAccesibilidad.setSelected(boveda.isAccesibilidad());

        if(boveda.getIdioma() != null){
            seleccionarIdioma(comboBoxIdiomas, boveda.getIdioma().getIdIdioma());
        }
        if(boveda.getTemaVisual() != null){
            seleccionarTemaVisual(comboBoxTemasVisuales, boveda.getTemaVisual().getIdTemaVisual());
        }

    }


    private void seleccionarIdioma(ComboBox<Idioma> comboBoxIdiomas, int idIdioma){

        for (Idioma idioma : comboBoxIdiomas.getItems()){
            if(idioma.getIdIdioma() == idIdioma){
                comboBoxIdiomas.getSelectionModel().select(idIdioma);
                return;
            }
        }
    }

    private void seleccionarTemaVisual(ComboBox<TemaVisual> comboBoxTemaVisual, int idTemaVisual){

        for (TemaVisual temaVisual : comboBoxTemaVisual.getItems()){
            if(temaVisual.getIdTemaVisual() == idTemaVisual){
                comboBoxTemaVisual.getSelectionModel().select(idTemaVisual);
                return;
            }
        }
    }



}
