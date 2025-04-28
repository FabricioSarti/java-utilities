package dev.sarti.goals.Clases;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;

public class CertificateUtils {

    public static KeyStore loadKeyStore(String resourcePath, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream is = CertificateUtils.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new FileNotFoundException("No se encontró el archivo: " + resourcePath);
            }
            ks.load(is, password.toCharArray());
        }
        return ks;
    }

    public static byte[] signData(byte[] data, PrivateKey key, Certificate[] chain) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(key);
        signature.update(data);
        return signature.sign();
    }
}
