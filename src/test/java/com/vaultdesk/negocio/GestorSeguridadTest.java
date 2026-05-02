package com.vaultdesk.negocio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para comprobar la gestión de seguridad
 *
 *
 */

class GestorSeguridadTest {

    private final GestorSeguridad gestorSeguridad = new GestorSeguridad();

    @Test
    void testDerivarClave() throws Exception {

        byte[] salt = gestorSeguridad.generarSalt();

        byte[] claveDerivada = gestorSeguridad.derivarClave("password".toCharArray(), salt);

        assertNotNull(claveDerivada);
        assertEquals(32, claveDerivada.length);
    }

    @Test
    void testSaltYPasswordSiempreDerivanMismaClave() throws Exception {

        byte[] salt = gestorSeguridad.generarSalt();

        byte[] claveDerivada1 = gestorSeguridad.derivarClave("password".toCharArray(), salt);
        byte[] claveDerivada2 = gestorSeguridad.derivarClave("password".toCharArray(), salt);

        assertArrayEquals(claveDerivada1, claveDerivada2);
    }

    @Test
    void testCifradoDescifradoMismosDatos() throws Exception {

        byte[] salt = gestorSeguridad.generarSalt();
        byte[] vectorInicializacion = gestorSeguridad.generarIV();

        byte[] claveDerivada = gestorSeguridad.derivarClave("password".toCharArray(), salt);

        byte[] datosOriginales = "prueba1234".getBytes();

        byte[] cifrado = gestorSeguridad.cifrar(datosOriginales, claveDerivada, vectorInicializacion);
        byte[] descifrado = gestorSeguridad.descifrar(cifrado, claveDerivada, vectorInicializacion);

        assertArrayEquals(datosOriginales, descifrado);

    }

    @Test
    void testDescifrarClaveIncorrectaFalla() throws Exception {

        byte[] salt = gestorSeguridad.generarSalt();
        byte[] vectorInicializacion = gestorSeguridad.generarIV();

        byte[] claveCorrecta = gestorSeguridad.derivarClave("password".toCharArray(), salt);
        byte[] claveIncorrecta = gestorSeguridad.derivarClave("drowssap".toCharArray(), salt);

        byte[] datosOriginales = "prueba1234".getBytes();


        byte[] cifrado = gestorSeguridad.cifrar(datosOriginales, claveCorrecta, vectorInicializacion);


        assertThrows(Exception.class, () -> {
            gestorSeguridad.descifrar(cifrado, claveIncorrecta, vectorInicializacion);
        });
    }


}