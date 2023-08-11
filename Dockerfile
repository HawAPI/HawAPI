FROM amazoncorretto:8-alpine-jdk

LABEL org.opencontainers.image.title="HawAPI"
LABEL org.opencontainers.image.licenses="MIT"
LABEL org.opencontainers.image.url="https://hawapi.theproject.id"
LABEL org.opencontainers.image.source="https://github.com/HawAPI/HawAPI"
LABEL org.opencontainers.image.documentation='https://hawapi.theproject.id/docs/'
LABEL org.opencontainers.image.description="A Free and Open Source API for Stranger Things"

LABEL maintainer="Lucas Josino <contact@lucasjosino.com>"
LABEL git="https://github/HawAPI/HawAPI"
LABEL website="https://hawapi.theproject.id"

WORKDIR /app

COPY target/hawapi-*.jar hawapi.jar

EXPOSE 8080:8080
CMD ["java","-jar","hawapi.jar", "-Dspring.profiles.active=prod"]