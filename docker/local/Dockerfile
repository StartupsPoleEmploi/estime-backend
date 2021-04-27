#https://spring.io/guides/gs/spring-boot-docker/
FROM adoptopenjdk/openjdk11:alpine

RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add \
    curl \
    perl \
    tzdata \
    && rm -rf /var/cache/apk/*


#create a non-root user named java
RUN addgroup -S java && adduser -S java -G java

#add useful scripts
COPY ./docker/commun/scripts/startup.sh .
RUN chmod +x startup.sh

#add application sources
ADD ./target/**.jar /app.jar
RUN jar -xf ../*.jar
RUN mkdir -p /app
RUN cp -r BOOT-INF/lib /app
RUN cp -r META-INF /app
RUN cp -a BOOT-INF/classes/. /app
COPY ./docker/local/springboot-config/application.yml /app/

## add permissions for java user
RUN chown java:java startup.sh && \
    chown -R java:java /app 

USER java

ENTRYPOINT ./startup.sh