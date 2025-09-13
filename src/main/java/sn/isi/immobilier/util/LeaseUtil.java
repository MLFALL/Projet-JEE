package sn.isi.immobilier.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.service.EmailService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LeaseUtil {

    // Génère le PDF, envoie l'email et sauvegarde sur serveur
    public static void generatePdfAndSendEmail(Lease lease, String servletRoot, EmailService emailService) throws Exception {
        ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(pdfOutput);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {

            document.add(new Paragraph("Contrat de location"));
            document.add(new Paragraph("Locataire : " + lease.getTenant().getFullName()));
            document.add(new Paragraph("Immeuble : " + lease.getUnit().getBuilding().getName()));
            document.add(new Paragraph("Unité : " + lease.getUnit().getUnitNumber()));
            document.add(new Paragraph("Loyer : " + lease.getRentAmount() + " FCFA"));
            document.add(new Paragraph("Date de début : " + lease.getStartDate()));
            document.add(new Paragraph("Date de fin : " + lease.getEndDate()));
        }

        byte[] pdfBytes = pdfOutput.toByteArray();
        String pdfFileName = "contrat_" + lease.getId() + ".pdf";
        System.out.println("Tentative d'envoi du mail au locataire : " + lease.getTenant().getEmail());

        // Envoi de l’email au locataire
        emailService.sendEmailWithAttachment(
                lease.getTenant().getEmail(),
                "Votre contrat de location",
                "Bonjour, veuillez trouver ci-joint votre contrat de location.",
                pdfBytes,
                pdfFileName
        );

        // Sauvegarde sur le serveur
        String uploadDir = servletRoot + "uploads/contracts/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        Path pdfPath = Path.of(uploadDir, pdfFileName);
        Files.write(pdfPath, pdfBytes);
    }
}
