package com.vaultdesk.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una bóveda de credenciales gestionada por la aplicación
 *
 */

public class Boveda {

    private int idBoveda;
    private String nombre;
    private int umbralAlerta; // Expresado en segundos
    private boolean accesibilidad;
    private Idioma idioma;
    private TemaVisual temaVisual;
    private List<Credencial> credenciales;

    private boolean modificadaSinGuardar; // Flag para saber si hay cambios sin guardar

    // Constructor vacío
    public Boveda() {
    }

    // Constructor estándar
    public Boveda(
            int idBoveda,
            String nombre,
            int umbralAlerta,
            boolean accesibilidad,
            Idioma idioma,
            TemaVisual temaVisual,
            List<Credencial> credenciales
    ) {

        this.idBoveda = idBoveda;
        this.nombre = nombre;
        this.umbralAlerta = umbralAlerta;
        this.accesibilidad = accesibilidad;
        this.idioma = idioma;
        this.temaVisual = temaVisual;
        if (credenciales != null) {
            this.credenciales = credenciales;
        } else {
            this.credenciales = new ArrayList<>();
        }

        this.modificadaSinGuardar = false; // Se inicializa a false, al crear la bóveda NO hay cambios sin guardar
    }

    public void setIdBoveda(int idBoveda) {
        this.idBoveda = idBoveda;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setUmbralAlerta(int umbralAlerta) {
        this.umbralAlerta = umbralAlerta;
    }

    public void setAccesibilidad(boolean accesibilidad) {
        this.accesibilidad = accesibilidad;
    }

    public void setIdioma(Idioma idioma) {
        this.idioma = idioma;
    }

    public void setTemaVisual(TemaVisual temaVisual) {
        this.temaVisual = temaVisual;
    }

    public void setCredenciales(List<Credencial> credenciales) {
        this.credenciales = credenciales;
    }

    public int getIdBoveda() {
        return this.idBoveda;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getUmbralAlerta() {
        return this.umbralAlerta;
    }

    public boolean isAccesibilidad() {
        return this.accesibilidad;
    }

    public Idioma getIdioma() {
        return this.idioma;
    }

    public TemaVisual getTemaVisual() {
        return this.temaVisual;
    }

    public List<Credencial> getCredenciales() {
        return this.credenciales;
    }

    public boolean isModificadaSinGuardar() {
        return modificadaSinGuardar;
    }

    public void setModificadaSinGuardar(boolean modificadaSinGuardar) {
        this.modificadaSinGuardar = modificadaSinGuardar;
    }


    @Override
    public String toString() {
        return this.nombre;
    }


}
