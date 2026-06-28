package com.vaultdesk.ui;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.negocio.GestorCategorias;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class DialogoNuevaCredencial {

    private final Stage owner;

    public DialogoNuevaCredencial(Stage owner) {
        this.owner = owner;
    }

    /**
     * Muestra el diálogo de creación de una nueva credencial
     *
     *
     */
    public void mostrar(List<Categoria> listaCategorias, Consumer<DatosNuevaCredencial> callback) {

        Stage dialogo = new Stage();

        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Nueva credencial");

        TextField campoUrlIdentificador = new TextField();
        TextField campoUsername = new TextField();
        PasswordField campoPassword = new PasswordField();
        CheckBox checkBoxDestacada = new CheckBox(GestorIdiomas.getText("label.destacada")); // "Destacada"
        TextField campoAnotaciones = new TextField();

        CheckBox checkBoxCaduca = new CheckBox(GestorIdiomas.getText("label.caduca")); // "Caduca"

        DatePicker campoFechaCaducidad = new DatePicker();

        TextField campoPeriodoCaducidad = new TextField();

        TextField campoReqLongitud = new TextField();
        TextField campoReqMayusculas = new TextField();
        TextField campoReqMinusculas = new TextField();
        TextField campoReqDigitos = new TextField();
        TextField campoReqEspeciales = new TextField();

        campoUrlIdentificador.setPromptText(GestorIdiomas.getText("prompt.url")); // "URL o identificador"
        campoUsername.setPromptText(GestorIdiomas.getText("prompt.usuario")); // "Username"
        campoPassword.setPromptText(GestorIdiomas.getText("prompt.password")); // "Password"
        campoAnotaciones.setPromptText(GestorIdiomas.getText("prompt.anotaciones")); // "Anotaciones"
        campoFechaCaducidad.setPromptText(GestorIdiomas.getText("prompt.fechacaducidad")); // "Fecha de caducidad"
        campoPeriodoCaducidad.setPromptText(GestorIdiomas.getText("prompt.periodocaducidad")); // "Días"
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

        if (categoriaOtros != null) {
            comboBoxCategoria.getSelectionModel().selectFirst();
        } else if (!listaCategorias.isEmpty()) {
            comboBoxCategoria.getSelectionModel().selectFirst();
        }


        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red;");

        Button botonAceptar = new Button(GestorIdiomas.getText("boton.aceptar")); // "Aceptar"
        Button botonCancerlar = new Button(GestorIdiomas.getText("boton.cancelar")); // "Cancelar"

        botonAceptar.setOnAction(e -> {

            try {

                String urlIdentificador = campoUrlIdentificador.getText() == null ? "" : campoUrlIdentificador.getText().trim();
                String username = campoUsername.getText() == null ? "" : campoUsername.getText().trim();
                String password = campoPassword.getText();
                Categoria categoriaSeleccionada = comboBoxCategoria.getValue();

                String fechaCaducidad = campoFechaCaducidad.getValue() == null ? null : campoFechaCaducidad.getValue().toString();

                if (urlIdentificador.isEmpty()) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errorurl")); // "Debes indicar una URL o identificador de sistema"
                    return;
                }

                if (username.isEmpty()) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errornombre")); // "Debes indicar un nombre de usuario"
                    return;
                }

                if (password == null) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errorpassword")); // "Debes indicar una contraseña"
                    return;
                }

                callback.accept(new DatosNuevaCredencial(
                        urlIdentificador,
                        username,
                        password,
                        checkBoxDestacada.isSelected(),
                        campoAnotaciones.getText(),
                        checkBoxCaduca.isSelected(),
                        fechaCaducidad,
                        parseEntero(campoPeriodoCaducidad.getText()),
                        parseEntero(campoReqLongitud.getText()),
                        parseEntero(campoReqMayusculas.getText()),
                        parseEntero(campoReqMinusculas.getText()),
                        parseEntero(campoReqDigitos.getText()),
                        parseEntero(campoReqEspeciales.getText()),
                        categoriaSeleccionada.getIdCategoria()
                )); // Por defecto se adjudica a la categoría 'Otros'
                dialogo.close();
            } catch (NumberFormatException ex) {
                etiquetaError.setText(GestorIdiomas.getText("label.errorvalidacionnumerica")); // "Los campos deben contener números enteros válidos"
            }
        });

        botonCancerlar.setOnAction(e -> {
            callback.accept(null);
            dialogo.close();
        });

        // Presentando todo en la ventana de diálogo
        GridPane parrilla = new GridPane();
        parrilla.setHgap(10);
        parrilla.setVgap(8);
        parrilla.setPadding(new Insets(15));

        int fila = 0;

        parrilla.add(new Label(GestorIdiomas.getText("label.url")), 0, fila); // "URL/Identificador"
        parrilla.add(campoUrlIdentificador, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.username")), 0, fila); // "Username"
        parrilla.add(campoUsername, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.password")), 0, fila); // "Contraseña"
        parrilla.add(campoPassword, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.categoria")), 0, fila); // "Categoria"
        parrilla.add(comboBoxCategoria, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.destacada")), 0, fila); // "Destacada"
        parrilla.add(checkBoxDestacada, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.anotaciones")), 0, fila); // "Anotaciones"
        parrilla.add(campoAnotaciones, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.caduca")), 0, fila); // "Caduca"
        parrilla.add(checkBoxCaduca, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.fechacaducidad")), 0, fila); // "Fecha caducidad"
        parrilla.add(campoFechaCaducidad, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.periodocaducidad")), 0, fila); // "Periodo caducidad"
        parrilla.add(campoPeriodoCaducidad, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.longitud")), 0, fila); // "Requisito Longitud"
        parrilla.add(campoReqLongitud, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.mayusculas")), 0, fila); // "Requisito mayúsculas"
        parrilla.add(campoReqMayusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minusculas")), 0, fila); // "Requisito minúsculas"
        parrilla.add(campoReqMinusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.digitos")), 0, fila); // "Requisito dígitos"
        parrilla.add(campoReqDigitos, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.especiales")), 0, fila); // "Requisito caracteres especiales"
        parrilla.add(campoReqEspeciales, 1, fila++);

        parrilla.add(botonAceptar, 0, fila);
        parrilla.add(botonCancerlar, 1, fila++);

        parrilla.add(etiquetaError, 0, fila, 2, 1);


        dialogo.setScene(new Scene(parrilla, 400, 600));
        dialogo.show();
    }


    private int parseEntero(String texto) {

        if (texto == null || texto.isBlank()) {
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
    ) {
    }

}
