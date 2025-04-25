package utils;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import Entites.Zone;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportPDF {

    public static void exporterZonesEnPDF(List<Zone> zones, String filePath) throws IOException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Titre
        Paragraph titre = new Paragraph("Liste des Zones Agricoles");
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Tableau
        PdfPTable table = new PdfPTable(4); // 4 colonnes
        table.setWidthPercentage(100);

        table.addCell("ID");
        table.addCell("Nom");
        table.addCell("Superficie (ha)");
        table.addCell("Localisation");

        for (Zone zone : zones) {
            table.addCell(String.valueOf(zone.getId()));
            table.addCell(zone.getNom_zone());
            table.addCell(String.valueOf(zone.getSuperficie_zone()));
            table.addCell(zone.getLocalisation_zone());
        }

        document.add(table);
        document.close();
    }
}
