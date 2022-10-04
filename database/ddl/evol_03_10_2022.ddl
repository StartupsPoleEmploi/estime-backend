ALTER TABLE estime.suivi_parcours_utilisateur
ADD id_estime TEXT;

CREATE TABLE estime.autres_situations (
    id_estime TEXT,
    salaire BOOLEAN,
    alternant BOOLEAN,
    formation BOOLEAN,
    cej BOOLEAN,
    ada BOOLEAN,
    securisation_professionnelle BOOLEAN,
    aucune_ressource BOOLEAN,
    autre BOOLEAN,
    autre_contenu TEXT,
    date_creation TIMESTAMP,
    PRIMARY KEY (id_estime)
);