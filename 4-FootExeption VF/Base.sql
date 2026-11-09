SET ECHO ON
SET FEEDBACK ON

-- 1. Suppression (chaque bloc ignore l'erreur si l'objet n'existe pas)
BEGIN EXECUTE IMMEDIATE 'DROP VIEW V_MITEMPS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE deroulement CASCADE CONSTRAINTS PURGE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE "MATCH" CASCADE CONSTRAINTS PURGE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE joueur CASCADE CONSTRAINTS PURGE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE equipe CASCADE CONSTRAINTS PURGE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE deroulement_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE match_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE joueur_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE equipe_seq'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- 2. Séquences (toutes à 1)
CREATE SEQUENCE equipe_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE joueur_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE match_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE deroulement_seq START WITH 1 INCREMENT BY 1;

-- 3. Tables
CREATE TABLE equipe (
    id  NUMBER PRIMARY KEY,
    nom VARCHAR2(100) NOT NULL
);

CREATE TABLE joueur (
    id        NUMBER PRIMARY KEY,
    numero    VARCHAR2(10)  NOT NULL,
    nom       VARCHAR2(100) NOT NULL,
    equipe_id NUMBER NOT NULL,
    x         NUMBER DEFAULT 0 NOT NULL,
    y         NUMBER DEFAULT 0 NOT NULL,
    CONSTRAINT fk_joueur_equipe FOREIGN KEY (equipe_id) REFERENCES equipe(id)
);

CREATE TABLE "MATCH" (
    id                  NUMBER PRIMARY KEY,
    equipe_domicile_id  NUMBER NOT NULL,
    equipe_exterieur_id NUMBER NOT NULL,
    date_match          VARCHAR2(50) NOT NULL,
    score_home          NUMBER DEFAULT 0 NOT NULL,
    score_away          NUMBER DEFAULT 0 NOT NULL,
    CONSTRAINT fk_match_domicile  FOREIGN KEY (equipe_domicile_id)  REFERENCES equipe(id),
    CONSTRAINT fk_match_exterieur FOREIGN KEY (equipe_exterieur_id) REFERENCES equipe(id),
    CONSTRAINT ck_match_equipes   CHECK (equipe_domicile_id <> equipe_exterieur_id)
);

-- action_realisee : 'passe', 'cadre', 'non cadre' ou 'but'
-- cible_id : joueur qui reçoit la passe (NULL pour un tir)
CREATE TABLE deroulement (
    id              NUMBER PRIMARY KEY,
    match_id        NUMBER NOT NULL,
    seconde         NUMBER(5) NOT NULL,
    joueur_id       NUMBER NOT NULL,
    action_realisee VARCHAR2(255) NOT NULL,
    dx              NUMBER DEFAULT 0 NOT NULL,
    dy              NUMBER DEFAULT 0 NOT NULL,
    cible_id        NUMBER,
    CONSTRAINT fk_deroulement_match   FOREIGN KEY (match_id)  REFERENCES "MATCH"(id),
    CONSTRAINT fk_deroulement_joueur  FOREIGN KEY (joueur_id) REFERENCES joueur(id),
    CONSTRAINT fk_deroulement_cible   FOREIGN KEY (cible_id)  REFERENCES joueur(id),
    CONSTRAINT ck_deroulement_seconde CHECK (seconde > 0 AND seconde <= 5400),
    CONSTRAINT ck_deroulement_action CHECK (action_realisee IN ('passe', 'cadre', 'non cadre', 'but')),
    CONSTRAINT ck_deroulement_cible_action CHECK (
        (action_realisee = 'passe' AND cible_id IS NOT NULL)
        OR (action_realisee IN ('cadre', 'non cadre', 'but') AND cible_id IS NULL)
    )
);

-- 4. Vue : mi-temps (1 si <= 2700 s, sinon 2) + durée depuis l'action précédente
--    duree = temps pendant lequel le joueur a eu le ballon avant d'agir
CREATE OR REPLACE VIEW V_MITEMPS AS
SELECT
    d.id, d.match_id, d.seconde, d.joueur_id, d.action_realisee, d.dx, d.dy, d.cible_id,
    CASE WHEN d.seconde <= 2700 THEN 1 ELSE 2 END AS mitemps,
    CASE
        WHEN EXISTS (SELECT 1 FROM deroulement p
                     WHERE p.match_id = d.match_id
                       AND (p.seconde < d.seconde OR (p.seconde = d.seconde AND p.id < d.id)))
        THEN d.seconde - (SELECT MAX(p.seconde) FROM deroulement p
                          WHERE p.match_id = d.match_id
                            AND (p.seconde < d.seconde OR (p.seconde = d.seconde AND p.id < d.id)))
        ELSE d.seconde
    END AS duree
FROM deroulement d;

-- 5. Données de départ (ids donnés par les séquences : 1, 2, 3...)
INSERT INTO equipe (id, nom) VALUES (equipe_seq.NEXTVAL, 'Real Madrid');
INSERT INTO equipe (id, nom) VALUES (equipe_seq.NEXTVAL, 'FC Barcelone');

INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '1',  'Courtois',    (SELECT id FROM equipe WHERE nom = 'Real Madrid'),   0,   0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '7',  'Vinicius Jr', (SELECT id FROM equipe WHERE nom = 'Real Madrid'),  10,  20);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '9',  'Mbappé',      (SELECT id FROM equipe WHERE nom = 'Real Madrid'),  25,   0);

INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '1',  'Ter Stegen',  (SELECT id FROM equipe WHERE nom = 'FC Barcelone'),   0,   0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '9',  'Lewandowski', (SELECT id FROM equipe WHERE nom = 'FC Barcelone'), -25,   0);
INSERT INTO joueur (id, numero, nom, equipe_id, x, y)
VALUES (joueur_seq.NEXTVAL, '10', 'Ansu Fati',   (SELECT id FROM equipe WHERE nom = 'FC Barcelone'), -10, -20);

COMMIT;
EXIT;
