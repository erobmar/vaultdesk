package com.vaultdesk.dominio;

/**
 * Representa la categoría a la que pertenece una credencial
 *
 */

public class Categoria {
    private int idCategoria;
    private String nombre;
    private String descripcion;
    private boolean esDelSistema;

    // Constructor vacío
    public Categoria() {
    }

    // Constructor estándar
    public Categoria(int idCategoria, String nombre, String descripcion, boolean esDelSistema) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esDelSistema = esDelSistema;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEsDelSistema(boolean esDelSistema) {
        this.esDelSistema = esDelSistema;
    }

    public int getIdCategoria() {
        return this.idCategoria;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public boolean isDelSistema() {
        return this.esDelSistema;
    }


    @Override
    public String toString() {
        return nombre;
    }
}
