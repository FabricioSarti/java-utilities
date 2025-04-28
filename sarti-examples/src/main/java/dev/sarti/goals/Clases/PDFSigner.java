package dev.sarti.goals.Clases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

public class PDFSigner {

    private final String keystorePath;
    private final String keystorePassword;
    private final String outputPath;

    public PDFSigner(String keystorePath, String keystorePassword, String outputPath) {
        this.keystorePath = keystorePath;
        this.keystorePassword = keystorePassword;
        this.outputPath = outputPath;
    }

    public void sign(String inputPdfPath, String signatureImagePath, List<String> signatureText) throws Exception {
        KeyStore keystore = CertificateUtils.loadKeyStore(keystorePath, keystorePassword);
        String alias = keystore.aliases().nextElement();

        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
        Certificate[] chain = keystore.getCertificateChain(alias);

        try (InputStream pdfInput = getClass().getClassLoader().getResourceAsStream(inputPdfPath)) {

            if (pdfInput == null) {
                throw new FileNotFoundException("No se encontró el PDF en resources: " + inputPdfPath);
            }
            PDDocument document = PDDocument.load(pdfInput);

            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Firmado digitalmente");
            signature.setLocation("Ubicación");
            signature.setSignDate(Calendar.getInstance());

            VisiblePDFSignature visualSigner = new VisiblePDFSignature(privateKey, chain, signatureImagePath,
                    signatureText);
            visualSigner.addVisualSignature(document); // ← agrega la firma visual antes de firmar

            document.addSignature(signature, visualSigner);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                ExternalSigningSupport externalSigning = document.saveIncrementalForExternalSigning(fos);
                // Read InputStream to byte[]
                try (java.io.InputStream contentStream = externalSigning.getContent()) {
                    byte[] contentBytes = toByteArray(contentStream);
                    byte[] cmsSignature = CertificateUtils.signData(contentBytes, privateKey, chain);
                    externalSigning.setSignature(cmsSignature);
                }
            }

            document.close();

        }

    }

    private static byte[] toByteArray(java.io.InputStream input) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

}
