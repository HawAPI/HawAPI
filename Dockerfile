FROM amazoncorretto:8

LABEL maintainer="Lucas Josino <contact@lucasjosino.com>"
LABEL git="https://github/HawAPI/HawAPI"
LABEL website="https://hawapi.theproject.id"

WORKDIR /app

COPY target/hawapi-*.jar hawapi.jar

EXPOSE 8080:8080
ENTRYPOINT ["java","-jar","hawapi.jar", "--spring.profiles.active=prod"]