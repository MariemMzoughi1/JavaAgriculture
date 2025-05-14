package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {

    // Hachage du mot de passe avec BCrypt ($2a$ → $2y$ pour compatibilité PHP)
    public static String hashPassword(String plainPassword) {
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return hashed.replace("$2a$", "$2y$");
    }

    // Vérification du mot de passe
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        String compatibleHash = hashedPassword.replace("$2y$", "$2a$");
        return BCrypt.checkpw(plainPassword, compatibleHash);
    }
}
