package com.vaultdesk.dominio;

/**
 * Representa el idioma de la aplicación
 * */

public class Idioma {

    private int idIdioma;
    private String nombre;

    // Constructor vacío
    public Idioma(){
    }

    // Constructor estándar
    public Idioma(int idIdioma, String nombre){
        this.idIdioma = idIdioma;
        this.nombre = nombre;
    }

    public void setIdIdioma(int idIdioma){
        this.idIdioma = idIdioma;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public int getIdIdioma(){
        return this.idIdioma;
    }

    public String getNombre(){
        return this.nombre;
    }

    @Override
    public String toString(){
        return this.nombre;
    }

}
