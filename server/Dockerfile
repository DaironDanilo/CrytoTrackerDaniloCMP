# Build context is the repo root, not server/ -- :server is part of the
# multi-module Gradle build (implementation(projects.core)), so Gradle needs
# the root settings.gradle.kts/build.gradle.kts and core/ to configure at
# all. Verified building :server:buildFatJar succeeds with no
# local.properties/ANDROID_HOME present, so the sibling Android/iOS/Desktop/
# Web modules being configured (but not built) doesn't require an SDK.
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew :server:buildFatJar --no-daemon

# distroless, not eclipse-temurin:17-jre: the fat jar bundles every class and
# dependency it needs (no runtime pip/apt-style resolution like the Python
# jobs in the data-platform repo), never shells out, and the entrypoint is
# already exec-form with no shell dependency -- a JVM server app is close to
# the ideal case for distroless. java17-debian12 (not the "static" distroless
# variant) still ships a JRE with cacerts + tzdata, so outbound TLS to
# Supabase keeps working.
FROM gcr.io/distroless/java17-debian12:nonroot
WORKDIR /app
COPY --from=build --chown=nonroot:nonroot /app/server/build/libs/server.jar server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "server.jar"]
