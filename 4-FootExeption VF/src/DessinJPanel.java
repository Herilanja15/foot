package gui;

import dao.DeroulementDAO;
import dao.EquipeDAO;
import dao.MatchDAO;
import data.Deroulement;
import data.Statistique;
import exception.MatchException;
import exception.ValidationSaisie;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import modele.Ballon;
import modele.Equipe;
import modele.Joueur;
import modele.Match;

public class DessinJPanel extends JPanel {

    private static final int DEMI_TEMPS = 2700;  // 45 * 60
    private static final int FIN_MATCH = 7200;   // 90 * 60

    private CardLayout cardLayout;
    private JPanel mainContainer;

    // ===== État du match =====
    private Match matchActuel;
    private Equipe equipeHome;
    private Equipe equipeAway;
    private Vector<Joueur> tousLesJoueurs;
    private Map<Integer, double[]> positionsInitiales;
    private Joueur joueurCourant;
    private Ballon ballon;
    private Deroulement deroulementCourant;
    private int numeroDeroulement;
    private int tempsMatch;
    private boolean matchTermine;

    // ===== Affichage (haut de la fenêtre) =====
    private JPanel panelAffichage;
    private JLabel lblScore;
    private JLabel lblTemps;

    // ===== Formulaire 1 =====
    private JComboBox<Equipe> cmbEquipeHome;
    private JComboBox<Equipe> cmbEquipeAway;
    private JTextField txtDateMatch;

    // ===== Formulaire 2 =====
    private JLabel lblJoueurCourant;
    private JLabel lblBallon;
    private JLabel lblDeroulement;
    private JLabel lblInfoSeconde;
    private JComboBox<String> cmbAction;
    private JPanel panelSpecifique;
    private CardLayout cardSpecifique;
    private JComboBox<Joueur> cmbCible;
    private JCheckBox chkCadre;
    private JTextField txtDx;
    private JTextField txtDy;
    private JTextField txtSeconde;
    private JButton btnValider;
    private JButton btnStat;
    private JButton btnStatMt1;
    private JButton btnStatMt2;
    private JButton btnDeroulement;

    // ===== Page Statistiques =====
    private JLabel lblStatsTitre;
    private JLabel lblStatsPossession;
    private JLabel lblStatsTirsCadres;
    private JLabel lblStatsTirsNonCadres;

    // ===== Page Déroulement =====
    private DefaultTableModel modeleDeroulement;

