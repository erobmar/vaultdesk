package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Credencial;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para comprobar la exportación de credenciales
 * */
class GestorExportacionCredencialesTest {

    private final GestorCredenciales gestorCredenciales = new GestorCredenciales();

    @TempDir
    Path rutaTemporal;

    // Comprobar si se crea el archivo CSV correctamente
    @Test
    void creaArchivoCSV() throws Exception {

        Credencial credencial1 = new Credencial();
        credencial1.setUrlIdentificador("google.com");
        credencial1.setUsername("usuario1");
        credencial1.setPassword("pass1");
        credencial1.setAnotaciones("nota1");

        Credencial credencial2 = new Credencial();
        credencial2.setUrlIdentificador("github.com");
        credencial2.setUsername("usuario2");
        credencial2.setPassword("pass2");
        credencial2.setAnotaciones("nota2");

        List<Credencial> listaCredenciales = List.of(credencial1, credencial2);

        Path archivoSalida = rutaTemporal.resolve("export.csv");

        gestorCredenciales.exportarCredencialesCSV(listaCredenciales, archivoSalida);

        assertTrue(Files.exists(archivoSalida));
        assertTrue(Files.size(archivoSalida) > 0);
    }

    // Comprobar que las comas de las cadenas de texto se escapan correctamente (evita crear campos no deseados en CSV)
    @Test
    void escapaComas() throws Exception {

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador("google.com");
        credencial.setUsername("usuario,con,comas");
        credencial.setPassword("password");
        credencial.setAnotaciones("Anotaciones");

        Path archivo = rutaTemporal.resolve("prueba.csv");

        gestorCredenciales.exportarCredencialesCSV(List.of(credencial), archivo);

        String contenido = Files.readString(archivo);

        assertTrue(contenido.contains("\"usuario,con,comas\""));
    }

    // Comprobar que se escapan las comillas correctamente (evita que el CSV se rompa cuando hay comillas en el texto)
    @Test
    void escapaComillas() throws Exception {

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador("google.com");
        credencial.setUsername("usuario\"test");
        credencial.setPassword("password");
        credencial.setAnotaciones("Anotaciones");

        Path archivo = rutaTemporal.resolve("prueba.csv");

        gestorCredenciales.exportarCredencialesCSV(List.of(credencial), archivo);

        String contenido = Files.readString(archivo);

        assertTrue(contenido.contains("\"usuario\"\"test\""));
    }

    // Comprobar que se gestionan correctamente los campos nulos al exportar a CSV
    @Test
    void soportaCamposNulos() throws Exception {

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador(null);
        credencial.setUsername(null);
        credencial.setPassword(null);
        credencial.setAnotaciones(null);

        Path archivo = rutaTemporal.resolve("prueba.csv");

        assertDoesNotThrow(() ->
                gestorCredenciales.exportarCredencialesCSV(List.of(credencial), archivo)
        );
    }

    // Comprobar que una lista vacía genera un archivo vacío o válido
    @Test
    void listaVaciaCreaArchivoVacioOValido() throws Exception {

        Path archivo = rutaTemporal.resolve("prueba.csv");

        gestorCredenciales.exportarCredencialesCSV(List.of(), archivo);

        assertTrue(Files.exists(archivo));
    }
}