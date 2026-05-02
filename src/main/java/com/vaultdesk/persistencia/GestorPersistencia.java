package com.vaultdesk.persistencia;

import com.vaultdesk.negocio.ExcepcionIntegridadBoveda;
import com.vaultdesk.negocio.GestorSeguridad;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Arrays;

/**
 * Clase encargada de gestionar las operaciones de persistencia
 * <p>
 * Esta clase centraliza las operaciones relacionadas con la persistencia de datos, como abrir y guardar archivos de
 * bóveda, cargarlos y guardarlos desde memoria
 * </p>
 *
 */
public class GestorPersistencia {

    /**
     * Abre un archivo de bóveda desde almacenamiento físico
     *
     * @param ruta            ruta donde está alojada la bóveda
     * @param passwordMaestra array de caracteres con la contraseña maestra de la bóveda
     * @return array de bytes con la bóveda descifrada y serializada
     * @throws IOException               si encuentra algún problema con el archivo de bóveda
     * @throws ExcepcionIntegridadBoveda si la comprobación de integridad de la bóveda falla
     * @see GestorSeguridad#derivarClave(char[], byte[])
     * @see GestorSeguridad#descifrar(byte[], byte[], byte[])
     *
     */
    public byte[] abrirArchivoBoveda(Path ruta, char[] passwordMaestra) throws Exception {

        GestorSeguridad gestorSeguridad = new GestorSeguridad();

        try (DataInputStream entrada = new DataInputStream(new FileInputStream(ruta.toFile()))) {

            // 1 - Leer 4 bytes: identificador
            byte[] identificador = new byte[4];
            entrada.readFully(identificador);

            String identificadorTexto = new String(identificador);

            if (!identificadorTexto.equals("VLTD")) {
                throw new IOException("El archivo proporcionado NO es una bóveda válida");
            }

            // 2 - Leer 1 byte: versión
            byte version = entrada.readByte();

            // Añadir más versiones en futuras iteraciones
            if (version != 1) {
                throw new IOException("La versión de bóveda " + version + " no está soportada por esta versión de VaultDesk");
            }

            // 3 - Leer 16 bytes: salt
            byte[] salt = new byte[16];
            entrada.readFully(salt);


            // 4 - Leer 4 bytes: iteraciones
            int iteraciones = entrada.readInt();

            // 5 - Leer 4 bytes: longitud de clave
            int longitudClave = entrada.readInt();

            // TODO - Validar iteraciones y longitudClave con constantes del sistema

            // 6 - Leer 12 bytes: vector de inicialización
            byte[] vectorInicializacion = new byte[12];
            entrada.readFully(vectorInicializacion);

            // 7 - Leer 4 bytes: longitud de bloque cifrado - integridad
            int longitudBloqueDatos = entrada.readInt();

            if (longitudBloqueDatos <= 0) {
                throw new IOException("La longitud del bloque cifrado no es válida");
            }

            // 8 - Leer bloque de datos cifrado
            byte[] bloqueCifrado = new byte[longitudBloqueDatos];
            entrada.readFully(bloqueCifrado);

            // 9 - Derivar clave -> password, salt, iv, bloque de datos
            byte[] claveDerivada = gestorSeguridad.derivarClave(passwordMaestra, salt);

            // 10 - Descifrar bloque cifrado -> clave derivada, iv, bloque de datos
            try {
                byte[] datosDescifrados = gestorSeguridad.descifrar(bloqueCifrado, claveDerivada, vectorInicializacion);

                // 11 - Devolver resultado
                return datosDescifrados;
            } catch (BadPaddingException e) {

                throw new ExcepcionIntegridadBoveda("La bóveda está corrupta, ha sido modificada o la contraseña es incorrecta", e);
            } finally {
                // 12 - Limpiar la clave derivada en memoria
                Arrays.fill(claveDerivada, (byte) 0);
            }


        }

    }

    /**
     * Guarda un archivo de bóveda en almacenamiento físico
     *
     * @param ruta            ruta donde será alojada la bóveda
     * @param passwordMaestra array de caracteres con la contraseña maestra de la bóveda
     * @throws Exception si encuentra algún problema durante el proceso
     * @see GestorSeguridad#cifrar(byte[], byte[], byte[])
     *
     */
    public void guardarArchivoBoveda(Path ruta, char[] passwordMaestra, byte[] bloqueDatos) throws Exception {

        GestorSeguridad gestorSeguridad = new GestorSeguridad();

        String identificador = "VLTD";
        byte[] salt = gestorSeguridad.generarSalt();
        byte[] vectorInicializacion = gestorSeguridad.generarIV();
        byte[] claveDerivada = gestorSeguridad.derivarClave(passwordMaestra, salt);

        try {

            byte[] bloqueCifrado = gestorSeguridad.cifrar(bloqueDatos, claveDerivada, vectorInicializacion);

            try (DataOutputStream salida = new DataOutputStream(new FileOutputStream(ruta.toFile()))) {
                salida.write(identificador.getBytes(StandardCharsets.UTF_8));
                salida.writeByte(1);    // Añadir nuevas versiones en futuras itereaciones
                salida.write(salt);
                salida.writeInt(600000); // Añadir un archivo con constantes - iteraciones
                salida.writeInt(256);   // Longitud de la clave
                salida.write(vectorInicializacion);
                salida.writeInt(bloqueCifrado.length);
                salida.write(bloqueCifrado);
                salida.flush();

            }

        } finally {
            Arrays.fill(claveDerivada, (byte) 0);
        }
    }

    /**
     * Abre una bóveda y carga su estructura y contenido en memoria
     *
     * @param ruta            ruta donde está alojada la bóveda
     * @param passwordMaestra array de caracteres con la contraseña maestra de la bóveda
     * @return conexión con la base de datos con los datos cargados
     * @throws Exception si encuentra algún problema durante el proceso
     * @see GestorPersistencia#abrirArchivoBoveda(Path, char[])
     * @see GestorBaseDatos#cargarBaseDatosDesdeBytes(byte[])
     *
     */
    public Connection abrirBovedaEnMemoria(Path ruta, char[] passwordMaestra) throws Exception {

        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        byte[] datosBaseDatos = abrirArchivoBoveda(ruta, passwordMaestra);
        try {
            return gestorBaseDatos.cargarBaseDatosDesdeBytes(datosBaseDatos);
        } finally {
            Arrays.fill(datosBaseDatos, (byte) 0);
        }
    }

    /**
     * Guarda una bóveda desde la memoria volátil a almacenamiento físico
     *
     * @param ruta            ruta donde será alojada la bóveda
     * @param passwordMaestra contraseña maestra de la bóveda
     * @param conexion        conexión activa con la base de datos
     * @throws Exception si encuentra algún problema durante el proceso
     * @see GestorBaseDatos#serializarBaseDatos(Connection)
     * @see GestorPersistencia#guardarArchivoBoveda(Path, char[], byte[])
     *
     */
    public void guardarBovedaDesdeMemoria(Path ruta, char[] passwordMaestra, Connection conexion) throws Exception {

        GestorBaseDatos gestorBaseDatos = new GestorBaseDatos();
        byte[] datosBaseDatos = gestorBaseDatos.serializarBaseDatos(conexion);
        try {
            guardarArchivoBoveda(ruta, passwordMaestra, datosBaseDatos);
        } finally {
            Arrays.fill(datosBaseDatos, (byte) 0); // Borrado seguro de memoria
        }
    }

}
