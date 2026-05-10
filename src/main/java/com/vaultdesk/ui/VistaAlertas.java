package com.vaultdesk.ui;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.negocio.GestorIdiomas;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Vista para la pestaña 'Alertas'
 *
 *
 */
public class VistaAlertas {

    private final ControladorPrincipal controladorPrincipal;

    public VistaAlertas(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    public BorderPane crearContenido() {

        BorderPane root = new BorderPane();

        TableView<AlertaCaducidad> tablaAlertas = new TableView<>();

        TableColumn<AlertaCaducidad, String> columnaEstado = new TableColumn<>(GestorIdiomas.getText("tablecolumn.estado")); // "Estado"
        TableColumn<AlertaCaducidad, String> columnaCategoria = new TableColumn<>(GestorIdiomas.getText("tablecolumn.categoria")); // "Categoría"
        TableColumn<AlertaCaducidad, String> columnaUrlIdentificador = new TableColumn<>(GestorIdiomas.getText("tablecolumn.url")); // "URL/Identificador"
        TableColumn<AlertaCaducidad, String> columnaUsername = new TableColumn<>(GestorIdiomas.getText("tablecolumn.username")); // "Username"
        TableColumn<AlertaCaducidad, String> columnaFechaCaducidad = new TableColumn<>(GestorIdiomas.getText("tablecolumn.fechacaducidad")); // "Fecha caducidad"
        TableColumn<AlertaCaducidad, String> columnaDiasRestantes = new TableColumn<>(GestorIdiomas.getText("tablecolumn.restante")); // "Días restantes"

        columnaEstado.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getEstado())
        );

        columnaCategoria.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(
                        datos.getValue().getCredencial().getCategoria() == null
                                ? "" : datos.getValue().getCredencial().getCategoria().getNombre()
                ));


        columnaUrlIdentificador.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCredencial().getUrlIdentificador())
        );

        columnaUsername.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCredencial().getUsername())
        );

        columnaFechaCaducidad.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(datos.getValue().getCredencial().getFechaCaducidad() == null
                        ? "" : datos.getValue().getFechaCaducidad().toString())
        );

        columnaDiasRestantes.setCellValueFactory(datos ->
                new ReadOnlyObjectWrapper<>(calcularDias(datos.getValue().getFechaCaducidad()))
        );

        tablaAlertas.getColumns().addAll(
                columnaEstado,
                columnaCategoria,
                columnaUrlIdentificador,
                columnaUsername,
                columnaFechaCaducidad,
                columnaDiasRestantes
        );

        columnaEstado.setPrefWidth(100);
        columnaCategoria.setPrefWidth(130);
        columnaUrlIdentificador.setPrefWidth(220);
        columnaUsername.setPrefWidth(100);
        columnaFechaCaducidad.setPrefWidth(130);
        columnaDiasRestantes.setPrefWidth(100);

        try {
            List<AlertaCaducidad> listaAlertas = controladorPrincipal.obtenerAlertasCaducidad();

            if (listaAlertas.isEmpty()) {
                root.setCenter(new Label(GestorIdiomas.getText("label.sincredenciales"))); // "No hay credenciales caducadas ni próximas a caducar"
                return root;
            }

            tablaAlertas.setItems(FXCollections.observableArrayList(listaAlertas));

            root.setCenter(tablaAlertas);

        } catch (Exception e) {

            root.setCenter(new Label(GestorIdiomas.getText("label.erroralertas") + e.getMessage())); // "Se produjo un error al cargar las alertas"

        }


        return root;
    }


    private String calcularDias(LocalDate fechaCaducidad) {

        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);

        if (dias < 0) {

            return GestorIdiomas.getText("texto.alertacaducada.inicio") + " " + Math.abs(dias) + " " +  GestorIdiomas.getText("texto.alertacaducada.fin"); // "Hace " + " " + Math.abs(dias) + " " +  " días"
        }
        if (dias == 0) {
            return GestorIdiomas.getText("texto.hoy"); // "Hoy"
        }

        return GestorIdiomas.getText("texto.alertaproxima.inicio") + " " + Math.abs(dias) + " " + GestorIdiomas.getText("texto.alertaproxima.fin"); // "En " + Math.abs(dias) + " días"
    }


}
