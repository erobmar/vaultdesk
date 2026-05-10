package com.vaultdesk.ui;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
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

public class DialogoEditarCredencial {

    private final Stage owner;

    public DialogoEditarCredencial(Stage owner) {
        this.owner = owner;
    }

    /**
     * Muestra el diálogo de edición de credenciales
     *
     *
     */
    public void mostrar(Credencial credencial, List<Categoria> listaCategorias, Consumer<DatosEdicionCredencial> callback) {

        Stage dialogo = new Stage();
        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle(GestorIdiomas.getText("dialogo.editarcredencial.title")); // "Editar credencial"

        TextField campoUrl = new TextField(credencial.getUrlIdentificador());
        TextField campoUsername = new TextField(credencial.getUsername());
        PasswordField campoPassword = new PasswordField();
        campoPassword.setText(credencial.getPassword());

        ComboBox<Categoria> comboBoxCategoria = new ComboBox<>();
        comboBoxCategoria.setItems(FXCollections.observableArrayList(listaCategorias));

        if (credencial.getCategoria() != null) {
            int idCategoriaActual = credencial.getCategoria().getIdCategoria();

            listaCategorias.stream()
                    .filter(c -> c.getIdCategoria() == idCategoriaActual)
                    .findFirst()
                    .ifPresent(c -> comboBoxCategoria.getSelectionModel().select(c));
        }
        if (comboBoxCategoria.getValue() == null && !listaCategorias.isEmpty()) {
            comboBoxCategoria.getSelectionModel().selectFirst();
        }

        CheckBox checkBoxDestacada = new CheckBox(GestorIdiomas.getText("check.destacada")); // "Destacada"
        checkBoxDestacada.setSelected(credencial.isDestacada());

        TextField campoAnotaciones = new TextField(credencial.getAnotaciones() == null ? "" : credencial.getAnotaciones());

        CheckBox checkBoxCaduca = new CheckBox(GestorIdiomas.getText("check.caduca")); // "Caduca"
        checkBoxCaduca.setSelected(credencial.isCaduca());

        DatePicker campoFechaCaducidad = new DatePicker(credencial.getFechaCaducidad() == null ? null : credencial.getFechaCaducidad());

        TextField campoPeriodoCaducidad = new TextField(credencial.getPeriodoCaducidad() <= 0 ? "" : String.valueOf(credencial.getPeriodoCaducidad()));
        TextField campoReqLongitud = new TextField(credencial.getReqLongitud() <= 0 ? "" : String.valueOf(credencial.getReqLongitud()));
        TextField campoReqMayusculas = new TextField(credencial.getReqMayusculas() <= 0 ? "" : String.valueOf(credencial.getReqMayusculas()));
        TextField campoReqMinusculas = new TextField(credencial.getReqMinusculas() <= 0 ? "" : String.valueOf(credencial.getReqMinusculas()));
        TextField campoReqDigitos = new TextField(credencial.getReqDigitos() <= 0 ? "" : String.valueOf(credencial.getReqDigitos()));
        TextField campoReqEspeciales = new TextField(credencial.getReqEspeciales() <= 0 ? "" : String.valueOf(credencial.getReqEspeciales()));

        campoPeriodoCaducidad.setPromptText(GestorIdiomas.getText("prompt.caducidad")); // "Días"
        campoReqLongitud.setPromptText(GestorIdiomas.getText("prompt.longitud")); // "Longitud mínima"
        campoReqMayusculas.setPromptText(GestorIdiomas.getText("prompt.mayusculas")); // "Mayúsculas"
        campoReqMinusculas.setPromptText(GestorIdiomas.getText("prompt.minusculas")); // "Minúsculas"
        campoReqDigitos.setPromptText(GestorIdiomas.getText("prompt.digitos")); // "Dígitos"
        campoReqEspeciales.setPromptText(GestorIdiomas.getText("prompt.especiales")); // "Caracteres especiales"


        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red");

        Button botonAceptar = new Button(GestorIdiomas.getText("boton.aceptar")); // "Aceptar"
        Button botonCancelar = new Button(GestorIdiomas.getText("boton.cancelar")); // "Cancelar"

        botonAceptar.setOnAction(e -> {

            try {

                String url = campoUrl.getText() == null ? "" : campoUrl.getText().trim();
                String username = campoUsername.getText() == null ? "" : campoUsername.getText().trim();
                String password = campoPassword.getText();
                Categoria categoriaSeleccionada = comboBoxCategoria.getValue();

                String fechaCaducidad = campoFechaCaducidad.getValue() == null ? null : campoFechaCaducidad.getValue().toString();

                if (url.isEmpty()) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errorurl")); // "Debes especificar una URL o Identificador de sistema"
                    return;
                }
                if (username.isEmpty()) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errornombre")); // "Debes especificar un nombre de usuario"
                    return;
                }
                if (password == null || password.isEmpty()) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errorpassword")); // "Debes indicar una contraseña"
                    return;
                }
                if (categoriaSeleccionada == null) {
                    etiquetaError.setText(GestorIdiomas.getText("label.errorcategoria")); // "Debes seleccionar una categoría"
                    return;
                }

                DatosEdicionCredencial datosEdicionCredencial = new DatosEdicionCredencial(
                        credencial.getIdCredencial(),
                        url,
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
                );

                dialogo.close();
                callback.accept(datosEdicionCredencial);

            } catch (NumberFormatException ex) {
                etiquetaError.setText(GestorIdiomas.getText("label.errorvalidacionnumerica")); // "Los campos numéricos deben contener número enteros válidos"
            }

        });

        botonCancelar.setOnAction(e -> {
            callback.accept(null);
            dialogo.close();
        });

        GridPane parrilla = new GridPane();
        parrilla.setVgap(10);
        parrilla.setHgap(8);
        parrilla.setPadding(new Insets(15));

        int fila = 0;

        parrilla.add(new Label(GestorIdiomas.getText("label.url")), 0, fila); // "URL/Identificador"
        parrilla.add(campoUrl, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.username")), 0, fila); // "Username"
        parrilla.add(campoUsername, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.password")), 0, fila); // "Password"
        parrilla.add(campoPassword, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.categoria")), 0, fila); // "Categoría"
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

        parrilla.add(new Label(GestorIdiomas.getText("label.longitud")), 0, fila); // "Requisito longitud"
        parrilla.add(campoReqLongitud, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.mayusculas")), 0, fila); // "Requisito mayúsculas"
        parrilla.add(campoReqMayusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.minusculas")), 0, fila); // "Requisito minúsculas"
        parrilla.add(campoReqMinusculas, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.digitos")), 0, fila); // "Requisito dígitos"
        parrilla.add(campoReqDigitos, 1, fila++);

        parrilla.add(new Label(GestorIdiomas.getText("label.especiales")), 0, fila); // "Requisito caracteres especiales"
        parrilla.add(campoReqEspeciales, 1, fila++);

        parrilla.add(etiquetaError, 0, fila++, 2, 1);
        parrilla.add(botonAceptar, 0, fila);
        parrilla.add(botonCancelar, 1, fila);

        dialogo.setScene(new Scene(parrilla, 400, 600));
        dialogo.show();

    }

    private int parseEntero(String texto) {
        if (texto == null || texto.isBlank()) {
            return 0;
        }
        return Integer.parseInt(texto);
    }

    public record DatosEdicionCredencial(
            int idCredencial,
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
