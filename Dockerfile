# syntax=docker/dockerfile:1

# ---------------------------------------------------------------------------
# Stage 1 - unpack the Spring Boot fat jar into cache-friendly layers.
# The jar is produced by Maven before this build runs; its name is never
# hardcoded, it is resolved by the caller through the JAR_FILE build arg.
# ---------------------------------------------------------------------------
FROM eclipse-temurin:26-jdk-alpine AS builder

WORKDIR /build

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted \
    && mkdir -p extracted/dependencies/lib extracted/snapshot-dependencies/lib

# ---------------------------------------------------------------------------
# Stage 2 - minimal runtime image (JRE only, no build tooling, no fat jar).
# ---------------------------------------------------------------------------
FROM eclipse-temurin:26-jre-alpine AS runtime

WORKDIR /app

# Non-root runtime user.
RUN addgroup -S app && adduser -S -G app app

# Dependencies first so application code changes do not invalidate this layer.
COPY --from=builder --chown=app:app /build/extracted/dependencies/lib/ ./lib/
COPY --from=builder --chown=app:app /build/extracted/snapshot-dependencies/lib/ ./lib/
COPY --from=builder --chown=app:app /build/extracted/application/application.jar ./application.jar

USER app

# Port is injected at build time so EXPOSE matches the published port.
ARG PORT=8080
ENV SERVER_PORT=${PORT}
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "application.jar", "--spring.profiles.active=dev"]
