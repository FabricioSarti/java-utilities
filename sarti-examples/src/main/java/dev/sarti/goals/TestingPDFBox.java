package dev.sarti.goals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;

public class TestingPDFBox {

    public static void signPdf(String inputPath, String outputPath, String base64ImagePath,
            String reason, String location, String signedBy, String fontPath) throws Exception {

        PDDocument document = PDDocument.load(new File(inputPath));

        // Crear la firma (solo metadatos, PDFBox no firma con PKCS#7 directamente)
        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName(signedBy);
        signature.setLocation(location);
        signature.setReason(reason);
        signature.setSignDate(Calendar.getInstance());

        document.addSignature(signature);

        try (InputStream fontStream = TestingPDFBox.class.getClassLoader()
                .getResourceAsStream("fonts/OpenSans-Regular.ttf")) {

            if (fontStream == null) {
                throw new FileNotFoundException("No se encontró la fuente en resources/fonts/OpenSans-Regular.ttf");
            }

            PDType0Font font = PDType0Font.load(document, fontStream);

            // Capa visual de la firma
            PDPage page = document.getPage(0);
            PDRectangle rect = new PDRectangle(50, 750, 250, 70); // X, Y, Ancho, Alto

            PDPageContentStream content = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true);

            // Imagen desde base64
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64ImagePath);
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, imageBytes, "firmante");

            content.drawImage(pdImage, rect.getLowerLeftX(), rect.getLowerLeftY(), 50, 50);

            // Fecha y texto
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
            String fecha = sdf.format(new Date());

            content.beginText();
            content.setFont(font, 8);
            content.newLineAtOffset(rect.getLowerLeftX() + 60, rect.getLowerLeftY() + 40);
            content.showText("Firmado por: " + signedBy);
            content.newLineAtOffset(0, -10);
            content.showText("Fecha: " + fecha);
            content.newLineAtOffset(0, -10);
            content.showText("Razón: " + reason);
            content.endText();

            content.close();

            // Guardar
            document.save(outputPath);
            document.close();
        }

        System.out.println("PDF firmado visualmente: " + outputPath);
    }

    public static void main(String[] args) {
        try {
            String inputPDF = "C:\\Users\\fabri\\Documents\\PDF_PROBANDO.pdf";
            String outputPDF = "C:\\Users\\fabri\\Documents\\PDF_PROBANDO_FIRMADO.pdf";
            String fontPath = "src/main/resources/fonts/OpenSans-Regular.ttf";

            // Imagen en base64 (usa tu propia cadena en base64 de un PNG)
            String imagenBase64 = "iVBORw0KGgoAAAANSUhEUgAAAwAAAAEBCAMAAAD4nDIwAAADAFBMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaATaaBLOFyUAAAA/3RSTlMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA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";

            signPdf(
                    inputPDF,
                    outputPDF,
                    imagenBase64,
                    "Aprobado electrónicamente",
                    "Guatemala",
                    "SAT Guatemala",
                    fontPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
