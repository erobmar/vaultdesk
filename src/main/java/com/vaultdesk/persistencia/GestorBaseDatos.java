package com.vaultdesk.persistencia;

import com.vaultdesk.negocio.GestorIdiomas;
import org.sqlite.SQLiteConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase encargada de gestionar las operaciones sobre la base de datos relacionadas con la persistencia
 * <p>
 * Esta clase actúa como enlace entre el GestorPersistencia y la base de datos, realizando tareas de inicialización de
 * bases de datos, carga de bases de datos en memoria o serialización de datos para la creación del archivo de bóveda.
 * </p>
 *
 */
public class GestorBaseDatos {

    private static final String URL_SQLITE_EN_MEMORIA = "jdbc:sqlite::memory:";


    /**
     * Centraliza las operaciones de inicialización de la base de datos
     *
     * @return conexión con la base de datos ya inicializada
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     * @see GestorBaseDatos#abrirConexionEnMemoria()
     * @see GestorBaseDatos#crearEsquema(Connection)
     * @see GestorBaseDatos#insertarDatosSemilla(Connection)
     *
     *
     */
    public Connection crearBaseDatosEnMemoria() throws SQLException {

        Connection conexion = abrirConexionEnMemoria();
        crearEsquema(conexion);
        insertarDatosSemilla(conexion);
        return conexion;

    }

    /**
     * Serializa la base de datos para prepararla para su cifrado
     *
     * @param conexion conexión activa con la base de datos
     * @return array de bytes con la base de datos serializada
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     *
     */
    public byte[] serializarBaseDatos(Connection conexion) throws SQLException {

        if (conexion == null) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.conexion")); // "La conexión no puede ser nula"
        }

        SQLiteConnection conexionSQLite = conexion.unwrap(SQLiteConnection.class);
        return conexionSQLite.serialize("main");
    }

    /**
     * Carga una base de datos en memoria desde un array de bytes ("de-serializa" una base de datos tras su descifrado)
     *
     * @param datoBaseDatos array de bytes con la base de datos serializada a cargar
     * @throws SQLException si la conexión con la base de datos presenta algún problema
     *
     *
     */
    public Connection cargarBaseDatosDesdeBytes(byte[] datoBaseDatos) throws SQLException {

        if (datoBaseDatos == null || datoBaseDatos.length == 0) {
            throw new IllegalArgumentException(GestorIdiomas.getText("excepcion.basedatosnula")); // "La base de datos no puede ser nula"
        }

        Connection conexion = abrirConexionEnMemoria();

        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
        }

        SQLiteConnection conexionSQLite = conexion.unwrap(SQLiteConnection.class);
        conexionSQLite.deserialize("main", datoBaseDatos);

        return conexion;
    }

    /**
     * Crea una conexión con la base de datos usando el driver :memory: de SQLite (sin persistencia física)
     *
     *
     */
    private Connection abrirConexionEnMemoria() throws SQLException {

        return DriverManager.getConnection(URL_SQLITE_EN_MEMORIA);
    }

    /**
     * Crea el esquema de tablas en la base de datos
     *
     *
     */
    private void crearEsquema(Connection conexion) throws SQLException {

        try (Statement sentenciaCreacion = conexion.createStatement()) {
            sentenciaCreacion.execute("PRAGMA foreign_keys=ON");

            sentenciaCreacion.execute("""
                    CREATE TABLE idioma(
                        id_idioma INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL
                        )
                    """);

            sentenciaCreacion.execute("""
                    CREATE TABLE tema_visual(
                        id_tema_visual INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL
                        )
                    """);

            sentenciaCreacion.execute("""
                    CREATE TABLE categoria(
                        id_categoria INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL,
                        descripcion TEXT,
                        es_del_sistema INTEGER NOT NULL CHECK(es_del_sistema IN (0,1))
                    )
                    """);

            sentenciaCreacion.execute("""
                    CREATE TABLE boveda(
                        id_boveda INTEGER PRIMARY KEY,
                        nombre TEXT NOT NULL,
                        umbral_alerta INTEGER NOT NULL,
                        accesibilidad INTEGER NOT NULL CHECK (accesibilidad IN (0,1)),
                        id_idioma INTEGER NOT NULL,
                        id_tema_visual INTEGER NOT NULL,
                        FOREIGN KEY (id_idioma) REFERENCES idioma(id_idioma),
                        FOREIGN KEY (id_tema_visual) REFERENCES tema_visual(id_tema_visual)
                    )
                    """);

            sentenciaCreacion.execute("""
                    CREATE TABLE credencial(
                        id_credencial INTEGER PRIMARY KEY,
                        url_identificador TEXT NOT NULL,
                        username TEXT NOT NULL,
                        password TEXT NOT NULL,
                        destacada INTEGER NOT NULL CHECK (destacada IN (0,1)),
                        anotaciones TEXT,
                        caduca INTEGER NOT NULL CHECK (caduca IN (0,1)),
                        ultimo_update TEXT,
                        fecha_caducidad TEXT,
                        periodo_caducidad INTEGER,
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
        }

    }

    /**
     * Inserta en la base de datos los datos iniciales necesarios para su funcionamiento
     *
     *
     */
    private void insertarDatosSemilla(Connection conexion) throws SQLException {

        try (Statement sentenciaCreacionSemilla = conexion.createStatement()) {
            sentenciaCreacionSemilla.execute("""
                    INSERT INTO idioma (id_idioma, nombre)
                    VALUES
                        (1, 'Español'),
                        (2, 'English'),
                        (3, 'Català'),
                        (4, 'Asturianu')
                    """);

            sentenciaCreacionSemilla.execute("""
                    INSERT INTO tema_visual (id_tema_visual, nombre)
                    VALUES
                        (1, 'Claro'),
                        (2, 'Oscuro')
                    """);

            sentenciaCreacionSemilla.execute("""
                    INSERT INTO categoria (id_categoria, nombre, descripcion, es_del_sistema)
                    VALUES
                        (1, 'Otros', 'Categoría por defecto del sistema', 1)
                    """);
        }

    }


}
