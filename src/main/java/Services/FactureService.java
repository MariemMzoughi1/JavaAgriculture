package Services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import Entites.Commande;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class FactureService {
    public static void generateInvoice(Commande commande, String emailClient) throws IOException, DocumentException {
        // Formatage de la date pour avoir un nom de fichier unique
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = commande.getDatecommande().format(formatter);
        String fileName = "Facture_Commande_" + timestamp + ".pdf";

        // Chemin vers le dossier Téléchargements
        String downloadFolderPath = System.getProperty("user.home") + "/Downloads";  // Spécifie le dossier Téléchargements
        String filePath = downloadFolderPath + "/" + fileName;  // Le fichier sera enregistré dans Téléchargements

        // Création du document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Titre de la facture
        Paragraph titre = new Paragraph("Facture de Commande", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Informations sur la commande et le client
        DateTimeFormatter infoFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        document.add(new Paragraph("Email client         : " + emailClient));
        document.add(new Paragraph("Date de commande     : " + commande.getDatecommande().format(infoFormatter)));
        document.add(new Paragraph("Montant total        : " + String.format("%.2f TND", commande.getTotal())));
        document.add(new Paragraph(" "));

        // Message de remerciement
        Paragraph merci = new Paragraph("Merci pour votre confiance !", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12));
        merci.setSpacingBefore(20);
        merci.setAlignment(Element.ALIGN_CENTER);
        document.add(merci);

        // Fermeture du document PDF
        document.close();

        // Message indiquant l'emplacement du fichier généré
        System.out.println("Facture générée à : " + filePath);
    }
}