    public DessinJPanel() {
        this.setLayout(new BorderLayout());

        this.add(creerAffichage(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.add(creerFormulaire1(), "FORM_1");
        mainContainer.add(creerFormulaire2(), "FORM_2");
        mainContainer.add(creerStatistiques(), "STATS");
        mainContainer.add(creerChronologie(), "CHRONO");
        this.add(mainContainer, BorderLayout.CENTER);

        cardLayout.show(mainContainer, "FORM_1");
    }

    // ==========================================
    // AFFICHAGE : équipes, score, temps du match
    // ==========================================
    private JPanel creerAffichage() {
        panelAffichage = new JPanel(new GridLayout(2, 1));
        panelAffichage.setBorder(BorderFactory.createTitledBorder("Affichage"));

        lblScore = new JLabel("", SwingConstants.CENTER);
        lblScore.setFont(lblScore.getFont().deriveFont(Font.BOLD, 18f));
        lblTemps = new JLabel("", SwingConstants.CENTER);

        panelAffichage.add(lblScore);
        panelAffichage.add(lblTemps);
        panelAffichage.setVisible(false); // visible une fois le match créé
        return panelAffichage;
    }

    private void majAffichage() {
        if (matchActuel == null) return;
        lblScore.setText(matchActuel.getEquipeHome().getNom() + "   "
                + matchActuel.getScoreHome() + " - " + matchActuel.getScoreAway()
                + "   " + matchActuel.getEquipeAway().getNom());
        lblTemps.setText("Temps du match : " + formatTemps(tempsMatch) + "  (" + tempsMatch + " s)"
                + (matchTermine ? "  -  MATCH TERMINÉ" : ""));
    }

    // ==========================================
    // 1. FORMULAIRE 1 : choix des équipes + date
    // ==========================================
    private JPanel creerFormulaire1() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire 1 : Initialisation du Match"));

        Vector<Equipe> equipes = EquipeDAO.getToutesLesEquipes();

        cmbEquipeHome = new JComboBox<>(equipes);
        cmbEquipeAway = new JComboBox<>(new Vector<>(equipes));
        cmbEquipeHome.setRenderer(rendererEquipe());
        cmbEquipeAway.setRenderer(rendererEquipe());
        if (equipes.size() > 1) {
            cmbEquipeAway.setSelectedIndex(1);
        }

        txtDateMatch = new JTextField(LocalDate.now().toString());

        panel.add(new JLabel("Équipe Home :"));
        panel.add(cmbEquipeHome);
        panel.add(new JLabel("Équipe Away :"));
        panel.add(cmbEquipeAway);
        panel.add(new JLabel("Date du match (AAAA-MM-JJ) :"));
        panel.add(txtDateMatch);

        JButton btnValiderMatch = new JButton("Valider");
        btnValiderMatch.addActionListener(e -> demarrerMatch());
        panel.add(new JLabel(""));
        panel.add(btnValiderMatch);

        return panel;
    }

    private void demarrerMatch() {
        Equipe selHome = (Equipe) cmbEquipeHome.getSelectedItem();
        Equipe selAway = (Equipe) cmbEquipeAway.getSelectedItem();

        if (selHome == null || selAway == null) {
            erreur("Aucune équipe disponible dans la base.");
            return;
        }
        if (selHome.getId() == selAway.getId()) {
            erreur("Choisis deux équipes différentes.");
            return;
        }

        String date = txtDateMatch.getText().trim();
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            erreur("Date invalide (format attendu : AAAA-MM-JJ).");
            return;
        }

        // Équipes complètes, avec leurs joueurs
        Equipe home = EquipeDAO.getEquipeByID(selHome.getId());
        Equipe away = EquipeDAO.getEquipeByID(selAway.getId());
        if (home == null || away == null || home.getJoueurs().isEmpty() || away.getJoueurs().isEmpty()) {
            erreur("Chaque équipe doit avoir au moins un joueur en base.");
            return;
        }

        Match match = new Match(0, home, away, date, 0, 0);
        int idMatch = MatchDAO.insererMatch(match);
        if (idMatch <= 0) {
            erreur("Erreur lors de la création du match en base.");
            return;
        }

        initialiserMatch(match, home, away);
        cardLayout.show(mainContainer, "FORM_2");
    }

    private void initialiserMatch(Match match, Equipe home, Equipe away) {
        matchActuel = match;
        equipeHome = home;
        equipeAway = away;

        tousLesJoueurs = new Vector<>();
        tousLesJoueurs.addAll(home.getJoueurs());
        tousLesJoueurs.addAll(away.getJoueurs());

        // Positions de départ, pour que tout le monde revienne à sa place après un tir
        positionsInitiales = new HashMap<>();
        for (Joueur j : tousLesJoueurs) {
            positionsInitiales.put(j.getId(), new double[] { j.getX(), j.getY() });
        }

        ballon = new Ballon(0, 0);
        tempsMatch = 0;
        matchTermine = false;
        numeroDeroulement = 1;
        deroulementCourant = new Deroulement(match.getId());
        remettreEnPlace();

        txtSeconde.setText("");
        txtDx.setText("0");
        txtDy.setText("0");
        chkCadre.setSelected(false);
        cmbAction.setSelectedIndex(0);

        panelAffichage.setVisible(true);
        majAffichage();
        majFormulaire2();
    }

    // Tous les joueurs reviennent à leur place, ballon au centre avec un joueur Home au centre
    private void remettreEnPlace() {
        for (Joueur j : tousLesJoueurs) {
            double[] pos = positionsInitiales.get(j.getId());
            j.setX(pos[0]);
            j.setY(pos[1]);
        }
        joueurCourant = equipeHome.getJoueurs().get(0);
        joueurCourant.setX(0);
        joueurCourant.setY(0);
        ballon.setX(0);
        ballon.setY(0);
    }

