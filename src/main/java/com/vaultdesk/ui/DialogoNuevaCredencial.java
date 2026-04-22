package com.vaultdesk.ui;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.negocio.GestorCategorias;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.util.List;
import java.util.function.Consumer;

public class DialogoNuevaCredencial {

    private final Stage owner;

    public DialogoNuevaCredencial(Stage owner){
        this.owner = owner;
    }

    public void mostrar(List<Categoria> listaCategorias, Consumer<DatosNuevaCredencial> callback){

        Stage dialogo = new Stage();

        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Nueva credencial");

        TextField campoUrlIdentificador = new TextField();
        TextField campoUsername = new TextField();
        PasswordField campoPassword = new PasswordField();
        CheckBox checkBoxDestacada = new CheckBox("Destacada");
        TextField campoAnotaciones = new TextField();

        CheckBox checkBoxCaduca = new CheckBox("Caduca");
        TextField campoFechaCaducidad = new TextField();
        TextField campoPeriodoCaducidad = new TextField();

        TextField campoReqLongitud = new TextField();
        TextField campoReqMayusculas = new TextField();
        TextField campoReqMinusculas = new TextField();
        TextField campoReqDigitos = new TextField();
        TextField campoReqEspeciales = new TextField();

        campoUrlIdentificador.setPromptText("URL o identificador");
        campoUsername.setPromptText("Username");
        campoPassword.setPromptText("Password");
        campoAnotaciones.setPromptText("Anotaciones");
        campoFechaCaducidad.setPromptText("yyyy-MM-dd");
        campoPeriodoCaducidad.setPromptText("Segundos");
        campoReqLongitud.setPromptText("0");
        campoReqMayusculas.setPromptText("0");
        campoReqMinusculas.setPromptText("0");
        campoReqDigitos.setPromptText("0");
        campoReqEspeciales.setPromptText("0");

        // Rutina para mostrar las categorías como lista
        ComboBox<Categoria> comboBoxCategoria = new ComboBox<>();
        comboBoxCategoria.setItems(FXCollections.observableArrayList(listaCategorias));

        Categoria categoriaOtros = listaCategorias.stream()
                .filter(c -> c.getIdCategoria() == GestorCategorias.ID_CATEGORIA_OTROS)
                .findFirst()
                .orElse(null);

        if(categoriaOtros != null){
            comboBoxCategoria.getSelectionModel().selectFirst();
        } else if(!listaCategorias.isEmpty()){
            comboBoxCategoria.getSelectionModel().selectFirst();
        }


        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        Button botonAceptar = new Button("Aceptar");
        Button botonCancerlar  = new Button("Cancelar");

        botonAceptar.setOnAction(e->{

            try {

                String urlIdentificador = campoUrlIdentificador.getText() == null ? "" : campoUrlIdentificador.getText().trim();
                String username = campoUsername.getText() == null ? "" : campoUsername.getText().trim();
                String password = campoPassword.getText();
                Categoria categoriaSeleccionada = comboBoxCategoria.getValue();

                if (urlIdentificador.isEmpty()) {
                    etiquetaError.setText("Debes indicar una URL o identificador de sistema");
                    return;
                }

                if (username.isEmpty()) {
                    etiquetaError.setText("Debes indicar un nombre de usuario");
                    return;
                }

                if (password == null) {
                    etiquetaError.setText("Debes indicar una contraseña");
                    return;
                }

                callback.accept(new DatosNuevaCredencial(
                        urlIdentificador,
                        username,
                        password,
                        checkBoxDestacada.isSelected(),
                        campoAnotaciones.getText(),
                        checkBoxCaduca.isSelected(),
                        campoFechaCaducidad.getText(),
                        parseEntero(campoPeriodoCaducidad.getText()),
                        parseEntero(campoReqLongitud.getText()),
                        parseEntero(campoReqMayusculas.getText()),
                        parseEntero(campoReqMinusculas.getText()),
                        parseEntero(campoReqDigitos.getText()),
                        parseEntero(campoReqEspeciales.getText()),
                        categoriaSeleccionada.getIdCategoria()
                )); // Por defecto se adjudica a la categoría 'Otros'
                dialogo.close();
            } catch (NumberFormatException ex){
                etiquetaError.setText("Los campos deben contener números enteros válidos");
            }
        });

        botonCancerlar.setOnAction(e->{
            callback.accept(null);
            dialogo.close();
        });

        // Presentando todo en la ventana de diálogo
        GridPane parrilla = new GridPane();
        parrilla.setHgap(10);
        parrilla.setVgap(8);
        parrilla.setPadding(new Insets(15));

        int fila = 0;

        parrilla.add(new Label("URL/Identificador"), 0, fila);
        parrilla.add(campoUrlIdentificador, 1, fila++);

        parrilla.add(new Label("Username"), 0, fila);
        parrilla.add(campoUsername, 1, fila++);

        parrilla.add(new Label("Contraseña"), 0, fila);
        parrilla.add(campoPassword, 1, fila++);

        parrilla.add(new Label("Categoria"), 0, fila);
        parrilla.add(comboBoxCategoria, 1, fila++);

        parrilla.add(new Label("Destacada"), 0, fila);
        parrilla.add(checkBoxDestacada, 1, fila++);

        parrilla.add(new Label("Anotaciones"), 0, fila);
        parrilla.add(campoAnotaciones, 1, fila++);

        parrilla.add(new Label("Caduca"), 0, fila);
        parrilla.add(checkBoxCaduca, 1, fila++);

        parrilla.add(new Label("Fecha caducidad"), 0, fila);
        parrilla.add(campoFechaCaducidad, 1, fila++);

        parrilla.add(new Label("Periodo caducidad"), 0, fila);
        parrilla.add(campoPeriodoCaducidad, 1, fila++);

        parrilla.add(new Label("Requisito Longitud"), 0, fila);
        parrilla.add(campoReqLongitud, 1, fila++);

        parrilla.add(new Label("Requisito mayúsculas"), 0, fila);
        parrilla.add(campoReqMayusculas, 1 ,fila++);

        parrilla.add(new Label("Requisito minúsculas"), 0, fila);
        parrilla.add(campoReqMinusculas, 1, fila++);

        parrilla.add(new Label("Requisito dígitos"), 0, fila);
        parrilla.add(campoReqDigitos, 1, fila++);

        parrilla.add(new Label("Requisito caracteres especiales"), 0, fila);
        parrilla.add(campoReqEspeciales, 1, fila++);

        parrilla.add(etiquetaError, 0, fila, 2, 1);
        parrilla.add(botonAceptar, 0,fila);
        parrilla.add(botonCancerlar, 1, fila);

        dialogo.setScene(new Scene(parrilla, 400, 600));
        dialogo.show();
    }


    private int parseEntero(String  texto){

        if(texto == null || texto.isBlank()){
            return 0;
        }
        return Integer.parseInt(texto.trim());
    }


    public record DatosNuevaCredencial(
            String urlIdentificador,
            String username,
            String password,
            boolean destacada,
            String anotaciones,
            boolean caduca,
            String fechaCaducidad,
            int periodoCaducidad,
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales,
            int idCategoria
    ){}

}
