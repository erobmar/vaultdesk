package com.vaultdesk.negocio;


import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase encargada de gestionar la internacionalización de la aplicación
 * <p>
 * Esta clase recibe la responsabilidad de seleccionar el texto a mostrar en el idioma adecuado mediante el estándar
 * i18n.
 * </p>
 *
 */

public class GestorIdiomas {

    // Carga el idioma español por defecto

    private static ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", new Locale("es"));

    /**
     * Cambia el idioma de la bóveda
     * @param codigoIdioma - idioma al que cambiar
     *
     * */
    public static void cambiarIdioma(String codigoIdioma){
        bundle = ResourceBundle.getBundle("i18n.messages", new Locale(codigoIdioma));
    }

    /**
     * Devuelve la cadena de texto del archivo .properties del idioma seleccionado que corresponda con la clave pasara
     * como parámetro
     *
     * @param clave - clave de la cadena de texto
     * @return cadena de texto en el idioma seleccionado
     *
     * */
    public static String getText(String clave){

        return bundle.getString(clave);
    }



}
