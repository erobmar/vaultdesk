package com.vaultdesk.negocio;

import com.vaultdesk.dominio.Categoria;
import com.vaultdesk.dominio.Credencial;
import com.vaultdesk.persistencia.GestorBaseDatos;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Clase de test para comprobar la gestión de credenciales
 * */
class GestorCredencialesTest {

    private Connection conexion;
    private GestorCredenciales gestorCredenciales;

    private static final int ID_BOVEDA = 1;
    private static final int ID_CATEGORIA_OTROS = 1;
    private static final int ID_CATEGORIA_PRUEBA = 2;

    // Inicialización de la base de datos y tablas y datos iniciales necesarios para las puebas
    @BeforeEach
    void inicializacion() throws Exception {
        conexion = DriverManager.getConnection("jdbc:sqlite::memory:");
        gestorCredenciales = new GestorCredenciales();

        try (Statement sentencia = conexion.createStatement()) {

            sentencia.execute("""
                CREATE TABLE boveda (
                    id_boveda INTEGER PRIMARY KEY,
                    nombre TEXT NOT NULL
                )
            """);

            sentencia.execute("""
                CREATE TABLE categoria (
                    id_categoria INTEGER PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    descripcion TEXT,
                    es_del_sistema INTEGER NOT NULL DEFAULT 0
                )
            """);

            sentencia.execute("""
                CREATE TABLE credencial (
                    id_credencial INTEGER PRIMARY KEY,
                    url_identificador TEXT NOT NULL,
                    username TEXT NOT NULL,
                    password TEXT NOT NULL,
                    destacada INTEGER NOT NULL DEFAULT 0,
                    anotaciones TEXT,
                    caduca INTEGER NOT NULL DEFAULT 0,
                    fecha_caducidad TEXT,
                    periodo_caducidad INTEGER,
                    ultimo_update TEXT,
                    req_longitud INTEGER,
                    req_mayusculas INTEGER,
                    req_minusculas INTEGER,
                    req_digitos INTEGER,
                    req_especiales INTEGER,
                    id_boveda INTEGER NOT NULL,
                    id_categoria INTEGER NOT NULL,
                    FOREIGN KEY (id_boveda) REFERENCES boveda(id_boveda),
                    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
                )
            """);

            sentencia.execute("""
                INSERT INTO boveda (id_boveda, nombre)
                VALUES (1, 'Bóveda de prueba')
            """);

            sentencia.execute("""
                INSERT INTO categoria (id_categoria, nombre, descripcion, es_del_sistema)
                VALUES (1, 'Otros', 'Categoría por defecto', 1)
            """);

            sentencia.execute("""
                INSERT INTO categoria (id_categoria, nombre, descripcion, es_del_sistema)
                VALUES (2, 'Pruebas', 'Credenciales de pruebas', 0)
            """);
        }
    }

    // Después de cada test cierra la conexión
    @AfterEach
    void cierre() throws Exception {
        conexion.close();
    }

    // Comprobar que se crea una credencial correctamente
    @Test
    void creaCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();

        int idGenerado = gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        assertEquals(1, idGenerado);
        assertEquals(1, credencial.getIdCredencial());

        List<Credencial> resultado = gestorCredenciales.buscarCredenciales(conexion, ID_BOVEDA, "");

