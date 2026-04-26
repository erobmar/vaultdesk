package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorPasswords;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ControladorPasswords {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorPasswords(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public String generarPassword(int longitud, int mayusculas, int minusculas, int digitos, int especiales){

        GestorPasswords gestorPasswords = new GestorPasswords();

        return gestorPasswords.generarPassword(longitud, mayusculas, minusculas, digitos, especiales);


    }

    public void actualizarPasswordCredencial(Credencial credencial) throws  Exception{

        if(credencial == null){
            throw new IllegalArgumentException("Se debe especificar una credencial");
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

    public boolean confirmarActualizacionPassword(){

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Actualizar contraseña");
        alerta.setHeaderText("Se generará una nueva contraseña para la credencial seleccionada");
        alerta.setContentText("La contraseña actual será sustituida ¿Deseas continuar?");

        Optional<ButtonType> eleccion = alerta.showAndWait();

        return eleccion.isPresent() && eleccion.get() == ButtonType.OK;


    }


}
