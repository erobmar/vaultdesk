package com.vaultdesk.controlador;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.ui.AlertaCaducidad;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ControladorAlertas {

    private final ControladorPrincipal controladorPrincipal;

    public ControladorAlertas(ControladorPrincipal controladorPrincipal){
        this.controladorPrincipal = controladorPrincipal;
    }

    public List<AlertaCaducidad> obtenerAlertasCaducidad() throws Exception{

        Connection conexionActual = controladorPrincipal.getConexionActual();
        Boveda bovedaActual = controladorPrincipal.getBovedaActual();

        if(conexionActual == null || conexionActual.isClosed()){
            throw new IllegalStateException("No hay ninguna conexión activa");
        }
        if(bovedaActual == null){
            throw new IllegalStateException("No hay ninguna bóveda activa");
        }

        List<Credencial> listaCredenciales = controladorPrincipal.obtenerCredenciales();
        List<AlertaCaducidad> listaAlertas = new ArrayList<>();



        LocalDate fechaHoy = LocalDate.now();

        int umbralDias = bovedaActual.getUmbralAlerta();

        if(umbralDias <= 0){
            umbralDias = 30;
        }


        for(Credencial credencial : listaCredenciales){



            if(!credencial.isCaduca()){
                continue;
            }

            LocalDate fechaCaducidad = calcularFechaCaducidad(credencial);

            if(fechaCaducidad == null){
                continue;
            }




            AlertaCaducidad alerta = new AlertaCaducidad();
            alerta.setCredencial(credencial);
            alerta.setFechaCaducidad(fechaCaducidad);

            if(fechaCaducidad.isBefore(fechaHoy) || fechaCaducidad.isEqual(fechaHoy)){

                alerta.setEstado("Caducada");
                listaAlertas.add(alerta);

            } else if(!fechaCaducidad.isAfter(fechaHoy.plusDays(umbralDias))){

                alerta.setEstado("Próxima a caducar");
                listaAlertas.add(alerta);

            }


        }

        listaAlertas.sort(Comparator.comparing(AlertaCaducidad::getFechaCaducidad)); // Genius

        return listaAlertas;


    }

    private LocalDate calcularFechaCaducidad(Credencial credencial){

        if(!credencial.isCaduca()){

            return null;
        }
        if(credencial.getFechaCaducidad() != null){

            return credencial.getFechaCaducidad();
        }
        if(credencial.getPeriodoCaducidad() > 0 && credencial.getFechaUltimoUpdate() != null){

            return credencial.getFechaUltimoUpdate().plusDays(credencial.getPeriodoCaducidad());
        }
        return null;
    }
}
