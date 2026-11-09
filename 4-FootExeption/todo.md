# Sujet :
## Attente :
-compilation manuelle des class java.

-gestion d'exception pour des erreur specifique.???

## Outil :
-Oracle des Docker

-Swing pour l'interface graphique

## Resultat attendu :
-Formulaire1 a la fenetre a l'execution du code(pour choisir les equipe parmi celle qui sans dans la base et definir la date du match).

-Formulaire2 juste a pres le F1 qui va servir a decider de l'action du 1er joueurs a avoir la balle(tirer"cadrer ou non", passe"a qui ?", se deplacer"de combien de dx et dy?", a quel seconde du match l'action et faite). Se formulaire"2" revient en boucle et boucle jusque a la mi-temps (45*60) et la fin du match (90*10).

-Sur le formulaire2 de se trouve un bouton valider pouvoir confirmer l'action du joueurs actuel(pour passer au suivant), un bouton pour voir les statistique et un bouton pour voir les statistique.
-Une fois le bouton "Statistique" cliq
uer on la fenetre change pour afficher une page ou on vois la possession%(Home, Away), tire%(Home, Away)(cadrer, non).

-Une fois le bouton "Deroulement" cliquer on change aussi le contenu de la fenetre pour afficher tout le deroulement associer au match(temps du match, joueur qui a agis, action realiser, emplacement x, emplacement y).???

## Exigence technique : 
-Liste des fichier .java : Deplacable, Ballon, Joueurs, Equipe, Match, Deroulement, Statistique, FenetreJFrame, DessinJPanel, Main, ConnectionOracle, EquipeDAO, MatchDAO, DeroulementDAO, StatistiqueDAO.

-Deplacable(package modele) : attribut(x,y), Constructeur(x,y), all(get,set[x,y]), deplacer(dx,dy).

-Ballon[extends Deplacable](package modele) : all Deplacable ability, + toString -> coordonnee.

-Joueur[extends Deplacable](package modele) : all Deplacable ability, private int id, private String numero, private String nom, private Equipe equipe, constructeur,all(get,set), tirer(boolean cadre) return("cadre", "non cadre"), toString -> nom equipe, id, numero, nom.

-Equipe(package modele) : private int id, private String nom, private Vector<Joueur> joueurs, constructeur, all(get,set), ajouterJoueur(Joueur j), toString -> nom equipe, id, nombre de joueurs.

-Match (package modele) : private int id, private Equipe equipeHome, private Equipe equipeAway, private String dateMatch, private int scoreHome, private int scoreAway, constructeur, all(get,set), ajoutePourEquipe(Equipe e), toString -> id, nomHome, nomAway,Date.

