package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GestorExportacionCredenciales {

    /**
     * Clase encargada de gestionar las operaciones de exportación de credenciales
     * <p>
     * Esta clase auxilixar actúa como apoyo para la clase GestorCredenciales, tomando algunas de sus responsabilidades,
     * como la exportación de credenciales a un archivo CSV, así como el formato de dicha salida
     * </p>
     *
     */
    private final GestorCredenciales gestorCredenciales;

    public GestorExportacionCredenciales(GestorCredenciales gestorCredenciales) {
        this.gestorCredenciales = gestorCredenciales;
    }

    /**
     * Exporta un listado con todas las credenciales de la bóveda a un archivo CSV
     *
     * @param credenciales lista de todas las credenciales de la bóveda
     * @param rutaDestino  directorio en el que se creará el archivo CSV
     * @see GestorExportacionCredenciales#exportarCredencialesCSV(List, Path)
     *
     */
    public void exportarCredencialesCSV(List<Credencial> credenciales, Path rutaDestino) throws Exception {

        if (credenciales == null) {
            throw new IllegalArgumentException("La lista de credenciales no puede ser null");
        }
        if (rutaDestino == null) {
            throw new IllegalArgumentException("Debes especificar una ruta para la exportación");
        }
        try (BufferedWriter bufferEscritura = Files.newBufferedWriter(rutaDestino, StandardCharsets.UTF_8)) {
            bufferEscritura.write("id,url_identificador,username,password,categoria,destacada,caduca,fecha_caducidad,periodo_caducidad,ultimo_update,anotaciones");
            bufferEscritura.newLine();
            for (Credencial credencial : credenciales) {
                bufferEscritura.write(lineaCSV(credencial));
                bufferEscritura.newLine();
            }
        }
    }

    private String lineaCSV(Credencial credencial) {

        String linea = "";

        linea += escaparCadena(String.valueOf(credencial.getIdCredencial())) + ",";
        linea += escaparCadena(credencial.getUrlIdentificador()) + ",";
        linea += escaparCadena(credencial.getUsername()) + ",";
        linea += escaparCadena(credencial.getPassword()) + ",";
        linea += escaparCadena(credencial.getCategoria() == null ? "" : credencial.getCategoria().getNombre()) + ",";
        linea += escaparCadena(credencial.isDestacada() ? "Sí" : "No") + ",";
        linea += escaparCadena(credencial.isCaduca() ? "Sí" : "No") + ",";
        linea += escaparCadena(credencial.getFechaCaducidad() == null ? "" : credencial.getFechaCaducidad().toString()) + ",";
        linea += escaparCadena(credencial.getPeriodoCaducidad() <= 0 ? "" : String.valueOf(credencial.getPeriodoCaducidad())) + ",";
        linea += escaparCadena(credencial.getFechaUltimoUpdate() == null ? "" : credencial.getFechaUltimoUpdate().toString()) + ",";
        linea += escaparCadena(credencial.getAnotaciones() == null ? "" : credencial.getAnotaciones());
        return linea;
    }

    private String escaparCadena(String cadena) {

        if (cadena == null) {
            return "";
        }
        String cadenaEscapada = cadena.replace("\"", "\"\"");

        return "\"" + cadenaEscapada + "\"";

    }
}
