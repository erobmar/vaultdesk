package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;

import java.security.SecureRandom;

/**
 * Clase encargada de gestionar las principales operaciones sobre contraseñas dentro de una bóveda
 * <p>
 * Esta clase actúa como punto de entrada de la capa de dominio para realizar operaciones de comprobación de cumplimiento
 * de requisitos y generación de cotraseñas
 * </p>
 *
 */
public class GestorPasswords {

    private static final String MAYUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Se excluye la ñ por compatibilida internacional
    private static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITOS = "0123456789";
    private static final String ESPECIALES = "!@#$%^&*()-_=+[]{};:,.?/";
    private static final String TODOS_LOS_CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{};:,.?/";

    private static final int LONGITUD_POR_DEFECTO = 16;
    private static final int MAYUSCULAS_POR_DEFECTO = 1;
    private static final int MINUSCULAS_POR_DEFECTO = 1;
    private static final int DIGITOS_POR_DEFECTO = 1;
    private static final int ESPECIALES_POR_DEFECTO = 1;

    private final SecureRandom random = new SecureRandom();

    /**
     * Comprueba si una contraseña cumple los requisitos definidos por una credencial
     *
     * @param password   contraseña que se quiere comprobar
     * @param credencial credencial de la que se tomarán los requisitos para la comprobación
     * @return true si cumple los requisitos, false en caso contrario
     * @see GestorPasswords#cumpleRequisitosPassword(String, int, int, int, int, int)
     *
     */
    public boolean cumpleRequisitosPassword(String password, Credencial credencial) {

        if (password == null) {
            return false;
        }
        if (credencial == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.credencialnula")); // "La credencial no puede ser nula"
        }

        return cumpleRequisitosPassword(
                password,
                credencial.getReqLongitud(),
                credencial.getReqMayusculas(),
                credencial.getReqMinusculas(),
                credencial.getReqDigitos(),
                credencial.getReqEspeciales()
        );

    }

    /**
     * Comprueba si una contraseña cumple unos requisitos específicos
     *
     * @param password      contraseña que se quiere comprobar
     * @param reqLongitud   número mínimo de caracteres de la contraseña
     * @param reqMayusculas número mínimo de mayúsculas de la contraseña
     * @param reqMinusculas núnmero mínimo de minúsculas de la contraseña
     * @param reqDigitos    número mínimo de dígitos de la contraseña
     * @param reqEspeciales número mínimo de caracteres especiales de la contraseña
     * @return true si la contraseña cumple con los requisitos, false en caso contrario
     * @see GestorPasswords#cumpleRequisitosPassword(String, Credencial)
     *
     */
    public boolean cumpleRequisitosPassword(
            String password,
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales
    ) {

        if (password == null) {
            return false;
        }
        if (password.length() < Math.max(reqLongitud, 0)) {
            return false;
        }


        // Rutina para clasificar los caracteres en función de su tipo - Susceptible de mover a un método
        int mayusculas = 0;
        int minusculas = 0;
        int digitos = 0;
        int especiales = 0;

        for (int i = 0; i < password.length(); i++) {

            char caracter = password.charAt(i);

            if (Character.isUpperCase(caracter)) {
                mayusculas++;
            } else if (Character.isLowerCase(caracter)) {
                minusculas++;
            } else if (Character.isDigit(caracter)) {
                digitos++;
            } else {
                especiales++;
            }

        }

        if (mayusculas < Math.max(reqMayusculas, 0)) {
            return false;
        }

        if (minusculas < Math.max(reqMinusculas, 0)) {
            return false;
        }

        if (digitos < Math.max(reqDigitos, 0)) {
            return false;
        }

        return especiales >= Math.max(reqEspeciales, 0);


    }

