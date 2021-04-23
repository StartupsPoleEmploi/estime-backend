CREATE SCHEMA estime;

CREATE TABLE estime.suivi_parcours_utilisateur (
    id_suivi_parcours_utilisateur BIGSERIAL,
    id_pole_emploi TEXT,
    nom TEXT,
    prenom TEXT,
    email TEXT,
    suivi_parcours TEXT,
    type_population TEXT,
    date_creation TIMESTAMP,
    PRIMARY KEY (id_suivi_parcours_utilisateur)
);