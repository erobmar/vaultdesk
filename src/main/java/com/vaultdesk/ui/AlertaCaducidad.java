package com.vaultdesk.ui;

import com.vaultdesk.dominio.Credencial;

import java.time.LocalDate;

public class AlertaCaducidad {

    private Credencial credencial;
    private LocalDate fechaCaducidad;
    private String estado;

    public void setCredencial(Credencial credencial) {
        this.credencial = credencial;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Credencial getCredencial(){
        return this.credencial;
    }

    public LocalDate getFechaCaducidad() {
        return this.fechaCaducidad;
    }

    public String getEstado(){
        return this.estado;
    }
}
