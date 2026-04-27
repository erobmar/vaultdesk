package com.vaultdesk.integracion;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.negocio.GestorCategorias;
import com.vaultdesk.negocio.GestorCredenciales;
import com.vaultdesk.persistencia.GestorBaseDatos;
import com.vaultdesk.persistencia.GestorPersistencia;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para prueba de integración
 * */
class VaultDeskIntegracionTest {

    @TempDir
    Path rutaTemporal;

    // Comprobar el flujo completo: Crear - Guardar - Abrir y Consultar una credencial
    @Test
    void flujoCompletoCrearGuardarAbrirYConsultarCredencial() throws Exception {

        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Path archivoBoveda = rutaTemporal.resolve("prueba.vlt");
        char[] passwordMaestra = "P@55w0rD".toCharArray();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            insertarBovedaPrueba(conexion);

            Credencial credencial = crearCredencialPrueba();

            int idGenerado = gestorCredenciales.crearCredencial(credencial,1, conexion);

            assertEquals(1, idGenerado);

            gestorPersistencia.guardarBovedaDesdeMemoria(archivoBoveda, passwordMaestra, conexion);
        }

        try (Connection conexionReabierta = gestorPersistencia.abrirBovedaEnMemoria(archivoBoveda, passwordMaestra)) {

            List<Credencial> listaCredenciales = gestorCredenciales.buscarCredenciales(conexionReabierta, 1, "github");

            assertEquals(1, listaCredenciales.size());
            assertEquals("github.com", listaCredenciales.get(0).getUrlIdentificador());
            assertEquals("eduardo", listaCredenciales.get(0).getUsername());
            assertEquals("password123", listaCredenciales.get(0).getPassword());
        }
    }

    // Comprobar flujo completo de integración entre categoríoas y credenciales, abrir bóveda con contraseña maestra, guardar y reabrir
    @Test
    void flujoCompletoCategoriaCredencialGuardarYAbrir() throws Exception {

        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCategorias gestorCategorias = new GestorCategorias();
        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Path archivoBoveda = rutaTemporal.resolve("prueba.vlt");
        char[] passwordMaestra = "P@55w0rD".toCharArray();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            insertarBovedaPrueba(conexion);

            Categoria categoria = new Categoria();
            categoria.setNombre("Pruebas");
            categoria.setDescripcion("Credenciales de prueba");

            gestorCategorias.crearCategoria(conexion, categoria);

            Categoria categoriaCreada = gestorCategorias.obtenerCategorias(conexion).get(1);

            Credencial credencial = crearCredencialPrueba();
            credencial.setCategoria(categoriaCreada);

            gestorCredenciales.crearCredencial(credencial, 1, conexion);

            gestorPersistencia.guardarBovedaDesdeMemoria(archivoBoveda, passwordMaestra, conexion);
        }

        try (Connection conexionReabierta = gestorPersistencia.abrirBovedaEnMemoria(archivoBoveda, passwordMaestra)) {

            List<Credencial> credencialesPrueba = gestorCredenciales.obtenerCredencialesPorCategoria(conexionReabierta, 2);

            assertEquals(1, credencialesPrueba.size());
            assertEquals("Pruebas", credencialesPrueba.get(0).getCategoria().getNombre());
            assertEquals("Credenciales de prueba", credencialesPrueba.get(0).getCategoria().getDescripcion());
        }
    }

    // Comprobar flujo completo de edición y presistencia de cambios en credenciales
    @Test
    void flujoCompletoEditarGuardarYAbrirMantieneCambios() throws Exception {

        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCredenciales gestorCredenciales = new GestorCredenciales();
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Path archivoBoveda = rutaTemporal.resolve("prueba.vlt");
        char[] passwordMaestra = "P@55w0rD".toCharArray();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {

            insertarBovedaPrueba(conexion);

            Credencial credencial = crearCredencialPrueba();
            gestorCredenciales.crearCredencial(credencial, 1, conexion);

            credencial.setUsername("usuario_editado");
            credencial.setPassword("password_editado");
            credencial.setUrlIdentificador("gitlab.com");

            gestorCredenciales.editarCredencial(conexion, 1, credencial);

            gestorPersistencia.guardarBovedaDesdeMemoria(archivoBoveda, passwordMaestra, conexion);
        }

        try (Connection conexionReabierta = gestorPersistencia.abrirBovedaEnMemoria(archivoBoveda, passwordMaestra)) {

            List<Credencial> credenciales = gestorCredenciales.buscarCredenciales(conexionReabierta, 1, "gitlab");

            assertEquals(1, credenciales.size());
            assertEquals("usuario_editado", credenciales.get(0).getUsername());
            assertEquals("password_editado", credenciales.get(0).getPassword());
        }
    }

    // Comprobar que al abrir una bóveda con una password incorrecta se lanza una excepción
    @Test
    void abrirBovedaConPasswordIncorrectaFalla() throws Exception {
        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Path archivoBoveda = rutaTemporal.resolve("prueba.vlt");

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {
            insertarBovedaPrueba(conexion);

            gestorPersistencia.guardarBovedaDesdeMemoria(archivoBoveda, "password-correcta".toCharArray(), conexion);
        }

        assertThrows(
                Exception.class,
                () -> gestorPersistencia.abrirBovedaEnMemoria(archivoBoveda, "password-incorrecta".toCharArray())
        );
    }

    // Comprobar si al abrir una bóveda corrupa se lanza una excepción
    @Test
    void abrirArchivoCorruptoFalla() throws Exception {
        GestorPersistencia gestorPersistencia = new GestorPersistencia();

        Path archivoCorrupto = rutaTemporal.resolve("prueba.vlt");

        Files.write(archivoCorrupto, new byte[]{1, 2, 3, 4, 5});

        assertThrows(
                Exception.class,
                () -> gestorPersistencia.abrirBovedaEnMemoria(
                        archivoCorrupto,
                        "password".toCharArray()
                )
        );
    }

    // Metodo auxiliar para insertar la bóveda de prueba
    private void insertarBovedaPrueba(Connection conexion) throws Exception {
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("""
                INSERT INTO boveda (
                    id_boveda,
                    nombre,
                    umbral_alerta,
                    accesibilidad,
                    id_idioma,
                    id_tema_visual
                )
                VALUES (1, 'Bóveda de prueba', 7, 0, 1, 1)
            """);
        }
    }

    // Metodo auxiliar para crear la credencial de prueba
    private Credencial crearCredencialPrueba() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Otros");
        categoria.setDescripcion("Categoría por defecto del sistema");
        categoria.setEsDelSistema(true);

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador("github.com");
        credencial.setUsername("eduardo");
        credencial.setPassword("password123");
        credencial.setDestacada(false);
        credencial.setAnotaciones("Cuenta de prueba");
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 12, 31));
        credencial.setPeriodoCaducidad(90);
        credencial.setFechaUltimoUpdate(LocalDate.of(2026, 4, 27));
        credencial.setReqLongitud(12);
        credencial.setReqMayusculas(1);
        credencial.setReqMinusculas(1);
        credencial.setReqDigitos(1);
        credencial.setReqEspeciales(1);
        credencial.setCategoria(categoria);

        return credencial;
    }
}