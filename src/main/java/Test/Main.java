import Entites.Commentaire;
import Services.CommentaireService;

import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        CommentaireService commentaireService = new CommentaireService();

        // ✅ 1. Ajouter un commentaire
        Commentaire commentaireAjout = new Commentaire();
        commentaireAjout.setPostId(1); // Assurez-vous qu'un post avec id=1 existe
        commentaireAjout.setContenu("Super post, merci pour le partage !");
        commentaireAjout.setAuteurId(null); // Peut être null ou une valeur int
        commentaireAjout.setDate(new Date());
        commentaireService.add(commentaireAjout);

        // ✅ 2. Afficher tous les commentaires
        System.out.println("\n📋 Liste des commentaires :");
        List<Commentaire> commentaires = commentaireService.find();
        for (Commentaire c : commentaires) {
            System.out.println(c);
        }

        // ✅ 3. Mettre à jour un commentaire (on prend le premier dans la liste)
        if (!commentaires.isEmpty()) {
            Commentaire commentaireUpdate = commentaires.get(0);
            commentaireUpdate.setContenu("📝 Contenu mis à jour !");
            commentaireService.update(commentaireUpdate);
        }

        // ✅ 4. Supprimer un commentaire (par exemple le dernier dans la liste)
        if (!commentaires.isEmpty()) {
            Commentaire commentaireDelete = commentaires.get(commentaires.size() - 1);
            commentaireService.delete(commentaireDelete);
        }

        // ✅ 5. Afficher à nouveau la liste après modifications
        System.out.println("\n📋 Liste après modification :");
        List<Commentaire> commentairesMaj = commentaireService.find();
        for (Commentaire c : commentairesMaj) {
            System.out.println(c);
        }
    }
}
