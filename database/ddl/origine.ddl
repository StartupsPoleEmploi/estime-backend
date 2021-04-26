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

create role estime with encrypted password '<password>';
revoke all on schema public from public;
revoke all on schema public from estime;
revoke all on database estime_database from public;
revoke all on database estime_database from estime;
grant connect on database estime_database to estime;
grant usage on schema estime TO estime;
grant select on all tables in schema estime to estime;