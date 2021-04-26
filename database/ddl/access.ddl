## création d'un role estime ayant simplement les droits de lecture.

CREATE ROLE estime WITH ENCRYPTED PASSWORD '<password>';
REVOKE ALL ON SCHEMA public FROM public;
REVOKE ALL ON SCHEMA public FROM estime;
REVOKE ALL ON DATABASE estime_database FROM public;
REVOKE ALL ON DATABASE estime_database FROM estime;
GRANT CONNECT ON DATABASE estime_database TO estime;
GRANT USAGE ON SCHEMA estime TO estime;
GRANT SELECT ON ALL TABLES IN SCHEMA estime TO estime;