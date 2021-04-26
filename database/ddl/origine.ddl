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

CREATE ROLE estime WITH ENCRYPTED PASSWORD '<password>';
REVOKE ALL ON SCHEMA public FROM public;
REVOKE ALL ON SCHEMA public FROM estime;
REVOKE ALL ON DATABASE estime_database FROM public;
REVOKE ALL ON DATABASE estime_database FROM estime;
GRANT CONNECT ON DATABASE estime_database TO estime;
GRANT USAGE ON SCHEMA estime TO estime;
GRANT SELECT ON ALL TABLES IN SCHEMA estime TO estime;