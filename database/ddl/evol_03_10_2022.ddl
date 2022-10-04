ALTER TABLE estime.suivi_parcours_utilisateur
ADD idEstime TEXT;

CREATE TABLE estime.autres_situations (
    id_autres_situations BIGSERIAL,
    idEstime TEXT,
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
    PRIMARY KEY (id_autres_situations)
);