    // ==========================================
    // 2. FORMULAIRE 2 : action du joueur qui a la balle
    // ==========================================
    private JPanel creerFormulaire2() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Formulaire 2 : Action du joueur qui a la balle"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblJoueurCourant = new JLabel("");
        lblBallon = new JLabel("");
        lblDeroulement = new JLabel("");
        lblInfoSeconde = new JLabel("");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(lblJoueurCourant, gbc);
        gbc.gridy = 1; panel.add(lblBallon, gbc);
        gbc.gridy = 2; panel.add(lblDeroulement, gbc);
        gbc.gridwidth = 1;

        // Action : passe ou tire
        cmbAction = new JComboBox<>(new String[] { "passe", "tire" });
        gbc.gridy = 3; gbc.gridx = 0; panel.add(new JLabel("Action :"), gbc);
        gbc.gridx = 1; panel.add(cmbAction, gbc);

        // Zone qui change selon l'action : cible de la passe OU case "cadré"
        cardSpecifique = new CardLayout();
        panelSpecifique = new JPanel(cardSpecifique);

        cmbCible = new JComboBox<>();
        cmbCible.setRenderer(rendererJoueur());
        JPanel panelPasse = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelPasse.add(new JLabel("Passer à :"));
        panelPasse.add(cmbCible);

        chkCadre = new JCheckBox("Tir cadré");
        JPanel panelTire = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTire.add(chkCadre);

        panelSpecifique.add(panelPasse, "passe");
        panelSpecifique.add(panelTire, "tire");

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(panelSpecifique, gbc);
        gbc.gridwidth = 1;

        cmbAction.addActionListener(e -> cardSpecifique.show(panelSpecifique, (String) cmbAction.getSelectedItem()));

        // Déplacement et seconde
        txtDx = new JTextField("0", 15);
        txtDy = new JTextField("0", 15);
        txtSeconde = new JTextField(15);

        gbc.gridy = 5; gbc.gridx = 0; panel.add(new JLabel("dx du joueur :"), gbc);
        gbc.gridx = 1; panel.add(txtDx, gbc);
        gbc.gridy = 6; gbc.gridx = 0; panel.add(new JLabel("dy du joueur :"), gbc);
        gbc.gridx = 1; panel.add(txtDy, gbc);
        gbc.gridy = 7; gbc.gridx = 0; panel.add(new JLabel("Seconde de l'action (vide = précédente +1s) :"), gbc);
        gbc.gridx = 1; panel.add(txtSeconde, gbc);
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(lblInfoSeconde, gbc);

        // Boutons
        btnValider = new JButton("Valider");
        btnStat = new JButton("Statistique");
        btnStatMt1 = new JButton("Statistique MT1");
        btnStatMt2 = new JButton("Statistique MT2");
        btnDeroulement = new JButton("Déroulement");

        JPanel panelBoutons = new JPanel(new FlowLayout());
        panelBoutons.add(btnValider);
        panelBoutons.add(btnStat);
        panelBoutons.add(btnStatMt1);
        panelBoutons.add(btnStatMt2);
        panelBoutons.add(btnDeroulement);

        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(panelBoutons, gbc);

        btnValider.addActionListener(e -> validerAction());
        btnStat.addActionListener(e -> afficherStatistiques(0, "Statistiques du match (jusqu'à maintenant)"));
        btnStatMt1.addActionListener(e -> afficherStatistiques(1, "Statistiques de la 1ère mi-temps"));
        btnStatMt2.addActionListener(e -> afficherStatistiques(2, "Statistiques de la 2e mi-temps"));
        btnDeroulement.addActionListener(e -> {
            if (matchActuel != null) {
                rafraichirChronologie();
                cardLayout.show(mainContainer, "CHRONO");
            }
        });

