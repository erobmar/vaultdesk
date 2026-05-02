package com.vaultdesk.integracion;

import com.vaultdesk.negocio.GestorBovedas;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para prueba de integración
 *
 */
class VaultDeskIntegracionTest {

    private final GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
    private final GestorBovedas gestorBovedas = new GestorBovedas();
    private final GestorPersistencia gestorPersistencia = new GestorPersistencia();


    @TempDir
    Path rutaTemporal;

    @Test
    void testGuardarYAbrirConPasswordCorrecto() throws Exception {

        Path ruta = rutaTemporal.resolve("boveda-prueba.vlt");

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            gestorBovedas.insertarBovedaInicial(conexion, "PruebasDeIntegración");

            gestorPersistencia.guardarBovedaDesdeMemoria(ruta, "passwordCorrecto".toCharArray(), conexion);

        }

        try (Connection segundaConexion = gestorPersistencia.abrirBovedaEnMemoria(ruta, "passwordCorrecto".toCharArray())) {
            assertNotNull(segundaConexion);
        }
    }

    @Test
    void testGuardarYAbrirConPasswordIncorrecto() throws Exception {

        Path ruta = rutaTemporal.resolve("boveda-prueba.vlt");

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            gestorBovedas.insertarBovedaInicial(conexion, "PruebasDeIntegración");

            gestorPersistencia.guardarBovedaDesdeMemoria(ruta, "passwordCorrecto".toCharArray(), conexion);

        }

        assertThrows(Exception.class, () ->
                gestorPersistencia.abrirBovedaEnMemoria(
                        ruta,
                        "passwordIncorrecta".toCharArray()
                )
        );


    }

}