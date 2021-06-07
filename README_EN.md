![logo Estime](.gitlab/images/logo_estime_v2.png)

:fr: [French version](https://github.com/StartupsPoleEmploi/estime-backend)

# [Pôle emploi Startup] Estime

This project is realized within the scope of the realization of [the Estime application](https://github.com/StartupsPoleEmploi/estime-frontend).

This project is an application which exposes REST services to simulate [Pôle emploi](https://en.wikipedia.org/wiki/P%C3%B4le_emploi) unemployment benefits (ASS, AGEPI, mobility helps) and [CAF](https://en.wikipedia.org/wiki/Caisse_d%27allocations_familiales) family benefits (prime activité, RSA) for a period of 6 months in back to work context.


:closed_lock_with_key: The access to these services is secured by an authentification with [Pôle emploi Connect](https://www.emploi-store-dev.fr/portail-developpeur/detailapicatalogue/-se-connecter-avec-p-le-emploi-v1?id=58d00957243a5f7809e17698).

# [Source Code] Some explanantions

This project is realized in [Java](https://docs.oracle.com/en/java/) with [Springboot framework](https://spring.io/projects/spring-boot).

This project has been generated with [Spring initializr](https://start.spring.io/) and use [Maven](https://maven.apache.org/) to manage its external dependencies.

##  Source code structuration

- **src/main/java** :
    - **fr.poleemploi.estime.configuration** : Springboot configuration classes (Cors, Security, etc.)
    - **fr.poleemploi.estime.services** : REST service classes  
    - **fr.poleemploi.estime.logique** : business logic classes
    - **fr.poleemploi.estime.clientsexternes** : external REST client classes (Emploi Store Dev, OpenFisca, etc.)
    - **fr.poleemploi.estime.commun** : common classes (enum, etc.)
- **src/main/resources** : configuration files (Springboot, Log4J, etc.)
- **src/test/java** : test classes 
- **src/test/resources** : test configuration
- **docker :** Docker configuration files

# [Local development] Launch the application on localhost

## Prerequisites

- Install Java with **11.x.x minimum version** version
- Install Maven with **3.6.x minimum version** 

:wrench:  Easily install these tools with [**SDKMAN**](https://sdkman.io/install). For Windows, [see sdkman-on-windows](https://medium.com/@gayanper/sdkman-on-windows-661976238042).

Install Java :

```
foo@bar:~$ sdk install java 11.0.5.hs-adpt
```

Install Maven :

```
foo@bar:~$ sdk install maven 3.6.3
```

Check your installation :

```console
foo@bar:~$ java -version
foo@bar:~$ mvn -v
```

## Steps to follow

1. Clone the project

1. Open it with **Spring Tool Suite**

    :thumbsup: [Spring Tool Suite ](https://spring.io/tools) is a free IDE based on Eclipse.

    **SpringToolSuite4.ini** file, replace **%% à renseigner %%** by your JDK path (ex : /home/user/.sdkman/candidates/java/current/bin).

    ```
    -startup
    plugins/org.eclipse.equinox.launcher_1.5.700.v20200207-2156.jar
    --launcher.library
    plugins/org.eclipse.equinox.launcher.gtk.linux.x86_64_1.1.1200.v20200508-1552
    -product
    org.springframework.boot.ide.branding.sts4
    --launcher.defaultAction
    openFile
    -vm
    %% à renseigner %%
    ```

    **Steps to follow to launch the application with Spring Tool Suite :**

    1. Create a workspace dedicated to the estime-backend project and launch Spring Tool Suite
    1. Import the prefences file **./springboot-tool-preferences/preferences_eclipse.epf** in **File -> Import -> General -> Preferences**
    1. Select **Java Custom** perspective in **Window -> Perspective -> Open Perspective -> Other**
    1. Configure Java 11 as **Execution Environment** in **Window -> Preferences -> Java -> Installed JREs**

        ![Springboot Tool Suite Java](.gitlab/images/springboot-tool-suite-java.png)

    1. Configure Maven in **Window -> Preferences -> Maven -> Installation** to indicate 3.6.x version

    1. Import the **estime-backend**  Maven project in  **File -> Import -> Maven -> Existing Maven Project**
    1. Install dependencies, right click via click droit on **estime-backend** and **Run As -> Maven install**
    1. Create configuration file named **application.yml** in **src/main/resources**

        - Copy the next lines and replace all variables **%% à renseigner %%**
        - For the **openfisca-api-uri** parameter, see [Application OpenFisca](#openfisca-france) section.
        <br />
    
        **application.yml file :**

        ```yaml
        server:
          servlet:
            contextPath: "/estime/v1"
        spring:
          application:
            name: "estime-backend"
          profiles:
            active: "localhost"
          security:
            oauth2:
              client:
                provider:
                  oauth-pole-emploi:
                    token-uri: "%% à renseigner %%"
                    user-info-uri: "%% à renseigner %%"
                registration:
                  estime:
                    authorization-grant-type: "authorization_code"
                    client-id: "%% à renseigner %%"
                    client-secret: "%% à renseigner %%"
                    provider: "oauth-pole-emploi"
              resourceserver:
                jwt:
                  jwk-set-uri: "%% à renseigner %%"
                  issuer-uri: "%% à renseigner %%"
          jpa:
            hibernate:
              ddl-auto: none
          datasource:
            url: %% à renseigner %%
            username: %% à renseigner %%
            password: %% à renseigner %%
            driver-class-name: %% à renseigner %%
        management:
          endpoint:
            health:
              show-details: "ALWAYS"
          endpoints:
            web:
              exposure:
                include: "*"
        emploi-store-dev:
          coordonnees-api-uri : "%% à renseigner %%"
          date-naissance-api-uri : "%% à renseigner %%"
          detail-indemnisation-api-uri : "%% à renseigner %%"
        openfisca-api-uri: "%% à renseigner %%"
        ```

    1. Create a **Springboot launcher** in **Run -> Debug Configurations**
        
        ![Springboot Tool Suite Launcher](.gitlab/images/springboot-tool-launcher.png)
    
    1. Launch it and watch it starting in **Console View** 
    1. Once the application has **Started EstimeApplication** status, test it by accessing to http://localhost:8081/estime/v1/actuator/health

# [Unit Tests and Integration Tests]

Unit tests and integration tests are implemented with [JUnit](https://junit.org/junit5/docs/current/user-guide/) in **src/test/java**.

## Launch tests locally

Right click on **src/test/java** => **Run As -> Junit Test**

# [Containerization] Use Docker

- **./docker/local** : configuration files to launch the application in local environment with Docker Compose
- **./docker/dist** : configuration files for staging and production environments and a deployment on a Docker Swarm server
- **./docker/commun** : common files (bash scripts, etc.)

## Launch the application in local environment with Docker Compose

**Prerequisites :** Install [Docker](https://docs.docker.com/engine/install/) and [Docker Compose](https://docs.docker.com/compose/install/).

1. Launch application build :

   ```
   foo@bar:~estime-backend$ mvn clean install
   ```
1.Launch Docker image build :

   ```
   foo@bar:~estime-backend$ docker build . -f ./docker/local/Dockerfile  -t estime-backend
   ```

1. Create a **docker-compose.yml** file
 
   - Copy the next lines and remplace all **%% à renseigner %%** variables

   <br />
   
   ```
   version: '3.8'

   services:
      estime-backend:
        image: estime-backend
        environment:
          ENVIRONMENT: "localhost"
          PE_CONNECT_CLIENT_ID: "%% à renseigner %%"
          PE_CONNECT_CLIENT_SECRET: "%% à renseigner %%"
          PE_CONNECT_ISSUER_URI: "%% à renseigner %%"
          PE_CONNECT_JWK_SET_URI: "%% à renseigner %%"
          PE_CONNECT_TOKEN_URI: "%% à renseigner %%"
          PE_CONNECT_USER_INFO_URI: "%% à renseigner %%"
          ESD_COORDONNEES_API_URI: "%% à renseigner %%"
          ESD_DATE_NAISSANCE_API_URI: "%% à renseigner %%"
          ESD_DETAIL_INDEMNISATION_API_URI: "%% à renseigner %%"
          TZ: "Europe/Paris"
        ports:
          - 8081:8080
   ```
1. Go to your docker-compose file directory and run the container :

   ```shell
   foo@bar:~docker-compose-directory$ docker-compose up -d
   ```

1. The application should be accessible on http://localhost:8081, test it by accessing to http://localhost:8081/estime/v1/actuator/health. 

# [CI/CD] build and automated deployment with Gitlab CI

See gitlab-ci.yml file.

# [Code Quality] Follow the quality of the source code

Dashboard with Sonarqube : https://sonarqube.beta.pole-emploi.fr/dashboard?id=estime-backend

# [Server Environment] How to manage the application on a Docker Swarm server ?

- Check application status :

   ```
   foo@bar:~$ docker container ls | grep estime-backend
   ```
   Containers must be in status UP and healthy.

- Watch logs :

   ```
   foo@bar:~$ docker service logs estime-backend_estime-backend 
   ```

- Start or restart the service

   - Go to /home/docker/estime-backend directory
   - Execute the next command :
      ```
      foo@bar:/home/docker/estime-backend$ docker stack deploy --with-registry-auth -c estime-backend-stack.yml estime-backend 
      ```

- Stop the service :

   ```
   foo@bar:~$ docker stack rm estime-frontend
   ```

## Zero Downtime Deployment

The Docker service is configured to get zero downtime during deployment.

```
healthcheck:
  test: curl -v --silent http://localhost:8080/estime/v1/actuator/health 2>&1 | grep UP || exit 1
  timeout: 30s
  interval: 1m
  retries: 10
  start_period: 180s
deploy:
  replicas: 2
  update_config:
    parallelism: 1
    order: start-first
    failure_action: rollback
    delay: 10s
  rollback_config:
    parallelism: 0
    order: stop-first
  restart_policy:
    condition: any
    delay: 5s
    max_attempts: 3
    window: 180s
```

This configuration allows to replicate the service with 2 replicas. When a restart coming, a service will be considered operationnal if healthcheck test succeeded. If a restart comming, Docker restart one service and when this first service is operationnal (healthy status), Docker updates the second service.

## CPU and memory reservations

To control server resources, limitations on CPU and memory usage have been configured :

```
resources:
  reservations:
    cpus: '0.20'
    memory: 512Mi
  limits:
    cpus: '0.40'
    memory: 1536Mi
```

To see CPU and memory used by Docker containers, execute this command :
```
foo@bar:~$ docker stats
```

##  How to know the deployed application version ?

You just have to access to [https://estime.pole-emploi.fr/estime/v1/actuator/info](https://estime.pole-emploi.fr/estime/v1/actuator/info )

# [OpenFisca France]

**2 posibilities :**

- Use public instance, **application.yml** file :

  ```
  openfisca-api-uri: https://fr.openfisca.org/api/v24/calculate
  ```

- Install OpenFisca in your local environment :

  - Follow the [project README](https://github.com/StartupsPoleEmploi/openfisca-france)
  - **application.yml** file :

    ```
    openfisca-api-uri: http://localhost:5000/calculate
    ```
# [Documentation] Swagger

TODO

# [Monitoring] Actuator

Use [Actuator module](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html).

Only the next 2 endpoints are accessibles (see SecurityConfig.java if you want to change it) :

- https://estime.pole-emploi.fr/estime/v1/actuator/health
- https://estime.pole-emploi.fr/estime/v1/actuator/info
