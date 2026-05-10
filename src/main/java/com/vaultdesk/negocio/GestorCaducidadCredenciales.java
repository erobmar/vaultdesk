package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.ui.AlertaCaducidad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Clase encargada de gestionar las operaciones sobre la caducidad de credenciales dentro de una bóveda
 * <p>
 * Esta clase auxilixar actúa como apoyo para la clase GestorCredenciales, tomando algunas de sus responsabilidades,
 * como la obtención del listado de credenciales caducadas o próximas a caducar y el cálculo de la fecha de caducidad
 * efectiva de las credenciales
 * </p>
 *
 */
public class GestorCaducidadCredenciales {

    private final GestorCredenciales gestorCredenciales;

    public GestorCaducidadCredenciales(GestorCredenciales gestorCredenciales) {
        this.gestorCredenciales = gestorCredenciales;
    }


    /**
     * Busca en la bóveda las credenciales que estén caducadas o pŕoximas a caducar (entendiendo por 'Próxima a caducar'
     * toda credencial que caduque entre la fecha actual y esa fecha más el número de días que establezca el umbral de
     * alerta.
     *
     * @param listaCredenciales listado de todas las credenciales de la bóveda
     * @param bovedaActual      bóveda sobre la que se está trabajando
     * @return Lista de credenciales caducadas o próximas a caducar
     * @see GestorCredenciales#obtenerAlertasCaducidad(List, Boveda)
     *
     */
    public List<AlertaCaducidad> obtenerAlertasCaducidad(List<Credencial> listaCredenciales, Boveda bovedaActual) {

        List<AlertaCaducidad> listaAlertas = new ArrayList<>();
        LocalDate fechaHoy = LocalDate.now();

        int umbralDias = bovedaActual.getUmbralAlerta();

        if (umbralDias <= 0) {
            umbralDias = 30;
        }


        for (Credencial credencial : listaCredenciales) {


            if (!credencial.isCaduca()) {
                continue;
            }

            LocalDate fechaCaducidad = calcularFechaCaducidadReal(credencial);

            if (fechaCaducidad == null) {
                continue;
            }


            AlertaCaducidad alerta = new AlertaCaducidad();
            alerta.setCredencial(credencial);
            alerta.setFechaCaducidad(fechaCaducidad);

            if (fechaCaducidad.isBefore(fechaHoy) || fechaCaducidad.isEqual(fechaHoy)) {

                alerta.setEstado(GestorIdiomas.getText("alerta.caducidad.caducada")); // "Caducada"
                listaAlertas.add(alerta);

            } else if (!fechaCaducidad.isAfter(fechaHoy.plusDays(umbralDias))) {

                alerta.setEstado(GestorIdiomas.getText("alerta.caducidad.proxima")); // "Próxima a caducar"
                listaAlertas.add(alerta);

            }


        }

        listaAlertas.sort(Comparator.comparing(AlertaCaducidad::getFechaCaducidad)); // Genius

        return listaAlertas;

    }


    /**
     * Calcula la fecha de caducidad efectiva de una credencial
     */
    private LocalDate calcularFechaCaducidadReal(Credencial credencial) {

        if (credencial == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.credencialnula")); // "La credencial no puede ser nula"
        }

        if (!credencial.isCaduca()) {
            return null;
        }

        if (credencial.getFechaCaducidad() != null) {
            return credencial.getFechaCaducidad();
        }

        // Si tiene un periodo de caducidad establecido
        if (credencial.getPeriodoCaducidad() > 0 && credencial.getFechaUltimoUpdate() != null) {

            long dias = credencial.getPeriodoCaducidad() / 86400L; // Número de segundos en un día

            return credencial.getFechaUltimoUpdate().plusDays(dias);

        }

        return null;


    }


}
