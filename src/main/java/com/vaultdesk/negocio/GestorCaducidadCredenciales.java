package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;

import java.time.LocalDate;

public class GestorCaducidadCredenciales {

    private final GestorCredenciales gestorCredenciales;

    public GestorCaducidadCredenciales(GestorCredenciales gestorCredenciales){
        this.gestorCredenciales = gestorCredenciales;
    }

    public boolean estaCaducada(Credencial credencial, LocalDate fechaReferencia){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(fechaReferencia == null){

            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }

        // Si la credencial NO caduca por definición
        if(!credencial.isCaduca()){
            return false;
        }

        LocalDate fechaCaducidad = calcularFechaCaducidadReal(credencial);

        if(fechaCaducidad == null){
            return false;
        }

        return !fechaCaducidad.isAfter(fechaReferencia);

    }

    public boolean estaProximaCaducar (Credencial credencial, LocalDate fechaReferencia, int umbral){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(fechaReferencia == null){
            throw new IllegalArgumentException("La fecha de referencia no puede ser nula");
        }

        if(umbral < 0){
            throw new IllegalArgumentException("El umbral de días no puede ser negativo");
        }

        if(!credencial.isCaduca()){
            return false;
        }

        LocalDate fechaCaducidad = calcularFechaCaducidadReal(credencial);


        if(fechaCaducidad == null){
            return false;
        }

        if(estaCaducada(credencial, fechaReferencia)){
            return false;
        }

        return !fechaCaducidad.isAfter(fechaReferencia.plusDays(umbral));

    }

    private LocalDate calcularFechaCaducidadReal(Credencial credencial){

        if(credencial == null){
            throw new IllegalArgumentException("La credencial no puede ser nula");
        }

        if(!credencial.isCaduca()){
            return null;
        }

        if(credencial.getFechaCaducidad() != null){
            return credencial.getFechaCaducidad();
        }

        // Si tiene un periodo de caducidad establecido
        if(credencial.getPeriodoCaducidad() > 0 && credencial.getFechaUltimoUpdate() != null){

            long dias = credencial.getPeriodoCaducidad() / 86400L; // Número de segundos en un día

            return credencial.getFechaUltimoUpdate().plusDays(dias);

        }

        return null;


    }


}
