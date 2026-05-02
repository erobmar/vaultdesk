package com.vaultdesk.negocio;

import com.vaultdesk.controlador.ControladorPrincipal;
import com.vaultdesk.dominio.Boveda;
import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.persistencia.GestorBaseDatos;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para comprobar la gestión de credenciales
 *
 *
 */
class GestorCredencialesTest {

    private Connection conexion;
    private GestorCredenciales gestorCredenciales;
    private GestorBaseDatos gestorBaseDatos;
    private GestorBovedas gestorBovedas;

    @BeforeEach
    void inicializacion() throws Exception {

        gestorBaseDatos = new GestorBaseDatos();
        gestorCredenciales = new GestorCredenciales();
        gestorBovedas = new GestorBovedas();

        conexion = gestorBaseDatos.crearBaseDatosEnMemoria();
        gestorBovedas.insertarBovedaInicial(conexion, "pruebas");

    }

    @AfterEach
    void cierre() throws Exception {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    @Test
    void testCreaCredencial() throws Exception {

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador("pruebas.com");
        credencial.setUsername("usuario");
        credencial.setPassword("1234");

        int idCredencial = gestorCredenciales.crearCredencial(credencial, 1, conexion);

        assertTrue(idCredencial > 0);
    }


    @Test
    void testEditarCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();

        credencial.setUsername("pruebaEdicion");

        Boveda bovedaActual = new Boveda();
        bovedaActual.setIdBoveda(1);

        gestorCredenciales.editarCredencial(conexion, bovedaActual, credencial);

        List<Credencial> listaEditarNoExiste = gestorCredenciales.buscarCredenciales(conexion, bovedaActual.getIdBoveda(), "usuario");
        List<Credencial> listaEditarExiste = gestorCredenciales.buscarCredenciales(conexion, bovedaActual.getIdBoveda(), "pruebaEdicion");

        assertTrue(listaEditarNoExiste.isEmpty());
        assertEquals(1, listaEditarExiste.size());

    }


    @Test
    void testEliminarCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();


        gestorCredenciales.eliminarCredencial(conexion, 1, credencial);

        List<Credencial> listaEliminacion = gestorCredenciales.buscarCredenciales(conexion, 1, "usuario");

        assertTrue(listaEliminacion.isEmpty());


    }

    @Test
    void testActualizarPassword() throws Exception {

        Credencial credencial = crearCredencialBase();

        String nuevoPassword = gestorCredenciales.actualizarPassword(conexion, 1, credencial);

        assertNotNull(nuevoPassword);
        assertEquals(nuevoPassword, credencial.getPassword());

    }

    @Test
    void testBusquedaCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();
        gestorCredenciales.crearCredencial(credencial, 1, conexion);

        List<Credencial> listaBusqueda = gestorCredenciales.buscarCredenciales(conexion, 1, "usuario");

        assertFalse(listaBusqueda.isEmpty());

    }

    @Test
    void testFiltrarPorCategoria() throws Exception {

        Credencial credencial = crearCredencialBase();

        Categoria categoria = new Categoria(1, "prueba", "categoría de prueba", false);

        credencial.setCategoria(categoria);

        List<Credencial> listaFiltradoCategorias = gestorCredenciales.obtenerCredencialesPorCategoria(conexion, categoria.getIdCategoria());

        assertFalse(listaFiltradoCategorias.isEmpty());


    }

    @Test
    void testMarcadoDestacada() throws Exception {

        Credencial credencial = crearCredencialBase();

        gestorCredenciales.actualizarDestacada(conexion, credencial.getIdCredencial(), true);

        List<Credencial> listaDestacadas = gestorCredenciales.obtenerCredencialesDestacadas(conexion, 1);

        assertEquals(1, listaDestacadas.size());

    }


    // Métodos auxiliares
    Credencial crearCredencialBase() throws Exception {

        try {

            Credencial credencial = new Credencial();
            credencial.setUrlIdentificador("credencialBase");
            credencial.setUsername("usuario");
            credencial.setPassword("Contraseña");

            gestorCredenciales.crearCredencial(credencial, 1, conexion);

            return credencial;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}