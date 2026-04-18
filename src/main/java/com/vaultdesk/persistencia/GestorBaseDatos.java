package com.vaultdesk.persistencia;

import org.sqlite.SQLiteConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorBaseDatos {

    private static final String URL_SQLITE_EN_MEMORIA = "jdbc:sqlite::memory:";

    public Connection abrirConexionEnMemoria() throws SQLException {

        return DriverManager.getConnection(URL_SQLITE_EN_MEMORIA);
    }

    public void crearEsquema(Connection conexion) throws SQLException{

        try(Statement sentenciaCreacion = conexion.createStatement()){
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

    public void insertarDatosSemilla(Connection conexion) throws SQLException{

        try (Statement sentenciaCreacionSemilla = conexion.createStatement()){
            sentenciaCreacionSemilla.execute("""
                    INSERT INTO idioma (id_idioma, nombre)
                    VALUES
                        (1, 'Español'),
                        (2, 'English')
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

    public Connection crearBaseDatosEnMemoria() throws SQLException{

        Connection conexion = abrirConexionEnMemoria();
        crearEsquema(conexion);
        insertarDatosSemilla(conexion);
        return conexion;

    }

    public byte[] serializarBaseDatos(Connection conexion) throws SQLException{

        if(conexion == null){
            throw new IllegalArgumentException("La conexión no puede ser nula");
        }

        SQLiteConnection conexionSQLite = conexion.unwrap(SQLiteConnection.class);
        return conexionSQLite.serialize("main");
    }

    public Connection cargarBaseDatosDesdeBytes(byte[] datoBaseDatos) throws SQLException{

        if(datoBaseDatos == null || datoBaseDatos.length == 0){
            throw new IllegalArgumentException("La base de datos no puede ser nula");
        }

        Connection conexion = abrirConexionEnMemoria();

        try (Statement sentencia = conexion.createStatement()){
            sentencia.execute("PRAGMA foreign_keys = ON");
        }

        SQLiteConnection conexionSQLite = conexion.unwrap(SQLiteConnection.class);
        conexionSQLite.deserialize("main", datoBaseDatos);

        return conexion;
    }


}
