/*package tn.enicarthage.backendandroid.services;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.stereotype.Service;
import tn.enicarthage.backendandroid.models.Booking;
import tn.enicarthage.backendandroid.models.BookingDetail;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class PdfService {

    public ByteArrayOutputStream generateTicketPdf(Booking booking) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // En-tête
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.WHITE);
            Paragraph header = new Paragraph("BILLET DE SPECTACLE", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);

            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell headerCell = new PdfPCell(header);
            headerCell.setBackgroundColor(new BaseColor(41, 128, 185));
            headerCell.setPadding(10);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerTable.addCell(headerCell);

            document.add(headerTable);
            document.add(Chunk.NEWLINE);

            // Référence
            Font refFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Paragraph reference = new Paragraph("Référence: #" + booking.getReferenceNumber(), refFont);
            document.add(reference);
            document.add(Chunk.NEWLINE);

            // Détails de l'événement
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new BaseColor(41, 128, 185));
            Paragraph eventTitle = new Paragraph("Détails de l'événement", titleFont);
            document.add(eventTitle);
            document.add(Chunk.NEWLINE);

            PdfPTable eventTable = new PdfPTable(2);
            eventTable.setWidthPercentage(100);

            Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 12);

            addTableRow(eventTable, "Événement:", booking.getEventSchedule().getEvent().getTitle(), labelFont, valueFont);
            addTableRow(eventTable, "Date:", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getEventSchedule().getDateTime()), labelFont, valueFont);
            addTableRow(eventTable, "Lieu:", String.valueOf(booking.getEventSchedule().getLieu()), labelFont, valueFont);

            document.add(eventTable);
            document.add(Chunk.NEWLINE);

            // Détails des billets
            Paragraph ticketsTitle = new Paragraph("Détails des billets", titleFont);
            document.add(ticketsTitle);
            document.add(Chunk.NEWLINE);

            PdfPTable ticketsTable = new PdfPTable(3);
            ticketsTable.setWidthPercentage(100);

            PdfPCell categoryHeader = new PdfPCell(new Phrase("Catégorie", labelFont));
            PdfPCell quantityHeader = new PdfPCell(new Phrase("Quantité", labelFont));
            PdfPCell priceHeader = new PdfPCell(new Phrase("Prix unitaire", labelFont));

            categoryHeader.setBackgroundColor(new BaseColor(240, 240, 240));
            quantityHeader.setBackgroundColor(new BaseColor(240, 240, 240));
            priceHeader.setBackgroundColor(new BaseColor(240, 240, 240));

            ticketsTable.addCell(categoryHeader);
            ticketsTable.addCell(quantityHeader);
            ticketsTable.addCell(priceHeader);

            for (BookingDetail detail : booking.getBookingDetails()) {
                ticketsTable.addCell(new Phrase(detail.getTicketCategory().getName(), valueFont));
                ticketsTable.addCell(new Phrase(detail.getQuantity().toString(), valueFont));
                ticketsTable.addCell(new Phrase(detail.getTicketCategory().getPrice().toString() + " €", valueFont));
            }

            document.add(ticketsTable);
            document.add(Chunk.NEWLINE);

            // Prix total
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);

            Font totalFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(231, 76, 60));

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Prix total:", labelFont));
            PdfPCell totalValueCell = new PdfPCell(new Phrase(booking.getTotalAmount().toString() + " €", totalFont));

            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setBorder(Rectangle.NO_BORDER);
            totalValueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totalTable.addCell(totalLabelCell);
            totalTable.addCell(totalValueCell);

            document.add(totalTable);
            document.add(Chunk.NEWLINE);

            // Informations client
            Paragraph clientTitle = new Paragraph("Informations client", titleFont);
            document.add(clientTitle);
            document.add(Chunk.NEWLINE);

            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);

            addTableRow(clientTable, "Email:", booking.getUser().getEmail(), labelFont, valueFont);
            addTableRow(clientTable, "Date de réservation:", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getBookingDate()), labelFont, valueFont);

            document.add(clientTable);
            document.add(Chunk.NEWLINE);

            // QR Code
            String qrContent = "REF:" + booking.getReferenceNumber() +
                    "\nEVENT:" + booking.getEventSchedule().getEvent().getTitle() +
                    "\nDATE:" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(booking.getEventSchedule().getDateTime()) +
                    "\nEMAIL:" + booking.getUser().getEmail();

            Image qrCodeImage = generateQRCode(qrContent, 200, 200);
            qrCodeImage.setAlignment(Element.ALIGN_CENTER);
            document.add(qrCodeImage);

            Paragraph qrLabel = new Paragraph("Scannez ce code à l'entrée du spectacle", valueFont);
            qrLabel.setAlignment(Element.ALIGN_CENTER);
            document.add(qrLabel);

            // Pied de page
            document.add(Chunk.NEWLINE);
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("Ce billet est personnel et ne peut être revendu. Merci de présenter une pièce d'identité à l'entrée.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return baos;
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        labelCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private Image generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);

            Image image = Image.getInstance(baos.toByteArray());
            return image;

        } catch (WriterException | IOException | BadElementException e) {
            e.printStackTrace();
            return null;
        }
    }
}*/