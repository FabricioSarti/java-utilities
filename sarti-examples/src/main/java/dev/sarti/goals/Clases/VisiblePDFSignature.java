package dev.sarti.goals.Clases;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;

import javafx.scene.paint.Color;

public class VisiblePDFSignature implements SignatureInterface {

    private final PrivateKey privateKey;
    private final Certificate[] chain;
    private final String imagePath;
    private final List<String> text;

    public VisiblePDFSignature(PrivateKey privateKey, Certificate[] chain, String imagePath, List<String> text) {
        this.privateKey = privateKey;
        this.chain = chain;
        this.imagePath = imagePath;
        this.text = text;
    }

    @Override
    public byte[] sign(InputStream content) throws IOException {
        try (InputStream is = content) {
            byte[] data = readAllBytes(is);
            return CertificateUtils.signData(data, privateKey, chain);
        } catch (Exception e) {
            throw new IOException("Error al firmar digitalmente", e);
        }
    }

    private byte[] readAllBytes(InputStream input) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[8192];
            int nRead;
            while ((nRead = input.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    public void addVisualSignature(PDDocument document) throws IOException {
        PDPage page = document.getPage(0);
        PDRectangle rect = new PDRectangle(50, 750, 250, 70); // X, Y, Ancho, Alto

        float imgWidth = 150;
        float imgHeight = 70;
        float margin = 20;

        float x = rect.getLowerLeftX() + margin;
        float y = rect.getLowerLeftY() + margin;

        float imageX = x + 100; // más a la derecha
        float imageY = y;

        float textX = x; // texto más a la izquierda
        float textY = y + imgHeight / 2; // centrar verticalmente el texto con la imagen

        // Cargar imagen desde resources
        PDImageXObject pdImage;
        try (InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath)) {
            if (imageStream == null) {
                throw new IllegalArgumentException("No se encontró la imagen: " + imagePath);
            }
            byte[] imageBytes = toByteArray(imageStream);
            pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "firma.png");
        }

        // Fuente configuracion
        /*
         * PDType0Font font;
         * try (InputStream fontStream =
         * getClass().getClassLoader().getResourceAsStream("fonts/OpenSans-Regular.ttf")
         * ) {
         * if (fontStream == null) {
         * throw new IOException("No se encontró la fuente personalizada.");
         * }
         * font = PDType0Font.load(document, fontStream); // true → embebida
         * 
         * }
         */

        // Dibujar imagen y texto en el PDF
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                PDPageContentStream.AppendMode.APPEND, true)) {

            float availableWidth = imageX - textX;
            float lineHeight = 12;
            contentStream.drawImage(pdImage, rect.getLowerLeftX(), rect.getLowerLeftY(), 50, 50);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
            // contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.newLineAtOffset(rect.getLowerLeftX() + 60, rect.getLowerLeftY() + 40);

            for (String rawLine : text) {
                String[] words = rawLine.split(" ");
                StringBuilder line = new StringBuilder();

                for (String word : words) {
                    String testLine = line + word + " ";
                    float testWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(testLine) / 1000 * 10;

                    if (testWidth > availableWidth) {
                        contentStream.showText(line.toString());
                        contentStream.newLineAtOffset(0, -lineHeight);
                        line = new StringBuilder(word + " ");
                    } else {
                        line.append(word).append(" ");
                    }
                }
                contentStream.showText(line.toString());
                contentStream.newLineAtOffset(0, -lineHeight);
            }
            contentStream.endText();
        }

    }

    private byte[] toByteArray(InputStream input) throws java.io.IOException {
        try (InputStream in = input; java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[8192];
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

}
