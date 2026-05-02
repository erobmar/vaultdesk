package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.nio.file.Path;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para pruebas unitarias del GestorBovedas
 *
 *
 */
public class GestorBovedasTest {

    private GestorBovedas gestorBovedas;
    private GestorBaseDatos gestorBaseDatos;
    private GestorPersistencia gestorPersistencia;

    private Connection conexion;
    private Boveda boveda;

    @TempDir
    Path rutaTemporal;

    @BeforeEach
    void inicializacion() throws Exception {

        gestorBovedas = new GestorBovedas();
        gestorBaseDatos = new GestorBaseDatos();
        gestorPersistencia = new GestorPersistencia();

        conexion = gestorBaseDatos.crearBaseDatosEnMemoria();
    }

    @AfterEach
    void cierre() throws Exception {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }


    @Test
    void testInsercionBovedaInicial() throws Exception {

        gestorBovedas.insertarBovedaInicial(conexion, "prueba");
        boveda = gestorBovedas.cargarBovedaActualDesdeBD(conexion);

        assertNotNull(boveda);
        assertEquals("prueba", boveda.getNombre());
    }

    @Test
    void testCargaBovedaDesdeBD() throws Exception {

        gestorBovedas.insertarBovedaInicial(conexion, "prueba");
        Boveda bovedaVolcada = gestorBovedas.cargarBovedaActualDesdeBD(conexion);

        assertNotNull(bovedaVolcada);
        assertTrue(bovedaVolcada.getIdBoveda() > 0);
        assertEquals("prueba", bovedaVolcada.getNombre());

    }

    @Test
    void testCambioPasswordMaestra() throws Exception {

        Path ruta = crearBovedaTemporal("prueba1234");

        gestorBovedas.cambiarPasswordMaestra(ruta, "prueba1234".toCharArray(), "4321abeurp".toCharArray());

        try (Connection conexionAbierta = gestorPersistencia.abrirBovedaEnMemoria(ruta, "4321abeurp".toCharArray())) {

            assertNotNull(conexionAbierta);
        }
    }

    @Test
    void testNoCambiaPassworIncorrecta() throws Exception {

        Path ruta = crearBovedaTemporal("passwordInicial");

        assertThrows(Exception.class, () ->
                gestorBovedas.cambiarPasswordMaestra(
                        ruta,
                        "passwordIncorrecta".toCharArray(),
                        "passwordNueva".toCharArray()
                )
        );

        try (Connection conexionReabierta =
                     gestorPersistencia.abrirBovedaEnMemoria(ruta, "passwordInicial".toCharArray())) {

            assertNotNull(conexionReabierta);
        }
    }

    private Path crearBovedaTemporal(String password) throws Exception {

        Path ruta = rutaTemporal.resolve("boveda-test.vlt");

        try (Connection conexionTemporal = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            gestorBovedas.insertarBovedaInicial(conexionTemporal, "Boveda Temporal");

            gestorPersistencia.guardarBovedaDesdeMemoria(ruta, password.toCharArray(), conexionTemporal);

        }

        return ruta;

    }

}