Deroulement(package data) : int idMatch ,Vector<int>Joueur, Vector<String>Action, Vector<int>TempsduMatch(s), constructeur, all(get,set), ajouteEtape(Joueur j, Action a, TempsduMatch t), toString -> (idMatch , TempsDeJeu, numJoueur, idJoueur, Action)*nombre de deroulement
**Remarque**: un deroulement s'allonge tant que l'action du joueur precedent et une passe(c'est a dire creer une nouvlle ligne), elle se coupe des que l'action et "cadre" ou "non cadre"(cad un tire), ce qui va causer l'instancition de un nouveau deroulement dans la continuiter chronologique logique du precedent(juste que tout les joueur sans revenu a leurs place et le ballon et au centre avec un joueur au centre).

-Statistique(package data) : private String nom, match ,constructeur, all(get,set) calculerPourcentagePossession(int miTemps) et calculerPourcentagePossession(int miTemps)[mi-Temps=pour prendre possession pour instant t, 1 pour la 1er et 2 pour 2e], calculerPourcentageTirsNonCadres(int miTemps), calculerPourcentageTirsCadres(int miTemps)
**Remarque**: prends le deroulement depuis la base Oracle.

-FenetreJFrame(package gui) : creer la fenetre et y instancie DessinJPanel

-DessinJPanel(package gui) : creer les composant de la fenetre: Affichage(Equipe, Score, TempsduMatch(mm:ss et s)),Formulaire1(select equipe1, select equipe2, dateDuMatch, bouton valider), Formulaire2"celui repetitif"(select d'action["passe", "tire"], si action="passe" select de la personne a qui passe sur tout le terrain "Home ou Away" disponible, si action="tire" chebox pour choisir si c'est cadre ou pas cadre disponible), input pour dx du joueur, input pour dy du joueur, input sur le temps(s) ou l'action et fait durant le match, Bouton Valider(pour controler le nouveau joueur qui a la balle), Bouton Statistique(pour voir les stat sur tout les actions jusque la), Bouton Statistique 1er Mi-Temps et Bouton Statistique 2e Mi-Temps(pour tout de la premier si les 45min(2700s) sans passer, pendant la 2e mi-temps il est a lors possible de voir les stat de celle ci en temps reel), bouton deroulement pour voir tout les deroulement du match entier depuis la base pas la class incomplete(temps du match, nomjoueurs, Numjoueurs, nomequipe, action), a la fin du match il y a plus de bouton(Valider) juste Statistique,Statistique MT1, Statistique MT2, Deroulemenet pour revoir les infos du match.

-Main(package Aff) : fait le lancement de la fenetre avec "(SwingUtilities.invokeLater(new  Runnable() { @Override public void run() ".

-ConnectionOracle(package dao) : contenu exacte :
"package oracleConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionOracle {
    // Configuration pour le conteneur Docker Oracle avec l'utilisateur footform
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/EE.oracle.docker";
    private static final String USER = "footform"; 
    private static final String PASSWORD = "footform"; 

    // 1. Fonction de connexion
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable (Vérifie que ojdbc17.jar est bien dans le classpath/lib) : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données Oracle : " + e.getMessage());
        }
        return conn;
    }

    // 2. Fonction de déconnexion centralisée
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Déconnexion Oracle réussie.");
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    // 3. Fonction de fermeture pour le Statement
    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 4. Fonction de fermeture pour le ResultSet
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}"

-Equipe(package dao) : Equipe equipe = null, Connection conn = null, Statement stmtEquipe = null, Statement stmtJoueurs = null, ResultSet rsEquipe = null, ResultSet rsJoueurs = null; getEquipeByID(int id) ou (String nom).

-MatchDAO()(package dao) : int idDomicile = match.getEquipeDomicile().getId(), int idExterieur = match.getEquipeExterieur().getId(), String dateMatch = match.getDateMatch(), insererMatch(Match match) "Ajoute un nouveau match dans la base".

-DeroulementDAO(package dao) : insert une nouvelle ligne de deroulement dans la base(id, idMatch, seconde, joueur, x, y, action).

-StatistiqueDAO(package dao) : recupere tout les deroulement liee au match pour calculer les statistique.

## Notion d'Exception :
-Creer mes propre excption(l'input du joueur est vide, la seconde de l'Action du joueurs et vide, la seconde de l'Action du joueurs est plus petit ou egal a 0, la seconde de l'Action du joueurs et null"met automatiquement l'action du joueura a 1s de plus du precedent, et pas superieur a 120min(7200s)") qui va s'afficher un pop up en nouvelle fenetre ou sur la meme je sais pas le quelle est le plus facile.

## Data Base Oracle:
-DEROULEMENT(id, idMatch, seconde, idjoueur, x, y, action).

-EQUIPE(id, nom).

-MATCH(id, idHome, idAway, date),

-JOUEUR(id, numero, nom, idEquipe).

