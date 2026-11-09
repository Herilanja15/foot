FROM eclipse-temurin:17-jdk-jammy

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        fluxbox \
        novnc \
        websockify \
        x11-utils \
        x11vnc \
        xvfb \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY src/ ./src/
COPY lib/ojdbc17.jar ./lib/ojdbc17.jar
COPY docker/entrypoint.sh ./docker/entrypoint.sh

RUN mkdir -p bin \
    && javac -encoding UTF-8 -cp lib/ojdbc17.jar -d bin src/*.java

EXPOSE 6080

ENTRYPOINT ["/bin/sh", "/app/docker/entrypoint.sh"]
