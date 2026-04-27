package com.vaultdesk.negocio;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para comprobar la gestión de seguridad
 * */

class GestorSeguridadTest {

    private final GestorSeguridad gestorSeguridad = new GestorSeguridad();

    // Comprobar si el salt generado tiene la longitud correcta
    @Test
    void generarSaltTieneLongitudCorrecta() {
        byte[] salt = gestorSeguridad.generarSalt();

        assertNotNull(salt);
        assertEquals(16, salt.length);
    }

    // Comprobar si el vector de inicialización generado tiene la longitud correcta
    @Test
    void generarIVTieneLongitudCorrecta() {
        byte[] iv = gestorSeguridad.generarIV();

        assertNotNull(iv);
        assertEquals(12, iv.length);
    }

    // Comprobar si el salt generado es aleatorio (comparando dos salt generados consecutivos)
    @Test
    void generarSaltEsAleatorio() {
        byte[] salt1 = gestorSeguridad.generarSalt();
        byte[] salt2 = gestorSeguridad.generarSalt();

        assertFalse(Arrays.equals(salt1, salt2));
    }

    // Comprobar si el vector de inicialización generado es aleatorio (comparando dos VIs generados consecutivos)
    @Test
    void generarIVEsAleatorio() {
        byte[] iv1 = gestorSeguridad.generarIV();
        byte[] iv2 = gestorSeguridad.generarIV();

        assertFalse(Arrays.equals(iv1, iv2));
    }

    // Comprobar si la clave derivada es de 256 bits
    @Test
    void derivarClaveGeneraClaveDe256Bits() throws Exception {
        char[] password = "P@55w0rD".toCharArray();
        byte[] salt = gestorSeguridad.generarSalt();

        byte[] clave = gestorSeguridad.derivarClave(password, salt);

        assertNotNull(clave);
        assertEquals(32, clave.length);
    }

    // Comprobar que el mismo password y salt derivan siempre la misma clave de cifrado
    @Test
    void mismaPasswordYSaltGeneranMismaClave() throws Exception {
        char[] password1 = "P@55w0rD".toCharArray();
        char[] password2 = "P@55w0rD".toCharArray();
        byte[] salt = gestorSeguridad.generarSalt();

        byte[] clave1 = gestorSeguridad.derivarClave(password1, salt);
        byte[] clave2 = gestorSeguridad.derivarClave(password2, salt);

        assertArrayEquals(clave1, clave2);
    }

    // Comprobar que al cifrar y descifrar se mantiene la integridad de los datos
    @Test
    void cifrarYDescifrarRecuperanDatosOriginales() throws Exception {
        byte[] datosOriginales = "Texto de prueba".getBytes(StandardCharsets.UTF_8);
        byte[] salt = gestorSeguridad.generarSalt();
        byte[] clave = gestorSeguridad.derivarClave("P@55w0rD".toCharArray(), salt);
        byte[] iv = gestorSeguridad.generarIV();

        byte[] datosCifrados = gestorSeguridad.cifrar(datosOriginales, clave, iv);
        byte[] datosDescifrados = gestorSeguridad.descifrar(datosCifrados, clave, iv);

        assertArrayEquals(datosOriginales, datosDescifrados);
    }

    // Comprobar que los datos cifrados no coinciden con los datos en claro
    @Test
    void datosCifradosNoCoincidenConDatosEnClaro() throws Exception {
        byte[] datosOriginales = "Texto de prueba".getBytes(StandardCharsets.UTF_8);
        byte[] salt = gestorSeguridad.generarSalt();
        byte[] clave = gestorSeguridad.derivarClave("P@55w0rD".toCharArray(), salt);
        byte[] iv = gestorSeguridad.generarIV();

        byte[] datosCifrados = gestorSeguridad.cifrar(datosOriginales, clave, iv);

        assertFalse(Arrays.equals(datosOriginales, datosCifrados));
    }

    // Comrpobar que intentar descifrar datos con una clave incorrecta arroja una excepción
    @Test
    void descifrarConClaveIncorrectaFalla() throws Exception {
        byte[] datosOriginales = "Texto de prueba".getBytes(StandardCharsets.UTF_8);

        byte[] claveCorrecta = gestorSeguridad.derivarClave(
                "P@55w0rDC0rr3Ct@".toCharArray(),
                gestorSeguridad.generarSalt()
        );

        byte[] claveIncorrecta = gestorSeguridad.derivarClave(
                "P@55w0rD1nC0rr3ct@".toCharArray(),
                gestorSeguridad.generarSalt()
        );

        byte[] iv = gestorSeguridad.generarIV();
        byte[] datosCifrados = gestorSeguridad.cifrar(datosOriginales, claveCorrecta, iv);

        assertThrows(
                Exception.class,
                () -> gestorSeguridad.descifrar(datosCifrados, claveIncorrecta, iv)
        );
    }

    // Comprobar que descifrar datos con un Vector de Inicialización incorrecto arroja una excepción
    @Test
    void descifrarConIVIncorrectoFalla() throws Exception {
        byte[] datosOriginales = "Texto de prueba".getBytes(StandardCharsets.UTF_8);
        byte[] clave = gestorSeguridad.derivarClave("P@55w0rD".toCharArray(), gestorSeguridad.generarSalt());

        byte[] ivCorrecto = gestorSeguridad.generarIV();
        byte[] ivIncorrecto = gestorSeguridad.generarIV();

        byte[] datosCifrados = gestorSeguridad.cifrar(datosOriginales, clave, ivCorrecto);

        assertThrows(
                Exception.class,
                () -> gestorSeguridad.descifrar(datosCifrados, clave, ivIncorrecto)
        );
    }
}