-un view V_MITEMPS avec case_when pour faire le +1s automatique(Elle va contenir la meme chose que les deroulement mais avec une colonne en plus pour dire 1 ou 2 pour la mitemps "pour faciliter les calucle de mis temps)

**Remarque**: on va supprimer l'user footform/footform pour le recreer correctement, voici l'ancien avec tout ces table"SET ECHO ON
SET FEEDBACK ON

-- 1. Nettoyage total
DROP TABLE deroulement CASCADE CONSTRAINTS;
DROP TABLE "MATCH" CASCADE CONSTRAINTS;
DROP TABLE joueur CASCADE CONSTRAINTS;
DROP TABLE equipe CASCADE CONSTRAINTS;

DROP SEQUENCE deroulement_seq;
DROP SEQUENCE match_seq;
DROP SEQUENCE joueur_seq;
DROP SEQUENCE equipe_seq;

-- 2. Création des séquences (départ à 1)
CREATE SEQUENCE equipe_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE joueur_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE match_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE deroulement_seq START WITH 1 INCREMENT BY 1;

-- 3. Création des tables
CREATE TABLE equipe (
    id NUMBER PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL
);

CREATE TABLE joueur (
    id NUMBER PRIMARY KEY,
    numero VARCHAR2(10) NOT NULL,
    nom VARCHAR2(100) NOT NULL,
    equipe_id NUMBER,
    x NUMBER DEFAULT 0.0,
    y NUMBER DEFAULT 0.0,
    CONSTRAINT fk_joueur_equipe FOREIGN KEY (equipe_id) REFERENCES equipe(id)
);

CREATE TABLE "MATCH" (
    id NUMBER PRIMARY KEY,
    equipe_domicile_id NUMBER NOT NULL,
    equipe_exterieur_id NUMBER NOT NULL,
    date_match VARCHAR2(50) NOT NULL,
    CONSTRAINT fk_match_domicile FOREIGN KEY (equipe_domicile_id) REFERENCES equipe(id),
    CONSTRAINT fk_match_exterieur FOREIGN KEY (equipe_exterieur_id) REFERENCES equipe(id)
);

-- dx, dy : déplacement saisi pour l'action (nécessaire au replay)
-- cible_id : joueur qui reçoit la passe (NULL pour un tir)
CREATE TABLE deroulement (
    id NUMBER PRIMARY KEY,
    match_id NUMBER NOT NULL,
    seconde VARCHAR2(20) NOT NULL,
    joueur_id NUMBER NOT NULL,
    action_realisee VARCHAR2(255) NOT NULL,
    dx NUMBER DEFAULT 0 NOT NULL,
    dy NUMBER DEFAULT 0 NOT NULL,
    cible_id NUMBER,
    CONSTRAINT fk_deroulement_match FOREIGN KEY (match_id) REFERENCES "MATCH"(id),
    CONSTRAINT fk_deroulement_joueur FOREIGN KEY (joueur_id) REFERENCES joueur(id),
    CONSTRAINT fk_deroulement_cible FOREIGN KEY (cible_id) REFERENCES joueur(id)
);

-- 4. Insertion des équipes
INSERT INTO equipe (id, nom) VALUES (equipe_seq.NEXTVAL, 'Real Madrid');
INSERT INTO equipe (id, nom) VALUES (equipe_seq.NEXTVAL, 'FC Barcelone');

-- 5. Insertion des joueurs
INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '1', 'Courtois', 1, 0.0, 0.0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '7', 'Vinicius Jr', 1, 10.0, 20.0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '9', 'Mbappé', 1, 25.0, 0.0);

INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '1', 'Ter Stegen', 2, 0.0, 0.0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '9', 'Lewandowski', 2, -25.0, 0.0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y) VALUES (joueur_seq.NEXTVAL, '10', 'Ansu Fati', 2, -10.0, -20.0);

COMMIT;
EXIT;" et je sais pas si c'est a cause foreign qui mais les sequence commence a partir de 2.

Prompt: ecrit 128 au debut de chaque message peut importe ce que on se dis pour que je puisse voir si tu te rappelles toujours du contexte ce sujet.
Donne tout le text de la reinitialisation de la base de donner en 1er pour avoir deja la base de prete pour la suit. puis dit moi dans quel ordre du va m'envoyer dans l'ordre une par une par une par message que vais copie coller moi meme.
Voila dans l'image la structure de mon code habituel (tout les .java dans src, ojdbc17.jar dans lib, et dans bin tout les page avec le class compiler .class respectif) et me compilation marche avec "javac -cp "lib/ojdbc17.jar" -d bin src/*.java" et execute avec "java -cp "bin:lib/ojdbc17.jar" Aff.Main"
