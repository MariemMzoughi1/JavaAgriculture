package Utils;

import Entites.Zone;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportCSV {

    public static void exportZonesToCSV(List<Zone> zones) {
        String fileName = "zones.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // Ecrire l'en-tête du fichier CSV
            writer.write("Nom Zone,Superficie (ha),Localisation,Latitude,Longitude\n");

            // Ecrire les données des zones
            for (Zone zone : zones) {
                writer.write(zone.getNom_zone() + "," +
                        zone.getSuperficie_zone() + "," +
                        zone.getLocalisation_zone() + "," );

            }
            System.out.println("Données exportées avec succès dans " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
