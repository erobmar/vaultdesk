package com.vaultdesk.negocio;

public class ExcepcionIntegridadBoveda extends Exception {

    public ExcepcionIntegridadBoveda(String mensaje){
        super(mensaje);
    }

    public ExcepcionIntegridadBoveda(String mensaje, Throwable causa){
        super(mensaje, causa);
    }

}
