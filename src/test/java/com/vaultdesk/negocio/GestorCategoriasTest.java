package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.persistencia.GestorBaseDatos;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para comprobar la gestión de categorías
 * */
class GestorCategoriasTest {

    private Connection conexion;
    private GestorCategorias gestorCategorias;

    // Inicializa la conexión con SQLite, las tablas de la base de datos y los datos iniciales necesarios para los tests
    @BeforeEach
    void inicializacion() throws Exception {
        conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
        gestorCategorias = new GestorCategorias();

        try (Statement sentencia = conexion.createStatement()) {

            // Crea la tabla categoria para pruebas
            sentencia.execute("""
                CREATE TABLE categoria (
                    id_categoria INTEGER PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    descripcion TEXT,
                    es_del_sistema INTEGER NOT NULL DEFAULT 0
                )
            """);

            // Crea la tabla credencial para mantener la integridad referencial
            sentencia.execute("""
                    CREATE TABLE credencial (
                    id_credencial INTEGER PRIMARY KEY,
                    id_categoria INTEGER,
                    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
                )
            """);

            // Insertar categoría protegida "Otros"
            sentencia.execute("""
                INSERT INTO categoria (id_categoria, nombre, descripcion, es_del_sistema)
                VALUES (1, 'Otros', 'Categoría por defecto', 1)
            """);
        }
    }

    // Después de cada test cierra la conexión
    @AfterEach
    void cierre() throws Exception {
        conexion.close();
    }

    // Comprobar que se crea una categoría correctamente
    @Test
    void creaCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("Tests Unitarios");
        categoria.setDescripcion("Categoría para tests unitarios");

        gestorCategorias.crearCategoria(conexion, categoria);

        List<Categoria> categorias = gestorCategorias.obtenerCategorias(conexion);

        assertEquals(2, categorias.size()); // La 1 es 'Otros'
        assertEquals("Tests Unitarios", categorias.get(1).getNombre());
        assertEquals("Categoría para tests unitarios", categorias.get(1).getDescripcion());
    }

    // Comprobar que se edita una categoría correctamente
    @Test
    void editaCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("Inicio");
        categoria.setDescripcion("Descripción inicial");

        gestorCategorias.crearCategoria(conexion, categoria);

        List<Categoria> categorias = gestorCategorias.obtenerCategorias(conexion);
        Categoria creada = categorias.get(1); // saltar 'Otros'

        creada.setNombre("Final");
        creada.setDescripcion("Descripción final");

        gestorCategorias.editarCategoria(conexion, creada);

        List<Categoria> actualizadas = gestorCategorias.obtenerCategorias(conexion);
        assertEquals("Final", actualizadas.get(1).getNombre());
        assertEquals("Descripción final", actualizadas.get(1).getDescripcion());
    }

    // Comprobar que se eliminar una categoría correctamente
    @Test
    void eliminaCategoria() throws Exception {

        Categoria categoria = new Categoria();
        categoria.setNombre("Eliminable");
        categoria.setDescripcion("Descripción");

        gestorCategorias.crearCategoria(conexion, categoria);

        List<Categoria> categorias = gestorCategorias.obtenerCategorias(conexion);
        Categoria creada = categorias.get(1); // Salter 'Otros'

        gestorCategorias.eliminarCategoria(conexion, creada.getIdCategoria());

        List<Categoria> resultado = gestorCategorias.obtenerCategorias(conexion);

        assertEquals(1, resultado.size()); // Solo queda 'Otros'
    }

    // Comprobar que el listado de categorías incluye a 'Otros'
    @Test
    void obtenerCategoriasIncluyeAOtros() throws Exception {

        List<Categoria> categorias = gestorCategorias.obtenerCategorias(conexion);

        assertNotNull(categorias);
        assertEquals(1, categorias.size());
        assertEquals("Otros", categorias.get(0).getNombre());
    }

    // Comprobar si, al crear una categoría con nombre vacío, se lanza una excepción
    @Test
    void crearCategoriaConNombreVacioFalla() throws Exception {
        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCategorias gestorCategorias = new GestorCategorias();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {
            insertarBovedaPrueba(conexion);

            Categoria categoria = new Categoria();
            categoria.setNombre("");
            categoria.setDescripcion("Categoría vacía");

            assertThrows(
                    IllegalArgumentException.class,
                    () -> gestorCategorias.crearCategoria(conexion, categoria)
            );
        }
    }

    // Comprobar que al eliminar la categoría 'Otros' se lanza una excepción
    @Test
    void eliminarCategoriaOtrosDebeFallar() throws Exception {
        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCategorias gestorCategorias = new GestorCategorias();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {
            insertarBovedaPrueba(conexion);

            assertThrows(
                    IllegalArgumentException.class,
                    () -> gestorCategorias.eliminarCategoria(conexion, 1)
            );
        }
    }

    // Metodo auxiliar para crear la bóveda de prueba
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
}