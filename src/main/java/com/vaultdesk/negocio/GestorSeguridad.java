package com.vaultdesk.negocio;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class GestorSeguridad {

    // Parámetros de configuración para la derivación de clave

    private static final int LONGITUD_SALT = 16; // 128 bits
    private static final int LONGITUD_CLAVE = 256; // 256 bits
    private static final int LONGITUD_IV = 12;
    private static final int ITERACIONES = 600000;
    private static final String ALGORITMO_DERIVACION = "PBKDF2WithHmacSHA256";


    /**
     * Genera un salt aleatorio
     *
     * @return salt de 16 bytes
     * */
    public byte[] generarSalt(){

        byte[] salt = new byte[LONGITUD_SALT];

        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        return salt;
    }

    /**
     * Deriva una clave criptográfica a partir de una contraseña maestra y salt
     *
     * @param passwordMaestra - Contraseña maestra del usuario
     * @param salt - valor aleatorio
     * @return array de bytes con la clave derivada
     * @throws NoSuchAlgorithmException si el algoritmo no está disponible
     * @throws InvalidKeySpecException si la especificación de clave no es válida
     * */
    public byte[] derivarClave(char[] passwordMaestra, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] claveDerivada;

        PBEKeySpec spec = new PBEKeySpec(
                passwordMaestra,
                salt,
                ITERACIONES,
                LONGITUD_CLAVE
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITMO_DERIVACION);
        claveDerivada = factory.generateSecret(spec).getEncoded();

        spec.clearPassword(); // NO borra por completo, pero es una buena práctica

        return claveDerivada;
    }

    /**
     * Genera un IV (Vector de inicialización) para AES
     *
     * @return vector de incialización de 12 bytes
     * */
    public byte[] generarIV(){
        byte[] vectorInicializacion = new byte[LONGITUD_IV];

        SecureRandom random = new SecureRandom();
        random.nextBytes(vectorInicializacion);

        return vectorInicializacion;
    }

    /**
     * Cifra un conjunto de datos, con la clave y vector de inicialización dados
     *
     * @param datos - array de bytes con los datos a encriptar
     * @param claveDerivada - clave de cifrado derivada mediante contraseña maestra
     * @param vectorInicializacion
     * @return datos cifrados
     * */
    public byte[] cifrar(byte[] datos, byte[] claveDerivada, byte[] vectorInicializacion) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        SecretKeySpec claveSecreta = new SecretKeySpec(claveDerivada, "AES");

        GCMParameterSpec parametros = new GCMParameterSpec(128, vectorInicializacion);

        cipher.init(Cipher.ENCRYPT_MODE, claveSecreta, parametros);

        return cipher.doFinal(datos);
    }


    /**
     * Descifra un conjunto de datos, con la clave y vector de inicialización dados
     *
     * @param datos - array de bytes con los datos a encriptar
     * @param claveDerivada - clave de cifrado derivada mediante contraseña maestra
     * @param vectorInicializacion
     * @return datos descifrados
     * */
    public byte[] descifrar(byte[] datos, byte[] claveDerivada, byte[] vectorInicializacion) throws Exception{

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        SecretKeySpec claveSecreta = new SecretKeySpec(claveDerivada, "AES");

        GCMParameterSpec parametros = new GCMParameterSpec(128, vectorInicializacion);

        cipher.init(Cipher.DECRYPT_MODE, claveSecreta, parametros);

        return cipher.doFinal(datos);
    }
}
