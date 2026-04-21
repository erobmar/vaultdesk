package com.vaultdesk.ui;

import com.vaultdesk.dominio.Credencial;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;


import java.util.function.Consumer;

public class DialogoEditarCredencial {

    private final Stage owner;

    public DialogoEditarCredencial(Stage owner){
        this.owner = owner;
    }

    public void mostrar(Credencial credencial, Consumer<DatosEdicionCredencial> callback){

        Stage dialogo = new Stage();
        dialogo.initOwner(owner);
        dialogo.initModality(Modality.APPLICATION_MODAL);
        dialogo.setTitle("Editar credencial");

        TextField campoUrl = new TextField(credencial.getUrlIdentificador());
        TextField campoUsername = new TextField(credencial.getUsername());
        PasswordField campoPassword = new PasswordField();
        campoPassword.setText(credencial.getPassword());

        CheckBox checkBoxDestacada = new CheckBox("Destacada");
        checkBoxDestacada.setSelected(credencial.isDestacada());

        TextField campoAnotaciones = new TextField(credencial.getAnotaciones() == null ? "" : credencial.getAnotaciones());

        CheckBox checkBoxCaduca = new CheckBox("Caduca");
        checkBoxCaduca.setSelected(credencial.isCaduca());

        TextField campoFechaCaducidad = new TextField(credencial.getFechaCaducidad() == null ? "" : credencial.getFechaCaducidad().toString());
        TextField campoPeriodoCaducidad = new TextField(credencial.getPeriodoCaducidad() <= 0 ? "" : String.valueOf(credencial.getPeriodoCaducidad()));
        TextField campoReqLongitud = new TextField(credencial.getReqLongitud() <= 0 ? "" : String.valueOf(credencial.getReqLongitud()));
        TextField campoReqMayusculas = new TextField(credencial.getReqMayusculas() <= 0 ? "" : String.valueOf(credencial.getReqMayusculas()));
        TextField campoReqMinusculas = new TextField(credencial.getReqMinusculas() <= 0 ? "" : String.valueOf(credencial.getReqMinusculas()));
        TextField campoReqDigitos = new TextField(credencial.getReqDigitos() <= 0 ? "" : String.valueOf(credencial.getReqDigitos()));
        TextField campoReqEspeciales = new TextField(credencial.getReqEspeciales() <= 0 ? "" : String.valueOf(credencial.getReqEspeciales()));

        Label etiquetaError = new Label();
        etiquetaError.setStyle("-fx-text-fill: red");

        Button botonAceptar = new Button("Aceptar");
        Button botonCancelar = new Button("Cancelar");

        botonAceptar.setOnAction(e-> {

            try {

                String url = campoUrl.getText() == null ? "" : campoUrl.getText().trim();
                String username = campoPassword.getText() == null ? "" : campoUsername.getText().trim();
                String password = campoPassword.getText();

                if (url.isEmpty()) {
                    etiquetaError.setText("Debes especificar una URL o Identificador de sistema");
                    return;
                }
                if (username.isEmpty()) {
                    etiquetaError.setText("Debes especificar un nombre de usuario");
                    return;
                }
                if (password == null || password.isEmpty()) {
                    etiquetaError.setText("Debes indicar una contraseña");
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
                        campoFechaCaducidad.getText(),
                        parseEntero(campoPeriodoCaducidad.getText()),
                        parseEntero(campoReqLongitud.getText()),
                        parseEntero(campoReqMayusculas.getText()),
                        parseEntero(campoReqMinusculas.getText()),
                        parseEntero(campoReqDigitos.getText()),
                        parseEntero(campoReqEspeciales.getText()),
                        1
                );

                dialogo.close();
                callback.accept(datosEdicionCredencial);

            }catch (NumberFormatException ex){
                etiquetaError.setText("Los campos numéricos deben contener número enteros válidos");
            }

        });

        botonCancelar.setOnAction(e->{
            callback.accept(null);
            dialogo.close();
        });

        GridPane parrilla = new GridPane();
        parrilla.setVgap(10);
        parrilla.setHgap(8);
        parrilla.setPadding(new Insets(15));

        int fila = 0;

        parrilla.add(new Label("URL/Identificador"), 0 , fila);
        parrilla.add(campoUrl, 1, fila++);

        parrilla.add(new Label("Username"), 0, fila);
        parrilla.add(campoUsername, 1,fila++ );

        parrilla.add(new Label("Password"), 0, fila);
        parrilla.add(campoPassword, 1,fila++);

        parrilla.add(new Label("Destacada"), 0, fila);
        parrilla.add(checkBoxDestacada, 1, fila++);

        parrilla.add(new Label("Anotaciones"), 0, fila);
        parrilla.add(campoAnotaciones, 1, fila++);

        parrilla.add(new Label("Caduca"), 0, fila);
        parrilla.add(checkBoxCaduca, 1 , fila++);

        parrilla.add(new Label("Fecha caducidad"), 0, fila);
        parrilla.add(campoFechaCaducidad, 1, fila++);

        parrilla.add(new Label("Periodo caducidad"), 0, fila);
        parrilla.add(campoPeriodoCaducidad, 1, fila++);

        parrilla.add(new Label("Requisito longitud"), 0, fila);
        parrilla.add(campoReqLongitud, 1, fila++);

        parrilla.add(new Label("Requisito mayúsculas"), 0, fila);
        parrilla.add(campoReqMayusculas, 1, fila++);

        parrilla.add(new Label("Requisito minúsculas"), 0, fila);
        parrilla.add(campoReqMinusculas, 1, fila++);

        parrilla.add(new Label("Requisito dígitos"), 0, fila);
        parrilla.add(campoReqDigitos, 1, fila++);

        parrilla.add(new Label("Requisito caracteres especiales"), 0, fila);
        parrilla.add(campoReqEspeciales, 1, fila++);

        parrilla.add(etiquetaError, 0,fila++,2,1);
        parrilla.add(botonAceptar, 0, fila);
        parrilla.add(botonCancelar, 1,fila);

        dialogo.setScene(new Scene(parrilla, 400,600));
        dialogo.show();

    }


    private int parseEntero(String texto){
        if(texto == null || texto.isBlank()){
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
    ){ }

}
