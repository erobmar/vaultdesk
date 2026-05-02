package com.vaultdesk.negocio;

/**
 * Excepción lanzada cuando falla la verificación de integridad de la bóveda
 *
 */
public class ExcepcionIntegridadBoveda extends Exception {

    public ExcepcionIntegridadBoveda(String mensaje) {
        super(mensaje);
    }

    public ExcepcionIntegridadBoveda(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
