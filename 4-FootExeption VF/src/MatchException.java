package exception;

/**
 * Classe de base de toutes les exceptions du match.
 * Les exceptions spécifiques (saisie vide, seconde invalide, seconde trop grande)
 * héritent de celle-ci, donc un seul "catch (MatchException e)" les attrape toutes.
 */
public class MatchException extends Exception {

    // Constructeur avec message d'erreur
    public MatchException(String message) {
        super(message);
    }

    // Constructeur avec message d'erreur et cause d'origine
    public MatchException(String message, Throwable cause) {
        super(message, cause);
    }
}