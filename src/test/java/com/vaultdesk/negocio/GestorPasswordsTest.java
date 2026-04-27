package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
* Clase de test para comprobar la gestión de contraseñas
* */

class GestorPasswordsTest {

    private final GestorPasswords gestorPasswords = new GestorPasswords();

    // Comprobar si se genera un password correcto con unos requisitos dados
    @Test
    void generarPasswordCumpleRequisitosIndicados() {
        String password = gestorPasswords.generarPassword(20, 2, 2, 2, 2);

        assertEquals(20, password.length());
        assertTrue(gestorPasswords.cumpleRequisitosPassword(password, 20, 2, 2, 2, 2));
    }

    /* Comprobar si se genera un password correcto con los requisitos por defecto:
    *   - 16 caracteres
    *   - 1 letra mayúscula
    *   - 1 letra minúscula
    *   - 1 número
    *   - 1 caracter especial
    * */
    @Test
    void generarPasswordLibreCumpleRequisitosPorDefecto() {
        String password = gestorPasswords.generarPasswordLibre();

        assertEquals(16, password.length());
        assertTrue(gestorPasswords.cumpleRequisitosPassword(password, 16, 1, 1, 1, 1));
    }

    // Comprobar si, al generar un password para una credencial, este cumple con los requisitos de la misma
    @Test
    void generarPasswordCredencialCumpleRequisitosDeLaCredencial() {
        Credencial credencial = new Credencial();
        credencial.setReqLongitud(18);
        credencial.setReqMayusculas(2);
        credencial.setReqMinusculas(2);
        credencial.setReqDigitos(2);
        credencial.setReqEspeciales(2);

        String password = gestorPasswords.generarPassword(credencial);

        assertTrue(gestorPasswords.cumpleRequisitosPassword(password, credencial));
    }

    // Comprobar si el metodo cumpleRequisitosPassword() devuelve false si el password es null
    @Test
    void cumpleRequisitosDebeDevolverFalseSiPasswordEsNull() {
        assertFalse(gestorPasswords.cumpleRequisitosPassword(null, 12, 1, 1, 1, 1));
    }

    // Comprobar si el metodo cumpleRequisitosPassword() dtecta passwords demasiado cortos
    @Test
    void cumpleRequisitosDetectaPasswordCorto() {
        assertFalse(gestorPasswords.cumpleRequisitosPassword("@bC1", 10, 1, 1, 1, 1));
    }

    // Comprobar si generarPassword() lanza una excepción si la suma de requisitos supera la longitud mínima de password
    @Test
    void generarPasswordLanzaExcepcionSiSumaDeRequisitosSuperanLongitud() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestorPasswords.generarPassword(4, 2, 2, 2, 2)
        );
    }

    // Comprobar si generarPassword() desde una credencial lanza una excepción si recibe una credencial null
    @Test
    void generarPasswordConCredencialNulaLanzaExcepcion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> gestorPasswords.generarPassword(null)
        );
    }
}