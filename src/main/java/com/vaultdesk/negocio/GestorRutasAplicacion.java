package com.vaultdesk.negocio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Clase encargada de gestionar las operaciones para inicializar las rutas de trabajo de la aplicación
 * <p>
 * Esta clase actúa como elemento de transparencia multiplataforma para crear el directorio de trabajo de la aplicación
 * independientemente del sistema operativo subyacente
 * </p>
 *
 */
public class GestorRutasAplicacion {

    private static final String NOMBRE_APLICACION = "VaultDesk";

    /**
     * Obtiene el directorio de trabajo del sistema e infirer el sistema operativo utilizado para utilizar el formato y
     * localización de ruta correspondiente a cada uno de ellos
     *
     * @return ruta del directorio de trabajo del sistema operativo
     *
     */
    public Path obtenerDirectorioTrabajo() {

        String sistemaOperativo = System.getProperty("os.name").toLowerCase();
        String directorioHome = System.getProperty("os.home");

        Path directorioTrabajo;

        if (sistemaOperativo.contains("win")) {

            String appData = System.getenv("APPDATA");

            if (appData != null && !appData.isBlank()) {

                directorioTrabajo = Path.of(appData, NOMBRE_APLICACION);
            } else {

                directorioTrabajo = Path.of(directorioHome, NOMBRE_APLICACION);
            }

        } else if (sistemaOperativo.contains("mac")) {

            directorioTrabajo = Path.of(directorioHome, "Library", "Application Support", NOMBRE_APLICACION);

        } else {
            if (directorioHome != null && !directorioHome.isBlank()) {
                directorioTrabajo = Path.of(directorioHome, "." + NOMBRE_APLICACION.toLowerCase());
            } else {
                directorioTrabajo = Path.of(".", "." + NOMBRE_APLICACION.toLowerCase());
            }
        }

        try {
            Files.createDirectories(directorioTrabajo);
        } catch (IOException e) {
            throw new RuntimeException("No se puede crear el directorio de trabajo de la aplicación", e);
        }


        return directorioTrabajo;

    }

    /**
     * Obtiene el directorio en que se guardan las bóvedas por defecto
     *
     * @return ruta del directorio de bóvedas por defecto
     *
     */
    public Path obtenerDirectorioBovedas() {

        Path directorioBovedas = obtenerDirectorioTrabajo().resolve("vaults");

        try {
            Files.createDirectories(directorioBovedas);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de bóvedas", e);
        }

        return directorioBovedas;
    }


}
