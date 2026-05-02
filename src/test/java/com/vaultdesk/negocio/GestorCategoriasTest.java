package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.persistencia.GestorBaseDatos;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de test para comprobar la gestión de categorías
 *
 *
 */
class GestorCategoriasTest {

    private Connection conexion;
    private GestorCategorias gestorCategorias;
    private GestorBaseDatos gestorBaseDatos;
    private GestorBovedas gestorBovedas;

    @BeforeEach
    void inicializacion() throws Exception {


        gestorBaseDatos = new GestorBaseDatos();
        gestorBovedas = new GestorBovedas();

        conexion = gestorBaseDatos.crearBaseDatosEnMemoria();
        gestorBovedas.insertarBovedaInicial(conexion, "pruebas");

        gestorCategorias = new GestorCategorias();

    }

    @AfterEach
    void cierre() throws Exception {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    @Test
    void testCrearCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("prueba");

        int idCategoria = gestorCategorias.crearCategoria(conexion, categoria);

        assertTrue(idCategoria > 0);
    }

    @Test
    void testEditarCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("Editada");

        gestorCategorias.crearCategoria(conexion, categoria);

        gestorCategorias.editarCategoria(conexion, categoria);

        List<Categoria> listaEdicion = gestorCategorias.obtenerCategorias(conexion);

        assertEquals("Editada", listaEdicion.get(1).getNombre());
    }

    @Test
    void testEliminarCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("prueba eliminación");

        gestorCategorias.crearCategoria(conexion, categoria);

        List<Categoria> listaAntesEliminacion = gestorCategorias.obtenerCategorias(conexion);

        gestorCategorias.eliminarCategoria(conexion, categoria.getIdCategoria());

        List<Categoria> listaDespuesEliminacion = gestorCategorias.obtenerCategorias(conexion);

        assertEquals(2, listaAntesEliminacion.size());
        assertEquals(1, listaDespuesEliminacion.size());
    }
}