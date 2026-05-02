package com.vaultdesk.dominio;

import java.time.LocalDate;

/**
 * Representa cada una de las credenciales de un usuario
 *
 */

public class Credencial {

    private int idCredencial;
    private String urlIdentificador;
    private String username;
    private String password;
    private boolean destacada;
    private String anotaciones;
    private boolean caduca;
    private LocalDate fechaCaducidad;
    private int periodoCaducidad; // Expresado en segundos
    private LocalDate fechaUltimoUpdate;
    private int reqLongitud;
    private int reqMayusculas;
    private int reqMinusculas;
    private int reqDigitos;
    private int reqEspeciales;

    private Categoria categoria;

    // Constructor vacío
    public Credencial() {
    }

    // Constructor estándar
    public Credencial(
            int idCredencial,
            String urlIdentificador,
            String username,
            String password,
            boolean destacada,
            String anotaciones,
            boolean caduca,
            LocalDate fechaCaducidad,
            int periodoCaducidad,
            LocalDate fechaUltimoUpdate,
            int reqLongitud,
            int reqMayusculas,
            int reqMinusculas,
            int reqDigitos,
            int reqEspeciales,
            Categoria categoria
    ) {
        this.idCredencial = idCredencial;
        this.urlIdentificador = urlIdentificador;
        this.username = username;
        this.password = password;
        this.destacada = destacada;
        this.anotaciones = anotaciones;
        this.caduca = caduca;
        this.fechaCaducidad = fechaCaducidad;
        this.periodoCaducidad = periodoCaducidad;
        this.fechaUltimoUpdate = fechaUltimoUpdate;
        this.reqLongitud = reqLongitud;
        this.reqMayusculas = reqMayusculas;
        this.reqMinusculas = reqMinusculas;
        this.reqDigitos = reqDigitos;
        this.reqEspeciales = reqEspeciales;
        this.categoria = categoria;
    }

    public void setIdCredencial(int idCredencial) {
        this.idCredencial = idCredencial;
    }

    public void setUrlIdentificador(String urlIdentificador) {
        this.urlIdentificador = urlIdentificador;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDestacada(boolean destacada) {
        this.destacada = destacada;
    }

    public void setAnotaciones(String anotaciones) {
        this.anotaciones = anotaciones;
    }

    public void setCaduca(boolean caduca) {
        this.caduca = caduca;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public void setPeriodoCaducidad(int periodoCaducidad) {
        this.periodoCaducidad = periodoCaducidad;
    }

    public void setFechaUltimoUpdate(LocalDate fechaUltimoUpdate) {
        this.fechaUltimoUpdate = fechaUltimoUpdate;
    }

    public void setReqLongitud(int reqLongitud) {
        this.reqLongitud = reqLongitud;
    }

    public void setReqMayusculas(int reqMayusculas) {
        this.reqMayusculas = reqMayusculas;
    }

    public void setReqMinusculas(int reqMinusculas) {
        this.reqMinusculas = reqMinusculas;
    }

    public void setReqDigitos(int reqDigitos) {
        this.reqDigitos = reqDigitos;
    }

    public void setReqEspeciales(int reqEspeciales) {
        this.reqEspeciales = reqEspeciales;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getIdCredencial() {
        return this.idCredencial;
    }

    public String getUrlIdentificador() {
        return this.urlIdentificador;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isDestacada() {
        return this.destacada;
    }

    public LocalDate getFechaCaducidad() {
        return this.fechaCaducidad;
    }

    public LocalDate getFechaUltimoUpdate() {
        return this.fechaUltimoUpdate;
    }

    public int getPeriodoCaducidad() {
        return this.periodoCaducidad;
    }

    public int getReqLongitud() {
        return this.reqLongitud;
    }

    public int getReqMayusculas() {
        return this.reqMayusculas;
    }

    public int getReqMinusculas() {
        return this.reqMinusculas;
    }

    public int getReqDigitos() {
        return this.reqDigitos;
    }

    public int getReqEspeciales() {
        return this.reqEspeciales;
    }

    public String getAnotaciones() {
        return this.anotaciones;
    }

    public boolean isCaduca() {
        return this.caduca;
    }

    public Categoria getCategoria() {
        return this.categoria;
    }


    @Override
    public String toString() {
        return this.username;
    }


}