        assertEquals(1, resultado.size());
        assertEquals("github.com", resultado.get(0).getUrlIdentificador());
        assertEquals("eduardo", resultado.get(0).getUsername());
        assertEquals(ID_CATEGORIA_PRUEBA, resultado.get(0).getCategoria().getIdCategoria());
    }

    // Comprobar que las búsquedas por texto devuelven el resultado correcto
    @Test
    void buscaCredencialPorTexto() throws Exception {

        gestorCredenciales.crearCredencial(crearCredencialBase(), ID_BOVEDA, conexion);

        List<Credencial> resultado = gestorCredenciales.buscarCredenciales(conexion, ID_BOVEDA, "git");

        assertEquals(1, resultado.size());
        assertEquals("github.com", resultado.get(0).getUrlIdentificador());
    }

    // Comproabar que se editan credenciales correctamente
    @Test
    void editaCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();
        gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        credencial.setUsername("usuario_modificado");
        credencial.setUrlIdentificador("gitlab.com");
        credencial.setPassword("nuevoPassword");

        gestorCredenciales.editarCredencial(conexion, ID_BOVEDA, credencial);

        List<Credencial> resultado = gestorCredenciales.buscarCredenciales(conexion, ID_BOVEDA, "gitlab");

        assertEquals(1, resultado.size());
        assertEquals("usuario_modificado", resultado.get(0).getUsername());
        assertEquals("nuevoPassword", resultado.get(0).getPassword());
    }

    // Comprobar que se eliminan credenciales correctamente
    @Test
    void eliminaCredencial() throws Exception {

        Credencial credencial = crearCredencialBase();
        gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        gestorCredenciales.eliminarCredencial(conexion, ID_BOVEDA, credencial);

        List<Credencial> resultado = gestorCredenciales.buscarCredenciales(conexion, ID_BOVEDA, "");

        assertTrue(resultado.isEmpty());
    }

    // Comprobar que el filtro por categorías devuelve el resultado correcto
    @Test
    void obtieneCredencialesPorCategoria() throws Exception {

        gestorCredenciales.crearCredencial(crearCredencialBase(), ID_BOVEDA, conexion);

        List<Credencial> resultado = gestorCredenciales.obtenerCredencialesPorCategoria(conexion, ID_CATEGORIA_PRUEBA);

        assertEquals(1, resultado.size());
        assertEquals(ID_CATEGORIA_PRUEBA, resultado.get(0).getCategoria().getIdCategoria());
    }

    // Comprobar que se obtiene el listado de credenciales destacadas correctamente
    @Test
    void obtieneCredencialesDestacadas() throws Exception {

        Credencial credencial = crearCredencialBase();
        credencial.setDestacada(true);

        gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        List<Credencial> resultado = gestorCredenciales.obtenerCredencialesDestacadas(conexion, ID_BOVEDA);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isDestacada());
    }

    // Comprobar que se cambia el estado de destacada correctamente
    @Test
    void cambiaEstadoDestacada() throws Exception {

        Credencial credencial = crearCredencialBase();
        credencial.setDestacada(false);

        gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        gestorCredenciales.cambiarEstadoDestacada(conexion, credencial.getIdCredencial(), ID_BOVEDA,true);

        List<Credencial> resultado = gestorCredenciales.obtenerCredencialesDestacadas(conexion, ID_BOVEDA);

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isDestacada());
    }

    // Comprobar que las credenciales sin categoría se asignan a 'Otros'
    @Test
    void credencialSinCategoriaSeAsignaAOtros() throws Exception {

        Credencial credencial = crearCredencialBase();
        credencial.setCategoria(null);

        gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion);

        List<Credencial> resultado = gestorCredenciales.buscarCredenciales(conexion, ID_BOVEDA, "");

        assertEquals(1, resultado.size());
        assertEquals(ID_CATEGORIA_OTROS, resultado.get(0).getCategoria().getIdCategoria());
    }

    // Comprobar que al crear una credencial sin usermane se arroja una excepción
    @Test
    void noCreaCredencialSinUsername() {

        Credencial credencial = crearCredencialBase();
        credencial.setUsername("");

        assertThrows(
                IllegalArgumentException.class,
                () -> gestorCredenciales.crearCredencial(credencial, ID_BOVEDA, conexion)
        );
    }

    // Comprobar si, al crear una credencial sin contraseña, se lanza una excepción
    @Test
    void crearCredencialSinPasswordDebeFallar() throws Exception {
        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        GestorCredenciales gestorCredenciales = new GestorCredenciales();

        try (Connection conexion = gestorBaseDatos.crearBaseDatosEnMemoria()) {
            insertarBovedaPrueba(conexion);

            Credencial credencial = crearCredencialBase();
            credencial.setPassword("");

            assertThrows(
                    IllegalArgumentException.class,
                    () -> gestorCredenciales.crearCredencial(credencial, 1, conexion)
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

    // Metodo auxiliar para crear credenciales
    private Credencial crearCredencialBase() {

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(ID_CATEGORIA_PRUEBA);
        categoria.setNombre("Pruebas");
        categoria.setDescripcion("Credenciales de pruebas");
        categoria.setEsDelSistema(false);

        Credencial credencial = new Credencial();
        credencial.setUrlIdentificador("github.com");
        credencial.setUsername("eduardo");
        credencial.setPassword("password123");
        credencial.setDestacada(false);
        credencial.setAnotaciones("Cuenta de prueba");
        credencial.setCaduca(true);
        credencial.setFechaCaducidad(LocalDate.of(2026, 12, 31));
        credencial.setPeriodoCaducidad(90);
        credencial.setFechaUltimoUpdate(LocalDate.of(2026, 4, 26));
        credencial.setReqLongitud(12);
        credencial.setReqMayusculas(1);
        credencial.setReqMinusculas(1);
        credencial.setReqDigitos(1);
        credencial.setReqEspeciales(1);
        credencial.setCategoria(categoria);

        return credencial;
    }
}