    /**
     * Genera una contraseña conforme a los requisitos de una credencial concreta
     *
     * @param credencial credencial de la que se toman los requisitos para la generación
     * @return contraseña generada
     * @see GestorPasswords#generarPassword(int, int, int, int, int)
     *
     */
    public String generarPassword(Credencial credencial) {

        if (credencial == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.credencialnula")); // "La credencial no puede ser nula"
        }

        int longitud = normalizarLongitud(credencial.getReqLongitud());

        int mayusculas = normalizarRequisito(credencial.getReqMayusculas(), MAYUSCULAS_POR_DEFECTO);
        int minusculas = normalizarRequisito(credencial.getReqMinusculas(), MINUSCULAS_POR_DEFECTO);
        int digitos = normalizarRequisito(credencial.getReqDigitos(), DIGITOS_POR_DEFECTO);
        int especiales = normalizarRequisito(credencial.getReqEspeciales(), ESPECIALES_POR_DEFECTO);

        return generarPassword(longitud, mayusculas, minusculas, digitos, especiales);
    }

    /**
     * Genera una contraseña conforme a unos requisitos específicos
     *
     * @param reqLongitud   número mínimo de caracteres de la contraseña
     * @param reqMayusculas número mínimo de mayúsculas de la contraseña
     * @param reqMinusculas número mínimo de minúsculas de la cotraseña
     * @param reqDigitos    número mínimo de dígitos de la contraseña
     * @param reqEspeciales númeor mínimo de caracteres especiales de la contraseña
     * @return contraseña generada
     * @see GestorPasswords#generarPassword(Credencial)
     *
     */
    public String generarPassword(
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales
    ) {

        int longitud = normalizarLongitud(reqLongitud);

        int mayusculas = normalizarRequisito(reqMayusculas, MAYUSCULAS_POR_DEFECTO);
        int minusculas = normalizarRequisito(reqMinusculas, MINUSCULAS_POR_DEFECTO);
        int digitos = normalizarRequisito(reqDigitos, DIGITOS_POR_DEFECTO);
        int especiales = normalizarRequisito(reqEspeciales, ESPECIALES_POR_DEFECTO);

        int sumaRequisitos = mayusculas + minusculas + digitos + especiales;

        if (sumaRequisitos > longitud) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.requisitos")); // "La suma de requisitos supera la longitud de contraseña establecida"
        }

        char[] password = new char[longitud];

        int indice = 0;

        for (int i = 0; i < mayusculas; i++) {
            password[indice++] = generarCaracterAleatorio(MAYUSCULAS);
        }
        for (int i = 0; i < minusculas; i++) {
            password[indice++] = generarCaracterAleatorio(MINUSCULAS);
        }
        for (int i = 0; i < digitos; i++) {
            password[indice++] = generarCaracterAleatorio(DIGITOS);
        }
        for (int i = 0; i < especiales; i++) {
            password[indice++] = generarCaracterAleatorio(ESPECIALES);
        }

        // Se generan caracteres aleatorios hasta completar la longitud del password
        while (indice < longitud) {
            password[indice++] = generarCaracterAleatorio(TODOS_LOS_CARACTERES);
        }

        // Se devuelve el array después de aleatorizar sus letras

        aleatorizarArray(password);

        return new String(password);

    }

    /**
     * Métodos que normalizan los campos que no se hayan establecido al valor por defecto del sistema
     *
     *
     */

    private int normalizarLongitud(int longitud) {
        if (longitud <= 0) {
            return LONGITUD_POR_DEFECTO;
        }
        return longitud;
    }

    private int normalizarRequisito(int valor, int valorPorDefecto) {
        if (valor <= 0) {
            return valorPorDefecto;
        }

        return valor;

    }

    /**
     * Métodos que generan caracteres para la generación de contraseñas:
     * <p>
     * - Toma una posición aleatoria del conjunto de caracteres pasado como parámetro
     * - Aleatoriza el array de carateres final antes de devolver la contraseña ya generada
     *
     */
    private char generarCaracterAleatorio(String conjunto) {

        int posicion = random.nextInt(conjunto.length());
        return conjunto.charAt(posicion);

    }


    private void aleatorizarArray(char[] password) {

        for (int i = password.length - 1; i > 0; i--) {

            int j = random.nextInt(i + 1);

            char temporal = password[i];
            password[i] = password[j];
            password[j] = temporal;
        }

    }

}