        return panel;
    }

    // Met à jour tout ce qui dépend de l'état du match dans le formulaire 2
    private void majFormulaire2() {
        if (matchActuel == null) return;

        lblJoueurCourant.setText("Joueur qui a la balle : " + libelleJoueur(joueurCourant));
        lblBallon.setText(ballon.toString());
        lblDeroulement.setText("Déroulement n°" + numeroDeroulement
                + " - étapes enregistrées : " + deroulementCourant.getActions().size());
        lblInfoSeconde.setText("Dernière seconde enregistrée : "
                + DeroulementDAO.tempsJoueursPrecedent(matchActuel.getId()) + " s");

        // Cibles possibles d'une passe : tous les joueurs Home et Away sauf celui qui a la balle
        DefaultComboBoxModel<Joueur> modeleCible = new DefaultComboBoxModel<>();
        for (Joueur j : tousLesJoueurs) {
            if (j.getId() != joueurCourant.getId()) {
                modeleCible.addElement(j);
            }
        }
        cmbCible.setModel(modeleCible);

        // Fin du match : plus de bouton Valider, il ne reste que la consultation
        boolean actif = !matchTermine;
        btnValider.setVisible(actif);
        cmbAction.setEnabled(actif);
        cmbCible.setEnabled(actif);
        chkCadre.setEnabled(actif);
        txtDx.setEnabled(actif);
        txtDy.setEnabled(actif);
        txtSeconde.setEnabled(actif);

        // Stats par mi-temps : disponibles une fois la 2e mi-temps commencée
        boolean deuxiemeMiTemps = tempsMatch > DEMI_TEMPS;
        btnStatMt1.setEnabled(deuxiemeMiTemps);
        btnStatMt2.setEnabled(deuxiemeMiTemps);
    }

    private void validerAction() {
        if (matchActuel == null || matchTermine) return;

        // 1. Seconde (vide -> seconde précédente +1s) et cible
        int seconde;
        boolean estPasse = "passe".equals(cmbAction.getSelectedItem());
        Joueur cible = null;
        try {
            int derniere = DeroulementDAO.tempsJoueursPrecedent(matchActuel.getId());
            seconde = ValidationSaisie.validerSeconde(txtSeconde.getText(), derniere);

            if (estPasse) {
                cible = (Joueur) cmbCible.getSelectedItem();
                ValidationSaisie.validerJoueur(cible);
            }
        } catch (MatchException ex) {
            erreur(ex.getMessage());
            return;
        }

        // 2. dx et dy
        double dx;
        double dy;
        try {
            dx = Double.parseDouble(txtDx.getText().trim().replace(',', '.'));
            dy = Double.parseDouble(txtDy.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            erreur("dx et dy doivent être des nombres.");
            return;
        }

        // 3. Action à enregistrer : "passe", "cadre" ou "non cadre"
        boolean cadre = chkCadre.isSelected();
        String action = estPasse ? "passe" : joueurCourant.tirer(cadre);
        Integer cibleId = estPasse ? Integer.valueOf(cible.getId()) : null;

        // 4. Enregistrement en base
        boolean ok = DeroulementDAO.insererDeroulement(matchActuel.getId(), seconde, joueurCourant, action, dx, dy, cibleId);
        if (!ok) {
            erreur("Erreur lors de l'enregistrement de l'action en base.");
            return;
        }

        // 5. Mise à jour du modèle en mémoire
        deroulementCourant.ajouteEtape(joueurCourant, action, seconde);
        joueurCourant.deplacer(dx, dy);
        ballon.setX(joueurCourant.getX());
        ballon.setY(joueurCourant.getY());
        tempsMatch = seconde;

        if (estPasse) {
            // Le ballon va chez le joueur ciblé, qui devient le joueur courant
            joueurCourant = cible;
            ballon.setX(cible.getX());
            ballon.setY(cible.getY());
        } else {
            // Tir : un tir cadré est un but, puis nouveau déroulement et remise en place
            if (cadre) {
                matchActuel.ajoutePourEquipe(joueurCourant.getEquipe());
            }
            numeroDeroulement++;
            deroulementCourant = new Deroulement(matchActuel.getId());
            remettreEnPlace();
        }

        // 6. Fin du match ?
        if (seconde >= FIN_MATCH) {
            matchTermine = true;
        }

        txtSeconde.setText("");
        txtDx.setText("0");
        txtDy.setText("0");
        chkCadre.setSelected(false);

        majAffichage();
        majFormulaire2();

        if (matchTermine) {
            JOptionPane.showMessageDialog(this, "Fin du match ! Score final : "
                    + matchActuel.getScoreHome() + " - " + matchActuel.getScoreAway());
        }
    }

    // ==========================================
    // 3. PAGE STATISTIQUES
    // ==========================================
    private JPanel creerStatistiques() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Page des Statistiques"));

        lblStatsTitre = new JLabel("", SwingConstants.CENTER);
        lblStatsTitre.setFont(lblStatsTitre.getFont().deriveFont(Font.BOLD, 16f));
        lblStatsPossession = new JLabel("", SwingConstants.CENTER);
        lblStatsTirsCadres = new JLabel("", SwingConstants.CENTER);
        lblStatsTirsNonCadres = new JLabel("", SwingConstants.CENTER);

        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> cardLayout.show(mainContainer, "FORM_2"));

        panel.add(lblStatsTitre);
        panel.add(lblStatsPossession);
        panel.add(lblStatsTirsCadres);
        panel.add(lblStatsTirsNonCadres);
        panel.add(btnRetour);

        return panel;
    }

    // miTemps : 0 = tout le match, 1 = 1ère mi-temps, 2 = 2e mi-temps
    private void afficherStatistiques(int miTemps, String titre) {
        if (matchActuel == null) return;

        Statistique stats = new Statistique("StatsMatch", matchActuel.getId());

        double[] possession = stats.calculerPourcentagePossession(miTemps);
        double[] tirsCadres = stats.calculerPourcentageTirsCadres(miTemps);
        double[] tirsNonCadres = stats.calculerPourcentageTirsNonCadres(miTemps);

        String home = equipeHome.getNom();
        String away = equipeAway.getNom();

        lblStatsTitre.setText(titre);
        lblStatsPossession.setText(String.format("Possession : %s %.1f %%   -   %s %.1f %%",
                home, possession[0], away, possession[1]));
        lblStatsTirsCadres.setText(String.format("Tirs cadrés : %s %.1f %%   -   %s %.1f %%",
                home, tirsCadres[0], away, tirsCadres[1]));
        lblStatsTirsNonCadres.setText(String.format("Tirs non cadrés : %s %.1f %%   -   %s %.1f %%",
                home, tirsNonCadres[0], away, tirsNonCadres[1]));

        cardLayout.show(mainContainer, "STATS");
    }

    // ==========================================
    // 4. PAGE DÉROULEMENT : tout le match depuis la base
    // ==========================================
    private JPanel creerChronologie() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Déroulement du match"));

        modeleDeroulement = new DefaultTableModel(
                new String[] { "Temps du match", "Joueur", "Numéro", "Équipe", "Action" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeleDeroulement);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnRetour = new JButton("Retour");
        btnRetour.addActionListener(e -> cardLayout.show(mainContainer, "FORM_2"));
        panel.add(btnRetour, BorderLayout.SOUTH);

        return panel;
    }

    private void rafraichirChronologie() {
        if (matchActuel == null) return;
        modeleDeroulement.setRowCount(0);
        for (String[] ligne : DeroulementDAO.listerParMatch(matchActuel.getId())) {
            int s = Integer.parseInt(ligne[0]);
            modeleDeroulement.addRow(new Object[] {
                formatTemps(s) + " (" + s + " s)", ligne[1], ligne[2], ligne[3], ligne[4]
            });
        }
    }

    // ==========================================
    // Outils
    // ==========================================
    private void erreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static String formatTemps(int secondes) {
        return String.format("%02d:%02d", secondes / 60, secondes % 60);
    }

    private static String libelleJoueur(Joueur j) {
        String nomEquipe = (j.getEquipe() != null) ? j.getEquipe().getNom() : "Sans équipe";
        return "#" + j.getNumero() + " " + j.getNom() + " (" + nomEquipe + ")";
    }

    // Affiche seulement le nom de l'équipe dans les listes déroulantes
    private DefaultListCellRenderer rendererEquipe() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Equipe) {
                    setText(((Equipe) value).getNom());
                }
                return this;
            }
        };
    }

    // Affiche "#numéro Nom (Équipe)" dans la liste des cibles de passe
    private DefaultListCellRenderer rendererJoueur() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Joueur) {
                    setText(libelleJoueur((Joueur) value));
                }
                return this;
            }
        };
    }
}