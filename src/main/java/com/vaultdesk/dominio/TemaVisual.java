package com.vaultdesk.dominio;

/**
 * Representa el tema visual que presenta la aplicación
 *
 */

public class TemaVisual {

    private int idTemaVisual;
    private String nombre;

    // Constructor vacío
    public TemaVisual() {
    }

    // Constructor estándar
    public TemaVisual(int idTemaVisual, String nombre) {

        this.idTemaVisual = idTemaVisual;
        this.nombre = nombre;
    }

    public void setIdTemaVisual(int idTemaVisual) {
        this.idTemaVisual = idTemaVisual;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdTemaVisual() {
        return this.idTemaVisual;
    }

    public String getNombre() {
        return this.nombre;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
