package dev.sarti.goals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.sarti.goals.Clases.PDFSigner;

public class UltimateTestingPDFSignCertificada {

    public static void main(String[] args) {
        // Configura las rutas a tus archivos
        String inputPdf = "docs/PDF_PROBANDO.pdf";
        String outputPdf = "C:\\Users\\fabri\\Documents\\documento-firmado.pdf";
        String keystorePath = "key/keystore.p12";
        String keystorePassword = "123456"; // Reemplaza con tu contraseña real
        String signatureImage = "img/sat-guatemala-seeklogo.png"; // Ruta a tu imagen de firma

        List<String> texts = new ArrayList<>();
        texts.add("Firmado por: Juan Perez");
        texts.add("Documento aprobado");
        texts.add("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

        try {
            PDFSigner signer = new PDFSigner(keystorePath, keystorePassword, outputPdf);
            signer.sign(inputPdf, signatureImage, texts);
            System.out.println("Documento firmado correctamente en: " + outputPdf);
        } catch (Exception e) {
            System.err.println("Error al firmar el documento: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
