package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para comprobar la gestión de contraseñas
 *
 *
 */

class GestorPasswordsTest {

    private final GestorPasswords gestorPasswords = new GestorPasswords();

    @Test
    void testGeneraPassword() {

        String password = gestorPasswords.generarPassword(10, 2, 2, 2, 2);

        assertNotNull(password);

        assertEquals(10, password.length());
    }

    @Test
    void testCumpleRequisitos() {

        String password = "P@$5w0rD";

        assertTrue(gestorPasswords.cumpleRequisitosPassword(password, 8, 2, 2, 2, 2));

    }

    @Test
    void testNoCumpleRequisitos() {

        String password = "P@$5w0rD";

        assertFalse(gestorPasswords.cumpleRequisitosPassword(password, 10, 2, 2, 2, 2));
    }

    @Test
    void testCumpleRequisitosSegunCredencial() {

        Credencial credencial = new Credencial();
        credencial.setReqLongitud(8);
        credencial.setReqMayusculas(2);
        credencial.setReqMinusculas(2);
        credencial.setReqDigitos(2);
        credencial.setReqEspeciales(2);

        assertTrue(gestorPasswords.cumpleRequisitosPassword("P@$5w0rD", credencial));

    }

}