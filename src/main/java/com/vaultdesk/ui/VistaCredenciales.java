package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Credencial;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class VistaCredenciales {

    private final ControladorPrincipal controladorPrincipal;

    public VistaCredenciales(ControladorPrincipal controladorPrincipal){

        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido(){

        BorderPane root = new BorderPane();

        TableView<Credencial> tablaCredenciales = new TableView<>();

        TableColumn<Credencial, Number> columnaID = new TableColumn<>("ID");
        TableColumn<Credencial, String> columnaUsername = new TableColumn<>("Username");
        TableColumn<Credencial, String> columnaUrlIdentificador = new TableColumn<>("URL/Identificador");
        TableColumn<Credencial, String> columnaDestacada = new TableColumn<>("Destacada");

        columnaID.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(datos.getValue().getIdCredencial())
        );
        columnaUsername.setCellValueFactory(datos->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUsername())
        );
        columnaUrlIdentificador.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getUrlIdentificador())
        );
        columnaDestacada.setCellValueFactory(datos ->
            new ReadOnlyObjectWrapper<>(datos.getValue().isDestacada() ? "Sí" : "No")
        );

        tablaCredenciales.getColumns().addAll(columnaID, columnaUrlIdentificador, columnaUsername, columnaDestacada);

        columnaID.setPrefWidth(80);
        columnaUrlIdentificador.setPrefWidth(400);
        columnaUsername.setPrefWidth(200);
        columnaDestacada.setPrefWidth(80);

        // Sección para botón de 'Nueva credencial' - "Editar credencial"
        Button botonNuevaCredencial = new Button("Nueva...");
        Button botonEditarCredencial = new Button("Editar...");

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
                                datos.idCategoria()
                        );

                        List<Credencial> credencialesActualizadas = controladorPrincipal.obtenerCredenciales();
                        tablaCredenciales.setItems(FXCollections.observableArrayList(credencialesActualizadas));
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
                            datos.idCategoria()
                    );

                    List<Credencial> credencialesActualizadas = controladorPrincipal.obtenerCredenciales();
                    tablaCredenciales.setItems(FXCollections.observableArrayList(credencialesActualizadas));
                } catch (Exception exc){

                    exc.printStackTrace();
                }});

        });




        HBox barraSuperior = new HBox(10, botonNuevaCredencial, botonEditarCredencial);


        try{
            List<Credencial> credenciales = controladorPrincipal.obtenerCredenciales();
            tablaCredenciales.setItems(FXCollections.observableArrayList(credenciales));
        } catch (Exception e){

            root.setCenter(new Label("Error al cargar las credenciales" + e.getMessage()));
            return root;
        }

        root.setTop(barraSuperior);
        root.setCenter(tablaCredenciales);

        return root;
    }



}
