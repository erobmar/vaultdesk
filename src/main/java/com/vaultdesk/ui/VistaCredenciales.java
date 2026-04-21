package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Credencial;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class VistaCredenciales {

    private final ControladorPrincipal controladorPrincipal;

    private Integer idCredencialVisible = null;

    public VistaCredenciales(ControladorPrincipal controladorPrincipal){

        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido(){

        BorderPane root = new BorderPane();

        TableView<Credencial> tablaCredenciales = new TableView<>();

        TableColumn<Credencial, Number> columnaID = new TableColumn<>("ID");
        TableColumn<Credencial, String> columnaUsername = new TableColumn<>("Username");
        TableColumn<Credencial, String> columnaUrlIdentificador = new TableColumn<>("URL/Identificador");
        TableColumn<Credencial, String> columnaPassword = new TableColumn<>("Contraseña");
        TableColumn<Credencial, String> columnaDestacada = new TableColumn<>("Destacada");
        TableColumn<Credencial, String> columnaAnotaciones = new TableColumn<>("Anotaciones");
        TableColumn<Credencial, String> columnaCaduca = new TableColumn<>("Caduca");
        TableColumn<Credencial, String> columnaFechaCaducidad = new TableColumn<>("Fecha de caducidad");
        TableColumn<Credencial, String> columnaPeriodoCaducidad = new TableColumn<>("Periodo caducidad (en días)");
        TableColumn<Credencial, String> columnaReqLongitud = new TableColumn<>("Longitud requerida");
        TableColumn<Credencial, String> columnaReqMayusculas = new TableColumn<>("Mayúsculas requeridas");
        TableColumn<Credencial, String> columnaReqMinusculas = new TableColumn<>("Minúsculasas requeridas");
        TableColumn<Credencial, String> columnaReqDigitos = new TableColumn<>("Dígitos requeridos");
        TableColumn<Credencial, String> columnaReqEspeciales = new TableColumn<>("Caracteres especiales requeridos");

        columnaID.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(datos.getValue().getIdCredencial())
        );
        columnaUsername.setCellValueFactory(datos->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUsername())
        );
        columnaUrlIdentificador.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUrlIdentificador())
        );
        columnaPassword.setCellValueFactory(datos ->

                new ReadOnlyObjectWrapper<>(
                        idCredencialVisible != null
                                && datos.getValue().getIdCredencial() == idCredencialVisible
                                ? datos.getValue().getPassword()
                                : ocultarPassword(datos.getValue().getPassword())
                )
        );
        columnaDestacada.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(datos.getValue().isDestacada() ? "Sí" : "No")
        );
        columnaCaduca.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().isCaduca() ? "Sí" : "No")
        );
        columnaFechaCaducidad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getFechaCaducidad() == null ? "" : datos.getValue().getFechaCaducidad().toString()
                )
        );
        columnaPeriodoCaducidad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getPeriodoCaducidad() <= 0 ? "" : String.valueOf(datos.getValue().getPeriodoCaducidad())
                )
        );
        columnaReqLongitud.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqLongitud() <= 0 ? "" : String.valueOf(datos.getValue().getReqLongitud())
                )
        );
        columnaReqMayusculas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqMayusculas() <= 0 ? "" : String.valueOf(datos.getValue().getReqMayusculas())
                )
        );
        columnaReqMinusculas.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqMinusculas() <= 0 ? "" : String.valueOf(datos.getValue().getReqMinusculas())
                )
        );
        columnaReqDigitos.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqDigitos() <= 0 ? "" : String.valueOf(datos.getValue().getReqDigitos())
                )
        );
        columnaReqEspeciales.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getReqEspeciales() <= 0 ? "" : String.valueOf(datos.getValue().getReqEspeciales())
                )
        );
        columnaAnotaciones.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getAnotaciones() == null ? "" : datos.getValue().getAnotaciones()
                ));








        tablaCredenciales.getColumns().addAll(
                columnaID,
                columnaUrlIdentificador,
                columnaUsername,
                columnaPassword,
                columnaDestacada,
                columnaCaduca,
                columnaFechaCaducidad,
                columnaPeriodoCaducidad,
                columnaReqLongitud,
                columnaReqMayusculas,
                columnaReqMinusculas,
                columnaReqDigitos,
                columnaReqEspeciales,
                columnaAnotaciones
        );

        columnaID.setPrefWidth(70);
        columnaUrlIdentificador.setPrefWidth(220);
        columnaUsername.setPrefWidth(160);
        columnaPassword.setPrefWidth(160);
        columnaDestacada.setPrefWidth(80);
        columnaAnotaciones.setPrefWidth(200);
        columnaCaduca.setPrefWidth(80);
        columnaFechaCaducidad.setPrefWidth(120);
        columnaPeriodoCaducidad.setPrefWidth(130);
        columnaReqLongitud.setPrefWidth(120);
        columnaReqMayusculas.setPrefWidth(120);
        columnaReqMinusculas.setPrefWidth(120);
        columnaReqDigitos.setPrefWidth(120);
        columnaReqEspeciales.setPrefWidth(120);

        // Sección para botón de 'Nueva credencial' - "Editar credencial"
        Button botonNuevaCredencial = new Button("Nueva...");
        Button botonEditarCredencial = new Button("Editar...");
        Button botonEliminarCredencial = new Button("Eliminar");
        Button botonTogglePassword = new Button("Mostrar/Ocultar password");

        botonNuevaCredencial.setOnAction(e->{

            DialogoNuevaCredencial dialogo = new DialogoNuevaCredencial((Stage) root.getScene().getWindow()); // TODO - Documentar

            dialogo.mostrar(datos ->{

                    if(datos == null){
                        return;
                    }
                    try{
                        controladorPrincipal.crearCredencial(
                                datos.urlIdentificador(),
                                datos.username(),
                                datos.password(),
                                datos.destacada(),
                                datos.anotaciones(),
                                datos.caduca(),
                                datos.fechaCaducidad(),
                                datos.periodoCaducidad(),
                                datos.reqLongitud(),
                                datos.reqMayusculas(),
                                datos.reqMinusculas(),
                                datos.reqDigitos(),
                                datos.reqEspeciales(),
                                datos.idCategoria()
                        );

                        refrescarTabla(tablaCredenciales);

                    } catch (Exception excepcion){
                        excepcion.printStackTrace();
                    }
            });

        });

        botonEditarCredencial.setOnAction(e->{
            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if(seleccionada == null){
                return;
            }

            DialogoEditarCredencial dialogoEditarCredencial = new DialogoEditarCredencial((Stage) root.getScene().getWindow());

            dialogoEditarCredencial.mostrar(seleccionada, datos ->{

                if(datos == null){
                    return;
                }
                try{
                    controladorPrincipal.editarCredencial(
                            datos.idCredencial(),
                            datos.urlIdentificador(),
                            datos.username(),
                            datos.password(),
                            datos.destacada(),
                            datos.anotaciones(),
                            datos.caduca(),
                            datos.fechaCaducidad(),
                            datos.periodoCaducidad(),
                            datos.reqLongitud(),
                            datos.reqMayusculas(),
                            datos.reqMinusculas(),
                            datos.reqDigitos(),
                            datos.reqEspeciales(),
                            datos.idCategoria()
                    );

                  refrescarTabla(tablaCredenciales);

                } catch (Exception exc){

                    exc.printStackTrace();
                }});

        });


        botonEliminarCredencial.setOnAction(e->{

            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if(seleccionada == null){

                return;
            }

            boolean confirmado = controladorPrincipal.confirmarEliminacionCredencial();

            if(!confirmado){
                return;
            }

            try{
                controladorPrincipal.eliminarCredencial(seleccionada);

                refrescarTabla(tablaCredenciales);

            } catch (Exception ex) {

                ex.printStackTrace();
                throw new RuntimeException(ex);
            }


        });


        botonTogglePassword.setOnAction(e ->{

            Credencial seleccionada = tablaCredenciales.getSelectionModel().getSelectedItem();

            if(seleccionada == null){
                return;
            }
            if(idCredencialVisible != null && idCredencialVisible == seleccionada.getIdCredencial()){
                idCredencialVisible = null;
            } else {
                idCredencialVisible = seleccionada.getIdCredencial();
            }
            try{
                refrescarTabla(tablaCredenciales);
            } catch (Exception ex){
                ex.printStackTrace();
            }

        });


        HBox barraSuperior = new HBox(
                10,
                botonNuevaCredencial,
                botonEditarCredencial,
                botonEliminarCredencial,
                botonTogglePassword);


        try{
            List<Credencial> credenciales = controladorPrincipal.obtenerCredenciales();
            tablaCredenciales.setItems(FXCollections.observableArrayList(credenciales));
        } catch (Exception e){
            root.setTop(barraSuperior);
            root.setCenter(new Label("Error al cargar las credenciales" + e.getMessage()));
            return root;
        }

        root.setTop(barraSuperior);
        root.setCenter(tablaCredenciales);

        return root;
    }

    private void refrescarTabla(TableView<Credencial> tablaCredenciales) throws Exception{
        List<Credencial> credencialesActualizadas = controladorPrincipal.obtenerCredenciales();
        tablaCredenciales.setItems(FXCollections.observableArrayList(credencialesActualizadas));
    }

    private String ocultarPassword(String password){
        if(password == null || password.isEmpty()){
            return "";
        }
        return "*".repeat(password.length());
    }

}
