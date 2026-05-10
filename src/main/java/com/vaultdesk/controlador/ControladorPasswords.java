package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorIdiomas;
import com.vaultdesk.negocio.GestorPasswords;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Clase auxiliar encargada de las operaciones sobre contraseñas de credenciales de una bóveda
 * <p>
 * Esta clase recibe del controlador principal las resposabilidades sobre operaciones relativas a generación de
 * contraseñas en modo libre o actualizando una credencial.
 * </p>
 *
 */
public class ControladorPasswords {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorPasswords(ControladorPrincipal controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    /**
     * Genera una contraseña con los requisitos dados
     *
     * @param longitud   longitud mínima de la contraseña
     * @param mayusculas número mínimo de mayúsculas de la contraseña
     * @param minusculas número mínimo de minúsculas de la contraseña
     * @param digitos    número mínimo de dígitos en la contraseña
     * @param especiales número mínimo de caracteres especiales de la contraseña
     * @return contraseña generada
     * @see ControladorPrincipal#generarPassword(int, int, int, int, int)
     * @see GestorPasswords#generarPassword(int, int, int, int, int)
     *
     *
     */
    public String generarPassword(int longitud, int mayusculas, int minusculas, int digitos, int especiales) {

        GestorPasswords gestorPasswords = new GestorPasswords();

        return gestorPasswords.generarPassword(longitud, mayusculas, minusculas, digitos, especiales);


    }

    /**
     * Genera una contraseña para actualizar una credencial
     *
     * @param credencial credencial que se actualiza
     * @throws Exception si encuentra algún problema durante el proceso
     * @see ControladorPrincipal#actualizarPasswordCredencial(Credencial)
     * @see ControladorPrincipal#editarCredencial(int, String, String, String, boolean, String, boolean, String, int, int, int, int, int, int, int)
     * @see GestorPasswords#generarPassword(Credencial)
     *
     *
     */
    public void actualizarPasswordCredencial(Credencial credencial) throws Exception {

        if (credencial == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.sincredencial")); // "Se debe especificar una credencial"
        }

        String nuevaPassword = generarPassword(
                credencial.getReqLongitud(),
                credencial.getReqMayusculas(),
                credencial.getReqMinusculas(),
                credencial.getReqDigitos(),
                credencial.getReqEspeciales()
        );

        controladorPrincipal.editarCredencial(
                credencial.getIdCredencial(),
                credencial.getUrlIdentificador(),
                credencial.getUsername(),
                nuevaPassword,
                credencial.isDestacada(),
                credencial.getAnotaciones(),
                credencial.isCaduca(),
                null,
                credencial.getPeriodoCaducidad(),
                credencial.getReqLongitud(),
                credencial.getReqMayusculas(),
                credencial.getReqMinusculas(),
                credencial.getReqDigitos(),
                credencial.getReqEspeciales(),
                credencial.getCategoria().getIdCategoria()


        );


    }

    /**
     * Muestra un diálogo de confirmación para la actualizaciópn de contraseñas
     *
     * @return true si se confirma la actualización, false en caso contrario
     * @see ControladorPrincipal#confirmarActualizacionPassword()
     *
     *
     */
    public boolean confirmarActualizacionPassword() {

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(GestorIdiomas.getText("alerta.actualizarpassword.title")); // "Actualizar contraseña"
        alerta.setHeaderText(GestorIdiomas.getText("alerta.actualizarpassword.header")); // "Se generará una nueva contraseña para la credencial seleccionada"
        alerta.setContentText(GestorIdiomas.getText("alerta.actualizarpassword.content")); // "La contraseña actual será sustituida ¿Deseas continuar?"

        Optional<ButtonType> eleccion = alerta.showAndWait();

        return eleccion.isPresent() && eleccion.get() == ButtonType.OK;


    }


}
