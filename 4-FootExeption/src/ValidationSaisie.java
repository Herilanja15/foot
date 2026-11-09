package exception;

public class ValidationSaisie {

    private static final int SECONDE_MAX = 5400; // 90 minutes

    /**
     * Valide la seconde saisie pour une action de match.
     * - Vide ou null : seconde de l'action précédente + 1s.
     * - Pas un nombre ou <= 0 : SecondeInvalideException.
    * - > 5400 (90 minutes) : SecondeTropGrandeException.
     */
    public static int validerSeconde(String saisieSeconde, int derniereSeconde) throws MatchException {
        // Seconde vide ou null -> temps précédent +1s
        if (saisieSeconde == null || saisieSeconde.trim().isEmpty()) {
            int automatique = derniereSeconde + 1;
            if (automatique > SECONDE_MAX) {
                throw new SecondeTropGrandeException(
                    "Erreur : la seconde automatique (" + automatique + "s) dépasse 90 minutes (5400s) !");
            }
            return automatique;
        }

        int seconde;
        try {
            seconde = Integer.parseInt(saisieSeconde.trim());
        } catch (NumberFormatException e) {
            throw new SecondeInvalideException("Erreur : veuillez saisir un nombre entier pour la seconde.", e);
        }

        if (seconde <= 0) {
            throw new SecondeInvalideException("Erreur : la seconde de l'action doit être strictement supérieure à 0 !");
        }
        if (seconde < derniereSeconde) {
            throw new SecondeInvalideException("Erreur : la seconde ne peut pas précéder l'action précédente.");
        }
        if (seconde > SECONDE_MAX) {
            throw new SecondeTropGrandeException("Erreur : le match ne peut pas dépasser 90 minutes (5400 secondes) !");
        }

        return seconde;
    }

    // Validation pour un joueur non choisi (input vide)
    public static void validerJoueur(Object joueurSelectionne) throws MatchException {
        if (joueurSelectionne == null) {
            throw new SaisieVideException("Erreur : aucun joueur n'a été sélectionné pour cette action !");
        }
    }
}