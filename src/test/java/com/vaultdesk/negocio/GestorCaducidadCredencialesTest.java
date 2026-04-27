package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para comprobar la caducidad de credenciales
 * */
class GestorCaducidadCredencialesTest {

    private final GestorCaducidadCredenciales gestorCaducidadCredenciales =
            new GestorCaducidadCredenciales(null);

    // Comprobar que estaCaducada() no detecta como caducada una credencial que no lo está
    @Test
    void credencialNoCaducaNoSeMarcaComoCaducada() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(false);

        boolean resultado = gestorCaducidadCredenciales.estaCaducada(credencial, LocalDate.of(2026, 4, 26));

        assertFalse(resultado);
    }

    // Comprobar que una credencial con fecha de caducidad anterior se detecta como caducada
    @Test
    void credencialConFechaAnteriorEstaCaducada() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 4, 20));

        boolean resultado = gestorCaducidadCredenciales.estaCaducada(credencial, LocalDate.of(2026, 4, 26));

        assertTrue(resultado);
    }

    // Comprobar que una credencial con fecha de caducidad posterior no se detecta como caducada
    @Test
    void credencialConFechaPosteriorNoEstaCaducada() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 5, 1));

        boolean resultado = gestorCaducidadCredenciales.estaCaducada(credencial, LocalDate.of(2026, 4, 26));

        assertFalse(resultado);
    }

    // Comprobar que una credencial que caduca en la fecha actual se detecta como caducada
    @Test
    void credencialQueCaducaHoyDebeEstaCaducada() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 4, 26));

        boolean resultado = gestorCaducidadCredenciales.estaCaducada(credencial, LocalDate.of(2026, 4, 26));

        assertTrue(resultado);
    }

    // Comprobar que una credencia dentro del umbral de alerta se detecta como 'Próxima a caducar'
    @Test
    void credencialDentroUmbralEstaProximaACaducar() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 4, 30));

        boolean resultado = gestorCaducidadCredenciales.estaProximaCaducar(
                credencial,
                LocalDate.of(2026, 4, 26),
                7
        );

        assertTrue(resultado);
    }

    // Comporbar que una credencial fuera del umbral de alerta no se detecta como 'Próxima a caducar'
    @Test
    void credencialFueraDelUmbralNoEstaProximaACaducar() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 5, 20));

        boolean resultado = gestorCaducidadCredenciales.estaProximaCaducar(credencial, LocalDate.of(2026, 4, 26),7);

        assertFalse(resultado);
    }

    // Comprobar que una credencial caducada no es detectada como 'Próxima a caducar'
    @Test
    void credencialCaducadaNoEstaProximaACaducar() {
        Credencial credencial = new Credencial();
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 4, 20));

        boolean resultado = gestorCaducidadCredenciales.estaProximaCaducar(credencial,LocalDate.of(2026, 4, 26),7);

        assertFalse(resultado);
    }

    // Comprobar que una credencial nula lanza una excepción
    @Test
    void credencialNulaLanzaExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestorCaducidadCredenciales.estaCaducada(null, LocalDate.of(2026, 4, 26))
        );
    }

    // Comprobar que una fecha de referencia para comprobar caducidad lanza una excepción
    @Test
    void fechaReferenciaNulaLanzaExcepcion() {
        Credencial credencial = new Credencial();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestorCaducidadCredenciales.estaCaducada(credencial, null)
        );
    }
}