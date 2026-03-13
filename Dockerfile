FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY ./pom.xml ./mvnw ./
COPY chainvault-migration ./chainvault-migration
COPY chainvault-orchestration ./chainvault-orchestration
COPY chainvault-report-aggregate ./chainvault-report-aggregate
RUN mvn -DskipTests -q package

FROM eclipse-temurin:25-jdk-jammy AS runtime
ARG USERNAME=chainvault
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ARG CHAINVAULT_VERSION=1.0.0-SNAPSHOT

# Create the user, install Tesseract and libraries
RUN addgroup --system "$USERNAME" \
    && adduser --system "$USERNAME" --ingroup "$USERNAME" \
    && apt-get update \
    && apt-get install -y \
        libleptonica-dev \
        libtesseract-dev \
        tesseract-ocr \
        tesseract-ocr-deu \
        tesseract-ocr-eng \
    && rm -rf /var/lib/apt/lists/*

USER $USERNAME
WORKDIR /app
COPY --from=build /workspace/chainvault-orchestration/target/chainvault-${CHAINVAULT_VERSION}.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
